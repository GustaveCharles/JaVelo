package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Create objects representing mathematical functions of reals on reals
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
//TODO demander si utiliser le triple ternary operator ou pas
public final class Functions {

    /**
     * Private constructor of Functions in order to keep the class not instantiable
     */
    private Functions() {
    }

    /**
     * Returns a constant function, for whose value is always y
     *
     * @param y a function
     * @return the constant y-function
     */
    public static DoubleUnaryOperator constant(double y) {
        return (x) -> y;
    }

    /**
     * Returns a function obtained by linear interpolation between samples,
     * regularly spaced and covering the range from 0 to xMax;
     *
     * @param samples list of altitude
     * @param xMax maximum value of x-coordinate
     * @return a linear interpolation
     * @throws IllegalArgumentException if the samples array contains less than two elements,
     *                                  or if xMax is less than or equal to 0.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(!(samples.length < 2 || xMax <= 0));
        return (x) -> {
            if (x <= 0) {
                return samples[0];
            } else if (x >= xMax) {
                return samples[samples.length - 1];
            }
            double interval = xMax / (samples.length - 1);
            int position = (int) (x / interval);
            double y0 = samples[position];
            double y1 = samples[position + 1];
            double xProportional = (x % interval) / interval;

            return Math2.interpolate(y0, y1, xProportional);
        };
    }
}
