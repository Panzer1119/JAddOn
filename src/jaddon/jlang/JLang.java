/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jlang;

import jaddon.controller.StaticStandard;
import jaddon.controller.Update;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Paul
 */
public class JLang implements Update {
    
    private static Properties lang_prop = new Properties();
    private static String lang_string = null;
    
    private final Properties lang = new Properties();
    private String file = null;
    private String name = null;
    private int times = 0;
    private final int maxTimes = 10;
    private final String lang_separator = "/";
    private String lang_start = "lang_";
    private String lang_extension = ".txt";
    private Class class_ref = JLang.class;
    private boolean doUpdate = false;
    
    public JLang() {
        this("EN");
    }
    
    public JLang(String lang_string) {
        this(lang_string, null);
    }
    
    public JLang(String lang_string, String name) {
        this.name = name;
        StaticStandard.setLang(this);
        setLang(lang_string);
    }
    
    public void setLang(String lang_string) {
        JLang.lang_string = lang_string;
        reloadLang();
        StaticStandard.log(String.format(lang_prop.getProperty("changed_language_to", "Changed lanugage to %s"), lang_string));
    }
    
    public void loadFileAsLang(File lang_file) {
        if(lang_file.exists() && lang_file.isFile()) {
            lang.clear();
            try {
                lang.load(new BufferedInputStream(new FileInputStream(lang_file)));
            } catch (Exception ex) {
                StaticStandard.logErr("Error while loading file \"" + lang_file.getAbsolutePath() + "\" as language: " + ex);
            }
        }
    }
    
    public String getLang() {
        return lang_string;
    }
    
    public void setFile(String file) {
        this.file = file;
    }
    
    public String getFile() {
        return file;
    }
    
    public void resetTimes() {
        times = 0;
    }
    
    public void setClassReference(Class class_ref) {
        this.class_ref = class_ref;
    }
    
    public Class getClassReference() {
        return class_ref;
    }
    
    private ArrayList<Language> getLanguages() {
        final ArrayList<Language> langs = new ArrayList<>();
        final ArrayList<String> lang_shorts = getAvailableLangShorts();
        for(String ls : lang_shorts) {
            try {
                ls = ls.toUpperCase();
                String language_name = getLang(ls.toLowerCase(), null);
                Language l = new Language(lang_string, ls, ((language_name == null || language_name.isEmpty()) ? null : language_name));
                langs.add(l);
            } catch (Exception ex) {
            }
        }
        return langs;
    }
    
    private Language getLanguage() {
        String language_name = getLang(lang_string.toLowerCase(), null);
        Language lang_ = new Language(lang_string, lang_string.toUpperCase(), ((language_name == null || language_name.isEmpty()) ? null : language_name));
        return lang_;
    }
    
