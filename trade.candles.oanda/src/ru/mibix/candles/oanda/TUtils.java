package ru.mibix.candles.oanda;

import java.util.ArrayList;

/**
 * Utils
 */
public class TUtils
{
    /**
     * Get ID by Symbol name
     *
     * @param symbol - symbol name
     * @return - symbol ID
     */
    public static int getSymbolIdByName(String symbol)
    {
        switch (symbol)
        {
            case "EUR_USD":
            case "EURUSD":
                return 1;
            case "GBP_JPY":
            case "GBPJPY":
                return 2;
        }

        return 0;
    }

    /**
     * Get Symbol name by ID
     *
     * @param symbol - symbol name
     * @return - symbol ID
     */
    public static String getSymbolNameById(int symbol, boolean isOandaType)
    {
        switch (symbol)
        {
            case 1:
                return (isOandaType)?"EUR_USD":"EURUSD";
            case 2:
                return (isOandaType)?"GBP_JPY":"GBPJPY";
        }

        return null;
    }

    /**
     * Get all symbols for load to DB
     *
     * @return - ArrayList symbols
     */
    public static ArrayList<String> getAllSymbols()
    {
        ArrayList<String> symbols = new ArrayList<>();

        symbols.add("EURUSD");
        symbols.add("GBPJPY");

        return symbols;
    }

    /**
     * Get all timeframes
     *
     * @return - ArrayList timeframes
     */
    public static ArrayList<Integer> getAllTimeFrameIDs()
    {
        ArrayList<Integer> tf_ids = new ArrayList<>();

        tf_ids.add(1);      // M1
        tf_ids.add(5);      // M5
        tf_ids.add(15);     // M15
        tf_ids.add(30);     // M30
        tf_ids.add(60);     // H1
        tf_ids.add(240);    // H4
        tf_ids.add(1440);   // D1
        tf_ids.add(10080);  // W
        tf_ids.add(43200);  // MN

        return tf_ids;
    }

    /**
     * Get previous timeframe for current timeframe
     *
     * @param tf - current timeframe
     * @return - previous timeframe
     */
    public static int getPreviousTimeframe(int tf)
    {
        switch (tf)
        {
            case 5: //M1
                return 1;
            case 15: //M15
                return 5;
            case 30: //M30
                return 15;
            case 60: //H1
                return 30;
            case 240: //H4
                return 60;
            case 1440: //D
                return 240;
            case 10080: //W
                return 1440;
            case 43200: //M
                return 1440; // месячный считаем на основании дневного
            default:
                return 0;
        }
    }

    /**
     * Conversion time frame to name DB where is placed
     *
     * @return - time frame name or Db name for specified time frame
     */
    public static String getNameDbByTf(int tf, boolean isDbName)
    {
        switch (tf)
        {
            case 1:
                if (isDbName)
                    return "td_m1";
                else
                    return "M1";
            case 5:
                if (isDbName)
                    return "td_m5";
                else
                    return "M5";
            case 15:
                if (isDbName)
                    return "td_m15";
                else
                    return "M15";
            case 30:
                if (isDbName)
                    return "td_m30";
                else
                    return "M30";
            case 60:
                if (isDbName)
                    return "td_h1";
                else
                    return "H1";
            case 240:
                if (isDbName)
                    return "td_h4";
                else
                    return "H4";
            case 1440:
                if (isDbName)
                    return "td_d1";
                else
                    return "D1";
            case 10080:
                if (isDbName)
                    return "td_w1";
                else
                    return "W";
            case 43200:
                if (isDbName)
                    return "td_mn";
                else
                    return "MN";
        }

        return null;
    }

    /**
     * Conversion time frame to name DB where is placed
     *
     * @return - time frame name or Db name for specified time frame
     */
    public static String getGranularityByTf(int tf)
    {
        switch (tf)
        {
            case 1:
                return "M1";
            case 5:
                return "M5";
            case 15:
                return "M15";
            case 30:
                return "M30";
            case 60:
                return "H1";
            case 240:
                return "H4";
            case 1440:
                return "D";
            case 10080:
                return "W";
            case 43200:
                return "M";
        }

        return null;
    }
}
