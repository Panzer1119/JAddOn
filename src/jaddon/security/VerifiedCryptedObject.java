/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.security;

import java.io.Serializable;

/**
 *
 * @author Paul
 */
public class VerifiedCryptedObject implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    private byte[] cryptedCertificate = null;
    private byte[] cryptedMessage = null;
    
    /**
     * Constructs the VerifiedCryptedObject object with no values
     */
    public VerifiedCryptedObject() {
        //Nothing
    }
    
    /**
     * Constructs the VerifiedCryptedObject ibject with a certificate and a message
     * @param cryptedCertificate byte Array Certificate
     * @param cryptedMessage byte Array Message
     */
    public VerifiedCryptedObject(byte[] cryptedCertificate, byte[] cryptedMessage) {
        this.cryptedCertificate = cryptedCertificate;
        this.cryptedMessage = cryptedMessage;
    }

    /**
     * Returns the crypted certificate
     * @return byte Array Crypted certificate
     */
    public byte[] getCryptedCertificate() {
        return cryptedCertificate;
    }

    /**
     * Sets the crypted certificate
     * @param cryptedCertificate byte Array Crypted certificate
     */
    public void setCryptedCertificate(byte[] cryptedCertificate) {
        this.cryptedCertificate = cryptedCertificate;
    }

    /**
     * Returns the crypted message
     * @return byte Array Crypted message
     */
    public byte[] getCryptedMessage() {
        return cryptedMessage;
    }

    /**
     * Sets the crypted message
     * @param cryptedMessage byte Array Crypted message
     */
    public void setCryptedMessage(byte[] cryptedMessage) {
        this.cryptedMessage = cryptedMessage;
    }
    
    /**
     * Rewrites the toString method
     * @return String String which is got if printing this object
     */
    @Override
    public String toString() {
        return "Crypted Certificate: " + new String(cryptedCertificate) + "\nCrypted Message: " + new String(cryptedMessage);
    }
    
}
