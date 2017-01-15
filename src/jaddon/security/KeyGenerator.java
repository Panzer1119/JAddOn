/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.security;

/**
 *
 * @author Paul
 */
public class KeyGenerator {
    
    /**
     * Generates a random key with the given length from the ascii code
     * @param length Integer Key length
     * @return String Key
     */
    public static String generateKey(int length) {
        if(length < 0) {
            return null;
        }
        String temp_key = "";
        for(int i = 0; i < length; i++) {
            double rand = (Math.random() * 126);
            char c = ((char) ((rand > 32) ? rand : rand + 33));
            temp_key += c;
        }
        return temp_key;
    }
    
}
