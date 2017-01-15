/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class JNumber {
    
    /**
     * UNARY
     * 0 - 0
     */
    public static final int UNARY = 1;
    
    /**
     * BINARY
     * 0 - 1
     */
    public static final int BINARY = 2;
    
    /**
     * TERNARY
     * 0 - 2
     */
    public static final int TERNARY = 3;
    
    /**
     * QUATERNARY
     * 0 - 3
     */
    public static final int QUATERNARY = 4;
    
    /**
     * QUINARY
     * 0 - 4
     */
    public static final int QUINARY = 5;
    
    /**
     * SENARY
     * 0 - 5
     */
    public static final int SENARY = 6;
    
    /**
     * SEPTARY
     * 0 - 6
     */
    public static final int SEPTARY = 7;
    
    /**
     * OCTAL
     * 0 - 7
     */
    public static final int OCTAL = 8;
    
    /**
     * NONAL
     * 0 - 8
     */
    public static final int NONAL = 9;
    
    /**
     * DECIMAL
     * 0 - 9
     */
    public static final int DECIMAL = 10;

    /**
     * 11 MAL
     * 0 - A
     */
    public static final int SYSTEM11 = 11;

    /**
     * 12 MAL
     * 0 - B
     */
    public static final int SYSTEM12 = 12;

    /**
     * 13 MAL
     * 0 - C
     */
    public static final int SYSTEM13 = 13;

    /**
     * 14 MAL
     * 0 - D
     */
    public static final int SYSTEM14 = 14;

    /**
     * 15 MAL
     * 0 - E
     */
    public static final int SYSTEM15 = 15;

    /**
     * 16 MAL
     * 0 - F
     */
    public static final int HEXADECIMAL = 16;

    /**
     * 17 MAL
     * 0 - G
     */
    public static final int SYSTEM17 = 17;

    /**
     * 18 MAL
     * 0 - H
     */
    public static final int SYSTEM18 = 18;

    /**
     * 19 MAL
     * 0 - I
     */
    public static final int SYSTEM19 = 19;

    /**
     * 20 MAL
     * 0 - J
     */
    public static final int SYSTEM20 = 20;

    /**
     * 21 MAL
     * 0 - K
     */
    public static final int SYSTEM21 = 21;

    /**
     * 22 MAL
     * 0 - L
     */
    public static final int SYSTEM22 = 22;

    /**
     * 23 MAL
     * 0 - M
     */
    public static final int SYSTEM23 = 23;

    /**
     * 24 MAL
     * 0 - N
     */
    public static final int SYSTEM24 = 24;

    /**
     * 25 MAL
     * 0 - O
     */
    public static final int SYSTEM25 = 25;

    /**
     * 26 MAL
     * 0 - P
     */
    public static final int SYSTEM26 = 26;

    /**
     * 27 MAL
     * 0 - Q
     */
    public static final int SYSTEM27 = 27;

    /**
     * 28 MAL
     * 0 - R
     */
    public static final int SYSTEM28 = 28;

    /**
     * 29 MAL
     * 0 - S
     */
    public static final int SYSTEM29 = 29;

    /**
     * 30 MAL
     * 0 - T
     */
    public static final int SYSTEM30 = 30;

    /**
     * 31 MAL
     * 0 - U
     */
    public static final int SYSTEM31 = 31;

    /**
     * 32 MAL
     * 0 - V
     */
    public static final int SYSTEM32 = 32;

    /**
     * 33 MAL
     * 0 - W
     */
    public static final int SYSTEM33 = 33;

    /**
     * 34 MAL
     * 0 - X
     */
    public static final int SYSTEM34 = 34;

    /**
     * 35 MAL
     * 0 - Y
     */
    public static final int SYSTEM35 = 35;

    /**
     * 36 MAL
     * 0 - Z
     */
    public static final int SYSTEM36 = 36;
    
    /**
     * All normal numbers from 0 to 9
     */
    public static final String NUMBERSNORMAL = "0123456789";
    
    /**
     * All normal numbers from 0 to 9 and all advanced numbers from A to Z
     */
    public static final String NUMBERSADVANCED = NUMBERSNORMAL + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /**
     * Actual number
     */
    private String number = "";
    
    /**
     * Actual number system
     */
    private int number_system = DECIMAL;
    
    /**
     * Basic Constructor
     */
    public JNumber() {
        number = "0";
        number_system = DECIMAL;
    }
    
    /**
     * Standard Constructor
     * @param number String Number
     * @param number_system Integer Number system
     */
    public JNumber(String number, int number_system) {
        setNumber(number);
        setNumberSystem(number_system);
    }
    
    /**
     * Converts the actual number to another number system
     * @param number_system Integer Number system
     * @return Boolean True if it worked False if not
     */
    public boolean convertTo(Integer number_system) {
        if(!isValidNumberSystem(number_system)) {
            return false;
        }
        JNumber number_c = convertFromTo(this, number_system);
        if(number_c != null) {
            this.number = number_c.number;
            this.number_system = number_c.number_system;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Converts the number to another number system
     * @param number JNumber Number to convert
     * @param number_system Integer Number system to convert to
     * @return Boolean True if it worked False if not
     */
    public static JNumber convertFromTo(JNumber number, int number_system) {
        if(!isValidNumberSystem(number_system)) {
            return null;
        }
        final BigInteger number_system_big = new BigInteger("" + number_system);
        String number_old = number.number;
        int number_system_old = number.number_system;
        BigInteger number_complete = BigInteger.ZERO;
        for(int i = 0; i < number_old.length(); i++) {
            int value = getNumberValue("" + number_old.charAt(i));
            number_complete = number_complete.add(new BigInteger("" + value).multiply((new BigInteger("" + number_system_old).pow(number_old.length() - i - 1))));
        }
        ArrayList<Integer> numbers_new = new ArrayList<>();
        if(number_system != UNARY) {
            boolean done = false;
            while(!done) {
                BigInteger rest = number_complete.mod(number_system_big);
                numbers_new.add(rest.intValueExact());
                BigInteger next = number_complete.divide(number_system_big);
                number_complete = next;
                if(number_complete.equals(BigInteger.ZERO)) {
                    done = true;
                }
            }
        } else {
            for(int i = 0; i < number_complete.intValueExact(); i++) {
                numbers_new.add(0);
            }
        }
        String number_end = "";
        for(int i = 0; i < numbers_new.size(); i++) {
            number_end += getNumberSymbol(numbers_new.get(numbers_new.size() - i - 1));
        }
        JNumber number_new = new JNumber(number_end, number_system);
        return number_new;
    }
    
    /**
     * Returns the number symbol from 0 to Z
     * @param number Integer Number
     * @return String Number
     */
    public static String getNumberSymbol(int number) {
        return "" + NUMBERSADVANCED.charAt(number);
    }
    
    /**
     * Returns the number value from 0 to 26
     * @param number String Number
     * @return Integer Number
     */
    public static int getNumberValue(String number) {
        return NUMBERSADVANCED.indexOf(number);
    }

    /**
     * Returns the actual number
     * @return String Number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the actual bumber
     * @param number String Number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Returns the actual number system
     * @return Integer Number system
     */
    public int getNumberSystem() {
        return number_system;
    }

    /**
     * Sets the actual number system
     * @param number_system Integer Number system
     */
    public void setNumberSystem(int number_system) {
        if(isValidNumberSystem(number_system)) {
            this.number_system = number_system;
        } else {
            this.number_system = DECIMAL;
        }
    }
    
    /**
     * Returns if a number system exists
     * @param number_system Integer Number system
     * @return Boolean True if it is a valid number system False if not
     */
    public static boolean isValidNumberSystem(int number_system) {
        return (number_system > 0) && (number_system <= SYSTEM36);
    }
    
    @Override
    public String toString() {
        return "Number: \"" + number + "\", Number System: " + number_system;
    }
    
}
