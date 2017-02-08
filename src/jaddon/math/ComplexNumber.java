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
public class ComplexNumber {
    
    public static final ComplexNumber ZEROPLUSZEROI = new ComplexNumber(0.0, 0.0);
    public static final ComplexNumber ZEROPLUSONEI = new ComplexNumber(0.0, 1.0);
    public static final ComplexNumber ONEPLUSZEROI = new ComplexNumber(1.0, 0.0);
    public static final ComplexNumber ONEPLUSONEI = new ComplexNumber(1.0, 1.0);
    
    private double real_part;
    private double imaginary_part;
    
    public ComplexNumber(double real_part, double imaginary_part) {
        setRealPart(real_part);
        setImaginaryPart(imaginary_part);
    }
    
    public ComplexNumber copy() {
        return new ComplexNumber(getRealPart(), getImaginaryPart());
    }
    
    public ComplexNumber add(ComplexNumber complexnumber) {
        ComplexNumber complexnumber_new = copy();
        complexnumber_new.setRealPart(getRealPart() + complexnumber.getRealPart());
        complexnumber_new.setImaginaryPart(getImaginaryPart() + complexnumber.getImaginaryPart());
        return complexnumber_new;
    }
    
    public ComplexNumber subtract(ComplexNumber complexnumber) {
        ComplexNumber complexnumber_new = copy();
        complexnumber_new.setRealPart(getRealPart() - complexnumber.getRealPart());
        complexnumber_new.setImaginaryPart(getImaginaryPart() - complexnumber.getImaginaryPart());
        return complexnumber_new;
    }
    
    public ComplexNumber multiply(ComplexNumber complexnumber) {
        ComplexNumber complexnumber_new = copy();
        complexnumber_new.setRealPart((getRealPart() * complexnumber.getRealPart()) - (getImaginaryPart() * complexnumber.getImaginaryPart()));
        complexnumber_new.setImaginaryPart((getRealPart() * complexnumber.getImaginaryPart()) + (getImaginaryPart() * complexnumber.getRealPart()));
        return complexnumber_new;
    }
    
    public ComplexNumber divide(ComplexNumber complexnumber) {
        ComplexNumber complexnumber_new = copy();
        complexnumber_new.setRealPart(((getRealPart() * complexnumber.getRealPart()) + (getImaginaryPart() * complexnumber.getImaginaryPart())) / ((complexnumber.getRealPart() * complexnumber.getRealPart()) + (complexnumber.getImaginaryPart() * complexnumber.getImaginaryPart())));
        complexnumber_new.setImaginaryPart(((getImaginaryPart() * complexnumber.getRealPart()) - (getRealPart() * complexnumber.getImaginaryPart())) / ((complexnumber.getRealPart() * complexnumber.getRealPart()) + (complexnumber.getImaginaryPart() * complexnumber.getImaginaryPart())));
        return complexnumber_new;
    }
    
    public double getNorm() {
        return Math.sqrt((getRealPart() * getRealPart()) + (getImaginaryPart() * getImaginaryPart()));
    }
    
    public double getNormBig() {
        return ((getRealPart() * getRealPart()) + (getImaginaryPart() * getImaginaryPart()));
    }
    
    public double arg() {
        return Math.atan2(getImaginaryPart(), getRealPart());
    }
    
    public ComplexNumber pow(int n) {
        return pow(new ComplexNumber(n, 0.0));
    }
    
    public ComplexNumber pow(double n) {
        return pow(new ComplexNumber(n, 0.0));
    }
    
    public ComplexNumber pow(ComplexNumber complexnumber) {
        ComplexNumber complexnumber_new = new ComplexNumber(Math.cos((complexnumber.getRealPart() * arg()) + (0.5 * complexnumber.getImaginaryPart() * Math.log(getNormBig()))), Math.sin((complexnumber.getRealPart() * arg()) + (0.5 * complexnumber.getImaginaryPart() * Math.log(getNormBig()))));
        complexnumber_new = complexnumber_new.multiply(new ComplexNumber(Math.pow(getNormBig(), complexnumber.getRealPart() * 0.5), 0.0));
        complexnumber_new = complexnumber_new.multiply(new ComplexNumber(Math.pow(Math.E, -1.0 * complexnumber.getImaginaryPart() * arg()), 0.0));
        return complexnumber_new;
    }
    
    public ComplexNumber powe() {
        return new ComplexNumber(Math.cos(getImaginaryPart()), Math.sin(getImaginaryPart())).multiply(new ComplexNumber(Math.pow(Math.E, getRealPart()), 0.0));
    }

    public double getRealPart() {
        return real_part;
    }

    public ComplexNumber setRealPart(double real_part) {
        this.real_part = real_part;
        return this;
    }

    public double getImaginaryPart() {
        return imaginary_part;
    }

    public ComplexNumber setImaginaryPart(double imaginary_part) {
        this.imaginary_part = imaginary_part;
        return this;
    }
    
    @Override
    public boolean equals(Object object) {
        if(object instanceof ComplexNumber) {
            ComplexNumber complexnumber = (ComplexNumber) object;
            return ((getRealPart() == complexnumber.getRealPart()) && (getImaginaryPart() == complexnumber.getImaginaryPart()));
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("(%f %s %fi)", getRealPart(), ((getImaginaryPart() < 0.0) ? "-" : "+"), Math.abs(getImaginaryPart()));
    }
    
    public static ComplexNumber ofInt(int real_part) {
        return ofInt(real_part, 0);
    }
    
    public static ComplexNumber ofInt(int real_part, int imaginary_part) {
        return new ComplexNumber(real_part, imaginary_part);
    }
    
    public static ComplexNumber ofLong(long real_part) {
        return ofLong(real_part, 0L);
    }
    
    public static ComplexNumber ofLong(long real_part, long imaginary_part) {
        return new ComplexNumber(real_part, imaginary_part);
    }
    
    public static ComplexNumber ofFloat(float real_part) {
        return ofFloat(real_part, 0.0F);
    }
    
    public static ComplexNumber ofFloat(float real_part, float imaginary_part) {
        return new ComplexNumber(real_part, imaginary_part);
    }
    
    public static ComplexNumber ofDouble(double real_part) {
        return ofDouble(real_part, 0.0);
    }
    
    public static ComplexNumber ofDouble(double real_part, double imaginary_part) {
        return new ComplexNumber(real_part, imaginary_part);
    }
    
}
