package ru.mibix.bar.forming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for processing ticks of different symbols
 */
public class TickThread implements Runnable
{
    private static final Logger log = LogManager.getLogger(TickThread.class.getName());

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
    public TickThread(String name)
    {
        log.info(name + " process thread is starting.");

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
            BarProcessing bp    = new BarProcessing(TUtils.getSymbolIdByName(name));

            while(true)
            {
                bp.FormBar();
                Thread.sleep(1000); // white (for low load)
            }
        }
        catch (Exception e)
        {
            log.error(name + " thread is crash with error : " + e.getMessage());
        }
    }
}
