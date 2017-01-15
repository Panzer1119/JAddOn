/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import jaddon.controller.StaticStandard;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Paul
 */
public class JUtils {
    
    public static JButton setActionListener(JButton button, ActionListener al) {
        if(button.getActionListeners().length > 0) {
            for(ActionListener g : button.getActionListeners()) {
                button.removeActionListener(g);
            }
        }
        button.addActionListener(al);
        return button;
    }
    
    public static Component setKeyListener(Component c, KeyListener kl) {
        if(c.getKeyListeners().length > 0) {
            for(KeyListener g : c.getKeyListeners()) {
                c.removeKeyListener(g);
            }
        }
        c.addKeyListener(kl);
        return c;
    }
    
    public static String getJarContainingFolder(Class aclass) throws Exception {
        CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

        File jarFile;

        if (codeSource.getLocation() != null) {
          jarFile = new File(codeSource.getLocation().toURI());
        }
        else {
          String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
          String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
          jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
          jarFile = new File(jarFilePath);
        }
        return jarFile.getParentFile().getAbsolutePath();
      }
    
    public static void unZip(File zip, File output) {
        byte[] buffer = new byte[1024];
        try {
            if(!output.exists()) {
                output.mkdirs();
            }
            if(!output.isDirectory()) {
                //System.err.println("\"" + output.getAbsolutePath() + "\" isnt a directory");
                return;
            }
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
            ZipEntry ze = zis.getNextEntry();
            while(ze != null) {
                if(ze.isDirectory()) {
                    ze = zis.getNextEntry();
                    continue;
                }
                String fileName = ze.getName();
                File newFile = new File(output.getAbsolutePath() + File.separator + fileName);
                //System.out.println("File unzip: " + newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    
    public static void zipFolder(final File folder, final File zipFile) throws IOException {
        zipFolder(folder, new FileOutputStream(zipFile));
    }

    public static void zipFolder(final File folder, final OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            processFolder(folder, zipOutputStream, folder.getPath().length() + 1);
        }
    }

    private static void processFolder(final File folder, final ZipOutputStream zipOutputStream, final int prefixLength) throws IOException {
        if (folder.isFile()) {
            final ZipEntry zipEntry = new ZipEntry(folder.getPath().substring(prefixLength));
            zipOutputStream.putNextEntry(zipEntry);
            try (FileInputStream inputStream = new FileInputStream(folder)) {
                IOUtils.copy(inputStream, zipOutputStream);
            }
            zipOutputStream.closeEntry();
        } else if (folder.isDirectory()) {
            for (final File file : folder.listFiles()) {
                if (file.isFile()) {
                    final ZipEntry zipEntry = new ZipEntry(file.getPath().substring(prefixLength));
                    zipOutputStream.putNextEntry(zipEntry);
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        IOUtils.copy(inputStream, zipOutputStream);
                    }
                    zipOutputStream.closeEntry();
                } else if (file.isDirectory()) {
                    processFolder(file, zipOutputStream, prefixLength);
                }
            }
        }
    }
    
    public static void removeLineStartsWith(File file, String... del) {
        if(del.length < 1) {
            return;
        }
        try {
            ArrayList<String> inhalt = new ArrayList<>();
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                boolean t = true;
                for(String g : del) {
                    if(line.startsWith(g)) {
                        t = false;
                        break;
                    }
                }
                if(t) {
                    inhalt.add(line);
                }
            }
            sc.close();
            sc = null;
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            for(String g : inhalt) {
                bw.write(g);
                bw.newLine();
            }
            bw.close();
            bw = null;
        } catch (Exception ex) {
            
        }
    }
    
    public static int countInString(String string, String toCount) {
        int i = 0;
        int ind = 0;
        while(string.indexOf(toCount, ind) != -1) {
            i++;
            ind = string.indexOf(toCount, ind) + 1;
        }
        return i;
    }
    
