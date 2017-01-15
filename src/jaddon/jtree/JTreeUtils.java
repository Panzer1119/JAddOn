/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jtree;

import jaddon.controller.StaticStandard;
import static jaddon.utils.JUtils.stringToStringArray;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Paul
 */
public class JTreeUtils {

    public static final String CATEGORYDEFAULT = "default";

    /**
     * Updates a JTree
     *
     * @param folder File Folder to display on jtree
     * @param tree JTree Tree to get updated
     * @return JTree Updated jtree
     */
    public static JTree updateJTree(File folder, JTree tree) {
        DefaultMutableTreeNode top = getDefaultMutableTreeNodeOfFolder(folder);
        DefaultTreeModel model = new DefaultTreeModel(top);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        model.reload(root);
        tree.setModel(model);
        return tree;
    }

    /**
     * Returns a defaultmutabletreenode with the directory structure of a folder
     *
     * @param folder File Folder which gets displayed
     * @return DefaultMutableTreeNode DefaultMutableTreeNode with the directory
     * structure
     */
    public static DefaultMutableTreeNode getDefaultMutableTreeNodeOfFolder(File folder) {
        return getDefaultMutableTreeNode(folder, null);
    }

    private static DefaultMutableTreeNode getDefaultMutableTreeNode(File folder, DefaultMutableTreeNode top) {
        DefaultMutableTreeNode top_new = new DefaultMutableTreeNode(folder.getName());
        if (top != null) {
            top.add(top_new);
        } else {
            top = top_new;
        }
        try {
            for (File f : folder.listFiles()) {
                if (f.isDirectory()) {
                    getDefaultMutableTreeNode(f, top_new);
                } else if (f.isFile()) {
                    top_new.add(new DefaultMutableTreeNode(f.getName()));
                }
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error: " + ex);
        }
        return top;
    }
    
    public static JTree updateJTree(JTree tree, TreeArrayList<?> data, ExpandedPaths ep) {
        ArrayList<String> expanded_paths = null;
        TreePath[] paths = null;
        if(ep != null) {
            expanded_paths = ep.getExpandedPaths(tree);
            paths = tree.getSelectionPaths();
        }
        DefaultMutableTreeNode top = getDefaultMutableTreeNodeOfHashMap(data);
        DefaultTreeModel model = new DefaultTreeModel(top);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        model.reload(root);
        tree.setModel(model);
        if(ep != null) {
            ep.expandPaths(tree, expanded_paths);
            tree.setSelectionPaths(paths);
        }
        return tree;
    }
    
    public static DefaultMutableTreeNode getDefaultMutableTreeNodeOfHashMap(TreeArrayList<?> data) {
        return getDefaultMutableTreeNodeOfHashMap(data, null);
    }
    
    public static DefaultMutableTreeNode getDefaultMutableTreeNodeOfHashMap(TreeArrayList<?> data, DefaultMutableTreeNode top) {
        if(data == null) {
            return top;
        }
        DefaultMutableTreeNode top_new = new DefaultMutableTreeNode(data.object);
        if(top != null) {
            top.add(top_new);
        } else {
            top = top_new;
        }
        try {
            for(Object o : data) {
                if(o instanceof TreeArrayList) {
                    getDefaultMutableTreeNodeOfHashMap((TreeArrayList) o, top_new);
                } else {
                    top_new.add(new DefaultMutableTreeNode(o));
                }
            }
        } catch (Exception ex) {
        }
        return top;
    }

    public static ArrayList<String> getExpandedPaths(JTree tree) {
        ArrayList<String> paths_expanded = new ArrayList<>();
        for(int i = 0; i < tree.getRowCount(); i++) {
            TreePath tp = tree.getPathForRow(i);
            if(tree.isExpanded(i)) {
                paths_expanded.add(tp.toString());
            }
        }
        return paths_expanded;
    }

    public static void expandPaths(JTree tree, ArrayList<String> paths_expanded) {
        for(int i = 0; i < tree.getRowCount(); i++) {
            TreePath tp = tree.getPathForRow(i);
            if(paths_expanded.contains(tp.toString())) {
                tree.expandRow(i);
            }
        }
    }

    private static void editCategory(Component frame, String path, File folder_categories) {
        String input = JOptionPane.showInputDialog(frame, StaticStandard.getLang().getLang("new_category_name", "New category name") + ":", path);
        if (input != null && !input.isEmpty() && !input.equals(path)) {
            File folder_old = getFolderFromCategory(path, folder_categories);
            input = input.replaceAll(File.separator + File.separator, ".");
            File folder = getFolderFromCategory(input, folder_categories);
            folder_old.renameTo(folder);
            StaticStandard.log("Edited category: \"" + path + "\" to \"" + input + "\"");
            folder.mkdirs();
        }
    }

    private static void addCategory(Component frame, String path, File folder_categories) {
        String input = JOptionPane.showInputDialog(frame, StaticStandard.getLang().getLang("new_category_name", "New category name") + ":", path);
        if (input != null && !input.isEmpty()) {
            input = input.replaceAll(File.separator + File.separator, ".");
            File folder = getFolderFromCategory(input, folder_categories);
            StaticStandard.log("Added category: \"" + input + "\"");
            folder.mkdirs();
        }
    }

    private static Object addObject(String path) {
        /*
        BackupTask backuptask = new BackupTask(path);
        BackupTaskProperties backuptaskproperties = new BackupTaskProperties(backuptask);
        backuptask = backuptaskproperties.showDialog(frame);
        if(backuptaskproperties.getResult() == BackupTaskProperties.CANCEL_OPTION) {
            backuptask.delete();
        }
        return backuptask;
         */
        return null;
    }

    /**
     * Node == The thing that can be expanded Point == An object in the tree
     *
     * @param tree JTree Tree
     * @param data ArrayList Object Data
     * @return Boolean
     */
    public static boolean isSelectedPathNode(JTree tree, ArrayList<Object> data) {
        TreePath tp = tree.getSelectionPath();
        Object object = null;
        String path = "";
        try {
            path = getPathOfTreePath(tp);
        } catch (Exception ex) {
            object = null;
        }
        return (tp != null) && (object == null);
    }

    private void removeBackupTask() {
        /*
        if (backuptasks.isEmpty()) {
            return;
        }
        BackupTask backuptask = null;
        int pos = -1;
        TreePath tp = tree_backuptasks.getSelectionPath();
        String path = "";
        try {
            path = getPathOfTreePath(tp);
            pos = getObjectPos(path);
            backuptask = backuptasks.get(pos);
        } catch (Exception ex) {
            backuptask = null;
            pos = -1;
        }
        boolean isNode = (tp != null) && (backuptask == null);
        boolean isPoint = (tp != null) && (backuptask != null);
        ArrayList<String> paths = new ArrayList<>();
        */
        /*
        for(BackupTask bt : backuptasks) {
            paths.add(bt.getCompletePathName());
        }
         */
 /*
        Object obj = JOptionPane.showInputDialog(frame, StaticStandard.getLang().getLang("remove_backup_task", "Remove Backup Task"), StaticStandard.getLang().getLang("remove_backup_task", "Remove Backup Task"), JOptionPane.QUESTION_MESSAGE, null, paths.toArray(), (isPoint) ? backuptask.getCompletePathName() : paths.get(0));
        if(obj != null) {
            backuptask = getBackupTask((String) obj);
            backuptask.delete();
            reloadBackupTasks();
        }
         */
    }

    public static String getPathOfTreePath(TreePath tp) {
        String[] paths = stringToStringArray(tp.toString());
        String path_string = "";
        for (String g : paths) {
            path_string += "." + g;
        }
        path_string = path_string.replaceFirst("\\.", "");
        return path_string;
    }

    @Deprecated
    public static DefaultMutableTreeNode addObject(File folder, DefaultMutableTreeNode top, File folder_categories) {
        String name_category = "";
        try {
            name_category = folder.getAbsolutePath().substring(folder_categories.getAbsolutePath().length() + 1).replaceAll(File.separator + File.separator, ".");
        } catch (Exception ex) {
            //StaticStandard.logErr("Error: " + ex);
        }
        //categories.add(name_category);
        DefaultMutableTreeNode top_new = new DefaultMutableTreeNode(getCategoryName(name_category));
        if (top != null) {
            top.add(top_new);
        } else {
            top = top_new;
        }
        /*
        try {
            for(File f : folder.listFiles()) {
                if(f.isDirectory()) {
                    addBackupTasks(f, top_new);
                } else if(f.isFile() && FilenameUtils.getExtension(f.getAbsolutePath()).equals(FILEEXTENSIONBACKUPTASK)) {
                    try {
                        //BackupTask backuptask = (BackupTask) ObjectManager.loadObjectFromFile(f);
                        BackupTask backuptask = BackupTask.loadBackupTaskFromFile(f);
                        backuptask.setCategory(name_category);
                        backuptask.setBackupTaskFile(f);
                        backuptasks.add(backuptask);
                        top_new.add(new DefaultMutableTreeNode(backuptask));
                        //StaticStandard.log("BackupTask found: " + backuptask);
                    } catch (Exception ex) {
                        StaticStandard.logErr("Error while reading backuptask file: " + ex);
                    }
                }
            }
        } catch (Exception ex) {
            StaticStandard.logErr("Error while adding backup tasks: " + ex);
        }
         */
        return top;
    }

    public static String getCategoryName(String category) {
        if (category.equals("")) {
            return CATEGORYDEFAULT;
        } else {
            while (category.contains(".")) {
                category = category.substring(category.indexOf(".") + 1);
            }
            return category;
        }
    }

    public static File getFolderFromCategory(String category, File folder_categories) {
        if (category.startsWith(CATEGORYDEFAULT) && !category.equals(CATEGORYDEFAULT)) {
            category = category.substring(CATEGORYDEFAULT.length() + 1);
        }
        if (category.equals(CATEGORYDEFAULT)) {
            return folder_categories;
        } else {
            File folder = new File(folder_categories.getAbsolutePath() + File.separator + category.replaceAll("\\.", File.separator + File.separator));
            return folder;
        }
    }

    public static File getFileFromTreePath(String path, boolean isFile, File folder_categories, String extension) {
        String filename = path;
        while (filename.contains(".")) {
            filename = filename.substring(filename.indexOf(".") + 1);
        }
        String category = path.substring(0, path.length() - filename.length() - 1);
        File folder = getFolderFromCategory(category, folder_categories);
        String extra = "";
        if (isFile) {
            extra = (extension.isEmpty() ? "" : "." + extension);
        }
        File file = new File(folder.getAbsolutePath() + File.separator + filename + extra);
        return file;
    }

    private static Object getObject(String path) {
        if (path.startsWith(CATEGORYDEFAULT) && path.lastIndexOf(".") > (CATEGORYDEFAULT.length() + 1)) {
            path = path.substring(CATEGORYDEFAULT.length() + 1);
        }
        /*
        for(Object o : objects) {
            if((backuptask.getCategoryForComboBox()+ "." + backuptask.getName()).equals(path)) {
                return backuptask;
            }
        }
         */
        return null;
    }

    private static int getObjectPos(String path, ArrayList<Object> data) {
        if (path.startsWith(CATEGORYDEFAULT) && path.lastIndexOf(".") > (CATEGORYDEFAULT.length() + 1)) {
            path = path.substring(CATEGORYDEFAULT.length() + 1);
        }
        int i = 0;
        for(Object o : data) {
            String path_temp = "";//(backuptask.getCategoryForComboBox()+ "." + backuptask.getName());
            if(path_temp.equals(path)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}
