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
public class Quaternion {
    
    public static Quaternion ZERO = new Quaternion(0.0, 0.0, 0.0, 0.0);
    
    private double q0;
    private double q1;
    private double q2;
    private double q3;

    public Quaternion(double q0, double q1, double q2, double q3) {
        this.q0 = q0;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
    }
    
    public Quaternion(double scalar, double[] vector) {
        this(scalar, vector[0], vector[1], vector[2]);
    }
    
    public Quaternion(double[] vector) {
        this(0, vector);
    }
    
    public Quaternion copy() {
        return new Quaternion(q0, q1, q2, q3);
    }
    
    public Quaternion add(Quaternion q) {
        return new Quaternion(q0 + q.q0,
                              q1 + q.q1,
                              q2 + q.q2,
                              q3 + q.q3);
    }
    
    public Quaternion subtract(Quaternion q) {
        return new Quaternion(q0 - q.q0,
                              q1 - q.q1,
                              q2 - q.q2,
                              q3 - q.q3);
    }
    
    public Quaternion multiply(Quaternion q) {
        return new Quaternion((q0 * q.q0) - (q1 * q.q1) - (q2 * q.q2) - (q3 * q.q3),
                              (q0 * q.q1) + (q1 * q.q0) + (q2 * q.q3) - (q3 * q.q2),
                              (q0 * q.q2) - (q1 * q.q3) + (q2 * q.q0) + (q3 * q.q1),
                              (q0 * q.q3) + (q1 * q.q2) - (q2 * q.q1) + (q3 * q.q0));
    }
    
    public Quaternion multiply(double a) {
        return new Quaternion(q0 * a,
                              q1 * a,
                              q2 * a,
                              q3 * a);
    }
    
    public Quaternion getConjugate() {
        return new Quaternion(q0 * +1.0,
                              q1 * -1.0,
                              q2 * -1.0,
                              q3 * -1.0);
    }
    
    public Quaternion getInverse() {
        final double squaredNorm = getBigNorm();
        return new Quaternion(q0 / squaredNorm * +1.0,
                              q1 / squaredNorm * -1.0,
                              q2 / squaredNorm * -1.0,
                              q3 / squaredNorm * -1.0);
    }
    
    public Quaternion normalize() {
        final double norm = getNorm();
        return new Quaternion(q0 / norm,
                              q1 / norm,
                              q2 / norm,
                              q3 / norm);
    }
    
    public Quaternion getPositivePolarForm() {
        if(q0 < 0.0) {
            Quaternion q = normalize();
            return new Quaternion(q.q0 * -1.0,
                                  q.q1 * -1.0,
                                  q.q2 * -1.0,
                                  q.q3 * -1.0);
        } else {
            return this.normalize();
        }
    }
    
    public Quaternion ln() {
        final double norm = getNorm();
        return copy().multiply(1.0 / norm).multiply(Math.acos(getScalarPart() / norm)).add(Quaternion.ofDouble(Math.log(norm)));
    }
    
    public double getNorm() {
        return Math.sqrt(getBigNorm());
    }
    
    public double getBigNorm() {
        return ((q0 * q0) + (q1 * q1) + (q2 * q2) + (q3 * q3));
    }
    
    public boolean isUnitQuaternion() {
        return getNorm() == 1.0;
    }
    
    public boolean isPureQuaternion() {
        return q0 == 0.0;
    }
    
    public double dotProduct(Quaternion q) {
        return (q0 * q.q0) + (q1 * q.q1) + (q2 * q.q2) + (q3 * q.q3);
    }

    public double getQ0() {
        return q0;
    }

    public Quaternion setQ0(double q0) {
        this.q0 = q0;
        return this;
    }
    
    public Quaternion addQ0(double add) {
        q0 += add;
        return this;
    }

    public double getQ1() {
        return q1;
    }

    public Quaternion setQ1(double q1) {
        this.q1 = q1;
        return this;
    }
    
    public Quaternion addQ1(double add) {
        q1 += add;
        return this;
    }

    public double getQ2() {
        return q2;
    }

    public Quaternion setQ2(double q2) {
        this.q2 = q2;
        return this;
    }
    
    public Quaternion addQ2(double add) {
        q2 += add;
        return this;
    }

    public double getQ3() {
        return q3;
    }

    public Quaternion setQ3(double q3) {
        this.q3 = q3;
        return this;
    }
    
    public Quaternion addQ3(double add) {
        q3 += add;
        return this;
    }
    
    public double getScalarPart() {
        return q0;
    }
    
    public double[] getVectorPart() {
        return new double[] {q1, q2, q3};
    }

    @Override
    public String toString() {
        return String.format("(%f %si %sj %sk)", q0, MathFormatter.formatNumberForComplex(q1), MathFormatter.formatNumberForComplex(q2), MathFormatter.formatNumberForComplex(q3));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj instanceof Quaternion) {
            Quaternion q = (Quaternion) obj;
            return (this.getQ0() == q.getQ0()) && (this.getQ1() == q.getQ1()) && (this.getQ2() == q.getQ2()) && (this.getQ3() == q.getQ3());
        } else {
            return false;
        }
    }
    
    public static Quaternion ofInt(int q0) {
        return ofInt(q0, 0, 0, 0);
    }
    
    public static Quaternion ofInt(int q0, int q1, int q2, int q3) {
        return new Quaternion(q0, q1, q2, q3);
    }
    
    public static Quaternion ofLong(long q0) {
        return ofLong(q0, 0L, 0L, 0L);
    }
    
    public static Quaternion ofLong(long q0, long q1, long q2, long q3) {
        return new Quaternion(q0, q1, q2, q3);
    }
    
    public static Quaternion ofFloat(float q0) {
        return ofFloat(q0, 0F, 0F, 0F);
    }
    
    public static Quaternion ofFloat(float q0, float q1, float q2, float q3) {
        return new Quaternion(q0, q1, q2, q3);
    }
    
    public static Quaternion ofDouble(double q0) {
        return ofDouble(q0, 0.0, 0.0, 0.0);
    }
    
    public static Quaternion ofDouble(double q0, double q1, double q2, double q3) {
        return new Quaternion(q0, q1, q2, q3);
    }
    
}
