/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jupdate;

import jaddon.config.JConfig;
import jaddon.controller.StaticStandard;
import static jaddon.jlang.JLang.getLangProp;
import jaddon.jlog.JLogger;
import static jaddon.utils.JUtils.unZip;
import static jaddon.utils.JVersion.isSecondVersionNewer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.JOptionPane;
import static jaddon.internet.ILoad.downloadAsFile;
import jaddon.utils.DataUtils;
import jaddon.utils.JUtils;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

/**
 * This is an object to update your programm with an own selected server
 * @author Paul Hagedorn
 */
public class JUpdater {
    
    public static final String FOLDERDATA = "data";
    public static final String FILEAPPLICATIONSUPDATING = "update.auto";
    public static final String FILEPATHS = "paths.txt";
    public static final String SPLITTER_EXTRA_URLS = "#";
    public static final String URLTEST = "test";
    public static final String URLUPDATER = "updater";
    public static final String URLFILES = "files";
    
    private String name = "";
    private String version = "";
    private String version_update = "";
    private String extension = ".jar";
    private Properties lang = new Properties();
    //private String url_test = "http://www.panzercraft.de/programmieren/test.txt";
    //private String url_files = "http://www.panzercraft.de/programmieren/Files";
    //private String url_updater = "http://www.panzercraft.de/programmieren/updater";
    private String url_test = "";
    private String url_files = "";
    private String url_updater = "";
    private boolean enabled = true;
    private boolean isDoneByIt = false;
    private boolean askForUpdate = true;
    private boolean update_available = false;
    private boolean internet_connection = false;
    private boolean download_extra_files = true;
    private boolean internet_connection_tested = false;
    private File file_for_temp = new File(System.getProperty("user.dir"));
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<Boolean> deZip = new ArrayList<>();
    private ArrayList<Boolean> delZip = new ArrayList<>();
    private ArrayList<File> files_downloaded = new ArrayList<>();
    private JLogger logger = new JLogger(StaticStandard.isIsIDE());
    private File update_file = null;
    private int download_tries = 5;
    private File folder_of_jar = null;
    
    static {
        JUpdater.isIDE();
        try {
            JUpdater.getFilePaths().createNewFile();
            JUpdater.getFilePrograms().createNewFile();
        } catch (Exception ex) {
        }
    }
    
    /**
     * This constructs the object
     * @param name String Name from your project
     * @param version String Current version from your project, dont have to be the newest!
     */
    public JUpdater(String name, String version) {
        this(name, version, false, new File(System.getProperty("user.dir")));
    }
    
    /**
     * This constructs the object
     * @param name String Name from your project
     * @param version String Current version from your project, dont have to be the newest!
     * @param start Boolean If the program should start instant, you need this to be false, if you want to set custom URLs
     * @param urls String Custom URLs (1. Test File, 2. Files Folder, 3. Updater Folder)
     */
    public JUpdater(String name, String version, boolean start, String... urls) {
        this(name, version, start, new File(System.getProperty("user.dir")), urls);
    }
    
    /**
     * This constructs the object
     * @param name String Name from your project
     * @param version String Current version from your project, dont have to be the newest!
     * @param start Boolean If the program should start instant, you need this to be false, if you want to set custom URLs
     * @param tempFolder File Folder for the temp files
     * @param urls String Custom URLs (1. Test File, 2. Files Folder, 3. Updater Folder)
     */
    public JUpdater(String name, String version, boolean start, File tempFolder, String... urls) {
        logger.setIsIDE(StaticStandard.isIsIDE());
        StaticStandard.setUpdater(this);
        this.name = name;
        this.version = version;
        this.file_for_temp = tempFolder;
        lang = getLangProp();
        if(urls.length > 0) {
            for(int i = 0; i < urls.length; i++) {
                switch (i) {
                    case 0:
                        this.url_test = urls[0];
                        break;
                    case 1:
                        this.url_files = urls[1];
                        break;
                    case 2:
                        this.url_updater = urls[2];
                        break;
                    default:
                        break;
                }
            }
        }
        if(start) {
            start();
        } else {
            deleteDeleteableFiles();
        }
    }
    
