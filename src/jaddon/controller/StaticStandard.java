/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.controller;

import jaddon.config.JConfig;
import jaddon.jlang.JLang;
import jaddon.jloader.JLoader;
import jaddon.jloader.JPluginLoader;
import jaddon.jlog.JLogger;
import jaddon.jlogin.JLogin;
import jaddon.math.JData;
import jaddon.jupdate.JUpdater;
import jaddon.system.OS;
import jaddon.time.JTimer;
import jaddon.utils.JUtils;
import java.io.File;
import java.time.Instant;
import javax.swing.JOptionPane;

/**
 *
 * @author Paul
 */
public class StaticStandard {
    
    private static String name = null;
    private static String version = null;
    private static int os = -1;
    private static File config_file = null;
    private static File plugin_folder = null;
    private static JConfig config = null;
    private static JLang lang = null;
    private static JLoader loader = null;
    private static JPluginLoader pluginloader = null;
    private static JLogger logger = null;
    private static JLogin login = null;
    private static JData data = null;
    private static JUpdater updater = null;
    private static JTimer timer = null;
    private static boolean isIDE = false;
    public static final Instant INSTANTSTART = Instant.now();
    
    public static void log(Object msg, int level) {
        if(logger != null) {
            logger.log(msg, level);
        } else {
            System.out.println(msg);
        }
    }
    
    public static void log(Object msg, int level, boolean speak) {
        if(logger != null) {
            logger.log(msg, false, level, speak);
        } else {
            System.out.println(msg);
            if(speak) {
                try {
                    JUtils.speak(msg.toString());
                } catch (Exception ex) {
                    
                }
            }
        }
    }
    
    public static void log(Object msg, boolean show, int level) {
        if(logger != null) {
            logger.log(msg, show, level);
        } else {
            System.out.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    public static void log(Object msg, boolean show, int level, boolean speak) {
        if(logger != null) {
            logger.log(msg, show, level, speak);
        } else {
            System.out.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            if(speak) {
                try {
                    JUtils.speak(msg.toString());
                } catch (Exception ex) {
                    
                }
            }
        }
    }
    
    public static void log(Object msg) {
        if(logger != null) {
            logger.log(msg);
        } else {
            System.out.println(msg);
        }
    }
    
    public static void log(Object msg, boolean show) {
        if(logger != null) {
            logger.log(msg, show);
        } else {
            System.out.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    public static void log(Object msg, boolean show, boolean speak) {
        if(logger != null) {
            logger.log(msg, show, speak);
        } else {
            System.out.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            if(speak) {
                try {
                    JUtils.speak(msg.toString());
                } catch (Exception ex) {
                    
                }
            }
        }
    }
    
    public static void logErr(Object msg) {
        if(logger != null) {
            logger.logErr(msg);
        } else {
            System.err.println(msg);
        }
    }
    
    public static void logErr(Object msg, boolean show) {
        if(logger != null) {
            logger.logErr(msg, show);
        } else {
            System.err.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void logErr(Object msg, boolean show, boolean speak) {
        if(logger != null) {
            logger.logErr(msg, show, speak);
        } else {
            System.err.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            if(speak) {
                try {
                    JUtils.speak(msg.toString());
                } catch (Exception ex) {
                    
                }
            }
        }
    }
    
    public static void logErr(Object msg, Exception ex) {
        if(logger != null) {
            logger.logErr(msg, ex);
        } else {
            System.err.println(msg);
        }
    }
    
    
    public static void logErr(Object msg, Exception ex, boolean speak) {
        if(logger != null) {
            logger.logErr(msg, ex, speak);
        } else {
            System.err.println(msg);
            if(speak) {
                try {
                    JUtils.speak(msg.toString());
                } catch (Exception ex2) {
                    
                }
            }
        }
    }
    
    public static void logErr(Object msg, boolean show, Exception ex) {
        if(logger != null) {
            logger.logErr(msg, show, ex);
        } else {
            System.err.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void logErr(Object msg, boolean show, Exception ex, boolean speak) {
        if(logger != null) {
            logger.logErr(msg, show, ex, speak);
        } else {
            System.err.println(msg);
            if(show) {
                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            if(speak) {
                try {
                    JUtils.speak(msg.toString());
                } catch (Exception ex2) {
                    
                }
            }
        }
    }
    
    public static void exit() {
        while(true) {
            try {
                System.exit(0);
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
    }

    public static String getName() {
        return name;
    }
    
    public static void setName(String name) {
        StaticStandard.name = name;
        reloadOS();
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        StaticStandard.version = version;
    }    
    
    public static int getOS() {
        return os;
    }
    
    public static void reloadOS() {
        StaticStandard.os = OS.getOS();
    }

    public static File getConfigFile() {
        return config_file;
    }

    public static void setConfigFile(File config_file) {
        StaticStandard.config_file = config_file;
    }

    public static File getPluginFolder() {
        return plugin_folder;
    }

    public static void setPluginFolder(File plugin_folder) {
        StaticStandard.plugin_folder = plugin_folder;
    }
    
    public static JConfig getConfig() {
        return config;
    }

    public static void setConfig(JConfig config) {
        StaticStandard.config = config;
    }
    
    public static void setLang(JLang lang) {
        StaticStandard.lang = lang;
    }
    
    public static JLang getLang() {
        return lang;
    }

    public static JLoader getLoader() {
        return loader;
    }

    public static void setLoader(JLoader loader) {
        StaticStandard.loader = loader;
    }

    public static JPluginLoader getPluginloader() {
        return pluginloader;
    }

    public static void setPluginloader(JPluginLoader pluginloader) {
        StaticStandard.pluginloader = pluginloader;
    }

    public static JLogger getLogger() {
        return logger;
    }

    public static void setLogger(JLogger logger) {
        StaticStandard.logger = logger;
    }

    public static JLogin getLogin() {
        return login;
    }

    public static void setLogin(JLogin login) {
        StaticStandard.login = login;
    }

    public static JData getData() {
        return data;
    }

    public static void setData(JData data) {
        StaticStandard.data = data;
    }

    public static JUpdater getUpdater() {
        return updater;
    }

    public static void setUpdater(JUpdater updater) {
        StaticStandard.updater = updater;
    }

    public static JTimer getTimer() {
        return timer;
    }

    public static void setTimer(JTimer timer) {
        StaticStandard.timer = timer;
    }

    public static boolean isIsIDE() {
        return isIDE;
    }

    public static void setIsIDE(boolean isIDE) {
        StaticStandard.isIDE = isIDE;
    }
    
    public static Thread execute(Runnable run) {
        Thread thread = new Thread(run);
        thread.start();
        return thread;
    }
    
}
