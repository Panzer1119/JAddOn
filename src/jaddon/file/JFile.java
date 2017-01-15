/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.file;

import jaddon.controller.StaticStandard;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Paul
 */
public class JFile extends File {
    
    /**
     * Constructs the advanced file class
     * @param pathname String Path
     */
    public JFile(String pathname) {
        super(pathname);
    }
    
    /**
     * Constructs the advanced file class
     * @param file File File
     */
    public JFile(File file) {
        super(file.getAbsolutePath());
    }
    
    /**
     * Writes text to the file
     * @param text String Text to be written to the file
     * @param append Boolean True if the text should be append to the file, False if not
     * @return Boolean True if it worked, False if not
     */
    public boolean writeText(String text, boolean append) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(super.getAbsoluteFile(), append));
            String[] split = text.split("\n");
            for(String g : split) {
                bw.write(g);
                bw.newLine();
            }
            bw.close();
            bw = null;
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while adding text to file \"" + super.getAbsolutePath() + "\": " + ex);
            return false;
        }
    }
    
}