    /**
     * Starts the JUpdater
     */
    public void start() {
        Runnable run = new Runnable() {
            
            @Override
            public void run() {
                deleteDeleteableFiles();
                if(url_test.isEmpty() || url_files.isEmpty() || url_updater.isEmpty()) {
                    logErr(lang.getProperty("no_urls_1", "You have some URLs not set!"), true);
                    return;
                }
                if(!enabled) {
                    return;
                }
                testInternetConncetion();
                checkForUpdate();
            }
                
        };
        StaticStandard.execute(run);
    }
    
    /**
     * Reloads the language
     */
    public void reloadLang() {
        lang = getLangProp();
    }
    
    /**
     * Adds a file to download
     * @param url String URL for the file to download
     * @param save File Folder to save the file
     * @param unzip First Boolean Sets if the downloaded file gets unzipped, Second Boolean Sets if the zip gets deleted after the unzip 
     */
    public void addFile(String url, File save, boolean... unzip) {
        urls.add(url);
        files.add(save);
        if(unzip.length > 0) {
            deZip.add(unzip[0]);
        } else {
            deZip.add(false);
        }
        if(unzip.length > 1) {
            delZip.add(unzip[1]);
        } else {
            delZip.add(false);
        }
    }
    
    /**
     * Deletes all downloaded extra files
     */
    public void deleteDownloadedExtraFiles() {
        ArrayList<File> deleted = new ArrayList<>();
        for(File f : files_downloaded) {
            if(f == null || deleted.contains(f)) {
                continue;
            }
            try {
                f.delete();
                deleted.add(f);
            } catch (Exception ex) {
                logErr("Error while deleting file \"" + f.getAbsolutePath() + "\": " + ex, false);
            }
        }
    }
    
    /**
     * Loads extra files that should be downloaded from a file
     * @param filetoload File where the URLs are
     * @return Boolean True if it worked, False if not
     */
    public boolean loadExtraFilesFromFile(File filetoload) {
        if(filetoload == null || !filetoload.exists() || !filetoload.isFile()) {
            if(logger != null) {
                logger.logErr("File not found while loading extra files that should be downloaded from it");
            } else {
                System.err.println("File not found while loading extra files that should be downloaded from it");
            }
            return false;
        }
        try {
            return loadExtraFilesFromInputStream(new BufferedInputStream(new FileInputStream(filetoload)));
        } catch (FileNotFoundException ex) {
            if(logger != null) {
                logger.logErr("File not found while loading extra files that should be downloaded from it: " + ex);
            } else {
                System.err.println("File not found while loading extra files that should be downloaded from it: " + ex);
            }
            return false;
        }
    }
    
