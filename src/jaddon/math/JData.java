/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import jaddon.controller.StaticStandard;
import jaddon.exceptions.UnsupportedSizeException;

/**
 *
 * @author Paul Hagedorn
 */
public class JData {
    /**
     * Bit - 0
     */
    public static int b = 0;
    /**
     * Byte - 1
     */
    public static int B = 1;
    /**
     * Kilobit - 2
     */
    public static int Kb = 2;
    /**
     * Kilobyte - 3
     */
    public static int KB = 3;
    /**
     * Megabit - 4
     */
    public static int Mb = 4;
    /**
     * Megabyte - 5
     */
    public static int MB = 5;
    /**
     * Gigabit - 6
     */
    public static int Gb = 6;
    /**
     * Gigabyte - 7
     */
    public static int GB = 7;
    /**
     * Terabit - 8
     */
    public static int Tb = 8;
    /**
     * Terabyte - 9
     */
    public static int TB = 9;
    /**
     * Petabit - 10
     */
    public static int Pb = 10;
    /**
     * Petabyte - 11
     */
    public static int PB = 11;
    /**
     * Exabit - 12
     */
    public static int Eb = 12;
    /**
     * Exabyte - 13
     */
    public static int EB = 13;
    /**
     * Zettabit - 14
     */
    public static int Zb = 14;
    /**
     * Zettabyte - 15
     */
    public static int ZB = 15;
    /**
     * Yottabit - 16
     */
    public static int Yb = 16;
    /**
     * Yottabyte - 17
     */
    public static int YB = 17;
    
    
    
    /**
     * Bit - 18
     */
    public static int ib = 18;
    /**
     * Byte - 19
     */
    public static int iB = 19;
    /**
     * Kibibit - 20
     */
    public static int Kib = 20;
    /**
     * Kibibyte - 21
     */
    public static int KiB = 21;
    /**
     * Mebibit - 22
     */
    public static int Mib = 22;
    /**
     * Mebibyte - 23
     */
    public static int MiB = 23;
    /**
     * Gibibit - 24
     */
    public static int Gib = 24;
    /**
     * Gibibyte - 25
     */
    public static int GiB = 25;
    /**
     * Tebibit - 26
     */
    public static int Tib = 26;
    /**
     * Tebibyte - 27
     */
    public static int TiB = 27;
    /**
     * Pebibit - 28
     */
    public static int Pib = 28;
    /**
     * Pebibyte - 29
     */
    public static int PiB = 29;
    /**
     * Exbibit - 30
     */
    public static int Eib = 30;
    /**
     * Exbibyte - 31
     */
    public static int EiB = 31;
    /**
     * Zebibit - 32
     */
    public static int Zib = 32;
    /**
     * Zebibyte - 33
     */
    public static int ZiB = 33;
    /* 
     * Yobibit - 34
     */
    public static int Yib = 34;
    /**
     * Yobibyte - 35
     */
    public static int YiB = 35;
    
    
    
    /**
     * Make only decimal perfect numbers - 100
     */
    public static int DECIMAL_ONLY = 100;
    /**
     * Make only binary perfect numbers - 101
     */
    public static int BINARY_ONLY = 101;
    /**
     * Make decimal and binary perfect numbers - 102
     */
    public static int DECIMAL_AND_BINARY = 102;
    
    
    
    /**
     * Make only bit sizes - 103
     */
    public static int BIT_ONLY = 103;
    /**
     * Make only byte sizes - 104
     */
    public static int BYTE_ONLY = 104;
    /**
     * Make bit and byte sizes - 105
     */
    public static int BIT_AND_BYTE = 105;
    
    
    
    private final float bitToByteMultiplicator = 1.0F / 8.0F;
    private final float byteToBitMultiplicator = 8.0F / 1.0F;
    private final double close = 1.0;
    
    private double number = 0.0; //Standard = 0
    private int size = MB; //Standard = MB;
    private double number_last_converted = 0.0;
    private int size_last_converted = MB;
    private double number_perfect = 0.0;
    private int size_perfect = MB;
    
    private int perfect_DECBIN = DECIMAL_AND_BINARY;
    private int perfect_BITBYTE = BIT_AND_BYTE;
    private boolean perfect = false;
    
