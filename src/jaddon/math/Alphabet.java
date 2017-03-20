/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Paul
 */
public class Alphabet extends HashMap<String, Integer> implements Serializable {
    
    /**
     * English Number splitter
     */
    public static final String DOT = ".";
    
    /**
     * European Number splitter
     */
    public static final String COMMA = ",";
    
    public Alphabet() {
        super();
    }
    
    public Alphabet(Object[][]... numbers) {
        super();
        put(numbers);
    }
    
    public final Alphabet copy() {
        Alphabet alphabet_new = new Alphabet();
        return alphabet_new;
    }
    
    public final Alphabet add(Alphabet alphabet) {
        put(alphabet.toArray());
        return this;
    }
    
    public final Alphabet put(Object[][]... numbers) {
        for(Object[] number : numbers) {
            if(number.length != 2) {
                continue;
            }
            if(number[0] instanceof String && number[1] instanceof Integer) {
                put((String) number[0], (Integer) number[1]);
            } else if(number[0] instanceof Integer && number[1] instanceof String) {
                put((String) number[1], (Integer) number[0]);
            }
        }
        return this;
    }
    
    public final String getNumberSymbol(int number) {
        if(number < 0 || !containsValue(number)) {
            return null;
        }
        for(String g : keySet()) {
            int n = get(g);
            if(n == number) {
                return g;
            }
        }
        return null;
    }
    
    public final int getNumberValue(String number) {
        if(number.equals(DOT) || number.equals(COMMA)) {
            return -1;
        }
        return get(number);
    }
    
    public final Object[][] toArray() {
        final Object[][] data = new Object[size()][2];
        int i = 0;
        for(String g : keySet()) {
            data[i][0] = g;
            data[i][1] = get(g);
            i++;
        }
        return data;
    }
    
    public final boolean isValid() {
        return isPatternValid() && isNumbersValid();
    }
    
    public final boolean isNumbersValid() {
        final ArrayList<Integer> numbers = new ArrayList<>();
        for(int i : values()) {
            if(!numbers.contains(i)) {
                numbers.add(i);
            } else {
                return false;
            }
        }
        numbers.sort((Integer o1, Integer o2) -> o1 - o2);
        int n = 0;
        for(int i : numbers) {
            if(i != n) {
                return false;
            }
            n++;
        }
        return true;
    }
    
    public final boolean isPatternValid() {
        for(String g : keySet()) {
            for(char c : g.toCharArray()) {
                if(c == COMMA.charAt(0) || c == DOT.charAt(0)) {
                    return false;
                }
                for(String g_ : keySet()) {
                    if(g.equals(g_)) {
                        continue;
                    }
                    if(g_.contains("" + c)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
}
