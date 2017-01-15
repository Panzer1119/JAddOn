/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import jaddon.controller.StaticStandard;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Paul
 */
public class DataUtils {
    
    public static final String delimiter = ";";
    public static final String equals = "=";
    public static final String escape = "\"";
    
    public static String formatData(Object data) {
        return formatData(null, data);
    }
    
    public static String formatData(Object key, Object value) {
        String extra = "";
        if(key != null) {
            extra = escape + key + escape + equals;
        }
        return String.format("%s%s", extra, (escape + value + escape));
    }
    
    private static void checkFile(File file) {
        if(file == null && !(file.exists() && file.isDirectory())) {
            return;
        }
        file.getParentFile().mkdirs();
    }
    
    public static Object[] getData(String line) {
        if(line == null) {
            return null;
        }
        String first = null;
        String second = null;
        int index_escape_start = line.indexOf(escape);
        if(index_escape_start != -1) {
            int index_escape_end = line.substring(index_escape_start + escape.length()).indexOf(escape) + index_escape_start + escape.length();
            if(index_escape_end != -1) {
                first = line.substring(index_escape_start + escape.length(), index_escape_end);
                if(line.length() > first.length() + (2 * escape.length()) && line.substring(index_escape_end + escape.length()).startsWith(equals)) {
                    String second_temp = line.substring(index_escape_end + escape.length() + equals.length());
                    int index_escape_2_start = second_temp.indexOf(escape);
                    if(index_escape_2_start != -1) {
                        second_temp = second_temp.substring(index_escape_2_start + escape.length());
                        int index_escape_2_end = second_temp.indexOf(escape);
                        if(index_escape_2_end != -1) {
                            second = second_temp.substring(0, index_escape_2_end);
                        }
                    }
                }
            }
        }
        if(second == null) {
            return new Object[] {first};
        } else {
            return new Object[] {first, second};
        }
    }
    
    public static File saveArrayListToFile(ArrayList<?> arraylist, File file) {
        if(arraylist == null || file == null || (file.exists() && file.isDirectory())) {
            return null;
        }
        checkFile(file);
        try {
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for(Object o : arraylist) {
                try {
                    bw.write(formatData(o));
                    bw.newLine();
                } catch (Exception ex) {
                }
            }
            bw.close();
            fw.close();
            bw = null;
            fw = null;
            return file;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while saving an arraylist to a file" + ex, ex);
            return null;
        }
    }
    
    public static File saveHashMapToFile(HashMap<?, ?> hashmap, File file) {
        if(hashmap == null || file == null || (file.exists() && file.isDirectory())) {
            return null;
        }
        checkFile(file);
        try {
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for(Object o : hashmap.keySet()) {
                try {
                    bw.write(formatData(o, hashmap.get(o)));
                    bw.newLine();
                } catch (Exception ex) {
                }
            }
            bw.close();
            fw.close();
            bw = null;
            fw = null;
            return file;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while saving a hashmap to a file" + ex, ex);
            return null;
        }
    }
    
    public static ArrayList<Object> loadArrayListFromFile(File file) {
        if(file == null || !file.exists() || (file.exists() && file.isDirectory())) {
            return new ArrayList<>();
        }
        try {
            ArrayList<Object> arraylist = new ArrayList<>();
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                try {
                    String line = scanner.nextLine();
                    Object[] data = getData(line);
                    if(data != null && data.length == 1) {
                        arraylist.add(data[0]);
                    }
                } catch (Exception ex) {
                }
            }
            scanner.close();
            scanner = null;
            return arraylist;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading an arraylist from a file" + ex, ex);
            return null;
        }
    }
    
    public static HashMap<Object, Object> loadHashMapFromFile(File file) {
        if(file == null || !file.exists() || (file.exists() && file.isDirectory())) {
            return new HashMap<>();
        }
        try {
            HashMap<Object, Object> hashmap = new HashMap<>();
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                try {
                    String line = scanner.nextLine();
                    Object[] data = getData(line);
                    if(data != null && data.length == 2) {
                        hashmap.put(data[0], data[1]);
                    }
                } catch (Exception ex) {
                }
            }
            scanner.close();
            scanner = null;
            return hashmap;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading a hashmap from a file" + ex, ex);
            return null;
        }
    }
    
    public static ArrayList<Object> add(File file, Object object) {
        if(file == null || !file.exists() || (file.exists() && file.isDirectory()) || object == null) {
            return null;
        }
        ArrayList<Object> data_old = loadArrayListFromFile(file);
        data_old.add(object);
        saveArrayListToFile(data_old, file);
        return data_old;
    }
    
    public static HashMap<Object, Object> put(File file, Object key, Object value) {
        if(file == null || !file.exists() || (file.exists() && file.isDirectory()) || key == null) {
            return null;
        }
        HashMap<Object, Object> data_old = loadHashMapFromFile(file);
        data_old.put(key, value);
        saveHashMapToFile(data_old, file);
        return data_old;
    }
    
}
