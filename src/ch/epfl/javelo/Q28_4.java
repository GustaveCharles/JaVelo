package ch.epfl.javelo;

import static java.lang.Math.scalb;

/**
 * Convert numbers between Q28.4 representation and other representations (Int, Double, Float)
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public final class Q28_4 {

    private Q28_4() {
    }

    /**
     * Gives the value of an integer in the Q28.4 representation
     *
     * @param i an integer
     * @return an integer in the Q28.4 representation
     */
    public static int ofInt(int i) {
        return i << 4;
    }

    /**
     * Gives the decimal value corresponding to a number in the Q28.4 representation
     *
     * @param q28_4 a number in the 28.4 representation
     * @return a decimal number
     */
    public static double asDouble(int q28_4) {
        return scalb((double) q28_4, -4);
    }

    /**
     * Gives the floating-point number corresponding to a number in the Q28.4 representation
     *
     * @param q28_4 a number in the 28.4 representation
     * @return a floating-point number
     */
    public static float asFloat(int q28_4) {
        return scalb(q28_4, -4);
    }

}