    public boolean changeLanguage() {
        setLangProp();
        try {
            ArrayList<Language> langs = getLanguages();
            Object input = JOptionPane.showInputDialog(null, lang_prop.getProperty("choose_language", "Choose language") + ":", lang_prop.getProperty("choose_language", "Choose language"), JOptionPane.QUESTION_MESSAGE, null, langs.toArray(), getLanguage());
            if(input == null) {
                return false;
            }
            if(input instanceof Language) {
                //StaticStandard.logErr("INSTANCEOF");
                Language l = (Language) input;
                if(!l.getUnlocalized().equals(lang_string)) {
                    setLang(l.getUnlocalized());
                    //StaticStandard.logErr("INSTANCEOF AND SET");
                    return true;
                } else {
                    return false;
                }
            } else {
                //StaticStandard.logErr("NOT INSTANCEOF");
                if(!input.toString().equals(lang_string)) {
                    //StaticStandard.logErr("NOT INSTANCEOF AND SET");
                    setLang(input.toString().toUpperCase());
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error while changing language: " + ex);
            return false;
        }
    }
    
    public ArrayList<String> getAvailableLangResource() {
        ArrayList<String> output = new ArrayList<>();
        try {
            URI uri = JLang.class.getResource(file).toURI();
            FileSystem fileSystem = null;
            Path myPath;
            if(uri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
                myPath = fileSystem.getPath(file);
            } else {
                myPath = Paths.get(uri);
            }
            FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
                
                @Override
                public FileVisitResult visitFile(Path file_found, BasicFileAttributes attrs) throws IOException {
                    String path = file_found.toString();
                    if(path.contains(file.replaceAll(lang_separator, File.separator + File.separator))) {
                        path = path.substring(path.indexOf(file.replaceAll(lang_separator, File.separator + File.separator)) + file.replaceAll(lang_separator, File.separator + File.separator).length() + 1).replaceAll(File.separator + File.separator, lang_separator);
                    }
                    String temp = file + lang_separator + path;
                    output.add(temp);
                    return FileVisitResult.CONTINUE;
                }
                
            };
            try {
                Files.walkFileTree(myPath, fv);
            } catch (Exception ex) {
                StaticStandard.logErr("Error while walking through the language files: " + ex, ex);
            }
            if(fileSystem != null) {
                fileSystem.close();
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error while getting available langs: " + ex);
        }
        return output;
    }
    
    public ArrayList<String> getAvailableLangFilenames() {
        ArrayList<String> input = getAvailableLangResource();
        ArrayList<String> output = new ArrayList<>();
        for(String g : input) {
            while(g.contains(lang_separator)) {
                g = g.substring(g.indexOf(lang_separator) + lang_separator.length());
            }
            output.add(g);
        }
        return output;
    }
    
    public ArrayList<String> getAvailableLangShorts() {
        ArrayList<String> input = getAvailableLangFilenames();
        ArrayList<String> output = new ArrayList<>();
        for(String g : input) {
            if(g.startsWith(lang_start)) {
                g = g.substring(g.indexOf(lang_start) + lang_start.length());
            }
            if(g.endsWith(lang_extension)) {
                g = g.substring(0, g.indexOf(lang_extension));
            }
            output.add(g);
        }
        return output;
    }
    
    public void reloadLang() {
        setLangProp();
        if(file == null) {
            return;
        }
        lang.clear();
        try {
            lang.load((class_ref.getResourceAsStream(((file.endsWith(lang_separator)) ? file : file + lang_separator) + ((name != null && !name.equals("")) ? name + "_" : "") + lang_start + lang_string  + lang_extension)));
        } catch (Exception ex) {
            times++;
            if(times < maxTimes) {
                reloadLang();
                StaticStandard.logErr("Tried " + times + " times to load the language \"" + lang_string + "\"");
            } else if(times < (maxTimes * 2)) {
                setLang("EN");
                StaticStandard.logErr("Tried " + (times - maxTimes) + " times to load the language \"" + lang_string + "\"");
            }
        }
    }
    
    public String getLang(String key, String defaultValue) {
        return lang.getProperty(key, defaultValue);
    }
    
    public static String getLocalLanguageShort() {
        return Locale.getDefault().getLanguage().toUpperCase();
    }
    
    public void setLocaleLanguage() {
        setLang(Locale.getDefault().getLanguage().toUpperCase());
    }
    
    /**
     * Gets the actual language properties with the matching file
     * @return Properties with locale language if exists
     */
    private static void setLangProp() {
        Properties lang = new Properties();
        lang.clear();
        //String lang_string = Locale.getDefault().getLanguage().toUpperCase();
        try {
            lang.load((JLang.class.getResourceAsStream("/jaddon/jlang/lang_" + lang_string + ".txt")));
            System.out.println("Loaded Language: " + lang_string);
        } catch(Exception ex) {
            try {
                lang.load((JLang.class.getResourceAsStream("/jaddon/jlang/lang_EN.txt")));
                StaticStandard.logErr("Error while Loading Language, because it wasnt found");
            } catch(Exception ex2) {
                StaticStandard.logErr("Lanugage Error: " + ex2);
            }
        }
        lang_prop = lang;
    }
    
    public static Properties getLangProp() {
        return lang_prop;
    }

    public String getLangStart() {
        return lang_start;
    }

    public void setLangStart(String lang_start) {
        this.lang_start = lang_start;
    }

    public String getLangExtension() {
        return lang_extension;
    }

    public void setLangExtension(String lang_extension) {
        this.lang_extension = lang_extension;
    }

    @Override
    public boolean update() {
        if(!doUpdate) {
            return false;
        }
        StaticStandard.setLang(this);
        return true;
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
