/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.security;

import jaddon.controller.StaticStandard;
import jaddon.objects.ObjectManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

/**
 *
 * @author Paul
 */
public class RSA {
    
    public static final int SIZEWORST = 512;
    public static final int SIZEUNDERPOWERED = 1024;
    public static final int SIZESTANDARD = 2048;
    public static final int SIZEOVERPOWERED = 4096;
    public static final int SIZEINCREDIBLE = 8192;
    
    public static final String RSA = "RSA";
    
    private KeyPair key = null;
    private PublicKey key_public = null;
    private PrivateKey key_private = null;
    private int keysize = 0;
    
    private int maximum_message_length = -1;
    
    /**
     * Constructs the RSA crypter with standard key size
     */
    public RSA() {
        setKeysize(SIZESTANDARD);
    }
    
    /**
     * Constructs the RSA crypter with a keysize
     * @param keysize Integer Keysize
     */
    public RSA(int keysize) {
        if(!isKeysizeValid(keysize)) {
            StaticStandard.logErr("Keysize is not a power of 2");
            return;
        }
        setKeysize(keysize);
    }

    /**
     * Returns the keysize
     * @return Integer Keysize
     */
    public int getKeysize() {
        return keysize;
    }

    /**
     * Sets the keysize
     * @param keysize Integer Keysize
     */
    public void setKeysize(int keysize) {
        if(!isKeysizeValid(keysize)) {
            StaticStandard.logErr("Keysize is not a power of 2");
            return;
        }
        this.keysize = keysize;
        setMaximumMessageLength();
    }
    
    /**
     * Generates the keys with the keysize
     * @return Thread
     */
    public Thread generateKeys() {
        return generateKeys(keysize);
    }
    
