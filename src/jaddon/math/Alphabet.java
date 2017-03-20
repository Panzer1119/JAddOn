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
public class Alphabet extends HashMap<Integer, String> implements Serializable {
    
    /**
     * English Number splitter
     */
    public static final String DOT = ".";
    
    /**
     * European Number splitter
     */
    public static final String COMMA = ",";
    
    private boolean locked = false;
    
    public Alphabet() {
        super();
    }
    
    public Alphabet(Object[][] numbers) {
        super();
        put(numbers);
    }
    
    public final Alphabet copy() {
        Alphabet alphabet_new = new Alphabet(toArray());
        return alphabet_new;
    }
    
    public final Alphabet add(Alphabet alphabet) {
        if(locked) {
            return this;
        }
        put(alphabet.toArray());
        return this;
    }

    @Override
    public String put(Integer key, String value) {
        if(locked) {
            return null;
        }
        return super.put(key, value);
    }
    
    public Integer put(String key, Integer value) {
        if(locked) {
            return -1;
        }
        int value_old = -1;
        if(containsValue(key)) {
            value_old = getNumberValue(key);
            remove(value_old);
        }
        put(value, key);
        return value_old;
    }
    
    public final Alphabet put(Object[][] numbers) {
        if(locked) {
            return this;
        }
        for(Object[] number : numbers) {
            if(number.length != 2) {
                continue;
            }
            if(number[0] instanceof Integer && number[1] instanceof String) {
                put((Integer) number[0], (String) number[1]);
            } else if(number[0] instanceof String && number[1] instanceof Integer) {
                put((String) number[0], (Integer) number[1]);
            }
        }
        return this;
    }
    
    public final String getNumberSymbol(int number) {
        if(number < 0 || !containsKey(number)) {
            return null;
        }
        return get(number);
    }
    
    public final int getNumberValue(String number) {
        if(number.equals(DOT) || number.equals(COMMA)) {
            return -1;
        }
        for(int i : keySet()) {
            String g = get(i);
            if(g.equals(number)) {
                return i;
            }
        }
        return -1;
    }
    
    public final Object[][] toArray() {
        final Object[][] data = new Object[size()][2];
        final ArrayList<Integer> numbers = new ArrayList<>();
        for(int i : keySet()) {
            numbers.add(i);
        }
        numbers.sort((Integer o1, Integer o2) -> o1 - o2);
        int n = 0;
        for(int i : numbers) {
            data[n][0] = i;
            data[n][1] = get(i);
            n++;
        }
        return data;
    }
    
    public final boolean isValid() {
        return isPatternValid() && isNumbersValid();
    }
    
    public final boolean isNumbersValid() {
        final ArrayList<Integer> numbers = new ArrayList<>();
        for(int i : keySet()) {
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
        for(String g : values()) {
            for(char c : g.toCharArray()) {
                if(c == COMMA.charAt(0) || c == DOT.charAt(0)) {
                    return false;
                }
                for(String g_ : values()) {
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
    
    public final boolean isAddingValid(int number, String number_symbol) {
        final String number_symbol_old = getNumberSymbol(number);
        if(number_symbol_old != null && !number_symbol_old.equals(number_symbol)) {
            return false;
        }
        final int number_old = getNumberValue(number_symbol);
        if(number_old != -1 && number_old != number) {
            return false;
        }
        final Alphabet alphabet_temp_1 = copy();
        final Alphabet alphabet_temp_2 = copy();
        alphabet_temp_1.put(number, number_symbol);
        alphabet_temp_2.put(number_symbol, number);
        return alphabet_temp_1.isValid() && alphabet_temp_2.isValid();
    }

    public boolean isLocked() {
        return locked;
    }

    public Alphabet setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }
    
}