    public static int countLines(File file, String... ignore) {
        if(file == null || !file.exists() || file.isDirectory()) {
            return -1;
        }
        try {
            Scanner sc = new Scanner(file);
            int i = 0;
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                int no = 0;
                for(String g : ignore) {
                    if(line.startsWith(g)) {
                        no++;
                    }
                }
                if(no == 0) {
                    i++;
                }
            }
            sc.close();
            sc = null;
            return i;
        } catch (Exception ex) {
            return -1;
        }
    }
    
    public static void restart() {
        restart(null);
    }
    
    public static void restart(String[] args) {
        try {
            String arguments = "";
            if(args != null && args.length > 0) {
                for(String g : args) {
                    arguments += " " + g;
                }
            }
            Runtime.getRuntime().exec("java -jar " + getJARLocation().getAbsolutePath() + arguments);
            System.exit(0);
        } catch (Exception ex) {
        }
    }
    
    public static File getJARLocation() {
        File dir = new File(System.getProperty("user.dir"));
        String class_path = System.getProperty("java.class.path");
        if(!class_path.startsWith(dir.getAbsolutePath())) {
            class_path = dir.getAbsolutePath() + ((dir.getAbsolutePath().endsWith(File.separator)) ? "" : File.separator) + class_path;
        }
        File class_path_file = new File(class_path);
        return class_path_file;
    }
    
    public static boolean isExactSame(Object o1, Object o2) {
        return (o1 == o2) && (o1.equals(o2)) && (o2.equals(o1));
    }
    
    public static File saveResource(String name) throws IOException {
        return saveResource(name, true);
    }

    public static File saveResource(String name, boolean replace) throws IOException {
        return saveResource(new File("."), name, replace);
    }

    public static File saveResource(File outputDirectory, String name) throws IOException {
        return saveResource(outputDirectory, name, true);
    }

    public static File saveResource(File outputDirectory, String name, boolean replace) throws IOException {
        File out = new File(outputDirectory, name);
        if (!replace && out.exists()) {
            return out;
        }
        InputStream resource = JUtils.class.getResourceAsStream(name);
        if (resource == null) {
            throw new FileNotFoundException(name + " (resource not found)");
        }
        try(InputStream in = resource; OutputStream writer = new BufferedOutputStream(new FileOutputStream(out))) {
            byte[] buffer = new byte[1024 * 4];
            int length;
            while((length = in.read(buffer)) >= 0) {
                writer.write(buffer, 0, length);
            }
        }
        return out;
    }
    
    public static void printArrayList(ArrayList<?> array) {
        for(Object o : array) {
            StaticStandard.log(o);
        }
    }
    
    public static String[] getNamesOfFiles(File[] files) {
        String[] ausgabe = new String[files.length];
        int i = 0;
        for(File f : files) {
            ausgabe[i] = f.getName();
            i++;
        }
        return ausgabe;
    }
    
    public static String[] getNamesOfFiles(ArrayList<File> files) {
        String[] ausgabe = new String[files.size()];
        int i = 0;
        for(File f : files) {
            ausgabe[i] = f.getName();
            i++;
        }
        return ausgabe;
    }
    
    public static File[] getFilesOfNames(String[] names, File[] files) {
        ArrayList<File> ausgabe_vor = new ArrayList<>();
        for(File f : files) {
            boolean temp = false;
            for(String g : names) {
                if(g.equals(f.getName())) {
                    temp = true;
                    break;
                }
            }
            if(temp) {
                ausgabe_vor.add(f);
            }
        }
        File[] ausgabe = new File[ausgabe_vor.size()];
        for(int i = 0; i < ausgabe.length; i++) {
            ausgabe[i] = ausgabe_vor.get(i);
        }
        return ausgabe;
    }
    
    public static ArrayList<File> getFilesOfNames(String[] names, ArrayList<File> files) {
        ArrayList<File> ausgabe = new ArrayList<>();
        for(File f : files) {
            boolean temp = false;
            for(String g : names) {
                if(g.equals(f.getName())) {
                    temp = true;
                    break;
                }
            }
            if(temp) {
                ausgabe.add(f);
            }
        }
        return ausgabe;
    }
    
    public static File getFileOutOfName(String name, File[] files) {
        for(File f : files) {
            if(f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }
    
    public static File getFileOutOfName(String name, ArrayList<File> files) {
        for(File f : files) {
            if(f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }
    
    public static String[] getNamesWithOutExtensionOufOfFiles(File[] files) {
        String[] ausgabe = new String[files.length];
        int i = 0;
        for(File f : files) {
            ausgabe[i] = FilenameUtils.getBaseName(f.getName());
            i++;
        }
        return ausgabe;
    }
    
    public static String[] getNamesWithOutExtensionOufOfFiles(ArrayList<File> files) {
        String[] ausgabe = new String[files.size()];
        int i = 0;
        for(File f : files) {
            ausgabe[i] = FilenameUtils.getBaseName(f.getName());
            i++;
        }
        return ausgabe;
    }
    
    public static String[] getNamesWithOutExtensionOufOfNames(String[] names) {
        String[] ausgabe = new String[names.length];
        int i = 0;
        for(String g : names) {
            ausgabe[i] = FilenameUtils.getBaseName(g);
            i++;
        }
        return ausgabe;
    }
    
    public static String[] getNamesWithOutExtensionOufOfNames(ArrayList<String> names) {
        String[] ausgabe = new String[names.size()];
        int i = 0;
        for(String g : names) {
            ausgabe[i] = FilenameUtils.getBaseName(g);
            i++;
        }
        return ausgabe;
    }
    
    public static String[] getNamesWithOnlyExtensionOufOfNames(String[] names) {
        String[] ausgabe = new String[names.length];
        int i = 0;
        for(String g : names) {
            ausgabe[i] = FilenameUtils.getExtension(g);
            i++;
        }
        return ausgabe;
    }
    
    public static String[] getNamesWithOnlyExtensionOufOfNames(ArrayList<String> names) {
        String[] ausgabe = new String[names.size()];
        int i = 0;
        for(String g : names) {
            ausgabe[i] = FilenameUtils.getExtension(g);
            i++;
        }
        return ausgabe;
    }
    
    public static boolean arrayCharContains(char f, char[] array) {
        for(char c : array) {
            if(c == f) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean arrayStringContains(String t, String[] array) {
        for(String g : array) {
            if(g.equals(t)) {
                return true;
            }
        }
        return false;
    }
    
    public static String[] addToStringArray(String toAdd, String[] array) {
        String[] old = array;
        array = new String[array.length + 1];
        System.arraycopy(old, 0, array, 0, old.length);
        array[array.length - 1] = toAdd;
        return array;
    }
    
    public static String[] subFromStringArray(String toRemove, String[] array) {
        if(!arrayStringContains(toRemove, array)) {
            return array;
        }
        String[] old = array;
        array = new String[array.length - 1];
        int z = 0;
        for(int i = 0; i < array.length; i++) {
            if(!old[i].equals(toRemove)) {
                array[z] = old[i];
                z++;
            }
        }
        return array;
    }
    
    public static String[] getInvertedStringArray(String[] weg, String[] array) {
        ArrayList<String> ausgabe_vor = new ArrayList<>();
        for(String g : array) {
            boolean temp = true;
            for(String t : weg) {
                if(g.equals(t)) {
                    temp = false;
                    break;
                }
            }
            if(temp) {
                ausgabe_vor.add(g);
            }
        }
        String[] ausgabe = new String[ausgabe_vor.size()];
        for(int i = 0; i < ausgabe.length; i++) {
            ausgabe[i] = ausgabe_vor.get(i);
        }
        return ausgabe;
    }
    
    public static ArrayList<ArrayList<String>> parseInputStreamToArrayList(InputStream is, String splitter, String... ignore) {
        ArrayList<ArrayList<String>> ausgabe = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while((line = br.readLine()) != null) {
                try {
                    boolean not = true;
                    for(String g : ignore) {
                        if(line.startsWith(g)) {
                            not = false;
                            break;
                        }
                    }
                    if(not) {
                        String[] split = line.split(splitter);
                        ArrayList<String> temp = new ArrayList<>();
                        for(String g : split) {
                            temp.add(g);
                        }
                        ausgabe.add(temp);
                    }
                } catch (Exception ex) {
                    System.err.println("Error while splitting string: " + ex);
                }
            }
            br.close();
            br = null;
            return ausgabe;
        } catch (Exception ex) {
            System.err.println("Error while parsing inputstream: " + ex);
            return null;
        }
    }
    
    public static ArrayList<ArrayList<String>> parseResourceToArrayList(String resource, String splitter, String... ignore) {
        ArrayList<ArrayList<String>> ausgabe = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(JUtils.class.getResourceAsStream(resource)));
            String line = "";
            while((line = br.readLine()) != null) {
                try {
                    boolean not = true;
                    for(String g : ignore) {
                        if(line.startsWith(g)) {
                            not = false;
                            break;
                        }
                    }
                    if(not) {
                        String[] split = line.split(splitter);
                        ArrayList<String> temp = new ArrayList<>();
                        for(String g : split) {
                            temp.add(g);
                        }
                        ausgabe.add(temp);
                    }
                } catch (Exception ex) {
                    System.err.println("Error while splitting string: " + ex);
                }
            }
            br.close();
            br = null;
            return ausgabe;
        } catch (Exception ex) {
            System.err.println("Error while parsing resource \"" + resource + "\": " + ex);
            return null;
        }
    }
    
    public static ArrayList<ArrayList<String>> parseFileToArrayList(File file, String splitter, String... ignore) {
        if(file == null || !file.isFile() || !file.exists()) {
            return null;
        }
        ArrayList<ArrayList<String>> ausgabe = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                try {
                    String line = sc.nextLine();
                    boolean not = true;
                    for(String g : ignore) {
                        if(line.startsWith(g)) {
                            not = false;
                            break;
                        }
                    }
                    if(not) {
                        String[] split = line.split(splitter);
                        ArrayList<String> temp = new ArrayList<>();
                        for(String g : split) {
                            temp.add(g);
                        }
                        ausgabe.add(temp);
                    }
                } catch (Exception ex) {
                    System.err.println("Error while splitting string: " + ex);
                }
            }
            sc.close();
            sc = null;
            return ausgabe;
        } catch (Exception ex) {
            System.err.println("Error while parsing file \"" + file.getAbsolutePath() + "\": " + ex);
            return null;
        }
    }
    
    public static ArrayList<String> fileToArrayList(File file) {
        if(file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        try {
            ArrayList<String> output = new ArrayList<>();
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                output.add(sc.nextLine());
            }
            sc.close();
            sc = null;
            return output;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while putting file to arraylist: " + ex);
            return null;
        }
    }
    
    public static String readFileToOneLine(File file) {
        String output = "";
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                output += sc.nextLine().replaceAll("\n", "");
            }
            sc.close();
            sc = null;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while reading a file to one line: " + ex);
        }
        return output;
    }
    
    public static void addTextToFile(String text, File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(text);
            bw.newLine();
            bw.close();
            bw = null;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while adding text to file: " + ex);
        }
    }
    
    public static long getLengthOfFileOrFolder(File fileOrFolder) {
        if(fileOrFolder == null) {
            return 0;
        }
        long length = -1;
        if(fileOrFolder.exists()) {
            if(fileOrFolder.isFile()) {
                length = fileOrFolder.length();
            } else if(fileOrFolder.isDirectory()) {
                length = getLengthOfFolder(fileOrFolder);
            }
        }
        return length;
    }
    
    private static long getLengthOfFolder(File folder) {
        if(folder == null) {
            return 0;
        }
        long length = 0;
        for(File f : folder.listFiles()) {
            if(f.isFile()) {
                length += f.length();
            } else if(f.isDirectory()) {
                length += getLengthOfFolder(f);
            }
        }
        return length;
    }
    
    public static void writeArrayListToFile(ArrayList<String> text, File file, boolean append) {
        if(file == null || !file.exists() || !file.isFile()) {
            return;
        }
        try {
            FileWriter fw = new FileWriter(file, append);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String g : text) {
                bw.write(g);
                bw.newLine();
            }
            bw.close();
            fw.close();
            bw = null;
            fw = null;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while adding text to file: " + ex);
        }
    }
    
    public static String[] stringToStringArray(String string) {
        string = string.substring(1, string.length() - 1);
        String[] array = string.split(", ");
        return array;
    }
    
    public static boolean speak(String text) {
        if(Desktop.isDesktopSupported()) {
            try {
                final String format = "dim fname\n" +
                "set objVoice=createobject(\"sapi.spvoice\")\n" +
                "objvoice.speak (\"%s\")";
                String temp = String.format(format, text);
                File file_temp = File.createTempFile("" + ((int) (Math.random() * 10000)), ".vbs");
                FileWriter fw = new FileWriter(file_temp);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(temp);
                bw.newLine();
                bw.close();
                fw.close();
                bw = null;
                fw = null;
                Desktop.getDesktop().open(file_temp);
                Thread.sleep(1000);
                file_temp.delete();
                return true;
            } catch (Exception ex) {
                StaticStandard.logErr("Error while speaking: " + ex, ex);
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * Returns the closest number that can be divided by the given number
     * @param number Integer Number to check
     * @param numberToBe Integer Number which the returned number should be divideable by
     * @param higherNumbers Boolean True returns the higher number of two if they are equal, False returns the lower of two numbers
     * @return Integer Closest number to a number which is dividable by the numberToBe
     */
    public static int getClosestNumber(int number, int numberToBe, boolean higherNumbers) {
        if(numberToBe < 0) {
            return -1;
        }
        if(number % numberToBe == 0) {
            return number;
        }
        int high = -1;
        int low = -1;
        for(int i = 1; i < (numberToBe / 2) + 1; i++) {
            int temp = number + i;
            if(temp % numberToBe == 0) {
                high = i;
                break;
            }
        }
        for(int i = 1; i < (numberToBe / 2) + 1; i++) {
            int temp = number - i;
            if(temp % numberToBe == 0) {
                low = i;
                break;
            }
        }
        if(high != -1 && low != -1 && high != low) {
            return ((high > low) ? number - low : number + high);
        } else if(high == low) {
            return ((higherNumbers) ? number + high : number - low);
        }
        if(high != -1) {
            return number + high;
        }
        if(low != -1) {
            return number - low;
        }
        return -1;
    }
    
    public static String formatDuration(Duration duration) {
        return formatDuration(duration, "%d:%02d:%02d");
    }
    
    public static String formatDuration(Duration duration, String format) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(format, absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
        return ((seconds < 0) ? "-" + positive : positive);
    }
    
    public static void setIcon(JFrame frame, String icon) {
        try {
            frame.setIconImage(ImageIO.read(JUtils.class.getResourceAsStream(icon)));
        } catch (IOException ex) {
            StaticStandard.logErr(ex, false);
        }
    }
    
}
