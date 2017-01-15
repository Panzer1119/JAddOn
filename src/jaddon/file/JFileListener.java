/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.file;

import jaddon.controller.StaticStandard;
import jaddon.jlog.LogEntry;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.Timer;

/**
 *
 * @author Paul
 */
public abstract class JFileListener {
    
    private File file = null;
    private HashMap<File, ArrayList<String>> file_data = new HashMap<>();
    private Timer timer = null;
    
    public JFileListener(File file) {
        setFile(file);
    }
    
    private void reloadFile() {
        if(file == null) {
            return;
        }
        HashMap<File, ArrayList<String>> file_data_temp = new HashMap<>();
        updateFile(file_data_temp, false);
        if(file.exists()) {
            checkFiles(file, file_data_temp);
        } else {
            try {
                fileRemoved(file);
            } catch (Exception ex) {
                StaticStandard.logErr("Error while executing \"fileRemoved\": " + ex);
            }
        }
        file_data = file_data_temp;
    }
    
    private void checkFiles(File file, HashMap<File, ArrayList<String>> file_data_temp) {
        if(file == null) {
            return;
        }
        if(!file.exists()) {
            for(File f : file_data.keySet()) {
                if(f.getAbsolutePath().equals(file.getAbsolutePath())) {
                    try {
                        fileRemoved(file);
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while executing \"fileRemoved\": " + ex);
                    }
                    return;
                }
            }
        }
        if(file.isDirectory()) {
            boolean isThere = false;
            for(File f : file_data_temp.keySet()) {
                if(f.isFile()) {
                    continue;
                }
                if(f.getAbsolutePath().equals(file.getAbsolutePath())) {
                    isThere = true;
                    break;
                }
            }
            if(!isThere) {
                try {
                    fileRemoved(file);
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while executing \"fileRemoved\": " + ex);
                }
            }
            for(File f : file.listFiles()) {
                checkFiles(f, file_data_temp);
            }
        } else if(file.isFile()) {
            boolean isThere = false;
            File t = null;
            for(File f : file_data_temp.keySet()) {
                if(f.isDirectory()) {
                    continue;
                }
                if(f.getAbsolutePath().equals(file.getAbsolutePath())) {
                    t = f;
                    isThere = true;
                } else {
                    try {
                        fileCreated(f);
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while executing \"fileCreated\": " + ex);
                    }
                }
            }
            if(!isThere) {
                try {
                    fileRemoved(file);
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while executing \"fileRemoved\": " + ex);
                }
            } else {
                /*ArrayList<String> data_temp_file_old = file_data.get(file);
                ArrayList<String> data_temp_file_new = file_data_temp.get(file);
                if(!data_temp_file_old.equals(data_temp_file_new)) {
                    try {
                        fileUpdated(file);
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while executing \"fileUpdated\": " + ex);
                    }
                }*/
                if(t.lastModified() != file.lastModified()) {
                    try {
                        fileUpdated(file);
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while executing \"fileUpdated\": " + ex);
                    }
                }
            }
        }
    }
    
    private void updateFile(HashMap<File, ArrayList<String>> file_data_temp, boolean reload) {
        file_data_temp.clear();
        if(file != null) {
            if(!file.exists()) {
                file_data_temp.put(file, null);
            } else {
                if(file.isFile()) {
                    ArrayList<String> data = new ArrayList<>();/*
                    if(file.exists()) {
                        try (Scanner scanner = new Scanner(file)) {
                            while(scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                data.add(line);
                            }
                            scanner.close();
                        } catch (Exception ex) {
                            StaticStandard.logErr("Error while scanning file: " + ex);
                        }
                    }*/
                    file_data_temp.put(file, data);
                } else if(file.isDirectory()) {
                    addFolder(file, file_data_temp);
                }
            }
        }
        if(reload) {
            reloadFile();
        }
    }
    
    private void addFolder(File folder, HashMap<File, ArrayList<String>> file_data_temp) {
        if(folder == null) {
            return;
        }
        file_data_temp.put(folder, null);
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                if(f.isFile()) {
                    ArrayList<String> data = new ArrayList<>();/*
                    if(f.exists()) {
                        try (Scanner scanner = new Scanner(f)) {
                            while(scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                data.add(line);
                            }
                            scanner.close();
                        } catch (Exception ex) {
                            StaticStandard.logErr("Error while scanning file: " + ex);
                        }
                    }*/
                    file_data_temp.put(f, data);
                } else if(f.isDirectory()) {
                    addFolder(f, file_data_temp);
                }
            }
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        updateFile(file_data, true);
    }
    
    public void setTimer(int time) {
        timer = new Timer(time, new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadFile();
                //StaticStandard.log("Test", LogEntry.LEVELLOW);
            }
            
        });
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }
    
    public abstract void fileUpdated(File file);
    public abstract void fileRemoved(File file);
    public abstract void fileCreated(File file);
    
}
