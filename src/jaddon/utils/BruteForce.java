/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

/**
 *
 * @author Paul
 */
public class BruteForce {
    
    public String chars = "abcdefghijklmnopqrstuvwxyz";
    
    public BruteForce() {
        
    }
    
    public BruteForce(String chars) {
        this.chars = chars;
    }
    
    public String nextWord(String word) {
        return nextWord(word, word.length()-1);
    }
 
    public String nextWord(String word, int stelle) {
        char[] alphabet = chars.toCharArray();
        char[] wordArray = word.toCharArray();
        if (wordArray.length == 0){
            return String.valueOf(alphabet[0]);
        }else if(wordArray[stelle] == alphabet[alphabet.length - 1]) {
            wordArray[stelle] = alphabet[0];
            if (stelle > 0) {
                return nextWord(String.valueOf(wordArray), stelle - 1);
            }
            else{
                return alphabet[0]+String.valueOf(wordArray);
            }
        }
         else{
            for (int i = 0; i< alphabet.length; i++){
                if (wordArray[stelle] == alphabet[i]){
                    wordArray[stelle] = alphabet[i+1];
                    break;
                }
            }
            return String.valueOf(wordArray);
        }
    }
    
    public void generateWords(String start, long count) {
        String word = start;
        long wordcount = 0;
        while(wordcount < count) {
            wordcount++;
            word = nextWord(word);
            doFinal(word);
        }
    }
    
    public void doFinal(String word) {
        
    }

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }
    
}
