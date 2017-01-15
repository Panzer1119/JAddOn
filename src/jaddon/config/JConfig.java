/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.config;

import jaddon.controller.StaticStandard;
import jaddon.controller.Update;
import jaddon.exceptions.FileIsNotAFolderException;
import jaddon.jlog.JLogger;
import static jaddon.system.OS.LINUX;
import static jaddon.system.OS.MACOS;
import static jaddon.system.OS.WINDOWS;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author Paul
 */
public class JConfig implements Update {
    
    private File config = null;
    private File plugin_folder = null;
    private Properties set = new Properties();
    private JLogger logger = null;
    private HashMap<String, String> config_default = new HashMap<>();
    private String changeExtra = "Changed on:";
    private boolean doUpdate = true;
    private boolean makeSelf = false;
    private boolean pluginsEnabled = false;
    private boolean enableLocalConfig = false;
    
    public JConfig() {
        this("");
    }
    
    public JConfig(String name) {
        this(null, null, false);
        setSelfMaking(true, name);
    }
    
    public JConfig(File config) {
        this(config, StaticStandard.getLogger());
    }
    
    public JConfig(File config, JLogger logger, Object... extras) {
        boolean setConfig = true;
        try {
            if(extras.length > 0) {
                setConfig = (Boolean) extras[0];
            }
        } catch (Exception ex) {
        }
        StaticStandard.reloadOS();
        this.config = config;
        this.logger = logger;
        StaticStandard.setConfigFile(config);
        if(setConfig) {
            StaticStandard.setConfig(this);
        }
    }
    
    public boolean isSelfMaking() {
        return makeSelf;
    }
    
    public File getConfigFolder() {
        try {
            return config.getParentFile();
        } catch (Exception ex) {
            return null;
        }
    }
    
    public void setSelfMaking(boolean makeSelf, String name) {
        this.makeSelf = makeSelf;
        if(this.makeSelf && name != null) {
            if(!enableLocalConfig && StaticStandard.getOS() == WINDOWS) {
                config = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "." + name + File.separator + "config.txt").getAbsoluteFile();
            } else if(!enableLocalConfig && StaticStandard.getOS() == LINUX || StaticStandard.getOS() == MACOS) {
                config = new File(System.getProperty("user.home") + File.separator + "." + name + File.separator + "config.txt").getAbsoluteFile();
            } else {
                config = new File("config.txt").getAbsoluteFile();
            }
            try {
                makePluginFolder();
            } catch (Exception ex) {
                if(logger != null) {
                    logger.logErr("Error while making the plugin folder: " + ex);
                } else {
                    System.err.println("Error while making the plugin folder: " + ex);
                }
            }
            StaticStandard.setConfigFile(config);
        }
    }
    
    public void makePluginFolder() throws FileIsNotAFolderException {
            if(config != null) {
                plugin_folder = new File(config.getParentFile().getAbsolutePath() + File.separator + "plugins").getAbsoluteFile();
                StaticStandard.setPluginFolder(plugin_folder);
                if(pluginsEnabled && plugin_folder.exists() && plugin_folder.isFile()) {
                    throw new FileIsNotAFolderException("The file you have selected to be the plugins folder isnt a directory");
                }
                if(pluginsEnabled && !plugin_folder.exists()) {
                    plugin_folder.mkdirs();
                }
            }
    }
    
    public File getTempFolder() {
        try {
            return new File(config.getParentFile().getAbsolutePath() + File.separator + "temp");
        } catch (Exception ex) {
            logger.logErr("Error while getting temp folder: " + ex);
            return null;
        }
    }

    public boolean isPluginsEnabled() {
        return pluginsEnabled;
    }

    public void setPluginsEnabled(boolean pluginsEnabled) {
        this.pluginsEnabled = pluginsEnabled;
    }

    public boolean isEnableLocalConfig() {
        return enableLocalConfig;
    }

    public void setEnableLocalConfig(boolean enableLocalConfig) {
        this.enableLocalConfig = enableLocalConfig;
    }
    
    public void setPluginFolder(File pluginFolder) throws FileIsNotAFolderException {
        if(pluginsEnabled && pluginFolder.exists() && pluginFolder.isFile()) {
            throw new FileIsNotAFolderException("The file you have selected to be the plugins folder isnt a directory");
        }
        this.plugin_folder = pluginFolder;
        StaticStandard.setPluginFolder(plugin_folder);
        if(pluginsEnabled && !pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
    }
    
    public boolean update() {
        if(!doUpdate) {
            return false;
        }
        boolean updated = false;
        JLogger logger = StaticStandard.getLogger();
        StaticStandard.setConfig(this);
        if(logger != null) {
            this.logger = logger;
            updated = true;
        }
        return updated;
    }
    
    public void setDefaultConfig(HashMap<String, String> config_default) {
        this.config_default = config_default;
    }
    
    public boolean setDefaultConfig(String[] keys, String[] defaultValues) {
        if(keys.length != defaultValues.length) {
            return false;
        }
        config_default.clear();
        for(int i = 0; i < keys.length; i++) {
            config_default.put(keys[i], defaultValues[i]);
        }
        return true;
    }
    
    public HashMap<String, String> getDefaultConfig() {
        return config_default;
    }
    
    public void setFile(File config) {
        this.config = config;
        StaticStandard.setConfigFile(config);
        try {
            makePluginFolder();
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr(ex);
            } else {
                System.err.println(ex);
            }
        }
    }
    
    public File getFile() {
        return config;
    }
    
    public void makeConfig() {
        if(config == null) {
            return;
        }
        if(!config.getParentFile().exists()) {
            config.getParentFile().mkdirs();
        }
        try {
            config.createNewFile();
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while creating config file: " + ex);
            }
        }
    }
    
    public void reloadConfig() {
        if(config == null) {
            logger.logErr("Config file problem");
            return;
        }
        makeConfig();
        set.clear();
        try {
            set.load(new BufferedInputStream(new FileInputStream(config)));
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while loading config file: " + ex);
            }
        }
        if(!testConfig(!config_default.isEmpty())) {
            reloadConfig();
        }
    }
    
    public boolean setProperty(String key, String value) {
        set.setProperty(key, value);
        return (set.containsKey(key) && set.containsValue(value));
    }
    
    public String getProperty(String key) {
        return set.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return set.getProperty(key, defaultValue);
    }
    
    public boolean saveConfig() {
        if(config == null || !config.exists()) {
            return false;
        }
        try {
            set.store(new BufferedOutputStream(new FileOutputStream(config)), changeExtra);
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while saving config file: " + ex);
            }
            return false;
        }
        return true;
    }
    
    public boolean testConfig(boolean makeItRight) {
        boolean isRight = true;
        boolean corrected = false;
        for(String g : config_default.keySet()) {
            if(!set.containsKey(g)) {
                isRight = false;
                if(!makeItRight) {
                    return isRight;
                } else {
                    set.setProperty(g, config_default.get(g));
                    corrected = true;
                }
            }
        }
        if(corrected) {
            saveConfig();
        }
        return isRight;
    }
    
    public void setLogger(JLogger logger) {
        this.logger = logger;
    }
    
    public JLogger getLogger() {
        return logger;
    }
    
    public void setComment(String comment) {
        this.changeExtra = comment;
    }
    
    public String getComment() {
        return changeExtra;
    }
    
    public void setProperties(Properties set) {
        this.set = set;
    }
    
    public Properties getProperties() {
        return set;
    }
    
    public void setDoUpdate(boolean doUpdate) {
        this.doUpdate = doUpdate;
        
    }
    
    public boolean isDoingUpdate() {
        return doUpdate;
    }
    
}
