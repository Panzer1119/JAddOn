/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author mkyong (http://www.mkyong.com/java/how-to-compress-files-in-zip-format/)
 */
public class JAppZip {
    ArrayList<String> fileList;
    private static File OUTPUT_ZIP_FILE = new File("C:\\MyFile.zip");
    private static File SOURCE_FOLDER = new File("C:\\testzip");
	
    public JAppZip() {
	fileList = new ArrayList<String>();
    }
    
    /**
     * Example
     * @param args nothing
     */
    public static void main(String[] args) {
    	JAppZip appZip = new JAppZip();
    	appZip.generateFileList(SOURCE_FOLDER);
    	appZip.zipIt();
    }
    
    public static void setFiles(File input, File output) {
        SOURCE_FOLDER = input;
        OUTPUT_ZIP_FILE = output;
    }
    
    /**
     * Zip it
     */
    public void zipIt(){
        byte[] buffer = new byte[1024];
        try{
            FileOutputStream fos = new FileOutputStream(OUTPUT_ZIP_FILE);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //System.out.println("Output to Zip : " + zipFile);
            for(String file : this.fileList){
                //System.out.println("File Added : " + file);
                ZipEntry ze= new ZipEntry(file);
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(SOURCE_FOLDER.getAbsolutePath() + File.separator + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
           }
           zos.closeEntry();
           zos.close();
           //System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();   
        }
    }
    
    /**
     * Traverse a directory and get all files,
     * and add the file into fileList  
     * @param node file or directory
     */
    public void generateFileList(File node) {
	if(node.isFile()){
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        } else if(node.isDirectory()){
            for(String filename : node.list()){
                generateFileList(new File(node, filename));
            }
	}
    }
    
    /**
     * Clears the file list
     */
    public void reset() {
        fileList.clear();
    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file){
    	return file.substring(SOURCE_FOLDER.getPath().length()+1, file.length());
    }
    
}
