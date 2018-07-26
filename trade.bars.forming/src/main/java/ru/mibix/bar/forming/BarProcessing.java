package ru.mibix.bar.forming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.bar.forming.database.TickDao;
import ru.mibix.bar.forming.database.BarDao;
import ru.mibix.bar.forming.model.BarModel;
import ru.mibix.bar.forming.model.TickModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Forming bar factory
 */
public class BarProcessing
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(BarProcessing.class.getName());

    /**
     * Last tick which had processed (protected for repeat processed)
     */
    private int lastProcessedTick;

    /**
     * Symbol for processing
     */
    private int symbol;

    /**
     * BaseDAO factory
     */
    private TickDao tickDAO;
    private BarDao barDAO;

    /**
     * Constructor
     *
     * @param symbol -
     */
    public BarProcessing(int symbol)
    {
        this.lastProcessedTick = 0;
        this.symbol = symbol;

        tickDAO = new TickDao();
    }

    /**
     * Forming bars on based information from ticks arrive to DB
     */
    public void FormBar()
    {
        TickModel lastTick;

        try
        {
            // Get last ticks and bars from DB by Symbol
            lastTick  = tickDAO.getLastTickBySymbol(symbol);

            // lastTick should not be empty
            if (lastTick==null)
            {
                log.error("LastTick is NULL for symbol " + TUtils.getSymbolNameById(symbol));
                return;
            }

            // No process tick which had been processed early
            if (lastProcessedTick == lastTick.getId())
            {
                lastTick = null;
                return;
            }

            // Update bar for every timeframe
            for (Integer tf : TUtils.getAllTimeFrameIDs())
                PrepareBar(tf, lastTick);

            lastProcessedTick = lastTick.getId(); // set ID for last processed tick
            lastTick = null;
        }
        catch (Exception e)
        {
            log.error("FormBar exception : " + e.getMessage());
        }
        finally
        {
            lastTick = null;
        }
    }

    /**
     * Prepare bar forming
     *
     * @param timeframe - timeframe
     * @param lastTick  - TickModel (last)
     */
    private void PrepareBar(int timeframe, TickModel lastTick)
    {
        int compareResult;  // результат сравнения
        int diffTime;       // разница во времени синхронизации
        Date updateBarTime; // время бара, с которого начнется синхронизация в БД
        Date nextBarTime;   // расчетное время следующего бара в зависимости от таймфрейма

        BarModel lastBar;

        try
        {
            barDAO  = new BarDao(timeframe);
            lastBar = barDAO.getLastBarBySymbol(symbol);

            // lastBar null only when DB empty (not do when is not exist bars for timeframe in DB)
            if (lastBar==null)
            {
                log.error("Last bar is NULL in DB for timeframe " + TUtils.getNameDbByTf(timeframe,false));
            }
            else
            {
                if (timeframe==1)
                {
                    //diffTime = 0;
                    compareResult = lastTick.getTime().compareTo(lastBar.getTime()); // результат сравнения дат (bar & tick)
                }
                else
                {
                    //diffTime = 1;

                    // lB <= lT < (lB+TF) - tick need between lB and lB+TF
                    nextBarTime = new Date(lastBar.getTime().getTime() + (timeframe * 60000)); // 60000 - millisecs in one minute
                    if (nextBarTime.after(lastTick.getTime()) && (lastTick.getTime().after(lastBar.getTime()) || lastTick.getTime().equals(lastBar.getTime())))
                        compareResult   = 0;    // update only
                    else if (lastTick.getTime().after(nextBarTime))
                        compareResult   = 1;    // add new
                    else
                        compareResult   = -1;   // no processing
                }

                diffTime        = (int) Math.ceil(((lastTick.getTime().getTime() - lastBar.getTime().getTime()) / 1000 / 60) / timeframe); // возвращаем разницу в количестве добавляемых баров
                updateBarTime   = lastBar.getTime();

                // set/update bar
                DoBarProcessing(timeframe, compareResult, diffTime, updateBarTime, lastBar);
            }
            barDAO = null;
            lastBar = null;
        }
        catch (Exception e)
        {
            log.error("PrepareBar exception : " + e.getMessage());
        }
        finally
        {
            barDAO = null;
            lastBar = null;
        }
    }

    /**
     * Add/Update need bar in DB
     *
     * @param timeframe - timeframe for forming bar
     * @param compareResult - 0 is compare, 1 tick > bar
     * @param diffTime  - diff time when tick > bar
     * @param barTime   - time last bar
     */
    private void DoBarProcessing(int timeframe, int compareResult, int diffTime, Date barTime, BarModel bm)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EET")); // восточноевропейское время (=MT4 Alpari)

        if (compareResult == 0)
        {
            UpdateBar(timeframe, barTime, bm);
        }
        else if (compareResult == 1)
        {
            for (int i = 0; i <= diffTime; i++) // calculate all minutes which is not set
            {
                if (i==0) // update last bar
                {
                    UpdateBar(timeframe, barTime, bm);
                    //log.debug("Update exist bar : " + barTime.toString());
                }
                else
                {
                    // calculate
                    calendar.setTime(barTime); // set last bar time
                    calendar.add(Calendar.MINUTE, i*timeframe);       // add minute which not calculated
                    //log.debug("Calculated time of new bar " + i + " : " + calendar.getTime().toString());

                    // add
                    AddBar(timeframe, calendar.getTime());
                }
            }
        }

        calendar = null;
    }

    /**
     * Add new bar to DB
     *
     * @param time - time with last tick
     */
    private void AddBar(int timeframe, Date time)
    {
        BarModel bm;

        try
        {
            bm = CalculateBar(time, timeframe, new BarModel());

            if (bm.getOpen()>0) // 0 when ticks not found for this bar
                barDAO.insert(bm);
        }
        catch (Exception e)
        {
            log.error("AddBar exception : " + e.getMessage());
        }
    }

    /**
     * Update bar in DB (after new calculation)
     *
     * @param time - time with last tick
     */
    private void UpdateBar(int timeframe, Date time, BarModel bm)
    {
        try
        {
            bm = CalculateBar(time, timeframe, bm);

            if (bm.getOpen()>0) // 0 when ticks not found for this bar
                barDAO.update(bm);
        }
        catch (Exception e)
        {
            log.error("UpdateBar exception : " + e.getMessage());
        }
    }

    /**
     * Calculate bar on base available ticks
     *
     * @param openTime - time with last tick
     * @return - BarModel
     */
    private BarModel CalculateBar(Date openTime, int timeframe, BarModel bm)
    {
        boolean isFirst = true;
        List<TickModel> ltm;

        try
        {
            // select need ticks from DB
            if (timeframe>1)
            {
                Date closeTime = new Date(openTime.getTime() + (timeframe * 60000)); // 60000 - millisecs in one minute

                //TODO: Надо это дело оптимизировать для TF>1 !!! Тики выбираются за час, день, месяц....!
                // Вначале обновлям М1 на основе тика, затем старший ТФ обновляем на основе младшего ТФ

                ltm = tickDAO.getAllTickBySymbolByDateBetween(symbol, openTime, closeTime);
            }
            else
                ltm = tickDAO.getAllTickBySymbolByDate(symbol, openTime);

            if (ltm != null)
            {
                // calculate bar
                for (TickModel t : ltm)
                {
                    if (isFirst) {                      // set price of open, high and low from first tick
                        bm.setOpen(t.getOpen());
                        bm.setHigh(t.getHigh());
                        bm.setLow(t.getLow());
                        isFirst = false;
                    } else {
                        if (t.getHigh() > bm.getHigh())
                            bm.setHigh(t.getHigh());   // set new high
                        if (t.getLow() < bm.getLow())
                            bm.setLow(t.getLow());     // set new low
                    }
                    bm.setClose(t.getClose());         // set close (update every tick)
                }

                // for new BarModel
                if (bm.getId()==0)
                {
                    bm.setSymbol(symbol);
                    bm.setTime(openTime);
                }
            }

            ltm = null;
        }
        catch (Exception e)
        {
            log.error("CalculateBar exception : " + e.getMessage());
        }
        finally
        {
            ltm = null;
        }

        return bm;
    }
}
