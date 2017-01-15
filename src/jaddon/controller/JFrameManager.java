/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Paul
 */
public class JFrameManager extends JFrame implements Update {
    
    private HashMap<Integer, String> work_prefix = new HashMap<>();
    private HashMap<Integer, String> work_suffix = new HashMap<>();
    
    public static String TITLE_SPLITTER = " - ";
    
    private String name = "";
    private String version = "";
    
    private boolean isDoingUpdate = false;
    
    private boolean showVersion = true;
    private boolean showIDETag = true;
    
    private boolean doneIDE = false;
    private int IDE_pre_work_id = -1;
    
    public JFrameManager() {
        setTitle("");
        name = "";
        this.version = "";
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        updateTitle();
    }
    
    public JFrameManager(String title, String version) {
        setTitle(title);
        name = title;
        this.version = version;
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        updateTitle();
    }
    
    public void show(Component c) {
        updateTitle();
        pack();
        setVisible(true);
        setLocationRelativeTo(c);
    }
    
    @Override
    public boolean update() {
        if(!isDoingUpdate) {
            return false;
        }
        name = StaticStandard.getName();
        version = StaticStandard.getVersion();
        updateTitle();
        return true;
    }
    
    @Override
    public void setSize(Dimension dim) {
        setPreferredSize(dim);
        setMinimumSize(dim);
    }
    
    public int getLowestKey(HashMap<Integer, String> toTest) {
        int lowest = 0;
        boolean first = true;
        for(Integer i : toTest.keySet()) {
            if(first) {
                lowest = i;
                first = false;
                continue;
            }
            if(i < lowest) {
                lowest = i;
            }
        }
        if(first) {
            return 0;
        } else {
            lowest++;
        }
        return lowest;
    }
    
    public int addPreWork(String work_title) {
        int lowest = getLowestKey(work_prefix);
        work_prefix.put(lowest, work_title);
        updateTitle();
        return lowest;
    }
    
    public boolean delPreWork(int work) {
        if(!work_prefix.containsKey(work)) {
            return false;
        }
        work_prefix.remove(work);
        updateTitle();
        return true;
    }
    
    public int addWork(String work_title, boolean localize) {
        int lowest = getLowestKey(work_suffix);
        work_suffix.put(lowest, (localize) ? ((StaticStandard.getLang() != null) ? StaticStandard.getLang().getLang(work_title, work_title) : work_title) : work_title);
        updateTitle();
        return lowest;
    }
    
    public boolean delWork(int work) {
        if(!work_suffix.containsKey(work)) {
            return false;
        }
        work_suffix.remove(work);
        updateTitle();
        return true;
    }
    
    public boolean delWork(String work_title, boolean localize) {
        for(int key : work_suffix.keySet()) {
            String g = work_suffix.get(key);
            if(g.equals((localize) ? ((StaticStandard.getLang() != null) ? StaticStandard.getLang().getLang(work_title, work_title) : work_title) : work_title)) {
                work_suffix.remove(key);
            }
        }
        updateTitle();
        return true;
    }
    
    public void delLastWork() {
        work_suffix.remove(work_suffix.size() - 1);
        updateTitle();
    }
    
    public void updateTitle() {
        if(!doneIDE && StaticStandard.isIsIDE()) {
            doneIDE = true;
            IDE_pre_work_id = addPreWork("IDE");
            return;
        }
        if(!showIDETag && doneIDE) {
            delPreWork(IDE_pre_work_id);
        }
        String prefix = "";
        String suffix = "";
        if(!work_prefix.isEmpty()) {
            for(String pre : work_prefix.values()) {
                prefix += pre + TITLE_SPLITTER;
            }
        }
        if(!work_suffix.isEmpty()) {
            for(String suf : work_suffix.values()) {
                suffix += TITLE_SPLITTER + suf;
            }
        }
        setTitle(prefix + name + ((showVersion) ? " V" + version : " ") + suffix);
    }

    public HashMap<Integer, String> getWork_prefix() {
        return work_prefix;
    }

    public HashMap<Integer, String> getWork_suffix() {
        return work_suffix;
    }

    @Override
    public void setDoUpdate(boolean doUpdate) {
        isDoingUpdate = doUpdate;
    }

    @Override
    public boolean isDoingUpdate() {
        return isDoingUpdate;
    }

    public static String getTitleSplitter() {
        return TITLE_SPLITTER;
    }

    public static void setTitleSplitter(String TitleSplitter) {
        JFrameManager.TITLE_SPLITTER = TitleSplitter;
    }

    public boolean isShowingVersion() {
        return showVersion;
    }

    public void setShowVersion(boolean showVersion) {
        this.showVersion = showVersion;
        updateTitle();
    }

    public boolean isShowingIDETag() {
        return showIDETag;
    }

    public void setShowIDETag(boolean showIDETag) {
        this.showIDETag = showIDETag;
        updateTitle();
    }
    
    public void setIconImage(String path_intern) {
        try {
            setIconImage(ImageIO.read(JFrameManager.class.getResourceAsStream(path_intern)));
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading icon: " + ex, ex);
        }
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
        updateTitle();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
