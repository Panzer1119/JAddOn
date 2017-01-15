/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Paul
 */
public class UnZipIt {
     public static void unZip(File zip, File output) {
        byte[] buffer = new byte[1024];
        try {
            if(!output.exists()) {
                output.mkdirs();
            }
            if(!output.isDirectory()) {
                System.err.println("\"" + output.getAbsolutePath() + "\" isnt a directory");
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
                System.out.println("File unzip: " + newFile.getAbsolutePath());
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
}
