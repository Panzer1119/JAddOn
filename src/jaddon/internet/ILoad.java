/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.internet;

import jaddon.jlog.JLogger;
import jaddon.math.JData;
import jaddon.statistics.JStatistic;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ProgressMonitorInputStream;

/**
 *
 * @author Paul
 */
public class ILoad {
    
    
    /**
     * Downloads a file from a website and saves it in a specific place
     * @param urltd Url to download
     * @param saveAs File to save
     * @param loadbar Boolean if a loadbar should be shown
     * @param logger JLogger to log data
     * @param tries Integer how often it should be tried to download a file
     * @return File where it was saved
     */
    public static File downloadAsFile(String urltd, File saveAs, boolean loadbar, JLogger logger, int tries) {
        int tryy = 1;
        String msg = "Trying to download file from \"" + urltd + "\" as \"" + saveAs.getAbsolutePath() + "\" Try: " + tryy;
        if(logger != null) {
            logger.log(msg);
        } else {
            System.out.println(msg);
        }
        while(downloadAsFile(urltd, saveAs, loadbar, logger) == null) {
            tryy++;
            if(tryy >= tries) {
                msg = "The downloaded failed also after " + tries + " tries, this exceed the maximum tries number and null will be returned";
                if(logger != null) {
                    logger.log(msg);
                } else {
                    System.out.println(msg);
                }
                return null;
            }
            msg = "Try " + (tryy - 1) + " failed, next try number: " + tryy;
            if(logger != null) {
                logger.log(msg);
            } else {
                System.out.println(msg);
            }
        }
        if(saveAs.exists()) {
            msg = "After " + tryy + " tries, the file was successfully downloaded";
            if(logger != null) {
                logger.log(msg);
            } else {
                System.out.println(msg);
            }
            return saveAs;
        } else {
            msg = "The downloaded failed also after " + tries + " tries, this exceed the maximum tries number and null will be returned";
            if(logger != null) {
                logger.log(msg);
            } else {
                System.out.println(msg);
            }
            return null;
        }
    }
    
    /**
     * Downloads a file from a website and saves it in a specific place
     * @param urltd Url to download
     * @param saveAs File to save
     * @param loadbar Boolean if a loadbar should be shown
     * @param logger JLogger to log data
     * @return File where it was saved
     */
    public static File downloadAsFile(String urltd, File saveAs, boolean loadbar, JLogger logger) {
        saveAs = new File(saveAs.getAbsolutePath());
        if(!saveAs.getParentFile().exists()) {
            saveAs.getParentFile().mkdirs();
        }
        try {
            boolean urlDa = true;
            String msg = "Trying to download file from \"" + urltd + "\" as \"" + saveAs.getAbsolutePath() + "\"";
            if(logger != null) {
                logger.log(msg);
            } else {
                System.out.println(msg);
            }
            if(urlDa) {
                if(loadbar) {
                    try {
                        URL url = new URL(urltd);
                        URLConnection uc = url.openConnection();
                        InputStream is = (InputStream) uc.getInputStream();
                        ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", is);
                        pmis.getProgressMonitor().setMaximum(uc.getContentLength());
                        FileOutputStream out = new FileOutputStream(saveAs);
                        byte[] buffer = new byte[1024];
                        for(int n; (n = pmis.read(buffer)) != -1; out.write(buffer, 0, n));
                        
                        pmis.close();
                        out.close();
                    } catch (java.io.FileNotFoundException ex) {
                        msg = "Error while downloading file: " + ex;
                        if(logger != null) {
                            logger.logErr(msg);
                        } else {
                            System.err.println(msg);
                        }
                    }
                } else {
                    try {
                        URL website = new URL(urltd);
                        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                        FileOutputStream fos = new FileOutputStream(saveAs);
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                        fos.close();
                        rbc.close();
                    } catch (java.io.FileNotFoundException ex) {
                        msg = "Error while downloading file: " + ex;
                        if(logger != null) {
                            logger.logErr(msg);
                        } else {
                            System.err.println(msg);
                        }
                    }
                }
            }
            String msg2 = ((saveAs.exists()) ? "Successfully downloaded" : "Not successfully downloaded");
            if(logger != null) {
                logger.log(msg2);
            } else {
                System.out.println(msg2);
            }
            if(saveAs.exists()) {
                try {
                    JStatistic.addData(saveAs.length(), JData.B);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
                return saveAs;
            } else {
                return null;
            }
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }
    
    /**
     * Downloads a file from a website and saves it in an arraylist
     * @param urltd Url to download
     * @param loadbar Boolean if a loadbar should be shown
     * @param logger JLogger to log data
     * @param tries Integer how often it should be tried to download a file
     * @return ArrayList String ArrayList with the file
     */
    public static ArrayList<String> downloadAsArrayList(String urltd, boolean loadbar, JLogger logger, int tries) {
        ArrayList<String> output = new ArrayList<>();
        File saveAsTemp = null;
        try {
            saveAsTemp = File.createTempFile(urltd.replaceAll(":", ".."), ".download");
            File saveAs = downloadAsFile(urltd, saveAsTemp, loadbar, logger, tries);
            if(saveAs != null && saveAsTemp.exists()) {
                try {
                    JStatistic.addData(saveAsTemp.length(), JData.B);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
                Scanner sc = new Scanner(saveAsTemp);
                while(sc.hasNextLine()) {
                    output.add(sc.nextLine());
                }
                sc.close();
                saveAsTemp.delete();
                return output;
            } else {
                if(saveAsTemp != null) {
                    saveAsTemp.delete();
                }
                return null;
            }
        } catch (Exception ex) {
            System.err.println(ex);
            if(saveAsTemp != null) {
                saveAsTemp.delete();
            }
            return null;
        }
    }
    
    /**
     * Downloads a file from a website and returns it in an arraylist
     * @param urltd Url to download
     * @param loadbar Boolean if a loadbar should be shown
     * @param logger JLogger to log data
     * @return ArrayList String ArrayList with the file
     */
    public static ArrayList<String> downloadAsArrayList(String urltd, boolean loadbar, JLogger logger) {
        ArrayList<String> output = new ArrayList<>();
        File saveAsTemp = null;
        try {
            saveAsTemp = File.createTempFile(urltd.replaceAll(":", ".."), ".download");
            File saveAs = downloadAsFile(urltd, saveAsTemp, loadbar, logger);
            if(saveAs != null && saveAsTemp.exists()) {
                try {
                    JStatistic.addData(saveAsTemp.length(), JData.B);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
                Scanner sc = new Scanner(saveAsTemp);
                while(sc.hasNextLine()) {
                    output.add(sc.nextLine());
                }
                sc.close();
                saveAsTemp.delete();
                return output;
            } else {
                if(saveAsTemp != null) {
                    saveAsTemp.delete();
                }
                return null;
            }
        } catch (Exception ex) {
            System.err.println(ex);
            if(saveAsTemp != null) {
                saveAsTemp.delete();
            }
            return null;
        }
    }
    
}
