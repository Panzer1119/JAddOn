/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.jlang;

/**
 *
 * @author Paul
 */
public class Language {
    
    private String lang = ""; //Language of the name
    private String unlocalized = ""; //Language short
    private String name = ""; //Language name
    
    public Language(String lang, String unlocalized, String name) {
        this.lang = lang;
        this.unlocalized = unlocalized;
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getUnlocalized() {
        return unlocalized;
    }

    public void setUnlocalized(String unlocalized) {
        this.unlocalized = unlocalized;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return (name != null) ? name : unlocalized.toUpperCase();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Language) {
            Language lang_ = (Language) o;
            return (lang_.getLang().equals(getLang())) && (lang_.getUnlocalized().equals(getUnlocalized()));
        } else {
            return false;
        }
    }
    
}
