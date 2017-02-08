/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

/**
 *
 * @author Paul
 */
public class MathFormatter {
    
    public static String formatNumberForComplex(double number) {
        return (((number < 0.0) ? "-" : "+") + " " + Math.abs(number));
    }
    
}
