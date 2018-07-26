package ru.mibix.bar.forming;

import java.util.ArrayList;

/**
 * Utils
 */
public class TUtils
{
    /**
     * Get symbol ID by name
     *
     * @param symbol - symbol name
     * @return - symbol ID
     */
    public static int getSymbolIdByName(String symbol)
    {
        switch (symbol)
        {
            case "EURUSD":
                return 1;
            case "GBPJPY":
                return 2;
//            case "GBPUSD":
//                return 3;
//            case "USDCHF":
//                return 4;
        }

        return 0;
    }

    /**
     * Get symbol name by ID
     *
     * @param symbol - symbol ID
     * @return - symbol name
     */
    public static String getSymbolNameById(int symbol)
    {
        switch (symbol)
        {
            case 1:
                return "EURUSD";
            case 2:
                return "GBPJPY";
//            case 3:
//                return "GBPUSD";
//            case 4:
//                return "USDCHF";
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
        //symbols.add("GBPUSD");
        //symbols.add("USDCHF");

        return symbols;
    }

    /**
     * Get all time frames
     *
     * @return - ArrayList time frames (mt4)
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
}
