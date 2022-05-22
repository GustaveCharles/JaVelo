package ch.epfl.javelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Creates objects representing mathematical functions of reals on reals
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public final class Functions {

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
     * @param samples altitude list
     * @param xMax    maximum value of x-coordinate
     * @return a linear interpolation
     * @throws IllegalArgumentException if the samples array contains less than two elements,
     *                                  or if xMax is less than or equal to 0.
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(!(samples.length < 2 || xMax <= 0));
        List<Float> temp = new ArrayList<>();
        for (Float f : samples) {
            temp.add(f);
        }
        List<Float> immutableSamples = List.copyOf(temp);
        return (x) -> {
            if (x <= 0) return immutableSamples.get(0);
            if (x >= xMax) return immutableSamples.get(immutableSamples.size() - 1);
            double interval = xMax / (immutableSamples.size() - 1);
            int position = (int) (x / interval);
            double y0 = immutableSamples.get(position);
            int nextPosition = Math2.clamp(0, position + 1, immutableSamples.size() - 1);
            double y1 = immutableSamples.get(nextPosition);
            double xProportional = (x % interval) / interval;
            return Math2.interpolate(y0, y1, xProportional);
        };
    }
}
