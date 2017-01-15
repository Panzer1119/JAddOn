/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_UP;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 *
 * @author Paul
 */
public class JBigNumber {
    
    private static final BigDecimal SQRT_DIG = new BigDecimal(150);
    private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());
    private static final BigDecimal TWO = new BigDecimal(2);
    public static final double LOG2 = Math.log(2.0);

    /**
     * Private utility method used to compute the square root of a BigDecimal.
     * See http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
     * 
     * @author Luciano Culacciatti 
     */
    private static BigDecimal sqrtNewtonRaphson(BigDecimal c, BigDecimal xn, BigDecimal precision){
        BigDecimal fx = xn.pow(2).add(c.negate());
        BigDecimal fpx = xn.multiply(new BigDecimal(2));
        BigDecimal xn1 = fx.divide(fpx, 2 * SQRT_DIG.intValue(), RoundingMode.HALF_DOWN);
        xn1 = xn.add(xn1.negate());
        BigDecimal currentSquare = xn1.pow(2);
        BigDecimal currentPrecision = currentSquare.subtract(c);
        currentPrecision = currentPrecision.abs();
        if(currentPrecision.compareTo(precision) <= -1){
            return xn1;
        }
        return sqrtNewtonRaphson(c, xn1, precision);
    }

    /**
     * Uses Newton Raphson to compute the square root of a BigDecimal.
     * See http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
     * 
     * @author Luciano Culacciatti 
     * @param c BigDecimal Number
     * @return BigDecimal Square root
     */
    public static BigDecimal bigSqrt(BigDecimal c){
        return sqrtNewtonRaphson(c, new BigDecimal(1), new BigDecimal(1).divide(SQRT_PRE));
    }
    
    public static BigDecimal sqrt(BigDecimal A, final int SCALE) {
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = new BigDecimal(Math.sqrt(A.doubleValue()));
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, SCALE, ROUND_HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(TWO, SCALE, ROUND_HALF_UP);

        }
        return x1;
    }
    
    public static BigInteger sqrtN(BigInteger in) {
        BigInteger TWOI = BigInteger.valueOf(2);
        int c;

        // Significantly speed-up algorithm by proper select of initial approximation
        // As square root has 2 times less digits as original value
        // we can start with 2^(length of N1 / 2)
        BigInteger n0 = TWOI.pow(in.bitLength() / 2);
        // Value of approximate value on previous step
        BigInteger np = in;

        do {
            // next approximation step: n0 = (n0 + in/n0) / 2
            n0 = n0.add(in.divide(n0)).divide(TWOI);

            // compare current approximation with previous step
            c = np.compareTo(n0);

            // save value as previous approximation
            np = n0;

            // finish when previous step is equal to current
        }  while (c != 0);

        return n0;
    }
    
    public static boolean isPrimeNumber(BigInteger bi) {
        boolean run = true;
        boolean isPrime = false;
        BigInteger test = new BigInteger(bi.toString());
        BigInteger ZERO = BigInteger.valueOf(0);
        BigInteger ONE = BigInteger.valueOf(1);
        BigInteger TWOI = BigInteger.valueOf(2);
        while(run) {
            test.subtract(ONE);
            if(bi.mod(test) == ZERO) {
                isPrime = false;
                run = false;
            }
            if(test.compareTo(TWOI) == -1) {
                isPrime = true;
                run = false;
            }
        }
        return isPrime;
    }
    
    public static double logBigInteger(BigInteger val) {
        int blex = val.bitLength() - 1022; // any value in 60..1023 is ok
        if(blex > 0) {
            val = val.shiftRight(blex);
        }
        double res = Math.log(val.doubleValue());
        return blex > 0 ? res + blex * LOG2 : res;
    }

}
