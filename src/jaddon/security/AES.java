/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.security;

import jaddon.controller.StaticStandard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Paul
 */
public class AES {
    
    public static final String AES = "AES";
    
    public static final int SIZELIGHT = 256;
    public static final int SIZESTANDARD = 512;
    
    private SecretKeySpec key = null;
    private int keysize_algorithm = SIZESTANDARD;
    
    /**
     * Creates the empty aes object
     */
    public AES() {
        
    }
    
    /**
     * Creates the aes object with a key from the text
     * @param key String Generates a key from the text
     */
    public AES(String key) {
        generateKey(key);
    }
    
    /**
     * Creates the aes object with a random key from the keysize
     * @param keysize Integer Generates a random key with the size keysize
     */
    public AES(int keysize) {
        generateRandomKey(keysize);
    }
    
    /**
     * Creates the aes object with an aes key
     * @param key SecretKeySpec Sets the aes key
     */
    public AES(SecretKeySpec key) {
        this.key = key;
    }
    
    /**
     * Generates the aes randomly from a keysize
     * @param keysize Integer Length of the key
     * @return SecretKeySpec SHA-256 Key
     */
    public SecretKeySpec generateRandomKey(int keysize) {
        return generateKey(KeyGenerator.generateKey(keysize));
    }
    
    /**
     * Generates the aes key from a string
     * @param key String key from text
     * @return SecretKeySpec SHA-256 Key
     */
    public SecretKeySpec generateKey(String key) {
        try {
            this.key = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("SHA-" + keysize_algorithm).digest(key.getBytes("UTF-8")), 16), AES);
            return this.key;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while generating the aes key: " + ex, ex);
            return null;
        }
    }
    
    /**
     * Encrypts a message with the key
     * @param message String Message
     * @return byte Array Encrypted message
     */
    public byte[] encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            return encrypted;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while encrypting with aes: " + ex, ex);
            return null;
        }
    }
    
    /**
     * Encrypts a file (if it where one line)
     * @param fileToEncrypt File to encrypt
     * @param toSave File where the encrypted file will be saved
     * @param skey SecretKeySpec Key
     * @return File Encrypted file
     */
    public File encryptFile(File fileToEncrypt, File toSave, SecretKeySpec skey) {
        if(fileToEncrypt == null || toSave == null || !fileToEncrypt.exists() || !fileToEncrypt.isFile() || (toSave.exists() && toSave.isDirectory())) {
            StaticStandard.logErr("Error while encrypting file, something is wrong");
            return null;
        }
        boolean done = doCrypto(Cipher.ENCRYPT_MODE, skey, fileToEncrypt, toSave);
        if(done) {
            return toSave;
        } else {
            return null;
        }
    }
    
    /**
     * Encrypts a file (if it where one line)
     * @param fileToEncrypt File to encrypt
     * @param toSave File where the encrypted file will be saved
     * @return File Encrypted file
     */
    public File encryptFile(File fileToEncrypt, File toSave) {
        return encryptFile(fileToEncrypt, toSave, key);
    }
    
    /**
     * Decrypts a message with the key
     * @param encrypted byte Array Encrypted message
     * @return String Decrypted Message
     */
    public String decrypt(byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception ex) {
            StaticStandard.logErr("Error while decrypting with aes: " + ex, ex);
            return null;
        }
    }
    
    /**
     * Decrypts a file (if it where one line)
     * @param fileToDecrypt File to decrypt
     * @param toSave File where the decrypted file will be saved
     * @return File Decrypted file
     */
    public File decryptFile(File fileToDecrypt, File toSave) {
        return decryptFile(fileToDecrypt, toSave, key);
    }
    
    /**
     * Decrypts a file (if it where one line)
     * @param fileToDecrypt File to decrypt
     * @param toSave File where the decrypted file will be saved
     * @param skey SecretKeySpec Key
     * @return File Decrypted file
     */
    public File decryptFile(File fileToDecrypt, File toSave, SecretKeySpec skey) {
        if(fileToDecrypt == null || toSave == null || !fileToDecrypt.exists() || !fileToDecrypt.isFile() || (toSave.exists() && toSave.isDirectory())) {
            StaticStandard.logErr("Error while decrypting file, something is wrong");
            return null;
        }
        boolean done = doCrypto(Cipher.DECRYPT_MODE, skey, fileToDecrypt, toSave);
        if(done) {
            return toSave;
        } else {
            return null;
        }
    }

    /**
     * Returns the aes key
     * @return SecretKeySpec Key
     */
    public SecretKeySpec getKey() {
        return key;
    }

    /**
     * Sets the aes key
     * @param key SecretKeySpec Key
     */
    public void setKey(SecretKeySpec key) {
        this.key = key;
    }

    /**
     * Returns the used keysize for the key algorithm
     * @return Integer Keysize
     */
    public int getAlgorithmKeysize() {
        return keysize_algorithm;
    }

    /**
     * Sets the keysize to be used for the key algorithm
     * @param keysize_algorithm Integer Keysize
     */
    public void setAlgorithmKeysize(int keysize_algorithm) {
        this.keysize_algorithm = keysize_algorithm;
    }
    
    private static boolean doCrypto(int cipherMode, SecretKeySpec secretKey, File inputFile, File outputFile) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(cipherMode, secretKey);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while de/encrypting file: " + ex, ex);
            return false;
        }
    }
    
}
