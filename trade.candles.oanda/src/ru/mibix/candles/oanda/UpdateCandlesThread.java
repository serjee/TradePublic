package ru.mibix.candles.oanda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for processing ticks of different symbols
 */
public class UpdateCandlesThread implements Runnable
{
    private static final Logger log = LogManager.getLogger(UpdateCandlesThread.class.getName());

    /**
     * Thread name
     */
    String name;

    /**
     * Thread object
     */
    Thread t;

    /**
     * Constructor - create new thread for specified symbol
     *
     * @param name - thread name
     */
    public UpdateCandlesThread(String name)
    {
        log.info(name + " update thread is starting.");

        this.name = name;
        t = new Thread(this, name);
        t.start();
    }

    /**
     * Run thread
     */
    public void run()
    {
        try
        {
            // Bar processing start
            CandlesUpdating cu = new CandlesUpdating(TUtils.getSymbolIdByName(name));

            while(true)
            {
                cu.formAndInsertCandles();
                Thread.sleep(1000*30); // white (for low load)
            }
        }
        catch (Exception e)
        {
            log.error(name + " thread is crash with error : " + e.getMessage());
        }
    }
}
