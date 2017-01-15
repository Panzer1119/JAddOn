/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.system;

/**
 *
 * @author Paul
 */
public class OS {
    
    public static final int ERROR = -1;
    public static final int WINDOWS = 0;
    public static final int LINUX = 1;
    public static final int MACOS = 2;
    
    public static int getOS() {
        String os = System.getProperty("os.name", "error");
        if(os.equalsIgnoreCase("error")) {
            return ERROR;
        }
        if(os.equalsIgnoreCase("linux") || os.contains("Linux") || os.contains("linux") || os.startsWith("Linux") || os.startsWith("linux")) {
            return LINUX;
        } else if(os.equalsIgnoreCase("windows") || os.contains("Windows") || os.contains("windows") || os.startsWith("Windows") || os.startsWith("windows")) {
            return WINDOWS;
        } else if(os.equalsIgnoreCase("macos") || os.contains("Macos") || os.contains("macos") || os.startsWith("Macos") || os.startsWith("macos")) {
            return MACOS;
        } else {
            return ERROR;
        }
    }
    
}
