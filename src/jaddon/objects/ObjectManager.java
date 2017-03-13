/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.objects;

import jaddon.controller.StaticStandard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Paul
 */
public class ObjectManager {
    
    
    /**
     * Saves an Object to a File
     * @param object Object to be saved in a File
     * @param file File where the Object gets saved
     * @return Boolean True if it worked, False if not
     */
    public static boolean saveObjectToFile(Object object, File file) {
        if(file == null || (file.exists() && file.isDirectory())) {
            return false;
        }
        if(file.exists()) {
            file.delete();
        }
        if(file.exists()) {
            return false;
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                writeObjectToOutputStream(object, fos);
                fos.close();
                fos = null;
                return true;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while saving Object to File: " + ex);
                return false;
            }
        }
    }
    
    /**
     * Writes an Object to an OutputStream
     * @param object Object to be written to an OutputStream
     * @param os OutputStream where the object gets written
     * @return Boolean True if it worked, False if not
     */
    public static boolean writeObjectToOutputStream(Object object, OutputStream os) {
        if(os == null) {
            return false;
        } else {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(object);
                oos.close();
                oos = null;
                return true;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while writing Object to Outputstream: " + ex);
                return false;
            }
        }
    }
    
    /**
     * Loads an Object from a File
     * @param file File where the Object is saved
     * @return Object that was loaded
     */
    public static Object loadObjectFromFile(File file) {
        if(file == null || !file.exists() || !file.isFile()) {
            return null;
        } else {
            try {
                FileInputStream fis = new FileInputStream(file);
                Object object = readObjectFromInputStream(fis);
                fis.close();
                fis = null;
                return object;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while loading Object from File: " + ex);
                return null;
            }
        }
    }
    
    /**
     * Reads an Object from an InputStream
     * @param is InputStream where the Object is saved
     * @return Object that was loaded
     */
    public static Object readObjectFromInputStream(InputStream is) {
        if(is == null) {
            return null;
        } else {
            try {
                ObjectInputStream ois = new ObjectInputStream(is);
                Object object = ois.readObject();
                ois.close();
                ois = null;
                return object;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while reading Object from Inputstream: " + ex);
                return null;
            }
        }
    }
}
