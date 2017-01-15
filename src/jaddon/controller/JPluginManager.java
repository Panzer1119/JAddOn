/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.controller;

import jaddon.jlang.JLang;
import jaddon.jloader.JPluginLoader;
import jaddon.jlog.JLogger;
import static jaddon.utils.JUtils.getInvertedStringArray;
import static jaddon.utils.JUtils.getNamesOfFiles;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import static jaddon.utils.JUtils.getNamesOfFiles;
import static jaddon.utils.JUtils.getNamesOfFiles;
import static jaddon.utils.JUtils.getNamesOfFiles;
import static jaddon.utils.JUtils.getNamesOfFiles;
import static jaddon.utils.JUtils.getNamesOfFiles;
import static jaddon.utils.JUtils.getNamesOfFiles;
import static jaddon.utils.JUtils.getNamesOfFiles;
import static jaddon.utils.JUtils.getNamesOfFiles;

/**
 *
 * @author Paul
 */
public class JPluginManager {
    
    //Plugin thins begin
    private ArrayList<Class> plugins = null;
    private boolean plugins_loaded = false;
    private boolean plugins_started = false;
    private boolean plugins_pre_started = false;
    private boolean plugins_first_run = true;
    private boolean show_plugin_names_only = false;
    private ArrayList<File> plugins_file_available = new ArrayList<>();
    private ArrayList<File> plugins_file_loaded = new ArrayList<>();
    private String[] plugins_deactivated = new String[0];
    private String[] plugins_deactivated_backup = plugins_deactivated;
    private ArrayList<File> plugins_file_loaded_backup = new ArrayList<>();
    private ArrayList<String> plugin_names = new ArrayList<>();
    private Duration plugin_update_duration = Duration.ofMillis(100);
    private Instant plugin_last_update = Instant.now();
    private File plugin_folder;
    private Class referenceClass = null;
    private Class application = null;
    //Plugin things end
    
    private JLogger logger = null;
    private JLang lang = null;
    
    public JPluginManager(File plugin_folder, Class application, Class referenceClass) {
        logger = StaticStandard.getLogger();
        lang = StaticStandard.getLang();
        JPluginLoader.setReferenceClass(referenceClass);
        this.plugin_folder = plugin_folder;
        this.application = application;
        this.referenceClass = referenceClass;
    }

    public boolean isShowingPluginNamesOnly() {
        return show_plugin_names_only;
    }

    public void setShowPluginNamesOnly(boolean show_plugin_names_only) {
        this.show_plugin_names_only = show_plugin_names_only;
    }

    public File getPluginFolder() {
        return plugin_folder;
    }

    public void setPluginFolder(File plugin_folder) {
        this.plugin_folder = plugin_folder;
    }
    
    public boolean reloadPlugins() {
        try {
            JPluginLoader.setReferenceClass(referenceClass);
            JPluginLoader.setFileFilterStandard();
            stopPlugins();
            unLoadPlugins();
            if(plugins != null) {
                plugins.clear();
            }
            plugins_loaded = false;
            if(!plugins_first_run) {
                plugins_pre_started = false;
            }
            plugins_file_available.clear();
            for(File f : plugin_folder.listFiles(JPluginLoader.getFileFilter())) {
                plugins_file_available.add(f);
                if(logger != null && lang != null) {
                    logger.log(String.format(lang.getLang("plugin_found_f", "Plugin found \"%s\""), f.getName()), false);
                } else {
                    System.out.println(String.format("Plugin found \"%s\"", f.getName()));
                }
            }
            if(plugins_deactivated.length > 0) {
                plugins = (ArrayList<Class>) (ArrayList<?>) JPluginLoader.loadPlugins(plugin_folder, getInvertedStringArray(plugins_deactivated, getNamesOfFiles(plugins_file_available)));
                
            } else {
                plugins = (ArrayList<Class>) (ArrayList<?>) JPluginLoader.loadPlugins(plugin_folder);
            }
            /*
            for(Pluggable p : plugins) {
                p.setPluginManager(this);
            }
            */
            plugins_file_loaded.clear();
            for(File f : JPluginLoader.getLoadedPluginsFile()) {
                plugins_file_loaded.add(f);
            }
            if(logger != null) {
                logger.log("Plugins loadable: " + plugins.size(), false);
            } else {
                System.out.println("Plugins loadble: " + plugins.size());
            }
            /*
            if(!plugins_pre_started && !plugins_first_run) {
                preStartPlugins();
            }
            updatePlugins(true);
            loadPlugins();
            */
            if(!plugins_first_run) {
                startPlugins();
            }
            if(logger != null) {
                logger.log("Reloaded plugins", false);
            } else {
                System.out.println("Reloaded plugins");
            }
            plugins_loaded = true;
        } catch (Exception ex) {
            if(logger != null) {
                logger.logErr("Error while loading plugins: " + ex, true);
            } else {
                System.err.println("Error while loading plugins: " + ex);
            }
            return false;
        }
        return true;
    }
    
    public void startPlugins() {
        plugin_names.clear();
        int i = 0;
        if(plugins != null && !plugins_started) {
            /*
            for(Pluggable p : plugins) {
                p.setPluginID(i);
                plugin_names.add(p.getName());
                p.resetFolder();
                boolean done = p.start();
                if(logger != null && lang != null) {
                    logger.log(String.format((done) ? lang.getLang("started_plugin", "Started plugin \"%s\" successfully") : lang.getLang("started_plugin_not", "Started plugin \"%s\" not successfully"), p.getName()), !done);
                } else {
                    System.out.println(String.format((done) ? "Started plugin \"%s\" successfully" : "Started plugin \"%s\" not successfully", p.getName()));
                }
                if(done) {
                    i++;
                } else {
                    p.setPluginID(-1);
                }
            }
            */
            plugins_started = true;
            //log(lang.getProperty("started_plugins", "Started plugins"), false);
        }
    }
    
    public void unLoadPlugins() {
        if(plugins != null && false) {
            /*
            for(Pluggable p : plugins) {
                //Delete plugins
            }
            */
        }
    }
    
    public void stopPlugins() {
        plugin_names.clear();
        //updatePlugins(true);
        int i = 0;
        if(plugins != null && plugins_started) {
            /*
            for(Pluggable p : plugins) {
                boolean done = p.stop();
                if(done) {
                    p.setPluginID(-1);
                } else {
                    p.setPluginID(i);
                    plugin_names.add(p.getName());
                    i++;
                }
                if(logger != null && lang != null) {
                    logger.log(String.format((done) ? lang.getLang("stopped_plugin", "Stopped plugin \"%s\" successfully") : lang.getLang("stopped_plugin_not", "Stopped plugin \"%s\" not successfully"), p.getName()), !done);
                } else {
                    System.out.println(String.format((done) ? "Stopped plugin \"%s\" successfully" : "Stopped plugin \"%s\" not successfully", p.getName()));
                }
            }
            */
            plugins_started = false;
            //log(lang.getProperty("stopped_plugins", "Stopped Plugins"), false);
        }
    }
    
}
