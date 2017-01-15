/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.test;

import jaddon.controller.StaticStandard;
import jaddon.jlang.JLang;
import java.util.Locale;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * @version 1.1
 * @author Paul Hagedorn
 */
public class Test {
    public static void main(String[] args) {
        if(StaticStandard.getLang() == null) {
            StaticStandard.setLang(new JLang(Locale.getDefault().getLanguage().toUpperCase()));
        }
        Properties lang = JLang.getLangProp();
        JOptionPane.showMessageDialog(null, lang.getProperty("default_start_text", "This only a library to use with java swing and even more!\nProgrammer: Paul Hagedorn"));
    }
}
