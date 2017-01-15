/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import static jaddon.utils.JUtils.countInString;
import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class JVersion {
    
    public static final int ERROR = -1;
    public static final int VERSION1 = 0;
    public static final int VERSION2 = 1;
    public static final int SAME = 2;
    
    public static final String VERSIONSPLITTER = ".";
    
    public static String version_1 = "";
    public static String version_2 = "";

    public static String getVersion1() {
        return version_1;
    }

    public static void setVersion1(String version_1) {
        JVersion.version_1 = version_1;
    }

    public static String getVersion2() {
        return version_2;
    }

    public static void setVersion2(String version_2) {
        JVersion.version_2 = version_2;
    }
    
    public static void prepare() {
        if(countDots(version_1) > countDots(version_2)) {
            for(int i = 0; i < (countDots(version_1) - countDots(version_2)); i++) {
                version_2 += VERSIONSPLITTER + "0";
            }
        } else if(countDots(version_1) < countDots(version_2)) {
            for(int i = 0; i < (countDots(version_2) - countDots(version_1)); i++) {
                version_1 += VERSIONSPLITTER + "0";
            }
        }
    }
    
    /**
     * Compares two versions and returns the newer one
     * @return Integer Newer Version
     */
    public static int compare() {
        prepare();
        if(version_1.equals(version_2)) {
            return SAME;
        }
        try {
            String[] split_1 = version_1.split("\\" + VERSIONSPLITTER);
            String[] split_2 = version_2.split("\\" + VERSIONSPLITTER);
            ArrayList<Integer> int_1 = new ArrayList<>();
            ArrayList<Integer> int_2 = new ArrayList<>();
            for(String g : split_1) {
                try {
                    int_1.add(Integer.parseInt(g));
                } catch (Exception ex) {
                    int_1.add(0);
                }
            }
            for(String g : split_2) {
                try {
                    int_2.add(Integer.parseInt(g));
                } catch (Exception ex) {
                    int_2.add(0);
                }
            }
            for(int i = 0; i < int_1.size(); i++) {
                if(int_1.get(i) == ERROR || int_2.get(i) == ERROR) {
                    return ERROR;
                }
                if(int_1.get(i) > int_2.get(i)) {
                    return VERSION1;
                } else if(int_1.get(i) < int_2.get(i)) {
                    return VERSION2;
                }
            }
            return ERROR;
        } catch (Exception ex) {
            System.err.println("Error while comparing versions: " + ex);
            return ERROR;
        }
    }
    
    /**
     * Compares two versions and returns the newer one
     * @param version_1 String First Version
     * @param version_2 String Second Version
     * @return Integer Newer Version
     */
    public static int newerVersion(String version_1, String version_2) {
        String old_version_1 = JVersion.version_1;
        String old_version_2 = JVersion.version_2;
        JVersion.version_1 = version_1;
        JVersion.version_2 = version_2;
        int result = compare();
        JVersion.version_1 = old_version_1;
        JVersion.version_2 = old_version_2;
        return result;
    }
    
    /**
     * Compares two versions and returns the newer one
     * @param version_1 String First Version
     * @param version_2 String Second Version
     * @return String Newer Version
     */
    public static String newerVersionString(String version_1, String version_2) {
        String old_version_1 = JVersion.version_1;
        String old_version_2 = JVersion.version_2;
        JVersion.version_1 = version_1;
        JVersion.version_2 = version_2;
        int result = compare();
        JVersion.version_1 = old_version_1;
        JVersion.version_2 = old_version_2;
        switch (result) {
            case VERSION1:
                return version_1;
            case VERSION2:
                return version_2;
            case SAME:
                return version_1;
            case ERROR:
                return null;
            default:
                return null;
        }
    }
    
    public static boolean isFirstVersionNewer(String version_1, String version_2) {
        return newerVersion(version_1, version_2) == VERSION1;
    }
    
    public static boolean isSecondVersionNewer(String version_1, String version_2) {
        return newerVersion(version_1, version_2) == VERSION2;
    }
    
    public static String getCodeName(int code) {
        String msg = "";
        switch(code) {
            case ERROR:
                msg = "ERROR";
                break;
            case VERSION1:
                msg = "VERSION1";
                break;
            case VERSION2:
                msg = "VERSION2";
                break;
            case SAME:
                msg = "SAME";
                break;
        }
        return msg;
    }
    
    public static int countDots(String version) {
        return countInString(version, VERSIONSPLITTER);
    }
    
}