    public final static int[] sizes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
    public final static String[] names = {"Bit", "Byte", "Kilobit", "Kilobyte", "Megabit", "Megabyte", "Gigabit", "Gigabyte", "Terabit", "Terabyte", "Petabit", "Petabyte", "Exabit", "Exabyte", "Zettabit", "Zettabyte", "Yottabit", "Yottabyte", "Bit", "Byte", "Kibibit", "Kibibyte", "Mebibit", "Mebibyte", "Gibibit", "Gibibyte", "Tebibit", "Tebibyte", "Pebibit", "Pebibyte", "Exbibit", "Exbibyte", "Zebibit", "Zebibyte", "Yobibit", "Yobibyte"};
    public final static String[] names_short = {"b", "B", "Kb", "KB", "Mb", "MB", "Gb", "GB", "Tb", "TB", "Pb", "PB", "Eb", "EB", "Zb", "ZB", "Yb", "YB", "ib", "iB", "Kib", "KiB", "Mib", "MiB", "Gib", "GiB", "Tib", "TiB", "Pib", "PiB", "Eib", "EiB", "Zib", "ZiB", "Yib", "YiB"};
    
    /**
     * Constructs the object
     */
    public JData() {
        this(0);
    }
    
    /**
     * Constructs the object with a number in standard size
     * @param number The first Number
     */
    public JData(double number) {
        this(number, MB);
    }
    
    /**
     * This is the main constructor for the objects
     * @param number Double Number
     * @param size Integer Size
     */
    public JData(double number, int size){
        StaticStandard.setData(this);
        setNumber(number);
        try {
            setSize(size);
        } catch (UnsupportedSizeException ex) {
            System.err.println(ex);
        }
    }
    
    /**
     * Sets complete new number and size
     * @param number Double New number
     * @param size Integer New size
     */
    public JData setNumberAndSize(double number, int size) {
        setNumber(number);
        try {
            setSize(size);
        } catch (UnsupportedSizeException ex) {
            System.err.println(ex);
        }
        perfect = false;
        return this;
    }
    
    /**
     * Returns a number converted from actual size to given size
     * @param size Integer New Size
     * @return Double Converted Number
     * @throws UnsupportedSizeException Error if the size does not exist
     */
    public double convert(int size) throws UnsupportedSizeException {
        boolean sizeGood = false;
        if(containsInteger(sizes, size)) {
            sizeGood = true;
        } else if(size < 0) {
            throw new UnsupportedSizeException("The size has to be greater equal 0");
        } else {
            throw new UnsupportedSizeException("The size has to be less than " + sizes.length);
        }
        if(sizeGood) {
            boolean temp_1 = isSizeBit(this.size);
            boolean temp_2 = isSizeBit(size);
            boolean isDezi_1 = this.size < (sizes.length / 2);
            boolean isDezi_2 = size < (sizes.length / 2);
            double temp_number = this.number;
            if(temp_1 != temp_2) {
                if(temp_2) {
                    temp_number *= byteToBitMultiplicator;
                } else {
                    temp_number *= bitToByteMultiplicator;
                }
            }
            double mult_1 = (temp_1) ? ((this.size + ((isDezi_1) ? 0.0 : -18.0)) / 2.0) : (((this.size + ((isDezi_1) ? 0.0 : -18.0)) - 1.0) / 2.0);
            double mult_2 = (temp_2) ? ((size + ((isDezi_2) ? 0.0 : -18.0)) / 2.0) : (((size + ((isDezi_2) ? 0.0 : -18.0)) - 1.0) / 2.0);
            double temp_multiplicator_1 = 0.0;
            double temp_multiplicator_2 = 0.0;
            if(isDezi_1 && isDezi_2) {
                temp_multiplicator_1 = getDecimalPrefix(mult_1);
                temp_multiplicator_2 = getDecimalPrefix(mult_2);
            } else if(!isDezi_1 && !isDezi_2) {
                temp_multiplicator_1 = getBinaryPrefix(mult_1);
                temp_multiplicator_2 = getBinaryPrefix(mult_2);
            } else if(isDezi_1 && !isDezi_2) {
                temp_multiplicator_1 = getDecimalPrefix(mult_1);
                temp_multiplicator_2 = getBinaryPrefix(mult_2);
            } else if(!isDezi_1 && isDezi_2) {
                temp_multiplicator_1 = getBinaryPrefix(mult_1);
                temp_multiplicator_2 = getDecimalPrefix(mult_2);
            }
            temp_number = temp_number * (temp_multiplicator_1 / temp_multiplicator_2);
            number_last_converted = temp_number;
            size_last_converted = size;
            return temp_number;
        }
        return -1;
    }
    
