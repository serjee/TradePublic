package ru.mibix.bar.forming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.bar.forming.database.impl.DatabaseFactory;

/**
 * Start point
 */
public class TBarForming
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(TBarForming.class.getName());
    //Example:
    //log.debug("Hello world - debug log");
    //log.info("Hello world - info log");
    //log.warn("Hello world - warn log");
    //log.error("Hello world - error log");

    /**
     * Start main thread
     *
     * @param args
     */
    public static void main(String[] args)
    {
        log.info("Start TBarForming (main) thread.");

        try
        {
            DatabaseFactory.init();

            // run new thread for each symbols
            TickThread tEurUsd = new TickThread("EURUSD");
            TickThread tGbpJpy = new TickThread("GBPJPY");

            tEurUsd.t.join();
            tGbpJpy.t.join();
        }
        catch (InterruptedException e)
        {
            log.error("TBarForming (main) thread is crashed.");
        }

        log.info("TBarForming (main) thread is end.");
    }
}
