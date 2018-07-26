package info.signaltrade.stat.getticks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utils static class
 *
 */
public class Utils
{
    /**
     * Log
     */
    private static final Logger log = LogManager.getLogger(Utils.class.getName());

    /**
     * Loaded properties from config file
     */
    private static PropsModel properties = null;

    /**
     * Buffer size
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Implode files to one for MT4
     *
     * @param month
     * @param year
     * @param currency
     */
    public static void implodeFiles(int month, int year, String currency)
    {
        try
        {
            String dOutput  = getProperties().getDirOutput() + File.separator + currency;
            String dUnZip   = dOutput + getProperties().getDirUnZip();
            String fOut     = getProperties().getDirFilesMT4() + File.separator + currency + ".csv";

            String stDate   = "01."+strMonthDay(month)+"."+year;
            DateFormat df   = new SimpleDateFormat("dd.MM.yyyy");

            Calendar c = Calendar.getInstance();
            c.setTime(df.parse(stDate));

            File f;
            List<String> lines;
            String fName,fPath;
            //boolean flagFirstIteration = true;

            // create new file and write data to it
            FileOutputStream fos = new FileOutputStream(fOut, true);
            //log.debug("out file size:"+fos.getChannel().size());
            //if (fos.getChannel().size()>0)
            //    flagFirstIteration = false;
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8)))
            {
                for (int i=1; i<=c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    fName = year+"-"+strMonthDay(month)+"-"+strMonthDay(i)+"_ecn1_" + currency + ".txt";
                    fPath = dUnZip + File.separator + fName;

                    // read each file and write to out
                    f = new File(fPath);
                    if (f.exists()) {
                        lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
                        for (String x : lines) {
                            if (x.contains("RateDateTime"))  // delete head from two and more *.txt files
                                continue;

                            x = x.replaceAll("-", ".");
                            x = x.replace("\t", ",");

                            out.write(x);
                            out.newLine();
                        }
                        out.flush();
                    }

                    //flagFirstIteration = false;
                }
                out.close();
                fos.close();
            } catch (Exception e) {
                log.error("write out file exception",e);
            }
        } catch (Exception e) {
            log.error("implode file exception",e);
        }
    }

    /**
     * Update file from tick fabric
     *
     * @param link
     * @param year
     * @param month
     * @param currency
     */
    public static void updateTickFile(String link, int year, int month, String currency)
    {
        int fsUrl, fsIO;

        String fName        = String.valueOf(year) + strMonthDay(month) + "_ecn1_" + currency + ".zip";
        String dOutput      = getProperties().getDirOutput() + File.separator + currency;
        String fLocalPath   = dOutput + File.separator + fName;
        String dUnZip       = dOutput + getProperties().getDirUnZip();
        String fUrlPath     = link + String.valueOf(year) + "/" + fName;

        File f = new File(fLocalPath);
        if(f.exists() && !f.isDirectory()) {
            fsUrl = getUrlFileSize(fUrlPath);
            fsIO  = (int) f.length();
            log.debug("file name " + fName + " is exist, url file size=" + fsUrl + "(bytes), local file size=" + fsIO + "(bytes)");
            if (fsUrl != fsIO) {
                if(!f.delete()) {
                    log.warn("error delete file name " + f.getName());
                    return;
                }
            } else {
                log.info("file name " + f.getName() + " had been downloaded already");
                unZip(fLocalPath, dUnZip); // unzip file
                return;
            }
        }

        downloadFile(fUrlPath, dOutput);    // download file
        unZip(fLocalPath, dUnZip);          // unzip file
    }

    /**
     * Get remove URL file size
     *
     * @param fileURL
     * @return
     */
    private static int getUrlFileSize(String fileURL)
    {
        HttpURLConnection conn = null;

        try
        {
            URL url = new URL(fileURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param dOutput path of the directory to save the file
     * @throws IOException
     */
    private static void downloadFile(String fileURL, String dOutput)
    {
        try {

            URL url = new URL(fileURL);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
                }

                log.debug("Content-Type = " + contentType);
                log.debug("Content-Disposition = " + disposition);
                log.debug("Content-Length = " + contentLength);
                log.debug("fileName = " + fileName);

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = dOutput + File.separator + fileName;

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                log.debug("File name " + fileName + " downloaded!");
            } else {
                log.debug("No file to download. Server replied HTTP code: " + responseCode);
            }

            httpConn.disconnect();
        }
        catch (IOException e)
        {
            log.error("download file exception", e);
        }
    }

    /**
     * Unzip it
     * @param zipFile input zip file
     */
    public static void unZip(String zipFile, String outputFolder)
    {
        byte[] buffer = new byte[1024];

        try
        {
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null)
            {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                log.debug("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            log.debug("Done unzip!");

        }
        catch(IOException ex)
        {
            log.error("unzip exception: ",ex);
        }
    }

    /**
     * Get string format for month and day
     * @param month
     * @return
     */
    public static String strMonthDay(int  month)
    {
        if (month<10) {
            return "0" + String.valueOf(month);
        } else {
            return String.valueOf(month);
        }
    }

    /**
     * Check and create output folder for currency
     *
     * @param currency
     */
    public static void checkAndCreateOutputDir(String currency)
    {
        // output file folder for currency
        String outputFolder = getProperties().getDirOutput() + File.separator + currency;

        // check folder and create if need
        try {
            File folder = new File(outputFolder);
            if(!folder.exists()) {
                folder.mkdir();
            }
        } catch(Exception e) {
            log.error("create output dir exception",e);
        }
    }

    /**
     * Check and create or clear unzip dir
     *
     * @param currency
     */
    public static void checkAndCreateOrClearUnZipDir(String currency)
    {
        // output file folder for currency
        String outputFolder = getProperties().getDirOutput() + File.separator + currency + getProperties().getDirUnZip();

        File folder = new File(outputFolder);
        if(!folder.exists()) {
            folder.mkdir();
        } else {
            Arrays.stream(folder.listFiles()).forEach(File::delete);
        }
    }

    /**
     * Check and delete old CSV file
     *
     * @param currency
     */
    public static void checkAndDeleteCSV(String currency)
    {
        // output file folder for currency
        String outputFile = getProperties().getDirOutput() + File.separator + currency + File.separator + currency + ".csv";

        File f = new File(outputFile);
        if(f.exists()) {
            f.delete();
        }
    }

    /**
     * Load properties from config file
     *
     * @return
     */
    public static void loadProperties()
    {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.stat.getticks.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            properties = new PropsModel();
            properties.setCurrencies(prop.getProperty("currencies"));
            properties.setDefaultLastMonth(Integer.parseInt(prop.getProperty("default_last_month")));
            properties.setDirOutput(prop.getProperty("dir_output").replaceAll("\"",""));
            properties.setDirUnZip(prop.getProperty("dir_unzip").replaceAll("\"",""));
            properties.setUrlTicks(prop.getProperty("url_ticks").replaceAll("\"",""));
            properties.setDirFilesMT4(prop.getProperty("dir_files_mt4").replaceAll("\"",""));

            log.debug("properties is loaded!");

        } catch (IOException ex) {
            log.error("load properties exception: ",ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get load config properties
     * @return - properties
     */
    public static PropsModel getProperties() {
        return properties;
    }
}