    /**
     * Generates the keys with a keysize
     * @param keysize Integer Keysize
     * @return Thread
     */
    public Thread generateKeys(int keysize) {
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                if(!isKeysizeValid(keysize)) {
                    StaticStandard.logErr("Keysize is not a power of 2");
                    return;
                }
                StaticStandard.log("Starting generating " + keysize + " bits keypair...");
                try {
                    setKeysize(keysize);
                    final KeyPairGenerator keygen = KeyPairGenerator.getInstance(RSA);
                    keygen.initialize(keysize);
                    key = keygen.generateKeyPair();
                    key_public = key.getPublic();
                    key_private = key.getPrivate();
                    StaticStandard.log("Generated successfully " + keysize + " bits keypair");
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while generating " + keysize + " bits keypair: " + ex);
                }
            }
            
        });
        thread.start();
        return thread;
    }
    
    /**
     * Encrypts a message with the public key
     * @param message String Message to encrypt
     * @return byte Array Encrypted message
     */
    public byte[] encrypt(String message) {
        if(!isMessageLengthValid(message)) {
            StaticStandard.logErr("Message must not be longer than " + maximum_message_length + " bytes");
            return null;
        }
        return encrypt(message, key_public);
    }
    
    /**
     * Encrypts a message with a public key
     * @param message String Message to encrypt
     * @param pk PublicKey Key for the encryption
     * @return byte Array Encrypted message
     */
    public byte[] encrypt(String message, PublicKey pk) {
        if(!isMessageLengthValid(message)) {
            StaticStandard.logErr("Message must not be longer than " + maximum_message_length + " bytes");
            return null;
        }
        try {
            final Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte[] chiffrat = cipher.doFinal(message.getBytes());
            return chiffrat;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while encrypting message: " + ex);
            return null;
        }
    }
    
    /**
     * Encrypts a file line after line
     * @param fileToEncrypt File to encrypt
     * @param toSave File where the encrypted file will be saved
     * @return File Encrypted file
     */
    public File encryptFileLineForLine(File fileToEncrypt, File toSave) {
        return encryptFileLineForLine(fileToEncrypt, toSave, key_public);
    }
    
    /**
     * Encrypts a file line after line
     * @param fileToEncrypt File to encrypt
     * @param toSave File where the encrypted file will be saved
     * @param pk PublicKey PublicKey
     * @return File Encrypted file
     */
    public File encryptFileLineForLine(File fileToEncrypt, File toSave, PublicKey pk) {
        if(fileToEncrypt == null || toSave == null || !fileToEncrypt.exists() || !fileToEncrypt.isFile() || (toSave.exists() && toSave.isDirectory())) {
            StaticStandard.logErr("Error while encrypting file, something is wrong");
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToEncrypt));
            BufferedWriter bw = new BufferedWriter(new FileWriter(toSave, false));
            String line = "";
            while((line = br.readLine()) != null) {
                if(!isMessageLengthValid(line)) {
                    StaticStandard.logErr("Line must not be longer than " + maximum_message_length + " bytes");
                    continue;
                }
                try {
                    bw.write(new String(encrypt(line)));
                    bw.newLine();
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while encrypting file, while looping: " + ex);
                }
            }
            br.close();
            br = null;
            bw.close();
            bw = null;
            return toSave;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while encrypting file, something went wrong: " + ex);
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
        return encryptFile(fileToEncrypt, toSave, key_public);
    }
    
    /**
     * Encrypts a file (if it where one line)
     * @param fileToEncrypt File to encrypt
     * @param toSave File where the encrypted file will be saved
     * @param pk PublicKey PublicKey
     * @return File Encrypted file
     */
    public File encryptFile(File fileToEncrypt, File toSave, PublicKey pk) {
        if(fileToEncrypt == null || toSave == null || !fileToEncrypt.exists() || !fileToEncrypt.isFile() || (toSave.exists() && toSave.isDirectory())) {
            StaticStandard.logErr("Error while encrypting file, something is wrong");
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToEncrypt));
            String inhalt = "";
            String line = "";
            while((line = br.readLine()) != null) {
                inhalt += line;
            }
            br.close();
            br = null;
            BufferedWriter bw = new BufferedWriter(new FileWriter(toSave, false));
            if(!isMessageLengthValid(inhalt)) {
                StaticStandard.logErr("All lines must not be longer than " + maximum_message_length + " bytes");
                bw.close();
                bw = null;
                return null;
            }
            bw.write(new String(encrypt(inhalt)));
            bw.newLine();
            bw.close();
            bw = null;
            return toSave;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while encrypting file, something went wrong: " + ex);
            return null;
        }
    }
    
    /**
     * Decrypts a file line after line
     * @param fileToDecrypt File to decrypt
     * @param toSave File where the decrypted file will be saved
     * @return File Decrypted file
     */
    public File decryptFileLineForLine(File fileToDecrypt, File toSave) {
        return decryptFileLineForLine(fileToDecrypt, toSave, key_private);
    }
    
    /**
     * Decrypts a file line after line
     * @param fileToDecrypt File to decrypt
     * @param toSave File where the decrypted file will be saved
     * @param pk PublicKey PublicKey
     * @return File Decrypted file
     */
    public File decryptFileLineForLine(File fileToDecrypt, File toSave, PrivateKey pk) {
        if(fileToDecrypt == null || toSave == null || !fileToDecrypt.exists() || !fileToDecrypt.isFile() || (toSave.exists() && toSave.isDirectory())) {
            StaticStandard.logErr("Error while decrypting file, something is wrong");
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToDecrypt));
            BufferedWriter bw = new BufferedWriter(new FileWriter(toSave, false));
            String line = "";
            while((line = br.readLine()) != null) {
                if(!isMessageLengthValid(line)) {
                    StaticStandard.logErr("Chiffrat line must not be longer than " + maximum_message_length + " bytes");
                    continue;
                }
                try {
                    bw.write(decrypt(line.getBytes()));
                    bw.newLine();
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while decrypting file, while looping: " + ex);
                }
            }
            br.close();
            br = null;
            bw.close();
            bw = null;
            return toSave;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while decrypting file, something went wrong: " + ex);
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
        return decryptFile(fileToDecrypt, toSave, key_private);
    }
    
    /**
     * Decrypts a file (if it where one line)
     * @param fileToDecrypt File to decrypt
     * @param toSave File where the decrypted file will be saved
     * @param pk PublicKey PublicKey
     * @return File Decrypted file
     */
    public File decryptFile(File fileToDecrypt, File toSave, PrivateKey pk) {
        if(fileToDecrypt == null || toSave == null || !fileToDecrypt.exists() || !fileToDecrypt.isFile() || (toSave.exists() && toSave.isDirectory())) {
            StaticStandard.logErr("Error while decrypting file, something is wrong");
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToDecrypt));
            String inhalt = "";
            String line = "";
            while((line = br.readLine()) != null) {
                inhalt += line;
            }
            br.close();
            br = null;
            BufferedWriter bw = new BufferedWriter(new FileWriter(toSave, false));
            bw.write(decrypt(inhalt.getBytes()));
            if(!isMessageLengthValid(inhalt)) {
                StaticStandard.logErr("All chiffrat lines must not be longer than " + maximum_message_length + " bytes");
                bw.close();
                bw = null;
                return null;
            }
            bw.newLine();
            bw.close();
            bw = null;
            return toSave;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while decrypting file, something went wrong: " + ex);
            return null;
        }
    }
    
    /**
     * Decrypts a message with the private key
     * @param chiffrat byte Array encrypted text to decrypt
     * @return String Decrypted message
     */
    public String decrypt(byte[] chiffrat) {
        if(!isMessageLengthValid(chiffrat)) {
            StaticStandard.logErr("Chiffrat must not be longer than " + maximum_message_length + " bytes");
            return null;
        }
        return decrypt(chiffrat, key_private);
    }
    
    /**
     * Decrypts a message with a private key
     * @param chiffrat byte Array encrypted text to decrypt
     * @param pk PrivateKey Key for the decryption
     * @return String Decrypted message
     */
    public String decrypt(byte[] chiffrat, PrivateKey pk) {
        if(!isMessageLengthValid(chiffrat)) {
            StaticStandard.logErr("Chiffrat must not be longer than " + maximum_message_length + " bytes");
            return null;
        }
        try {
            final Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, pk);
            String message = new String(cipher.doFinal(chiffrat));
            return message;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while decrypting message: " + ex);
            return null;
        }
    }
    
    /**
     * Returns the modulus of the publickey as a biginteger
     * @return BigInteger BigInteger of the modulus
     */
    public BigInteger getModulus() {
        if(key_public == null) {
            return null;
        }
        String temp = key_public.toString();
        temp = temp.replaceFirst("\n", "");
        temp = temp.substring(temp.indexOf("modulus"), temp.indexOf("\n"));
        temp = temp.replace("modulus: ", "");
        BigInteger bi = new BigInteger(temp);
        return bi;
    }
    
    /**
     * Saves the keypair to a file
     * @param file File File
     * @return Boolean True if it worked, False if not
     */
    public boolean saveKeypairToFile(File file) {
        try {
            ObjectManager.saveObjectToFile(key, file);
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while saving keypair to file: " + ex);
            return false;
        }
    }
    
    /**
     * Loads the keypair from a file
     * @param file File File
     * @return Boolean True if it worked, False if not
     */
    public boolean loadKeypairFromFile(File file) {
        try {
            setKey((KeyPair) ObjectManager.loadObjectFromFile(file));
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading keypair from file: " + ex);
            return false;
        }
    }
    
    /**
     * Saves the public key to a file
     * @param file File File
     * @return Boolean True if it worked, False if not
     */
    public boolean savePublicKeyToFile(File file) {
        try {
            ObjectManager.saveObjectToFile(key_public, file);
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while saving public key to file: " + ex);
            return false;
        }
    }
    
    /**
     * Loads the public key from a file
     * @param file File File
     * @return Boolean True if it worked, False if not
     */
    public boolean loadPublicKeyFromFile(File file) {
        try {
            setPublicKey((PublicKey) ObjectManager.loadObjectFromFile(file));
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading public key from file: " + ex);
            return false;
        }
    }
    
    /**
     * Saves the private key to a file
     * @param file File File
     * @return Boolean True if it worked, False if not
     */
    public boolean savePrivateKeyToFile(File file) {
        try {
            ObjectManager.saveObjectToFile(key_private, file);
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while saving private key to file: " + ex);
            return false;
        }
    }
    
    /**
     * Loads the private key from a file
     * @param file File File
     * @return Boolean True if it worked, False if not
     */
    public boolean loadPrivateKeyFromFile(File file) {
        try {
            setPrivateKey((PrivateKey) ObjectManager.loadObjectFromFile(file));
            return true;
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading private key from file: " + ex);
            return false;
        }
    }
    
    /**
     * Returns if a message is to long or not
     * @param message String Message
     * @return Boolean True if the message is ok, False if not
     */
    public boolean isMessageLengthValid(String message) {
        return (message.getBytes().length <= getMaximumMessageLength());
    }
    
    /**
     * Returns if a message is to long or not
     * @param message byte Array Message
     * @return Boolean True if the message is ok, False if not
     */
    public boolean isMessageLengthValid(byte[] message) {
        return (message.length <= getMaximumMessageLength());
    }
    
    /**
     * Checks if a keysize is a power of 2
     * @param keysize Integer Keysize
     * @return Boolean True if keysize is a power of 2, False if not
     */
    public static boolean isKeysizeValid(int keysize) {
        if(keysize <= 0) {
            return false;
        }
        int i = 1;
        while(keysize >= (Math.pow(2, i))) {
            if(keysize == Math.pow(2, i)) {
                return true;
            }
            i++;
        }
        return false;
    }
    
    /**
     * Returns the publickey
     * @return PublicKey PublicKey
     */
    public PublicKey getPublicKey() {
        return key_public;
    }
    
    /**
     * Returns the privatekey (ATTENTION! GIVE THIS KEY TO NOBODY!!!)
     * @return PrivateKey PrivateKey
     */
    public PrivateKey getPrivateKey() {
        return key_private;
    }

    /**
     * Returns the keypair (ATTENTION! GIVE THE PRIVATE KEY TO NOBODY!!!)
     * @return KeyPair KeyPair
     */
    public KeyPair getKey() {
        return key;
    }

    /**
     * Sets the keypair (ATTENTION! DONT USE A PRIVATE KEY THAT YOU GOT FROM SOMEBODY ELSE!!!)
     * @param key KeyPair KeyPair
     */
    public void setKey(KeyPair key) {
        this.key = key;
        this.key_public = key.getPublic();
        this.key_private = key.getPrivate();
    }

    /**
     * Returns the maximum length of a message in bytes
     * @return Integer Maximum length of a message in bytes
     */
    public int getMaximumMessageLength() {
        return maximum_message_length;
    }
    
    /**
     * Sets the meximum message length in bytes
     */
    private void setMaximumMessageLength() {
        maximum_message_length = (keysize / 8);
    }

    /**
     * Sets the public key
     * @param key_public PublicKey Public key
     */
    public void setPublicKey(PublicKey key_public) {
        this.key_public = key_public;
    }

    /**
     * Sets the private key
     * @param key_private PrivateKey Private key
     */
    public void setPrivateKey(PrivateKey key_private) {
        this.key_private = key_private;
    }
    
}
