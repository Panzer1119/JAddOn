/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import java.util.ArrayList;

/**
 * This is a class with mathematical functions
 * @author Paul Hagedorn
 */
public class JFunction {
    
    /**
     * Returns the binomialcoefficient of two numbers
     * @param n Integer Number of element mass
     * @param k Integer Number of element half mass
     * @return Integer Binomialcoefficient
     */
    public static int binomialCoefficient(int n, int k) {
        if(k == 0) {
            return 1;
        } else if((2.0 * k) > n) {
            return binomialCoefficient(n, n - k);
        } else {
            int erg = n - k + 1;
            for(int i = 2; i <= k; i++) {
                erg *= (n - k + i);
                erg /= i;
            }
            return erg;
        }
    }
    
    /**
     * Returns the sum of all numbers (inclusive) between the two given numbers (This is based on the gauss algorithm, but improved by paul hagedorn)
     * @param number1 Double First number
     * @param number2 Double Second number
     * @return Double Sum of all numbers (inclusive) between the two given numbers
     */
    public static Double getGaussPaulSum(Double number1, Double number2) {
        Double min = Math.min(number1, number2);
        Double max = Math.max(number1, number2);
        Double sum = ((max - min) / 2 + 0.5) * (max + min);
        return sum;
    }
    
    /**
     * Returns all prime numbers (inclusive) between two given numbers
     * @param from Integer From this number
     * @param to Integer To this number
     * @return ArrayList Integer Prime numbers (inclusive) between the two given numbers
     */
    public static ArrayList<Integer> primeNumbers(int from, int to) {
        ArrayList<Integer> primenumber = new ArrayList<>();
        ArrayList<Integer> primenumber_not = new ArrayList<>();
        for(int i = 2; i <= to; i++) {
            primenumber.add(i);
        }
        for(int i = 0; i < primenumber.size(); i++) {
            int z = primenumber.get(i);
            if(Math.pow(z, 2) > to) {
                break;
            }
            int test = -1;
            for(int x = 2; test < primenumber.get(primenumber.size() - 1); x++) {
                test = x * z;
                if(!primenumber_not.contains(test)) {
                    primenumber.remove((Object) test);
                    primenumber_not.add(test);
                }
            }
        }
        for(int i = 0; i < from; i++) {
            if(primenumber.contains(i)) {
                primenumber.remove((Object) i);
                primenumber_not.add(i);
            }
        }
        return primenumber;
    }
    
}
