/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import java.util.Arrays;

/**
 *
 * @author Paul
 */
public class ArrayUtils {
    
    /**
     * This function concats two Arrays
     * http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
     * @param <T> Generic type
     * @param first First Array
     * @param second Second Array
     * @return Concatenated Array
     */
    public static final <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    /**
     * This function concats multiple Arrays
     * http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
     * @param <T> Generic Type
     * @param first First Array
     * @param rest Many other Arrays
     * @return Concatenated Array
     */
    public static final <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
          totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
          System.arraycopy(array, 0, result, offset, array.length);
          offset += array.length;
        }
        return result;
    }
    
    /**
     * This function returns the index of an object in an array
     * @param <T> Generic Type
     * @param array Array
     * @param object Object to search for
     * @return Integer Position (-1 for not existing)
     */
    public static final <T> int indexOf(T[] array, T object) {
        if(array == null) {
            return -1;
        }
        for(int i = 0; i < array.length; i++) {
            if(array[i].equals(object)) {
                return i;
            }
        }
        return -1;
    }
    
}
