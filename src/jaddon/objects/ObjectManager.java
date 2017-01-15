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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Paul
 */
public class ObjectManager {
    
    
    /**
     * Saves an object to a file
     * @param object Object to be written in a file
     * @param file File which gets written
     * @return Boolean True if this gone right, False if not
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
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(object);
                oos.close();
                fos.close();
                oos = null;
                fos = null;
                return true;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while saving object to file: " + ex);
                return false;
            }
        }
    }
    
    /**
     * Loads an object from a file
     * @param file File where the Object is saves
     * @return Object which was loaded
     */
    public static Object loadObjectFromFile(File file) {
        if(file == null || !file.exists() || !file.isFile()) {
            return null;
        } else {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object object = ois.readObject();
                ois.close();
                fis.close();
                ois = null;
                fis = null;
                return object;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while loading object from file: " + ex);
                return null;
            }
        }
    }
    
}
