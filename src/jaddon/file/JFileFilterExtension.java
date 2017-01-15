/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.file;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Paul
 */
public class JFileFilterExtension implements FileFilter {

    String extension = "";
    
    public JFileFilterExtension(String extension) {
        this.extension = extension;
    }
    
    @Override
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(extension);
    }
    
}
