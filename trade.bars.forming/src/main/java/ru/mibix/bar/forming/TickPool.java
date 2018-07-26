package ru.mibix.bar.forming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class not use now! (create additional threads)
 */
public class TickPool
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(TickPool.class.getName());

    /**
     * Queue
     */
    private final Queue<Runnable> workerQueue;

    /**
     * Threads array
     */
    private final Thread[] threads;

    /**
     * Constructor
     *
     * @param threadCapacity
     */
    public TickPool(int threadCapacity)
    {
        checkIfProperThreadCapacity(threadCapacity);
        threads = new Thread[threadCapacity];
        workerQueue = new LinkedList<>();
    }

    /**
     * Check capacity
     * @param threadCapacity
     */
    private void checkIfProperThreadCapacity(int threadCapacity)
    {
        if (threadCapacity < 0)
        {
            //throw new IllegalArgumentException("Thread pool minimum capacity is 0. Given capacity: " + threadCapacity);
            log.error("Thread pool minimum capacity is 0. Given capacity: " + threadCapacity);
        } else if (threadCapacity == 0) {
            log.info("Thread pool is eq 0.");
        }
    }

    public synchronized void addTask(Runnable runnable)
    {
        if (threads[0] == null)
        {
            init();
        }
        workerQueue.add(runnable);
        notify();
    }

    private void init()
    {
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Worker("Pool Thread " + i);
            threads[i].start();
        }
    }

    public void shutdown()
    {
        while (!workerQueue.isEmpty())
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ignore) { }
        }
        for (Thread workerThread : threads) {
            workerThread.interrupt();
        }
    }

    private class Worker extends Thread
    {
        public Worker(String name) {
            super(name);
        }

        public void run()
        {
            while (!isInterrupted())
            {
                Runnable runnable = null;
                synchronized (TickPool.this)
                {
                    while (workerQueue.isEmpty())
                    {
                        try
                        {
                            TickPool.this.wait();
                        }
                        catch (InterruptedException e)
                        {
                            return;
                        }
                    }
                    runnable = workerQueue.poll();
                }
                if (runnable != null)
                {
                    runnable.run();
                    runnable = null;
                }
            }
        }
    }
}