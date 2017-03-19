/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;

/**
 *
 * @author Paul
 */
public class JNumber {
    
    /**
     * English Number splitter
     */
    public static final String DOT = ".";
    
    /**
     * European Number splitter
     */
    public static final String COMMA = ",";
    
    /**
     * The Period letter
     */
    public static final String PERIODSIGN = "\u0305";
    
    private static final int PERIODREPEATING = 100;
    
    /**
     * Actual number
     */
    private String number = "";
    
    /**
     * Actual number system
     */
    private NumeralSystem number_system = NumeralSystem.DEZIMAL;
    
    
    /**
     * Start position of the repeating pattern (-1 for no period)
     */
    private int period = -1;
    
    /**
     * Basic Constructor
     */
    public JNumber() {
        number = "0";
        number_system = NumeralSystem.DEZIMAL;
    }
    
    /**
     * Standard Constructor
     * @param number String Number
     * @param number_system NumeralSystem Number system
     */
    public JNumber(String number, NumeralSystem number_system) {
        setNumber(number);
        setNumberSystem(number_system);
    }
    
    /**
     * Converts the actual number to another number system
     * @param number_system NumeralSystem Number system
     * @return Boolean True if it worked False if not
     */
    public final JNumber convertTo(NumeralSystem number_system) {
        return convertTo(number_system, NumeralSystem.NUMBERSADVANCED);
    }
    
    /**
     * Converts the actual number to another number system
     * @param number_system NumeralSystem Number system
     * @param alphabet String Alphabet
     * @return Boolean True if it worked False if not
     */
    public final JNumber convertTo(NumeralSystem number_system, String alphabet) {
        if(!isValidNumberSystem(number_system)) {
            return null;
        }
        JNumber number_c = convertFromTo(this, number_system, alphabet);
        if(number_c != null) {
            return number_c;
        } else {
            return null;
        }
    }
    
    /**
     * Converts the number to another number system using the normal numeral alphabet
     * @param number JNumber Number to convert
     * @param number_system NumeralSystem Number system to convert to
     * @return Boolean True if it worked False if not
     */
    public static final JNumber convertFromTo(JNumber number, NumeralSystem number_system) {
        return convertFromTo(number, number_system, NumeralSystem.NUMBERSADVANCED);
    }
    
    /**
     * Converts the number to another number system using an alphabet
     * @param number JNumber Number to convert
     * @param number_system NumeralSystem Number system to convert to
     * @param alphabet String Alphabet
     * @return Boolean True if it worked False if not
     */
    public static final JNumber convertFromTo(JNumber number, NumeralSystem number_system, String alphabet) {
        if(!isValidNumberSystem(number_system)) {
            return null;
        }
        final BigDecimal number_system_big = new BigDecimal("" + number_system.getSystem());
        final BigDecimal number_system_big_old = new BigDecimal("" + number.getNumberSystem().getSystem());
        String number_old = number.getNumber();
        if(number.getPeriodStart() != -1) {
            String period = number.getPeriod();
            for(int i = 0; i < PERIODREPEATING; i++) {
                number_old += period;
            }
        }
        final boolean isNegative = number.getNumber().startsWith("-");
        if(isNegative) {
            number_old = number_old.substring(1);
        }
        BigDecimal number_complete = BigDecimal.ZERO;
        String point = DOT;
        int index_point = number_old.indexOf(point);
        if(index_point == -1) {
            point = COMMA;
            index_point = number_old.indexOf(point);
        }
        for(int i = (number_old.length() - 1); i >= 0; i--) {
            int i_ = i;
            if((number_old.length() - i - 1) == index_point) {
                continue;
            } else if((number_old.length() - i - 1) > index_point) {
                i_ += 1;
            }
            final int value = getNumberValue("" + number_old.charAt(number_old.length() - i - 1), alphabet);
            final int exponent = i_ - ((index_point != -1) ? (number_old.length() - index_point) : 0);
            BigDecimal add = null;
            if(exponent >= 0) {
                add = BigDecimal.valueOf(value).multiply(number_system_big_old.pow(exponent));
            } else {
                add = BigDecimal.valueOf(value).divide(number_system_big_old.pow(-1 * exponent));
            }
            number_complete = number_complete.add(add);
        }
        if(number.getPeriodStart() != -1) {
            number_complete = number_complete.round(new MathContext(PERIODREPEATING));
        }
        BigInteger number_complete_pre_comma = number_complete.toBigInteger();
        BigDecimal number_complete_post_comma = number_complete.subtract(new BigDecimal(number_complete_pre_comma));
        final ArrayList<Integer> numbers_new = new ArrayList<>();
        final ArrayList<Integer> numbers_new_comma = new ArrayList<>();
        final ArrayList<BigDecimal> numbers_loop = new ArrayList<>();
        int loop_begin = -1;
        if(number_system != NumeralSystem.UNÄR) {
            boolean done = false;
            int i = 0;
            while(!done) {
                if((index_point != -1 && i < index_point) || index_point == -1) {
                    final BigInteger rest = number_complete_pre_comma.mod(number_system_big.toBigInteger());
                    numbers_new.add(rest.intValue());
                    number_complete_pre_comma = number_complete_pre_comma.divide(number_system_big.toBigInteger());
                } else {
                    number_complete_post_comma = number_complete_post_comma.multiply(number_system_big);
                    if(!numbers_loop.contains(number_complete_post_comma)) {
                        numbers_loop.add(number_complete_post_comma);
                    } else {
                        loop_begin = numbers_loop.indexOf(number_complete_post_comma);
                        numbers_loop.clear();
                        done = true;
                        break;
                    }
                    numbers_new_comma.add(number_complete_post_comma.intValue());
                    number_complete_post_comma = number_complete_post_comma.remainder(BigDecimal.ONE, MathContext.UNLIMITED);
                }
                if((number_complete_pre_comma.compareTo(BigInteger.ZERO) == 0) && (number_complete_post_comma.compareTo(BigDecimal.ZERO) == 0)) {
                    done = true;
                }
                i++;
            }
        } else {
            for(int i = 0; i < number_complete.intValue(); i++) {
                numbers_new.add(1);
            }
        }
        String number_end = "";
        for(int i = 0; i < numbers_new.size(); i++) {
            number_end += getNumberSymbol(numbers_new.get(numbers_new.size() - i - 1), alphabet);
        }
        if(!numbers_new_comma.isEmpty()) {
            number_end += point;
            for(int i = 0; i < numbers_new_comma.size(); i++) {
                number_end += getNumberSymbol(numbers_new_comma.get(i), alphabet);
            }
        }
        if(isNegative) {
            number_end = "-" + number_end;
        }
        JNumber number_new = new JNumber(number_end, number_system).setPeriodStart(loop_begin);
        return number_new;
    }
    
