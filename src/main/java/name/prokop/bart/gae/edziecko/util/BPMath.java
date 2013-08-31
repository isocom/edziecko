/*
 * BPMath.java
 *
 * Created on 23 styczeń 2005, 12:33
 */
package name.prokop.bart.gae.edziecko.util;

/**
 *
 * @author bart
 */
public class BPMath {

    public static void main(String[] args) {
        System.out.println(round(10.50, 0));
        System.out.println(roundCurrency(1.4449999999999999999));
    }

    public static double roundCurrency(double val) {
        return round(val + 0.000001, 2);
    }

    public static double round(double val, int radix) {
        double shift = Math.pow(10, radix);
        return Math.round(val * shift) / shift;
    }

    public static long truncate(double x) {
        return (long) x;
    }

    public static double fractional(double x) {
        return x - truncate(x);
    }
}
