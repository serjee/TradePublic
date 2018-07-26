package ru.mibix.candles.oanda;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mibix.candles.oanda.json.Json;
import ru.mibix.candles.oanda.json.JsonArray;
import ru.mibix.candles.oanda.json.JsonValue;
import ru.mibix.candles.oanda.model.CandlesModel;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class REST API V20 for get response from OANDA.COM
 */
public class OandaRestApi
{
    /**
     * Logger
     */
    private static final Logger log = LogManager.getLogger(OandaRestApi.class.getName());

    /**
     * API-key
     */
    private static final String API_KEY = "";

    /**
     * Server
     */
    private static final String SERVER_URL = "https://api-fxpractice.oanda.com/";

    /**
     * Build query (GET)
     *
     * @param endpoint - endpoint
     * @param param - params
     */
    private String buildQuery(String endpoint, String param)
    {
        String query = SERVER_URL + endpoint + "?" + param;
        log.debug("Build query : " + query);

        return query;
    }

    /**
     * Send GET HTTP query
     *
     * @throws Exception
     */
    private StringBuffer sendRequest(String query) throws Exception
    {
        StringBuffer response;

        URL obj = new URL(query);
        HttpURLConnection con = (HttpsURLConnection) obj.openConnection();

        // optional default
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);

        int responseCode = con.getResponseCode();
        log.debug("Sending 'GET' request : " + query);
        log.debug("Response code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();

        //print result
        log.debug("Response on request : " + response.toString());

        return response;
    }

    /**
     * Get candles for symbol
     *
     * @param instrument - Name of the Instrument (http://developer.oanda.com/rest-live-v20/primitives-df/#InstrumentName)
     * @param granularity - The granularity of the candlesticks to fetch (http://developer.oanda.com/rest-live-v20/instrument-df/#CandlestickGranularity)
     * @param count - The number of candlesticks to return in the reponse (maximum=5000)
     */
    public ArrayList<CandlesModel> getInstrumentCandles(String instrument, String granularity, int count)
    {
        CandlesModel cm;
        ArrayList<CandlesModel> acm = new ArrayList<>();

        log.debug("REQUEST USING REST API : Instrument="+instrument+"; Granularity="+granularity+"; Count="+count);

        try
        {
            // Send request and get json response about candles
            StringBuffer jsonResult = sendRequest(buildQuery("v3/instruments/" + instrument + "/candles", "granularity=" + granularity + "&count=" + count)); //URLEncoder.encode(text, "UTF-8")

            // Parse json response and add the one to model
            JsonArray jsonResults = Json.parse(jsonResult.toString()).asObject().get("candles").asArray();
            for (JsonValue candle : jsonResults)
            {
                cm = new CandlesModel();
                cm.setTime(parseRFC3339Date(candle.asObject().getString("time", "")));
                cm.setSymbol(TUtils.getSymbolIdByName(instrument));
                cm.setOpen(Double.parseDouble(candle.asObject().get("mid").asObject().getString("o", "0")));
                cm.setHigh(Double.parseDouble(candle.asObject().get("mid").asObject().getString("h", "0")));
                cm.setLow(Double.parseDouble(candle.asObject().get("mid").asObject().getString("l", "0")));
                cm.setClose(Double.parseDouble(candle.asObject().get("mid").asObject().getString("c", "0")));
                acm.add(cm);
            }
        }
        catch (Exception e)
        {
            log.error("getInstrumentCandles error : " + e.getMessage());
        }

        return acm;
    }

    /**
     * Parse date from RFC3339
     */
    public Date parseRFC3339Date(String datestring) throws java.text.ParseException, IndexOutOfBoundsException
    {
        Date d = new Date();

        // if there is no time zone, we don't need to do any special parsing.
        if (datestring.endsWith("Z"))
        {
            try
            {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // spec for RFC3339
                s.setTimeZone(TimeZone.getTimeZone("GMT"));
                d = s.parse(datestring);
            }
            catch (java.text.ParseException pe) // try again with optional decimals
            {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");// spec for RFC3339 (with fractional seconds)
                s.setTimeZone(TimeZone.getTimeZone("GMT"));
                s.setLenient(true);
                d = s.parse(datestring);
            }

            return d;
        }

        // step one, split off the timezone.
        String firstpart = datestring.substring(0, datestring.lastIndexOf('-'));
        String secondpart = datestring.substring(datestring.lastIndexOf('-'));

        // step two, remove the colon from the timezone offset
        secondpart = secondpart.substring(0, secondpart.indexOf(':')) + secondpart.substring(secondpart.indexOf(':') + 1);
        datestring = firstpart + secondpart;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");// spec for RFC3339
        s.setTimeZone(TimeZone.getTimeZone("GMT"));

        try
        {
            d = s.parse(datestring);
        }
        catch (java.text.ParseException pe) // try again with optional decimals
        {
            s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");// spec for RFC3339 (with fractional seconds)
            s.setTimeZone(TimeZone.getTimeZone("GMT"));
            s.setLenient(true);
            d = s.parse(datestring);
        }

        return d;
    }
}
