/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jloader;

import jaddon.jlog.JLogger;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author Paul
 */
public class JPluginLoader {
    
    private static File[] loaded_plugins = new File[0];
    private static JLogger logger = null;
    private static Properties lang = null;
    private static Class ref_class = null;
    private static FileFilter filefilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".jar");
            }
        };
    
    public static ArrayList<Class> loadPlugins(File plugDir, String[] name) throws IOException {
        File[] jars = plugDir.listFiles(filefilter);
        ArrayList<File> files = new ArrayList<>();
        for(int i = 0; i < jars.length; i++) {
            boolean temp = false;
            for(String g : name) {
                if(jars[i].getName().equals(g) || jars[i].getName().equals(g + ".jar")) {
                    temp = true;
                    break;
                }
            }
            if(temp) {
                files.add(jars[i]);
            }
        }
        File[] plugJars = new File[files.size()];
        loaded_plugins = new File[files.size()];
        for(int i = 0; i < files.size(); i++) {
            plugJars[i] = files.get(i);
            loaded_plugins[i] = files.get(i);
        }
        ClassLoader cl = new URLClassLoader(JPluginLoader.fileArrayToURLArray(plugJars));
        ArrayList<Class<Class>> plugClasses = JPluginLoader.extractClassesFromJARs(plugJars, cl);
        return JPluginLoader.createPluggableObjects(plugClasses);
    }
    
    public static ArrayList<Class> loadPlugins(File plugDir) throws IOException {
        File[] plugJars = plugDir.listFiles(filefilter);
        loaded_plugins = plugJars;
        ClassLoader cl = new URLClassLoader(JPluginLoader.fileArrayToURLArray(plugJars));
        ArrayList<Class<Class>> plugClasses = JPluginLoader.extractClassesFromJARs(plugJars, cl);
        return JPluginLoader.createPluggableObjects(plugClasses);
    }
    
    private static URL[] fileArrayToURLArray(File[] files) throws MalformedURLException {
        URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }
        return urls;
    }
    
    private static ArrayList<Class<Class>> extractClassesFromJARs(File[] jars, ClassLoader cl) throws IOException {
        ArrayList<Class<Class>> classes = new ArrayList<Class<Class>>();
        for (File jar : jars) {
            classes.addAll(JPluginLoader.extractClassesFromJAR(jar, cl));
        }
        return classes;
    }
 
    @SuppressWarnings("unchecked")
    private static List<Class<Class>> extractClassesFromJAR(File jar, ClassLoader cl) throws IOException { 
        List<Class<Class>> classes = new ArrayList<Class<Class>>();
        JarInputStream jaris = new JarInputStream(new FileInputStream(jar));
        JarEntry ent = null;
        while ((ent = jaris.getNextJarEntry()) != null) {
            if (ent.getName().toLowerCase().endsWith(".class")) {
                try {
                    Class<Class> cls = (Class<Class>) cl.loadClass(ent.getName().substring(0, ent.getName().length() - 6).replace('/', '.'));
                    if (JPluginLoader.isPluggableClass(cls)) {
                        classes.add((Class<Class>)cls);
                    }
                } catch (ClassNotFoundException e) {
                    String msg = "";
                    if(lang != null) {
                        msg = String.format(lang.getProperty("cant_load_class", "Could not load Class \"%s\""), ent.getName());
                    } else {
                        msg = "Can't load Class " + ent.getName();
                    }
                    if(logger != null) {
                        logger.logErr(msg, false);
                    } else {
                        System.err.println(msg);
                    }
                    e.printStackTrace();
                }
            }
        }
        jaris.close();
        return classes;
    }
 
    private static boolean isPluggableClass(Class<Class> cls) {
        if(cls == null) {
            return false;
        }
        for (Class<?> i : cls.getInterfaces()) {
            if (i.equals(ref_class)) {
                return true;
            }
        }
        return false;
    }
    
    private static ArrayList<Class> createPluggableObjects(ArrayList<Class<Class>> pluggables) { 
        ArrayList<Class> plugs = new ArrayList<>(pluggables.size());
        for (Class<Class> plug : pluggables) {
            try {
                plugs.add(plug.newInstance());
            } catch (InstantiationException e) {
                String msg = "";
                if(lang != null) {
                    msg = String.format(lang.getProperty("cant_instantiate_plugin", "Could not instantiate plugin \"%s\""), plug.getName());
                } else {
                    msg = "Can't instantiate plugin: " + plug.getName();
                }
                if(logger != null) {
                    logger.logErr(msg, false);
                } else {
                    System.err.println(msg);
                }
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                String msg = "";
                if(lang != null) {
                    msg = String.format(lang.getProperty("illegalaccess_for_plugin", "IllegalAccess for plugin \"%s\""), plug.getName());
                } else {
                    msg = "IllegalAccess for plugin: " + plug.getName();
                }
                if(logger != null) {
                    logger.logErr(msg, false);
                } else {
                    System.err.println(msg);
                }
                e.printStackTrace();
            }
        }
        return plugs;
    }
    
    public static File[] getLoadedPluginsFile() {
        return loaded_plugins;
    }
    
    public static void setLogger(JLogger logger_neu) {
        logger = logger_neu;
    }
    
    public static void setLang(Properties lang_neu) {
        lang = lang_neu;
    }

    public static Class getReferenceClass() {
        return ref_class;
    }

    public static void setReferenceClass(Class ref_class) {
        JPluginLoader.ref_class = ref_class;
    }

    public static FileFilter getFileFilter() {
        return filefilter;
    }

    public static void setFileFilter(FileFilter filefilter) {
        JPluginLoader.filefilter = filefilter;
    }
    
    public static void setFileFilterStandard() {
        JPluginLoader.filefilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".jar");
            }
        };
    }
    
}
