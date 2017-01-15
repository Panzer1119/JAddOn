/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.security;

import jaddon.controller.StaticStandard;
import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class CrazyCrypter {
    
    private int[] key = null;
    
    public CrazyCrypter() {
        this(null);
    }
    
    public CrazyCrypter(int[] key) {
        this.key = key;
    }
    
    public String encrypt(String message) {
        if(key == null || key.length < 1) {
            return null;
        }
        String encrypted = "";
        String[] split = message.split("\n");
        for(int i = 0; i < split.length; i++) {
            String g = split[i];
            String temp_encrypted = "";
            int pos_extra = 0;
            String temp_key = "";
            if(g.length() <= key.length) {
                for(int z = 0; z < g.length(); z++) {
                    int pos = key[z % key.length] + pos_extra;
                    pos_extra += pos;
                }
                temp_key = KeyGenerator.generateKey(key[0] + pos_extra + key[g.length() - 1]);
                pos_extra = 0;
            } else {
                for(int z = 0; z < g.length(); z++) {
                    int pos = key[z % key.length] + pos_extra;
                    pos_extra += pos;
                }
                temp_key = KeyGenerator.generateKey(key[0] + pos_extra);
                pos_extra = 0;
            }
            ArrayList<String> temp_encrypted_arraylist = new ArrayList<>();
            for(char c : temp_key.toCharArray()) {
                temp_encrypted_arraylist.add("" + c);
            }
            for(int z = 0; z < g.length(); z++) {
                int pos = key[z % key.length] + pos_extra;
                temp_encrypted_arraylist.add(pos, "" + g.charAt(z));
                pos_extra += pos;
            }
            for(String gg : temp_encrypted_arraylist) {
                temp_encrypted += gg;
            }
            encrypted += temp_encrypted;
            if(split.length > 1) {
                encrypted += "\n";
            }
        }
        return encrypted;
    }
    
    public String decrypt(String encrypted_message) {
        if(key == null || key.length < 1) {
            return null;
        }
        String message = "";
        String[] split = encrypted_message.split("\n");
        for(int i = 0; i < split.length; i++) {
            String g = split[i];
            String temp_decrypted = "";
            int pos_extra = 0;
            for(int z = 0; z < g.length(); z++) {
                int pos = key[z % key.length] + pos_extra;
                if(pos >= g.length()) {
                    break;
                }
                temp_decrypted += g.charAt(pos);
                pos_extra += pos;
            }
            message += temp_decrypted;
            if(split.length > 1) {
                message += "\n";
            }
        }
        return message;
    }

    public int[] getKey() {
        return key;
    }

    public void setKey(int[] key) {
        this.key = key;
    }
    
}