    /**
     * Converts actual saved number to new size
     * @param size Integer Size
     * @throws UnsupportedSizeException Error if the size does not exists
     */
    public void convertThis(int size) throws UnsupportedSizeException {
        if(containsInteger(sizes, size)) {
            this.number = convert(size);
            this.size = size;
        } else if(size < 0) {
            throw new UnsupportedSizeException("The size has to be greater equal 0");
        } else {
            throw new UnsupportedSizeException("The size has to be less than " + sizes.length);
        }
    }
    
    /**
     * Makes the perfect number with presettings
     */
    public JData makePerfectNumber() {
        makePerfectNumber(perfect_DECBIN, perfect_BITBYTE);
        return this;
    }
    
    /**
     * Makes the perfect number, so that the number is close to zero
     * @param decOrBinOrAll Integer
     * @param bitOrByteOrAll Integer
     */
    public JData makePerfectNumber(int decOrBinOrAll, int bitOrByteOrAll) {
        setPerfectDECBIN(decOrBinOrAll);
        setPerfectBITBYTE(bitOrByteOrAll);
        int max_size = sizes.length;
        int min_size = 0;
        double max_size_mult = 1.0;
        if(decOrBinOrAll != DECIMAL_AND_BINARY) {
            if(decOrBinOrAll == BINARY_ONLY) {
                min_size = max_size / 2;
            } else if(decOrBinOrAll == DECIMAL_ONLY) {
                max_size /= 2;
            }
        }
        if(bitOrByteOrAll != BIT_AND_BYTE) {
            max_size_mult = 0.5;
        }
        double[] temp_d = new double[(int) (max_size * max_size_mult)];
        int z = 0;
        for(int i = min_size; i < max_size; i++) {
            boolean fg = true;
            if(bitOrByteOrAll == BYTE_ONLY) {
                if(i % 2 == 0) {
                    fg = false;
                }
            } else if(bitOrByteOrAll == BIT_ONLY) {
                if(i % 2 == 1) {
                    fg = false;
                }
            }
            if(fg) {
                try {
                    temp_d[z] = getNumber(i);
                    z++;
                } catch (UnsupportedSizeException ex) {
                    System.err.println(ex);
                }
            }
        }
        String num_temp = "" + number;
        String[] split = num_temp.split("\\.");
        if(split.length > 1) {
            while(split[0].startsWith("0")) {
                split[0] = split[0].replaceFirst("0", "");
            }
            num_temp = "";
            for(String g : split) {
                num_temp += g;
            }
        }
        num_temp = num_temp.replaceAll("\\.", "");
        if(num_temp.length() < 2) {
            num_temp += "00";
        } else if(num_temp.length() < 3) {
            num_temp += "0";
        }
        int num = -1;
        int i = 0;
        int i_2 = min_size;
        int num_number = -1;
        double closest = temp_d[0];
        boolean found = false;
        for(double g : temp_d) {
            double temp_c = Math.abs(g);
            if(temp_c <= closest && temp_c >= 1.0) {
                num = i; 
                num_number = ((bitOrByteOrAll == BYTE_ONLY) ? (i_2 * 2 + 1) : (bitOrByteOrAll == BIT_ONLY) ? (i_2 * 2) : i_2);
                closest = temp_c;
                found = true;
            }
            i_2++;
            i++;
        }
        if(!found) {
            number_perfect = this.number;
            size_perfect = this.size;
            perfect = true;
        } else {
            number_perfect = temp_d[num];
            size_perfect = num_number;
            perfect = true;
        }
        return this;
    }
    
