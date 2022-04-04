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

    private Functions() {
    }

    /**
     * Returns a constant function, for whose value is always y
     *
     * @param y a function
     * @return the y-function
     */
    public static DoubleUnaryOperator constant(double y) {
        return (x) -> y;
    }

    /**
     * Returns a function obtained by linear interpolation between samples,
     * regularly spaced and covering the range from 0 to xMax;
     *
     * @param samples
     * @param xMax
     * @return a function obtain by linear interpolation between samples
     * @throws IllegalArgumentException if the samples array contains less than two elements,
     *                                  or if xMax is less than or equal to 0.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {

        Preconditions.checkArgument(!(samples.length < 2 || xMax <= 0));

        return (x) -> {
            double interval = xMax / (samples.length - 1);
            if (x <= 0) {
                return samples[0];
            } else if (x >= xMax) {
                return samples[samples.length - 1];
            }

            int i1 = (int) (x / interval);
            double y0 = samples[i1];
            double y1 = samples[i1 + 1];
            double b = (x % interval) / interval;

            return Math2.interpolate(y0, y1, b);
        };
    }

}
