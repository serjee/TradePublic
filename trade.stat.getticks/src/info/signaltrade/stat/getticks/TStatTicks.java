package info.signaltrade.stat.getticks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Main class
 */
public class TStatTicks
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(TStatTicks.class.getName());

    /**
     * Main
     *
     * @param args - params
     */
    public static void main(String[] args)
    {
        //TODO: отправить SMS-уведомление мне

        // get args
        int[] stDate = null;
        int[] fnDate = null;

        // default values
        String link;
        Calendar cal;

        try {

            // load properties
            Utils.loadProperties();

            // get start and finish dates from args
            if (args.length > 0)
            {
                // allowed 1 or 2 arguments only
                if (args.length > 2)
                    throw new IllegalArgumentException("not a valid argument");

                // get start date from args and convert to int array
                stDate = Arrays.asList(args[0].split("-")).stream().mapToInt(Integer::parseInt).toArray();
                log.debug("args stDate : month=" + stDate[0] + ", year=" + stDate[1]);

                // get finish date from args
                if (args.length ==2) {
                    fnDate = Arrays.asList(args[1].split("-")).stream().mapToInt(Integer::parseInt).toArray();
                    log.debug("args fnDate : month=" + fnDate[0] + ", year=" + fnDate[1]);
                }
            }

            // define finish date if finish date args is null
            if (fnDate == null) {
                fnDate = new int[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                fnDate[0] = cal.get(Calendar.MONTH)+1;
                fnDate[1] = cal.get(Calendar.YEAR);

                log.debug("calculated fnDate : month=" + fnDate[0] + ", year=" + fnDate[1]);
            }

            // loop for each currency
            for (String currency : Utils.getProperties().getCurrencies())
            {
                // url link
                link = Utils.getProperties().getUrlTicks() + currency + "/";

                // create output currency dir if not exist
                Utils.checkAndCreateOutputDir(currency);

                // create or clear unzip dir
                Utils.checkAndCreateOrClearUnZipDir(currency);

                // delete old csv file
                Utils.checkAndDeleteCSV(currency);

                // unzip/update the tick filed for period
                if (stDate != null) {
                    // start and finish year is equal
                    if (stDate[1] == fnDate[1]) {
                        for (int i = stDate[0]; i <= fnDate[0]; i++) {
                            Utils.updateTickFile(link, stDate[1], i, currency); // update tick
                            Utils.implodeFiles(i, stDate[1], currency);         // append to out file
                        }
                    } else if (stDate[1] < fnDate[1]) {
                        for (int y = stDate[1]; y <= fnDate[1]; y++) {
                            if (y==fnDate[1]) {
                                for (int i = stDate[0]; i <= fnDate[0]; i++) {
                                    Utils.updateTickFile(link, y, i, currency); // update tick
                                    Utils.implodeFiles(i, y, currency);         // append to out file
                                }
                            } else {
                                for (int i = stDate[0]; i <= 12; i++) {
                                    Utils.updateTickFile(link, y, i, currency); // update tick
                                    Utils.implodeFiles(i, y, currency);         // append to out file
                                }
                            }
                        }
                    }
                } else {
                    Utils.updateTickFile(link, fnDate[1], fnDate[0], currency); // update tick
                    Utils.implodeFiles(fnDate[0], fnDate[1], currency);         // append to out file
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Parse args date exception",e);
        } catch (NullPointerException e) {
            log.error("Null pointer exception",e);
        }
    }

}