    /**
     * Returns the perfect number
     * @return Double Perfect number
     */
    public double getPerfectNumber() {
        if(!perfect) {
            makePerfectNumber();
        }
        return number_perfect;
    }
    
    /**
     * Returns the perfect size
     * @return Integer Perfect size
     */
    public int getPerfectSize() {
        if(!perfect) {
            makePerfectNumber();
        }
        return size_perfect;
    }
    
    /**
     * Returns the binary prefix of the multiplicator
     * @param mult Double Multiplicator
     * @return Double The binary prefix of the multiplicator
     */
    public double getBinaryPrefix(double mult) {
        return Math.pow(2.0, 10.0 * mult);
    }
    
    /**
     * Returns the decimal prefix of the multiplicator
     * @param mult Double Multiplicator
     * @return Double The decimal prefix of the multiplicator
     */
    public double getDecimalPrefix(double mult) {
        return Math.pow(10.0, 3.0 * mult);
    }
    
    /**
     * Returns the multiplicator to get from decimal prefix to binary prefix
     * @param decMult Double Decimal multiplicator
     * @return Double Decimal to Binary Multiplicator
     */
    public double getDecToBin(double decMult) {
        return getDecimalPrefix(decMult) / getBinaryPrefix(decMult);
    }
    
    /**
     * Returns the multiplicator to get from binary prefix to decimal prefix
     * @param binMult Double Binary multiplicator
     * @return Double Binary to Decimal Multiplicator
     */
    public double getBinToDec(double binMult) {
        return getBinaryPrefix(binMult) / getDecimalPrefix(binMult);
    }
    
    /**
     * Returns if given size is in bit or in byte
     * @param size Integer SIze to be testet
     * @return Boolean True if it is bit, and false if it is byte
     * @throws UnsupportedSizeException Error if the size does not exist;
     */
    public boolean isSizeBit(int size) throws UnsupportedSizeException {
        if(containsInteger(sizes, size)) {
            return (size % 2 == 0);
        } else if(size < 0) {
            throw new UnsupportedSizeException("The size has to be greater equal 0");
        } else {
            throw new UnsupportedSizeException("The size has to be less than " + sizes.length);
        }
    }

    /**
     * Returns the multiplicator to go from bit to byte
     * @return Float Multiplicator to go from bit to byte
     */
    public float getBitToByteMultiplicator() {
        return bitToByteMultiplicator;
    }

    /**
     * Returns the multiplicator to go from byte to bit
     * @return Float Multiplicator to go from byte to bit
     */
    public float getByteToBitMultiplicator() {
        return byteToBitMultiplicator;
    }

    /**
     * Returns the actual saved number in format of the actual size
     * @return Double Actual saved number in actual size
     */
    public double getNumber() {
        return number;
    }
    
    /**
     * Returns the actual saved number in format of the size which was given
     * @param size Integer Size to format
     * @return Double Actual saved number in own size
     * @throws UnsupportedSizeException Error if the size does not exist
     */
    public double getNumber(int size) throws UnsupportedSizeException {
        if(containsInteger(sizes, size)) {
            return convert(size);
        } else if(size < 0) {
            throw new UnsupportedSizeException("The size has to be greater equal 0");
        } else {
            throw new UnsupportedSizeException("The size has to be less than " + sizes.length);
        }
    }
    
    /**
     * Sets the number
     * @param number Double Number
     */
    public JData setNumber(double number) {
        this.number = number;
        perfect = false;
        return this;
    }
    
    /**
     * Adds a number to the actual number
     * @param number Double Added number
     */
    public JData addNumber(double number) {
        return setNumber(this.number + number);
    }
    
    /**
     * Adds a size to the actual size
     * @param size Integer Added size
     */
    public JData addSize(int size) throws UnsupportedSizeException {
        return setSize(this.size + size);
    }
    
    /**
     * Adds a size and a number to the actual stats
     * @param number Double Added number
     * @param size Integer Added size
     */
    public JData addNumberAndSize(double number, int size) throws UnsupportedSizeException {
        addNumber(number);
        addSize(size);
        return this;
    }
    
