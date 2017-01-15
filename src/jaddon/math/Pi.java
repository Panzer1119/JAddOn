/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 *
 * @author Paul
 */
public class Pi {
    
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal FOUR = new BigDecimal("4");
    private static final BigDecimal FIVE = new BigDecimal("5");
    private static final BigDecimal TWO_THIRTY_NINE = new BigDecimal("239");
    
    public static int test = 0;

    public static BigDecimal calculatePi(int numDigits) {
        final int calcDigits = numDigits + 10;
        final BigDecimal pi = FOUR.multiply((FOUR.multiply(arccot(FIVE, calcDigits))).subtract(arccot(TWO_THIRTY_NINE, calcDigits))).setScale(numDigits, RoundingMode.DOWN);
        return pi;
    }
    
    private static BigDecimal arccot(BigDecimal x, int numDigits) {
        final BigDecimal unity = BigDecimal.ONE.setScale(numDigits, RoundingMode.DOWN);
        BigDecimal sum = unity.divide(x, RoundingMode.DOWN);
        BigDecimal xpower = new BigDecimal(sum.toString());
        BigDecimal term = null;
        boolean add = false;
        for(BigDecimal n = new BigDecimal("3"); term == null || term.compareTo(BigDecimal.ZERO) != 0; n = n.add(TWO)) {
            xpower = xpower.divide(x.pow(2), RoundingMode.DOWN);
            term = xpower.divide(n, RoundingMode.DOWN);
            sum = ((add) ? sum.add(term) : sum.subtract(term));
            add = !add;
            test++;
        }
        return sum;
    }
    
    private static BigDecimal calculatePiactivevb(BigInteger steps) {
        BigDecimal pi = BigDecimal.ZERO;
        for(BigInteger i = BigInteger.ONE; i.compareTo(steps) == -1; i.add(BigInteger.ONE)) {
            pi.add(BigDecimal.ONE.divide(new BigDecimal(i.toString()).pow(2)));
        }
        pi.multiply(new BigDecimal("6"));
        pi = JBigNumber.sqrt(pi, pi.scale());
        return pi;
    }
    
}
