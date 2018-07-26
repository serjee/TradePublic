package info.signaltrade.stat.getticks;

import java.util.ArrayList;

/**
 * Properties class model
 */
public class PropsModel
{
    private ArrayList<String> currencies = new ArrayList<>();
    private int defaultLastMonth;
    private String dirOutput;
    private String dirUnZip;
    private String urlTicks;
    private String dirFilesMT4;

    public ArrayList<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        for (String currency : currencies.split(",")) {
            this.currencies.add(currency);
        }
    }

    public int getDefaultLastMonth() {
        return defaultLastMonth;
    }

    public void setDefaultLastMonth(int defaultLastMonth) {
        this.defaultLastMonth = defaultLastMonth;
    }

    public String getDirOutput() {
        return dirOutput;
    }

    public void setDirOutput(String dirOutput) {
        this.dirOutput = dirOutput;
    }

    public String getDirUnZip() {
        return dirUnZip;
    }

    public void setDirUnZip(String dirUnZip) {
        this.dirUnZip = dirUnZip;
    }

    public String getUrlTicks() {
        return urlTicks;
    }

    public void setUrlTicks(String urlTicks) {
        this.urlTicks = urlTicks;
    }

    public String getDirFilesMT4() {
        return dirFilesMT4;
    }

    public void setDirFilesMT4(String dirFilesMT4) {
        this.dirFilesMT4 = dirFilesMT4;
    }
}
