package ru.mibix.candles.oanda.model;

import ru.mibix.candles.oanda.model.AbstractModel;

import java.util.Date;

/**
 * Candles model
 */
public class CandlesModel extends AbstractModel
{
    private int timeframe;
    private int symbol;
    private Date time;
    private double open;
    private double high;
    private double low;
    private double close;

    public int getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(int timeframe) {
        this.timeframe = timeframe;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }
}