    /**
     * Adds two data objects together
     * @param data JData data
     */
    public JData addData(JData data) {
        try {
            addNumber(data.getNumber(this.size));
            return this;
        } catch (UnsupportedSizeException ex) {
            return null;
        }
    }

    /**
     * Gets the actual size
     * @return Int Actual size
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Returns the short name of the actual size
     * @return String Short name of the actual size
     */
    public String getLocalizedSizeShort() {
        return names_short[this.size];
    }
    
    /**
     * Returns the short name of a size
     * @param size Integer Size
     * @return String Short name of the size
     * @throws UnsupportedSizeException Error if the size does not exist
     */
    public String getLocalizedSizeShort(int size) throws UnsupportedSizeException {
        if(containsInteger(sizes, size)) {
            return names_short[size];
        } else if(size < 0) {
            throw new UnsupportedSizeException("The size has to be greater equal 0");
        } else {
            throw new UnsupportedSizeException("The size has to be less than " + sizes.length);
        }
    }
    
    /**
     * Returns the name of the actual size
     * @return String Name of the actual size
     */
    public String getLocalizedSizeName() {
        return names[this.size];
    }
    
    /**
     * Returns the name of a size
     * @param size Integer Size
     * @return String Name of the size
     * @throws UnsupportedSizeException Error if the size does not exist
     */
    public String getLocalizedSizeName(int size) throws UnsupportedSizeException {
        if(containsInteger(sizes, size)) {
            return names[size];
        } else if(size < 0) {
            throw new UnsupportedSizeException("The size has to be greater equal 0");
        } else {
            throw new UnsupportedSizeException("The size has to be less than " + sizes.length);
        }
    }
    
    /**
     * Returns the int of an size or -1 if it does not exist
     * @param name String Name of a size to be searched for
     * @return Integer Size
     */
    public int getSizeFromName(String name) {
        int i = 0;
        for(String g : names) {
            if(g.equalsIgnoreCase(name)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Sets the size
     * @param size Int New size
     * @throws UnsupportedSizeException Error if the size does not exist
     */
    public JData setSize(int size) throws UnsupportedSizeException {
        if(containsInteger(sizes, size)) {
            this.size = size;
            perfect = false;
        } else if(size < 0) {
            throw new UnsupportedSizeException("The size has to be greater equal 0");
        } else {
            throw new UnsupportedSizeException("The size has to be less than " + sizes.length);
        }
        return this;
    }
    
    /**
     * Searches for an object in an object array
     * @param array Object Array which is searched
     * @param toSearch Object For search
     * @return Boolean if the object array contains the object
     */
    public static boolean containsObject(Object[] array, Object toSearch) {
        for(Object g : array) {
            if(g.equals(toSearch)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Searches for an integer in an integer array
     * @param array Integer Array which is searched
     * @param toSearch Integer For search
     * @return Boolean if the integer array contains the integer
     */
    public static boolean containsInteger(int[] array, int toSearch) {
        for(int g : array) {
            if(g == toSearch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the last converted number
     * @return Double Last number converted
     */
    public double getLastConvertedNumber() {
        return number_last_converted;
    }

    /**
     * Returns the last converted size
     * @return Integer Last size converted
     */
    public int getLastConvertedSize() {
        return size_last_converted;
    }

    /**
     * Returns the state for perfect numbers for decimal or binary
     * @return Integer State
     */
    public int getPerfectDECBIN() {
        return perfect_DECBIN;
    }

    /**
     * Sets the state for perfect numbers for decimal or binary
     * @param perfect_DECBIN Integer decimal or binary or both
     */
    public JData setPerfectDECBIN(int perfect_DECBIN) {
        this.perfect_DECBIN = perfect_DECBIN;
        perfect = false;
        return this;
    }

    /**
     * Returns the state for perfect numbers for bit or byte
     * @return Integer State
     */
    public int getPerfectBITBYTE() {
        return perfect_BITBYTE;
    }

    /**
     * Sets the state for perfect numbers for bit or byte
     * @param perfect_BITBYTE Integer bit or byte or both
     */
    public JData setPerfectBITBYTE(int perfect_BITBYTE) {
        this.perfect_BITBYTE = perfect_BITBYTE;
        perfect = false;
        return this;
    }
}
