package ru.mibix.candles.oanda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.candles.oanda.database.impl.DatabaseFactory;
import ru.mibix.candles.oanda.model.CandlesModel;

import java.util.ArrayList;

public class ТCandlesOanda
{
    /**
     * Logger
     */
    private static final Logger log = LogManager.getLogger(OandaRestApi.class.getName());

    /**
     * Start point
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            DatabaseFactory.init();

            // run new thread for each symbols
            UpdateCandlesThread tEurUsd = new UpdateCandlesThread("EURUSD");
            //UpdateCandlesThread tGbpJpy = new UpdateCandlesThread("GBPJPY");

            tEurUsd.t.join();
            //tGbpJpy.t.join();
        }
        catch (InterruptedException e)
        {
            log.error("ТCandlesOanda thread is crashed.");
        }

	    // write your code here
//        OandaRestApi oandaApi = new OandaRestApi();
//        ArrayList<CandlesModel> acm = oandaApi.getInstrumentCandles("EUR_USD", "M1", 10);
//        for (CandlesModel cm : acm)
//        {
//            log.debug("Time="+cm.getTime()+"; Symbol="+cm.getSymbol()+"; Open="+cm.getOpen()+"; High="+cm.getHigh()+"; Low="+cm.getLow()+"; Close="+cm.getClose());
//        }
    }
}
