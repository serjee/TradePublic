package ru.mibix.candles.oanda;

import com.sun.xml.internal.ws.api.server.ThreadLocalContainerResolver;
import com.sun.xml.internal.ws.util.Pool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.candles.oanda.database.CandlesDao;
import ru.mibix.candles.oanda.model.CandlesModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Forming candles factory
 */
public class CandlesUpdating
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(CandlesUpdating.class.getName());

    /**
     * Granularity
     */
    private final static String GRANULARITY_DEFAULT = "M1";

    /**
     * Count candles for get from oanda by default when M1 is exist in Db
     */
    private final static int OANDA_GET_CANDLES_COUNT_BY_DEFAULT_M1 = 10;

    /**
     * Count candles for get from oanda when M1 is not exist in Db
     */
    private final static int OANDA_GET_CANDLES_COUNT_FOR_NULL_M1 = 10;

    /**
     * Count candles for get from oanda when older TF is not exist in Db
     */
    private final static int OANDA_GET_CANDLES_COUNT_FOR_OLDER_NULL_TF = 10;

    /**
     * The sleep time (seconds) before request to oanda
     */
    private final static int SLEEP_OANDA_REQUEST_TIME = 2;

    /**
     * Symbol for processing
     */
    private int symbol;

    /**
     * BaseDAO factory
     */
    private CandlesDao candlesDAO;

    /**
     * Constructor
     *
     * @param symbol -
     */
    public CandlesUpdating(int symbol)
    {
        this.symbol = symbol;

        candlesDAO = new CandlesDao();
    }

    /**
     * Get Candles from OANDA (through REST API)
     *
     * @return - last candles ArrayList
     */
    private ArrayList<CandlesModel> getOandaCandles() throws InterruptedException
    {
        return getOandaCandles(GRANULARITY_DEFAULT, OANDA_GET_CANDLES_COUNT_BY_DEFAULT_M1);
    }

    /**
     * Get Candles from OANDA (through REST API)
     *
     * @return - last candles ArrayList
     */
    private ArrayList<CandlesModel> getOandaCandles(int gapCandles) throws InterruptedException
    {
        return getOandaCandles(GRANULARITY_DEFAULT, gapCandles);
    }

    /**
     * Get Candles from OANDA (through REST API)
     *
     * @param gapCandles - count candles for get
     * @return
     */
    private synchronized ArrayList<CandlesModel> getOandaCandles(String granularity, int gapCandles) throws InterruptedException
    {
        log.debug("Sleep before request to API OANDA : " + SLEEP_OANDA_REQUEST_TIME + " sec.");

        if (SLEEP_OANDA_REQUEST_TIME > 0)
            Thread.sleep(SLEEP_OANDA_REQUEST_TIME * 1000);

        if (gapCandles>5000)
            gapCandles = 5000;

        return new OandaRestApi().getInstrumentCandles(TUtils.getSymbolNameById(symbol, true), granularity, gapCandles);
    }

    /**
     * Forming bars on based information from ticks arrive to DB
     */
    public void formAndInsertCandles()
    {
        CandlesModel cLastDbModel, cLastOandaModel, cFirstOandaModel;
        ArrayList<CandlesModel> acm;

        try
        {
            // Get last candle from DB by Symbol for M1
            candlesDAO.setTimeframe(1);
            cLastDbModel = candlesDAO.getLastBarBySymbol(symbol);

            // Last bar should not be empty
            if (cLastDbModel==null)
            {
                // Get candles from oanda for first fill the one to Db
                log.debug("M1 => last model is null in Db.");
                acm = getOandaCandles(GRANULARITY_DEFAULT, OANDA_GET_CANDLES_COUNT_FOR_NULL_M1);

                // Insert each candle which had got from oanda
                for (CandlesModel cm : acm)
                {
                    candlesDAO.insert(cm);
                    log.debug("M1 => inserted for null new candle : " + cm.getTime());
                }

                // Get last candle from DB by Symbol for M1
                cLastDbModel = candlesDAO.getLastBarBySymbol(symbol);
                log.error("M1 => null have been filled. LastDbModel is : " + cLastDbModel.getTime());
            }
            else
            {
                acm = getOandaCandles();
                cFirstOandaModel = acm.get(0);

                log.debug("M1 => cLastDbModel.Time : " + cLastDbModel.getTime());
                log.debug("M1 => cFirstOandaModel.Time : " + cFirstOandaModel.getTime());

                // If first candle is not the next after last candles from Db, than we need fill the gap (repeat request with +GAP)
                if (cFirstOandaModel.getTime().after(cLastDbModel.getTime()))
                {
                    int diffTime = (int) Math.ceil(((cFirstOandaModel.getTime().getTime() - cLastDbModel.getTime().getTime()) / 1000 / 60) / 1); // 1 is timeframe
                    log.debug("M1 => found GAP of size : " + (diffTime+OANDA_GET_CANDLES_COUNT_BY_DEFAULT_M1));
                    acm = getOandaCandles(diffTime + OANDA_GET_CANDLES_COUNT_BY_DEFAULT_M1); // get gap + default count candles (update ArrayList)
                    log.debug("M1 => got all candles from Oanda for fill the GAP.");
                }

                // Get last candle from oanda
                cLastOandaModel = acm.get(acm.size()-1);
                log.debug("M1 => cLastOandaModel.Time : " + cLastOandaModel.getTime());

                // Update M1 from OANDA to DB (only if M1 from Oanda more or equal for last candle from Db)
                if (cLastOandaModel.getTime().after(cLastDbModel.getTime()) || cLastOandaModel.getTime().equals(cLastDbModel.getTime()))
                {
                    // For each candle which got from Oanda
                    for (CandlesModel cm : acm)
                    {
                        log.debug("M1 => cm.Time (cycle) : " + cm.getTime());
                        log.debug("M1 => cLastDbModel.Time : " + cLastDbModel.getTime());

                        // When candle is exist into DB (update)
                        if (cLastDbModel.getTime().equals(cm.getTime()))
                        {
                            cm.setId(cLastDbModel.getId());  // set Id for update
                            candlesDAO.update(cm);
                            log.debug("M1 => updated candle from Oanda : Time="+cm.getTime()+"; Symbol="+cm.getSymbol()+"; Open="+cm.getOpen()+"; High="+cm.getHigh()+"; Low="+cm.getLow()+"; Close="+cm.getClose());
                        }
                        // when oanda candle is not exist in DB (add new)
                        else if (cm.getTime().after(cLastDbModel.getTime()))
                        {
                            candlesDAO.insert(cm);
                            log.debug("M1 => inserted candle from Oanda : Time="+cm.getTime()+"; Symbol="+cm.getSymbol()+"; Open="+cm.getOpen()+"; High="+cm.getHigh()+"; Low="+cm.getLow()+"; Close="+cm.getClose());
                        }
                    }
                }
            }

            // Update candles for each timeframe
            for (int tf : TUtils.getAllTimeFrameIDs()) {
                realculateCandlesIntoDb(tf, cLastDbModel.getTime());
            }

            cLastDbModel = null;
            cLastOandaModel = null;
        }
        catch (Exception e)
        {
            log.error("formAndInsertCandles exception : " + e.getMessage());
        }
        finally
        {
            cLastDbModel = null;
            cLastOandaModel = null;
        }
    }

    /**
     * Re-calculate and update candles for point timeframe
     * (update M5 candles using M1 => update M15 candles using M5 (prev) and etc...)
     *
     * @param tf - timeframe for update candles
     * @param lastCandleDate - date of last candle in Db
     */
    private void realculateCandlesIntoDb(int tf, Date lastCandleDate)
    {
        // M1 timeframe must be updated only from oanda
        if (tf==1)
            return;

        // Init values
        ArrayList<CandlesModel> acm;    // Array of Oanda candles
        ArrayList<CandlesModel> arUpdateCandles = new ArrayList<>();
        Date openTime, closeTime;       // Open and close dates
        CandlesModel cLastDbModel, calcCandlesModel;    // Models
        boolean isFirst;         // Flag for calculation the candle values

        try
        {
            // Get last candle from Db by Symbol for timeframe
            candlesDAO.setTimeframe(tf);
            cLastDbModel = candlesDAO.getLastBarBySymbol(symbol);
            if (cLastDbModel == null)
            {
                // Get candles from oanda for first fill the one to Db
                log.debug(TUtils.getGranularityByTf(tf) + " => model is null into Db.");
                acm = getOandaCandles(TUtils.getGranularityByTf(tf), OANDA_GET_CANDLES_COUNT_FOR_OLDER_NULL_TF);

                // Insert each candle which had got from oanda
                for (CandlesModel cm : acm)
                {
                    candlesDAO.insert(cm);
                    log.debug(TUtils.getGranularityByTf(tf) + " => inserted for null new candle : " + cm.getTime());
                }

                return; // after update TF we should exit from here
            }

            // Get count candles for update for current timeframe
            int candlesCntForUpdate = (int) Math.ceil(((lastCandleDate.getTime() - cLastDbModel.getTime().getTime()) / 1000 / 60) / tf);
            log.debug(TUtils.getGranularityByTf(tf) + " => total candles for update : " + candlesCntForUpdate);

            // Each of candle for update
            for (int i = 0; i <= candlesCntForUpdate; i++)
            {
                // calculate
                //calendar.setTime(barTime); // set last bar time
                //calendar.add(Calendar.MINUTE, i*timeframe);       // add minute which not calculated
                //calendar.getTime();

                calcCandlesModel = new CandlesModel();

                // Get open time and close time for last bar in timeframe
                openTime = cLastDbModel.getTime();
                if (i>0)
                    openTime = new Date(openTime.getTime() + (i * tf * 60000)); // 60000 - millisecs in one minute

                closeTime = new Date(openTime.getTime() + (tf * 60000)); // 60000 - millisecs in one minute

                // For month timeframe we need use closeTime=last day of month for day of openTime
                if (tf==43200)
                {
                    Calendar c = Calendar.getInstance();
                    c.setTime(openTime);
                    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                    closeTime = c.getTime();
                }

                log.debug(TUtils.getGranularityByTf(tf) + " => candle №" + i + " openTime.Time :" + openTime);
                log.debug(TUtils.getGranularityByTf(tf) + " => candle №" + i + " closeTime.Time : " + closeTime);

                // Begin fill the new CandleModel
                calcCandlesModel.setTimeframe(tf);
                calcCandlesModel.setSymbol(symbol);
                calcCandlesModel.setTime(openTime);

                // Define the previous TF
                candlesDAO.setTimeframe(TUtils.getPreviousTimeframe(tf));
                if (tf==43200)
                    candlesDAO.setTimeframe(1440);   // for calculate month candle we use day timefrmae
                log.debug(TUtils.getGranularityByTf(tf) + " => candle №" + i + " previous TF : " + TUtils.getPreviousTimeframe(tf));

                // Get values of candles for previous timeframe using openTime and closeTime
                List<CandlesModel> lcm = candlesDAO.getAllCandlesBySymbolByDateBetween(symbol, openTime, closeTime);

                try
                {
                    if (lcm.size()>0)
                    {
                        isFirst = true; // set flag of begin cycle

                        // calculate bar
                        for (CandlesModel c : lcm)
                        {
                            log.debug(TUtils.getGranularityByTf(tf) + " => candle №" + i + " is calculate using previous candle : " + c.getTime());
                            if (isFirst) {                      // set price of open, high and low from first candle
                                calcCandlesModel.setOpen(c.getOpen());
                                calcCandlesModel.setHigh(c.getHigh());
                                calcCandlesModel.setLow(c.getLow());
                                isFirst = false;
                            } else {
                                if (c.getHigh() > calcCandlesModel.getHigh())
                                    calcCandlesModel.setHigh(c.getHigh());   // set new high
                                if (c.getLow() < calcCandlesModel.getLow())
                                    calcCandlesModel.setLow(c.getLow());     // set new low
                            }
                            calcCandlesModel.setClose(c.getClose());         // set close (update every tick)
                        }

                        // Add calculated candles to ArrayList
                        arUpdateCandles.add(calcCandlesModel);
                        log.debug(TUtils.getGranularityByTf(tf) + " => candle №" + i + " had been added to ArrayList : TF="+tf+"; Time="+calcCandlesModel.getTime()+"; Open="+calcCandlesModel.getOpen()+"; High="+calcCandlesModel.getHigh()+"; Low="+calcCandlesModel.getLow()+"; Close="+calcCandlesModel.getClose());
                    }

                    lcm = null;
                }
                catch (Exception e)
                {
                    log.error("realculateCandlesIntoDb exception : " + e.getMessage());
                }
                finally
                {
                    lcm = null;
                }
            }

            // Update/Add candles for DB
            candlesDAO.setTimeframe(tf);
            for (CandlesModel ncm : arUpdateCandles)
            {
                if (ncm.getTime().equals(cLastDbModel.getTime()))
                {
                    ncm.setId(cLastDbModel.getId()); // set Id for update
                    candlesDAO.update(ncm); // update
                    log.debug(TUtils.getGranularityByTf(tf) + " => updated candles time : " + ncm.getTime());
                }
                else if (ncm.getTime().after(cLastDbModel.getTime()))
                {
                    candlesDAO.insert(ncm); // add
                    log.debug(TUtils.getGranularityByTf(tf) + " => added new candles time : " + ncm.getTime());
                }
            }
        }
        catch (Exception e)
        {
            log.error("realculateCandlesIntoDb exception : " + e.getMessage());
        }
    }
}
