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
import jaddon.time.JTimer;
import jaddon.utils.JUtils;

/**
 *
 * @author Paul
 */
public class JAddOnStandard implements Update {
    
    private boolean doUpdate = false;
    
    public JAddOnStandard(String name, String version) {
        this(name, version, false, false, false, false, false, false, false, false, false);
    }
    
    public JAddOnStandard(String name, String version, boolean jlang, boolean jconfig, boolean jloader, boolean jpluginloader, boolean jlogger, boolean jlogin, boolean jdata, boolean jupdater, boolean jtimer) {
        StaticStandard.setName(name);
        StaticStandard.setVersion(version);
        if(jlang) {
            JLang lang = new JLang();
            StaticStandard.setLang(lang);
        }
        if(jlogger) {
            JLogger logger = new JLogger(false);
            StaticStandard.setLogger(logger);
        }
        if(jconfig) {
            JConfig config = new JConfig(StaticStandard.getName());
            config.setLogger(StaticStandard.getLogger());
            StaticStandard.setConfig(config);
        }
        if(jloader) {
            JLoader loader = new JLoader(null);
            StaticStandard.setLoader(loader);
        }
        if(jpluginloader) {
            JPluginLoader pluginloader = new JPluginLoader();
            StaticStandard.setPluginloader(pluginloader);
        }
        if(jlogin) {
            JLogin login = new JLogin(null);
            StaticStandard.setLogin(login);
        }
        if(jdata) {
            JData data = new JData();
            StaticStandard.setData(data);
        }
        if(jupdater) {
            JUpdater updater = new JUpdater(StaticStandard.getName(), StaticStandard.getVersion());
            updater.loadURLsFromInternResource("/jaddon/stuff/urls/standard_jupdater.txt");
            StaticStandard.setUpdater(updater);
        }
        if(jtimer) {
            JTimer timer = new JTimer();
            StaticStandard.setTimer(timer);
        }
        if(!StaticStandard.isIsIDE()) {
            JUpdater.registerProgram(JUtils.getJARLocation(), name, version);
        }
    }

    public JAddOnStandard(String name, String version, boolean jlang, boolean jconfig, boolean jpluginloader, boolean jlogger, boolean jupdater) {
        this(name, version, jlang, jconfig, false, jpluginloader, jlogger, false, false, jupdater, false);
    }
    
    @Override
    public boolean update() {
        if(doUpdate) {
            return true;
        }
        return false;
    }

    @Override
    public void setDoUpdate(boolean doUpdate) {
        this.doUpdate = doUpdate;
        
    }
    
    @Override
    public boolean isDoingUpdate() {
        return doUpdate;
    }
    
}