    /**
     * Returns the number symbol from 0 to Z
     * @param number Integer Number
     * @param alphabet String Alphabet
     * @return String Number
     */
    public static final String getNumberSymbol(int number, String alphabet) {
        return "" + alphabet.charAt(number);
    }
    
    /**
     * Returns the number value from 0 to 26
     * @param number String Number
     * @param alphabet String Alphabet
     * @return Integer Number
     */
    public static final int getNumberValue(String number, String alphabet) {
        if(number.equals(DOT) || number.equals(COMMA)) {
            return -1;
        }
        return alphabet.indexOf(number.toUpperCase());
    }

    /**
     * Returns the actual number
     * @return String Number
     */
    public final String getNumber() {
        return number;
    }

    /**
     * Sets the actual bumber
     * @param number String Number
     */
    public final JNumber setNumber(String number) {
        int index_point = indexOfPoint(number);
        if(number.contains(PERIODSIGN) && index_point != -1) {
            setPeriodStart(number.indexOf(PERIODSIGN) - index_point - 1);
        }
        this.number = number.trim().replaceAll(PERIODSIGN, "").replaceAll(" ", "").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", "");
        return this;
    }

    /**
     * Returns the actual number system
     * @return NumeralSystem Number system
     */
    public final NumeralSystem getNumberSystem() {
        return number_system;
    }

    /**
     * Sets the actual number system
     * @param number_system NumeralSystem Number system
     */
    public final JNumber setNumberSystem(NumeralSystem number_system) {
        if(isValidNumberSystem(number_system)) {
            this.number_system = number_system;
        } else {
            this.number_system = NumeralSystem.DEZIMAL;
        }
        return this;
    }
    
    /**
     * Returns the start of a repeating pattern (-1 for no one)
     * @return Integer Period begin
     */
    public final int getPeriodStart() {
        return period;
    }
    
    /**
     * Sets the start of a repeating pattern (-1 for no one)
     * @param period Integer Period begin
     * @return JNumber This object
     */
    private final JNumber setPeriodStart(int period) {
        this.period = period;
        return this;
    }
    
    public final String getPeriod() {
        if(getPeriodStart() != -1) {
            return getNumber().substring(indexOfPoint() + getPeriodStart() + 1);
        } else {
            return "";
        }
    }
    
    /**
     * Returns the index of the point
     * @return Integer Index of point
     */
    public final int indexOfPoint() {
        return indexOfPoint(number);
    }
    
    
    /**
     * Returns the index of the point
     * @param number String number
     * @return Integer Index of point
     */
    public static final int indexOfPoint(String number) {
        int index = number.indexOf(DOT);
        if(index == -1) {
            index = number.indexOf(COMMA);
        }
        return index;
    }
    
    /**
     * Returns if a number system exists
     * @param number_system NumeralSystem Number system
     * @return Boolean True if it is a valid number system False if not
     */
    public static final boolean isValidNumberSystem(NumeralSystem number_system) {
        return number_system != null;
    }
    
    @Override
    public final String toString() {
        return String.format("Number: \"%s\", Number System: %s", number, number_system);
    }
    
    public final String toStringFormatted(String point) {
        return String.format("Number: \"%s\", Number System: %s", toStringFormattedNumberOnly(point), number_system);
    }
    
    public final String toStringFormattedNumberOnly(String point) {
            String temp = "";
            for(int i = 0; i < number.length(); i++) {
                String temp_ = "" + number.charAt(i);
                if((period != -1) && (i > (indexOfPoint() + period))) {
                    temp += PERIODSIGN;
                }
                if(temp_.equals(DOT) || temp_.equals(COMMA)) {
                    temp_ = point;
                }
                temp += temp_;
            }
            return temp;
    }
    
}
