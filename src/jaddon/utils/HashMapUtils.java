/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.utils;

import java.util.HashMap;

/**
 *
 * @author Paul
 */
public class HashMapUtils {
    
    /**
     * This function concats many HashMap
     * @param <T> First Generic Type
     * @param <U> Second Generic Type
     * @param hashmaps HashMaps
     * @return HashMap resulting HashMap
     */
    public static final <T, U> HashMap<T, U> concatAll(HashMap<T, U>... hashmaps) {
        final HashMap<T, U> hashmap_result = new HashMap<>();
        for(HashMap<T, U> hashmap : hashmaps) {
            for(T t : hashmap.keySet()) {
                hashmap_result.put(t, hashmap.get(t));
            }
        }
        return hashmap_result;
    }
    
}
