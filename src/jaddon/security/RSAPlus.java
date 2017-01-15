/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.security;

import jaddon.controller.StaticStandard;
import jaddon.objects.ObjectManager;
import java.io.File;
import java.security.PublicKey;

/**
 *
 * @author Paul
 */
public class RSAPlus extends RSA {
    
    public static final int CERTIFICATESIZEUNDERPOWERED = 10;
    public static final int CERTIFICATESIZESTANDARD = 100;
    public static final int CERTIFICATESIZEOVERPOWERED = 245;
    
    private String certificate = null;
    private String certificate_partner = null;
    private PublicKey key_public_partner = null;
    private int certificate_keysize = CERTIFICATESIZESTANDARD;
    private final RSAPlus rsaplus = this;
    
    /**
     * Constructs the RSAPlus crypter with standard key size
     */
    public RSAPlus() {
        super();
    }
    
    /**
     * Constructs the RSAPlus crypter with a keysize
     * @param keysize Integer Keysize
     * @param certificate_keysize Integer Certificate keysize
     */
    public RSAPlus(int keysize, int certificate_keysize) {
        super(keysize);
        setCertificateKeysize(certificate_keysize);
    }
    
    /**
     * Generates randomly the certificate with the standard keysize
     * @return Thread
     */
    public Thread generateCertificate() {
        return generateCertificate(certificate_keysize);
    }
    
    /**
     * Generates randomly the certificate with the standard keysize
     * @param keysize Integer Keysize
     * @return Thread
     */
    public Thread generateCertificate(final int keysize) {
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                if(!isCertificateKeysizeValid(keysize)) {
                    StaticStandard.logErr("Certificate keysize is invalid, it must be greater than 0 and less or equal than " + rsaplus.getMaximumMessageLength() + " in bytes");
                    return;
                }
                StaticStandard.log("Starting generating " + keysize + " long certificate...");
                try {
                    certificate = KeyGenerator.generateKey(keysize);
                    StaticStandard.log("Generated successfully " + keysize + " long certificate");
                } catch (Exception ex) {
                    StaticStandard.logErr("Error while generating " + keysize + " long certificate:" + ex);
                }
            }
            
        });
        thread.start();
        return thread;
    }

    /**
     * Returns the VerifiedCryptedObject with the crypted certificate and the crypted messsage
     * @param message String Message
     * @return VerifiedCryptedObject VerifiedCryptedObject
     */
    public VerifiedCryptedObject getVerifiedCryptedObject(String message) {
        VerifiedCryptedObject vco = new VerifiedCryptedObject(super.encrypt(certificate_partner, key_public_partner), super.encrypt(message, key_public_partner));
        return vco;
    }
    
    /**
     * Returns if a VerifiedCryptedObject is valid
     * @param vco VerifiedCryptedObject VerifiedCryptedObject
     * @return Boolean True if the VerifiedCryptedObject is valid, False if not
     */
    public boolean isVerifiedCryptedObjectValid(VerifiedCryptedObject vco) {
        if(vco == null || vco.getCryptedCertificate() == null || vco.getCryptedMessage() == null) {
            return false;
        }
        final String certificate_temp = super.decrypt(vco.getCryptedCertificate());
        return certificate.equals(certificate_temp);
    }
    
    /**
     * Returns the decrypted message of a VerifiedCryptedObject if it is valid
     * @param vco VerifiedCryptedObject VerifiedCryptedObject
     * @return String Decrypted message
     */
    public String getMessage(VerifiedCryptedObject vco) {
        if(isVerifiedCryptedObjectValid(vco)) {
            final String message_temp = super.decrypt(vco.getCryptedMessage());
            return message_temp;
        } else {
            StaticStandard.logErr("Invalid VerifiedCryptedObject");
            return null;
        }
    }
    
    /**
     * Loads the partner public key from a file
     * @param file File File
     */
    public void loadPartnerPublicKeyFromFile(File file) {
        if(file == null || !file.exists() || !file.isFile()) {
            return;
        }
        try {
            setPartnerPublicKey((PublicKey) ObjectManager.loadObjectFromFile(file));
        } catch (Exception ex) {
            StaticStandard.logErr("Error while loading partner public key from a file: " + ex);
        }
    }
    
    /**
     * Saves the partner public key to a file
     * @param file File File
     */
    public void savePartnerPublicKeyToFile(File file) {
        if(file == null || (file.exists() && !file.isFile())) {
            return;
        }
        try {
            ObjectManager.saveObjectToFile(key_public_partner, file);
        } catch (Exception ex) {
            StaticStandard.logErr("Error while saving partner public key to a file: " + ex);
        }
    }
    
    /**
     * Returns if a keysize for the certificate is valid or not
     * @param keysize Integer Certificate keysize
     * @return Boolean True if the keysize is valid, False if not
     */
    public boolean isCertificateKeysizeValid(int keysize) {
        return (keysize > 0) && (keysize <= super.getMaximumMessageLength());
    }
    
    /**
     * Returns the certificate
     * @return String Certificate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Sets the certificate
     * @param certificate String Certificate
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * Returns the certificate of the partner
     * @return String Certificate of the partner
     */
    public String getPartnerCertificate() {
        return certificate_partner;
    }

    /**
     * Sets the certificate of the partner
     * @param certificate_partner String Certificate of the partner
     */
    public void setPartnerCertificate(String certificate_partner) {
        this.certificate_partner = certificate_partner;
    }

    /**
     * Returns the public key of the partner
     * @return PublicKey PublicKey of the partner
     */
    public PublicKey getPartnerPublicKey() {
        return key_public_partner;
    }

    /**
     * Sets the public key of the partner
     * @param key_public_partner PublicKey PublicKey of the partner
     */
    public void setPartnerPublicKey(PublicKey key_public_partner) {
        this.key_public_partner = key_public_partner;
    }

    /**
     * Returns the keysize of the certificate
     * @return Integer Certificate keysize
     */
    public int getCertificateKeysize() {
        return certificate_keysize;
    }

    /**
     * Sets the certificate keysize
     * @param certificate_keysize Integer Certificate Keysize
     */
    public void setCertificateKeysize(int certificate_keysize) {
        if(isCertificateKeysizeValid(certificate_keysize)) {
            this.certificate_keysize = certificate_keysize;
        } else {
            StaticStandard.logErr("Certificate keysize is invalid, it must be greater than 0 and less or equal than " + super.getMaximumMessageLength() + " in bytes");
        }
    }
    
}
