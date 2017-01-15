/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.pfile;

import java.io.File;

/**
 *
 * @author Paul
 */
public class PFile {
    
    /**
     * Counts the files in a folder recursive
     * @param folder File to be searched
     * @param countOnlyThisFolder Boolean True if it should only count files in the given directory or False if it count ALL files in all directories in the folder
     * @param extras String array with extra parameter, the first is for the extension, and all others are to test if the file name contains something, place a ! before names to invert it
     * @return Int with number of files that agree to the extras parameter
     */
    public static int countFiles(File folder, boolean countOnlyThisFolder, String... extras) {
        int i = 0;
        for(File g : folder.listFiles()) {
            if(g.isFile()) {
                if(extras.length > 0) {
                    boolean p = false;
                    if(extras[0].equals("!!")) {
                        p = true;
                    } else {
                        if(extras[0].contains("!")) {
                            extras[0] = extras[0].replaceAll("!", "");
                            if(!g.getName().endsWith(extras[0])) {
                                p = true;
                            }
                            extras[0] = "!" + extras[0];
                        } else {
                            if(g.getName().endsWith(extras[0])) {
                                p = true;
                            }    
                        }  
                    }
                    if(p) {
                        int z = 0;
                        for(String h : extras) {
                            if(h.contains("!")) {
                                h = h.replaceAll("!", "");
                                if(!g.getName().contains(h)) {
                                    z++;
                                }
                                h = "!" + h;
                            } else {
                                if(g.getName().contains(h)) {
                                    z++;
                                }
                            }
                        }
                        if(z == extras.length) {
                            i++;
                        }
                    }
                } else {
                    i++;
                }
            } else if(g.isDirectory()) {
                if(!countOnlyThisFolder) {
                    i += countFiles(g, countOnlyThisFolder, extras);
                }
            }
        }
        return i;
    }
    
}
