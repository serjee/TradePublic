package ru.mibix.bar.synchronizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Synchronizer NIO server
 */
public class SynchronizerNIOServer
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(SynchronizerNIOServer.class.getName());

    /**
     * Server host or IP
     */
    private static final String SERVER_HOST = "localhost";

    /**
     * Server port
     */
    private static final int SERVER_PORT = 5168;

    /**
     * Start point
     * @param args - args from command line
     */
    @SuppressWarnings("unused")
    public static void main(String[] args)
    {
        Selector selector;
        ServerSocketChannel serverSocket;
        InetSocketAddress hostAddress;

        Set<SelectionKey> selectedKeys;
        Iterator<SelectionKey> iterator;
        SelectionKey myKey;
        SocketChannel client;
        ByteBuffer buffer;

        String result;

	    try
        {
            // Get selector
            selector = Selector.open();

            // Get server socket channel and register with selector
            serverSocket = ServerSocketChannel.open();
            hostAddress = new InetSocketAddress(SERVER_HOST, SERVER_PORT);

            // Binds the channel's socket to a local address and configures the socket to listen for connections
            serverSocket.bind(hostAddress);

            // Adjusts this channel's blocking mode.
            serverSocket.configureBlocking(false);

            int ops = serverSocket.validOps();
            serverSocket.register(selector, ops, null);

            log.info("Server is staring...");

            // Keep server running
            while (true)
            {
                selector.select();

                // token representing the registration of a SelectableChannel with a Selector
                selectedKeys = selector.selectedKeys();
                iterator = selectedKeys.iterator();

                while (iterator.hasNext())
                {
                    myKey = iterator.next();

                    // Tests whether this key's channel is ready to accept a new socket connection
                    if (myKey.isAcceptable())
                    {
                        client = serverSocket.accept();

                        // Adjusts this channel's blocking mode to false
                        client.configureBlocking(false);

                        // Operation-set bit for read operations
                        client.register(selector, SelectionKey.OP_READ);

                        log.info("Connection Accepted: " + client.getLocalAddress());
                    }
                    else if (myKey.isReadable()) // Tests whether this key's channel is ready for reading
                    {
                        client = (SocketChannel) myKey.channel();

                        buffer = ByteBuffer.allocate(256);
                        client.read(buffer);
                        result = new String(buffer.array()).trim();

                        log.info("Message received: " + result);

                    }
                    iterator.remove();
                }
            }

        }
        catch (Exception e)
        {
            log.error("NIO server exception " + e.getMessage());
        }
    }
}