    /**
     * Loads extra files that should be downloaded from a resourcestream
     * @param name Name of the intern file
     * @return Boolean True if it worked, False if not
     */
    public boolean loadExtraFilesFromInternResource(String name) {
        if(!name.startsWith("/")) {
            name = "/" + name;
        }
        try {
            InputStream is = this.getClass().getResourceAsStream(name);
            return loadExtraFilesFromInputStream(is);
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while loading extra files that should be downloaded from resource stream: " + ex);
            } else {
                System.err.println("Error while loading extra files that should be downloaded from resource stream: " + ex);
            }
            return false;
        }
    }
   
    /**
     * Loads extra files that should be downloaded from an inputstream
     * @param is InputStream information
     * @return Boolean True if it worked, False if not
     */
    public boolean loadExtraFilesFromInputStream(InputStream is) {
        if(is == null) {
            return false;
        }
        clearFiles();
        //Example:
        //test.txt#www.test.txt#true#true
        try {
            Scanner sc = new Scanner(is);
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] split = line.split(SPLITTER_EXTRA_URLS);
                try {
                    switch(split.length) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            addFile(split[1], new File(file_for_temp.getAbsolutePath() + ((split[0].startsWith(File.separator)) ? "" : File.separator) + split[0]), false, false);
                            break;
                        case 3:
                            addFile(split[1], new File(file_for_temp.getAbsolutePath() + ((split[0].startsWith(File.separator)) ? "" : File.separator) + split[0]), Boolean.parseBoolean(split[2]), false);
                            break;
                        case 4:
                            addFile(split[1], new File(file_for_temp.getAbsolutePath() + ((split[0].startsWith(File.separator)) ? "" : File.separator) + split[0]), Boolean.parseBoolean(split[2]), Boolean.parseBoolean(split[3]));
                            break;
                        default:
                            break;
                    }
                } catch (Exception ex) {
                    if(logger != null) {
                        logger.logErr("Error while processing something: " + ex);
                    } else {
                        System.err.println("Error while processing something: " + ex);
                    }
                }
            }
            sc.close();
            sc.reset();
            sc = null;
            log("Loaded " + files.size() + " extra file URL" + ((files.size() == 1) ? "" : "s") + " from file/inputstream", false);
            return true;
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while loading extra files from inputstream: " + ex);
            } else {
                System.err.println("Error while loading extra files from inputstream: " + ex);
            }
            return false;
        }
    }
    
    /**
     * Clears the extra files list
     */
    public void clearFiles() {
        urls.clear();
        files.clear();
        deZip.clear();
        delZip.clear();
    }

    /**
     * Tests the internet connection trough downloading a test file and tests if it is existing
     * @return True or False
     */
    public boolean testInternetConncetion() {
        if(!enabled) {
            return false;
        }
        File testFile = new File(file_for_temp.getAbsolutePath() + File.separator + "test.txt");
        testFile.delete();
        try {
            downloadAsFile(url_test, testFile, false, logger, download_tries);
            if(testFile.exists()) {
                internet_connection = true;
            } else {
                internet_connection = false;
            }
        } catch (Exception ex) {
            logErr("Error while testing internet connection: " + ex, false);
            internet_connection = false;
        }
        testFile.delete();
        internet_connection_tested = true;
        return internet_connection;
    }
    
    /**
     * Loads URLs from a file
     * @param filetoload File where the URLs are
     * @return Boolean True if it worked, False if not
     */
    public boolean loadURLsFromFile(File filetoload) {
        if(filetoload == null || !filetoload.exists() || !filetoload.isFile()) {
            if(logger != null) {
                logger.logErr("File not found while loading URLs from it");
            } else {
                System.err.println("File not found while loading URLs from it");
            }
            return false;
        }
        try {
            return loadURLsFromInputStream(new BufferedInputStream(new FileInputStream(filetoload)));
        } catch (FileNotFoundException ex) {
            if(logger != null) {
                logger.logErr("File not found while loading URLs from it: " + ex);
            } else {
                System.err.println("File not found while loading URLs from it: " + ex);
            }
            return false;
        }
    }
    
    /**
     * Loads URLs from a resourcestream
     * @param name Name of the intern file
     * @return Boolean True if it worked, False if not
     */
    public boolean loadURLsFromInternResource(String name) {
        if(!name.startsWith("/")) {
            name = "/" + name;
        }
        try {
            InputStream is = this.getClass().getResourceAsStream(name);
            return loadURLsFromInputStream(is);
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while loading URLs from resource stream: " + ex);
            } else {
                System.err.println("Error while loading URLs from resource stream: " + ex);
            }
            return false;
        }
    }
    
    /**
     * Loads URLs from an InputStream
     * @param is InputStream with the URLs
     * @return Boolean True if it worked, False if not
     */
    public boolean loadURLsFromInputStream(InputStream is) {
        if(is == null) {
            return false;
        }
        try {
            int i = 0;
            Properties urls_test = new Properties();
            urls_test.load(is);
            for(String g : urls_test.stringPropertyNames()) {
                String value = urls_test.getProperty(g, null);
                switch(g) {
                    case URLTEST:
                        url_test = value;
                        break;
                    case URLUPDATER:
                        url_updater = value;
                        break;
                    case URLFILES:
                        url_files = value;
                        break;
                    default:
                        i--;
                        break;
                }
                i++;
            }
            urls_test.clear();
            urls_test = null;
            log("Loaded " + i + " URL" + ((i == 1) ? "" : "s") + " from file/inputstream", false);
            return true;
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while loading URLs from a file/inputstream: " + ex);
            } else {
                System.err.println("Error while loading URLs from a file/inputstream: " + ex);
            }
            return false;
        }
    }
    
    /**
     * Loads URLs from a HashMap
     * @param urls_map HashMap URLs
     * @return Boolean True if it worked, False if not
     */
    public boolean loadURLsFromHashMap(HashMap<String, String> urls_map) {
        if(urls_map == null) {
            return false;
        }
        try {
            int i = 0;
            for(String g : urls_map.keySet()) {
                String value = urls_map.get(g);
                switch(g) {
                    case URLTEST:
                        url_test = value;
                        break;
                    case URLUPDATER:
                        url_updater = value;
                        break;
                    case URLFILES:
                        url_files = value;
                        break;
                    default:
                        i--;
                        break;
                }
                i++;
            }
            log("Loaded " + i + " URL" + ((i == 1) ? "" : "s") + " from hashmap", false);
            return true;
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while loading URLs from a hashmap: " + ex);
            } else {
                System.err.println("Error while loading URLs from a hashmap: " + ex);
            }
            return false;
        }
    }
    
    /**
     * Marks this program to be deleted on the next start
     */
    public void markToDelete(File file) {
        if(!StaticStandard.isIsIDE() && enabled) {
            try {
                File file_todel = new File(file_for_temp.getAbsolutePath() + File.separator + "todel_" + name + ".txt");
                file_todel.createNewFile();
                DataUtils.add(file_todel, file);
                StaticStandard.log("Marked \"" + file + "\" to get deleted (" + file_todel + ")");
            } catch (Exception ex) {
                logErr("Error while marking this to be deleted: " + ex, false);
            }
        }
    }
    
    public void deleteUpdateFile() {
        if(update_file != null) {
            try {
                update_file.delete();
            } catch (Exception ex) {
                logErr("Error while deleting the update file: " + ex, false);
            }
        }
    }
    
    /**
     * Checks for a file and when it is there if there is a name in it it will be deleted
     */
    public void deleteDeleteableFiles() {
        File file_todel = new File(file_for_temp.getAbsolutePath() + File.separator + "todel_" + name + ".txt");
        if(!StaticStandard.isIsIDE() && file_todel.exists() && enabled) {
            try {
                ArrayList<Object> files_todel = DataUtils.loadArrayListFromFile(file_todel);
                for(Object o : files_todel) {
                    try {
                        File file = new File(o.toString());
                        if(!(file.equals(JUtils.getJARLocation()))) {
                            file.delete();
                        }
                    } catch (Exception ex) {
                    }
                }
                file_todel.delete();
                log("Deleteable files were deleted", false);
            } catch (Exception ex) {
                logErr("Error while deleting files to be deleted: " + ex, false);
            }
        } else {
            log("Deleteable files could not be deleted", false);
        }
    }
    
    /**
     * Reinstalls the program
     */
    public void reinstall() {
        if(!enabled) {
            return;
        }
        String msg = "";
        if(update_available) {
            msg = String.format(lang.getProperty("update_available_install", "An update from current version \"%s\" to version \"%s\" is available, do you want to install it?"), version, version_update);
        } else {
            msg = String.format(lang.getProperty("update_reinstall", "Do you want to reinstall the version \"%s\"?"), version);
        }
        int auswahl = JOptionPane.showConfirmDialog(null, msg, lang.getProperty("update_installer", "Update installer"), JOptionPane.YES_NO_CANCEL_OPTION);
        if(auswahl == 0) {
            downloadUpdate(false);
        }
    }
    
    /**
     * Updates the program
     */
    public File doUpdate(boolean ask) {
        if(!enabled) {
            return null;
        }
        if(!internet_connection_tested) {
            testInternetConncetion();
        }
        boolean temp_afu = askForUpdate;
        if(!isDoneByIt) {
            askForUpdate = false;
            checkForUpdate();
        }
        askForUpdate = temp_afu;
        if(update_available) {
            if(!ask) {
                return downloadUpdate(true);
            } else {
                int auswahl = JOptionPane.showConfirmDialog(null, String.format(getDoUpdateQuestion(), version, version_update), lang.getProperty("update_installer", "Update installer"), JOptionPane.YES_NO_CANCEL_OPTION);
                if(auswahl == 0) {
                    return downloadUpdate(true);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }
    
    /**
     * Returns the question which gets asked when an update was found
     * @return String Question
     */
    public String getDoUpdateQuestion() {
        return lang.getProperty("update_available_install", "An update from current version \"%s\" to the newer version \"%s\" is available, do you want to install it?");
    }
    
    /**
     * Returns the question which gets asked when an update was found with the name of the application
     * @return String Question
     */
    public String getDoUpdateQuestionWithName() {
        return lang.getProperty("update_available_install_with_name", "An update for \"%s\" from current version \"%s\" to the newer version \"%s\" is available, do you want to install it?");
    }
    
    /**
     * This is the function which makes the update
     * @param First: Boolean True if the program gets reinstalled, False if not; Second: Boolean True if dialogs should be shown, False if not; Third: File already downloaded; Fourth: File old file
     */
    public File downloadUpdate(Object... extras) {
        if(!enabled) {
            return null;
        }
        boolean isRe = false;
        boolean show = true;
        File file_alt = null;
        File file_neu = null;
        try {
            if(extras.length > 0) {
                isRe = (Boolean) extras[0];
            }
            if(extras.length > 1) {
                show = (Boolean) extras[1];
            }
            if(extras.length > 2) {
                file_alt = (File) extras[2];
            }
            if(extras.length > 3) {
                file_neu = (File) extras[3];
            }
        } catch (Exception ex) {
        }
        File neue_v = new File(((folder_of_jar != null) ? folder_of_jar.getAbsolutePath() + File.separator : "") + (name + "_v" + version_update + extension));
        if(neue_v.exists() && isRe) {
            neue_v = new File(((folder_of_jar != null) ? folder_of_jar.getAbsolutePath() + File.separator : "") + (name + "_v" + version_update + "_reinstall" + extension));
        }
        int i = 2;
        while(neue_v.exists() && isRe) {
            neue_v = new File(((folder_of_jar != null) ? folder_of_jar.getAbsolutePath() + File.separator : "") + (name + "_v" + version_update + "_reinstall_" + i + extension));
            i++;
        }
        String url_td = url_files + ((url_updater.endsWith("/") ? "" : "/")) + name + "_v" + version_update + extension;
        neue_v.delete();
        try {
            if(file_neu != null) {
                FileUtils.copyFile(file_neu, neue_v);
            } else {
                downloadAsFile(url_td, neue_v, true, logger, download_tries);
            }
        } catch (Exception ex) {
        }
        downloadExtraFiles();
        if(show) {
            JOptionPane.showMessageDialog(null, ((neue_v.exists()) ? lang.getProperty("updated_successfully", "Updated successfully") : lang.getProperty("update_failed", "Update failed")), lang.getProperty("update", "Update"), ((neue_v.exists()) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
        }
        if(neue_v.exists()) {
            if(show) {
                int auswahl2 = JOptionPane.showConfirmDialog(null, lang.getProperty("deinstall_this", "Do you want to deinstall this version?"), lang.getProperty("update_installer", "Update installer"), JOptionPane.YES_NO_CANCEL_OPTION);
                if(auswahl2 == 0) {
                    markToDelete((file_alt != null) ? file_alt : JUtils.getJARLocation());
                }
            } else {
                markToDelete((file_alt != null) ? file_alt : JUtils.getJARLocation());
            }
            if(show) {
                int auswahl3 = JOptionPane.showConfirmDialog(null, lang.getProperty("start_new", "Do you want to start the new version?"), lang.getProperty("update_installer", "Update installer"), JOptionPane.YES_NO_CANCEL_OPTION);
                if(auswahl3 == 0) {
                    try {
                        if(!isIDE()) {
                            Runtime.getRuntime().exec("java -jar " + neue_v.getAbsolutePath());
                        }
                    } catch (Exception ex) {
                        logErr(String.format(lang.getProperty("error_while_restarting", "Error while restarting: %s"), ex.getMessage()), true);
                    }
                    System.exit(0);
                }
            }
            return neue_v;
        } else {
            return null;
        }
    }
    
    /**
     * Downloads all extra files
     */
    public void downloadExtraFiles() {
        if(!enabled) {
            logErr("JUpdater is not enabled", false);
            return;
        }
        if(download_extra_files) {
            if(urls.size() == files.size()) {
                for(int i = 0; i < urls.size(); i++) {
                    boolean unzip = deZip.get(i);
                    boolean del = delZip.get(i);
                    String url = urls.get(i);
                    File filetd = files.get(i);
                    try {
                        log("Starting downloading extra file from \"" + url + "\"", false);
                        downloadAsFile(url, filetd, false, logger, download_tries);
                        if(filetd.exists()) {
                            files_downloaded.add(filetd);
                            log("Extra file \"" + filetd.getAbsolutePath() + "\" was successfully downloaded", false);
                        }
                        if(unzip) {
                            unZip(filetd, filetd.getParentFile());
                        }
                        if(del) {
                            //while(filetd.exists()) {
                                filetd.delete();
                            //}
                        }
                    } catch (Exception ex) {
                        System.out.println("Error while downloading the file" + ex);
                    }
                }
            } else {
                System.err.println("YOU HAVE NOT EXACT SAME URLS AS FILES!");
            }
        }
    }
    
    /**
     * Tests the program if it is running via an IDE
     * @return Boolean If it is in an IDE
     */
    public static boolean isIDE() {
        String classpath = System.getProperty("java.class.path");
        StaticStandard.setIsIDE((!classpath.endsWith(".jar") && !classpath.endsWith(".exe")));
        return StaticStandard.isIsIDE();
    }
    
    /**
     * Checks for an update trough downloading the update file from the server
     * @return Boolean If update is available
     */
    public boolean checkForUpdate() {
        if(!enabled) {
            return false;
        }
        if(!internet_connection_tested) {
            testInternetConncetion();
        }
        isDoneByIt = true;
        update_file = new File(file_for_temp.getAbsolutePath() + File.separator + "update_" + name + ".txt");
        update_file.delete();
        try {
            downloadAsFile(url_updater + ((url_updater.endsWith("/") ? "" : "/")) + name + ".txt", update_file, false, logger, download_tries);
            if(update_file.exists()) {
                Properties temp_prop = new Properties();
                temp_prop.clear();
                temp_prop.load(new BufferedInputStream(new FileInputStream(update_file)));
                version_update = temp_prop.getProperty("version", "error");
                temp_prop.clear();
                update_available = isSecondVersionNewer(version, version_update);
                /*
                if(version.equals(version_update) || version_update.equals("error")) {
                    update_available = false;
                } else {
                    update_available = true;
                }
                */
            } else {
                update_available = false;
                logErr("Error while downloading update test file", false);
            }
        } catch (Exception ex) {
            update_available = false;
            logErr("Error while checking for update: " + ex, false);
        }
        update_file.delete();
        if(update_available && askForUpdate) {
            doUpdate(true);
        }
        isDoneByIt = false;
        return update_available;
    }
    
    /**
     * Logs an error
     * @param msg String Error to log
     * @param show Boolean If a JOptionPane should be shown
     */
    public void logErr(Object msg, boolean show) {
        if(logger != null) {
            logger.logErr(msg, show);
        } else {
        System.err.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Logs a message
     * @param msg String Message to log
     * @param show Boolean If a JOptionPane should be shown
     */
    public void log(Object msg, boolean show) {
        if(logger != null) {
            logger.log(msg, show);
        } else {
        System.out.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Sets the logger
     * @param logger JLogger logger to log data
     */
    public void setLogger(JLogger logger) {
        this.logger = logger;
    }
    
    /**
     * Gets the actual logger
     * @return JLogger logger
     */
    public JLogger getLogger() {
        return logger;
    }
    
    /**
     * Sets the URL to the test file, which should be downloaded
     * @param url_test String ULR to the test file
     */
    public void setURLTest(String url_test) {
        this.url_test = url_test;
    }
    
    /**
     * Sets the URL to the folder, where the updated files are
     * @param url_files String URL to the folder of updates
     */
    public void setURLFiles(String url_files) {
        this.url_files = url_files;
    }
    
    /**
     * Sets the URL to folder, where the files are, in which the newest version is declared
     * @param url_updater String URL to the folder, where the new version numbers are
     */
    public void setURLUpdater(String url_updater) {
        this.url_updater = url_updater;
    }
    
    /**
     * Sets if the program should ask to update the program if one is available
     * @param askForUpdate Boolean If it should ask for an update
     */
    public void setAskForUpdate(boolean askForUpdate) {
        this.askForUpdate = askForUpdate;
    }
    
    /**
     * Sets the extension which the downloaded file should have
     * @param extension String Extension of the file name
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    /**
     * Returns if you have an internet connection
     * @return Boolean True if you have an internet connection
     */
    public boolean isInternetConnection() {
        return internet_connection;
    }
    
    /**
     * Returns if an update ist available
     * @return Boolean True if an update is available
     */
    public boolean isUpdateAvailable() {
        return update_available;
    }
    
    /**
     * Returns the folder where the temp files are saved
     * @return File Folder for the temp files
     */
    public File getFileTemp() {
        return file_for_temp;
    }
    
    /**
     * Sets the folder where the temp files are saved
     * @param newLocation File Folder for the temp files
     */
    public void setFileTemp(File newLocation) {
        if(newLocation != null && newLocation.exists() && newLocation.isDirectory()) {
            file_for_temp = newLocation;
        } else if(newLocation != null && !newLocation.exists()) {
            file_for_temp = newLocation;
            file_for_temp.mkdirs();
        } else {
            logErr(lang.getProperty("wtpffs", "Wrong temp file folder selected!"), true);
        }
    }

    /**
     * Returns the extra urls
     * @return ArrayList(String) Extra URLs
     */
    public ArrayList<String> getUrls() {
        return urls;
    }

    /**
     * Returns the normal urls
     * @return HashMap<String, String> URLs
     */
    public HashMap<String, String> getURLs() {
        HashMap<String, String> urls_map = new HashMap<>();
        urls_map.put(URLTEST, url_test);
        urls_map.put(URLUPDATER, url_updater);
        urls_map.put(URLFILES, url_files);
        return urls_map;
    }
    
    /**
     * Returns the extra files
     * @return ArrayList(File) Extra files
     */
    public ArrayList<File> getFiles() {
        return files;
    }

    /**
     * Returns if extra files will be downloaded
     * @return Boolean True if extra files will be downloaded, false if not
     */
    public boolean isDownloadExtraFiles() {
        return download_extra_files;
    }

    /**
     * Sets if the extra files should be downloaded
     * @param download_extra_files Boolean True if extra files should be downloaded, false if not
     */
    public void setDownloadExtraFiles(boolean download_extra_files) {
        this.download_extra_files = download_extra_files;
    }

    /**
     * Returns if the updater is enabled
     * @return Boolean True if the updater is enabled, False if not
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the updater on or off
     * @param enabled Boolean True = on, False = off
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns how many tries will be done if a download failes
     * @return Integer Tries to download something
     */
    public int getDownloadTries() {
        return download_tries;
    }

    /**
     * Sets how many tries a download can do
     * @param download_tries Integer Tries to download somethimng
     */
    public void setDownloadTries(int download_tries) {
        this.download_tries = download_tries;
    }

    /**
     * Returns the test URL
     * @return String Test URL
     */
    public String getUrlTest() {
        return url_test;
    }

    /**
     * Returns the files URL
     * @return String Files URL
     */
    public String getUrlFiles() {
        return url_files;
    }

    /**
     * Returns the updater URL
     * @return String Updater URL
     */
    public String getUrlUpdater() {
        return url_updater;
    }

    /**
     * Returns the actual version of the program
     * @return 
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the version of the update available for the program
     * @return 
     */
    public String getVersionUpdate() {
        return version_update;
    }
    
    /**
     * Returns the language properties
     * @return Properties Language
     */
    public Properties getLang() {
        return lang;
    }

    /**
     * Returns the folder of the jar
     * @return 
     */
    public File getFolderOfJar() {
        return folder_of_jar;
    }

    /**
     * Sets the folder where the jar is located
     * @param folder_of_jar File Folder of jar
     */
    public void setFolderOfJar(File folder_of_jar) {
        this.folder_of_jar = folder_of_jar;
    }
    
    /**
     * Returns the config folder
     * @return File Folder
     */
    public static File getFolder() {
        JConfig config = new JConfig("JUpdater");
        return config.getConfigFolder();
    }
    
    /**
     * Returns the data folder
     * @return File Data Folder
     */
    public static File getFolderData() {
        return new File(getFolder().getAbsolutePath() + File.separator + FOLDERDATA);
    }
    
    /**
     * Returns the programs file
     * @return File File programs
     */
    public static File getFilePrograms() {
        return new File(getFolderData().getAbsolutePath() + File.separator + FILEAPPLICATIONSUPDATING);
    }
    
    /**
     * Returns the paths file
     * @return File File paths
     */
    public static File getFilePaths() {
        return new File(getFolderData().getAbsolutePath() + File.separator + FILEPATHS);
    }
    
    /**
     * Registers a program
     * @param file_jar File Location of jar file
     * @param name String Name of the program
     * @param version String Version of the program
     */
    public static void registerProgram(File file_jar, String name, String version) {
        File folder_data = jaddon.jupdate.JUpdater.getFolderData();
        folder_data.mkdirs();
        File file_programs = jaddon.jupdate.JUpdater.getFilePrograms();
        File file_paths = jaddon.jupdate.JUpdater.getFilePaths();
        DataUtils.put(file_programs, name, version);
        DataUtils.put(file_paths, file_jar, name);
    }
    
}
