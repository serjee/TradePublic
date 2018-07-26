package info.signaltrade.base.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UtilClass
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(UtilClass.class.getName());

    /**
     * Get TfId by code
     *
     * @param tf - string timeframe
     * @return - converted to int timeframe
     */
    public static int getTfByCode(String tf)
    {
        if (tf.equals("M1")) {
            return 1;
        } else if (tf.equals("M5")) {
            return 5;
        } else if (tf.equals("M15")) {
            return 15;
        } else if (tf.equals("M30")) {
            return 30;
        } else if (tf.equals("H1")) {
            return 60;
        } else if (tf.equals("H4")) {
            return 240;
        } else if (tf.equals("D1")) {
            return 1440;
        } else if (tf.equals("W")) {
            return 10080;
        } else if (tf.equals("MN")) {
            return 43200;
        }

        return 0;
    }

    /**
     * Check string for number format
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * Convert symbol id from MT4 to Binary.com format
     *
     * @param symbol - symbol
     * @return - symbol with prefix "frx"
     */
    public static String getBinarySymbolByID(String symbol)
    {
        return "frx" + symbol;
    }

    /**
     * Calculate duration by timeframe of MT4
     *
     * @param tf - timeframe of MT4
     * @param exp - expiration candles
     *
     * @return - duration
     */
    public static int getDurationByTf(int tf, int exp)
    {
        switch (tf)
        {
            case 1:
            case 5:
            case 15:
                return tf*exp;
            case 30:
            case 60:
            case 240:
                return (tf/60)*exp;
            case 1440:
                return (tf/1440)*exp;
        }

        return 0;
    }

    /**
     * Calculate duration unit for binary.com by timeframe of MT4
     *
     * @param tf - timeframe of MT4
     *
     * @return - duration for binary
     */
    public static String getDurationTypeByTf(int tf)
    {
        switch (tf)
        {
            case 1:
            case 5:
            case 15:
                return "m";
            case 30:
            case 60:
            case 240:
                return "h";
            case 1440:
                return "d";
        }

        return null;
    }

    /**
     * Calculate expiration unix-time using start time and duration
     *
     * @param startTime - start time (unix)
     * @param duration - duration
     * @param durationUnit - duration unit
     *
     * @return - expiration time (unix)
     */
    public static long getExpirationTime(long startTime, int duration, String durationUnit)
    {
        return getExpirationTime(startTime, duration, durationUnit, 10); // offset is 10 sec.
    }

    /**
     * Calculate expiration unix-time using start time and duration
     *
     * @param startTime - start time (unix)
     * @param duration - duration
     * @param durationUnit - duration unit
     * @param offset - offset of duration
     *
     * @return - expiration time (unix)
     */
    public static long getExpirationTime(long startTime, int duration, String durationUnit, int offset)
    {
        int secIncrease = 0;

        if (durationUnit.equals("m")) {
            secIncrease = 60;
        } else if (durationUnit.equals("h")) {
            secIncrease = 60 * 60;
        } else if (durationUnit.equals("d")) {
            secIncrease = 24 * 60 * 60;
        }

        return startTime + (duration * secIncrease) + offset;
    }

    /**
     * Get new random value (int)
     *
     * @return - random value
     */
    public static int getRandomInt()
    {
        return ThreadLocalRandom.current().nextInt(1000000000, 2147483647);
    }

    /**
     * Get datetime of start day
     *
     * @param date - date for calculation
     *
     * @return - date of start day
     */
    public static Date getStartOfDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Get datetime of end day
     *
     * @param date - date for calculation
     *
     * @return - date of end day
     */
    public static Date getEndOfDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * Delete files
     *
     * @param path - dir path
     * @param listFiles - list name of files for delete
     * @param extension - extension of files for delete
     */
    public static void deleteFiles(String path, List<String> listFiles, String extension)
    {
        for (String file : listFiles) {
            File f = new File(path + "/" + file + "." + extension);
            if (!f.isDirectory() && f.exists())
                f.delete();
        }
    }

    /**
     * Convert string to md5
     *
     * @param message - message for convert
     *
     * @return - md5 hash
     */
    public static String md5(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            //converting byte array to HexadecimalString
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            log.error("UnsupportedEncodingException",ex);
        } catch (NoSuchAlgorithmException ex) {
            log.error("NoSuchAlgorithmException",ex);
        }

        return digest;
    }
}
