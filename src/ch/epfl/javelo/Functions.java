package ch.epfl.javelo;
import java.util.function.DoubleUnaryOperator;

/**
 * Create objects representing mathematical functions of reals on reals
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public final class Functions {

    private Functions() {}

    /**
     * Returns a constant function, for whose value is always y
     * @param y a function
     * @return the y-function
     */
    public static DoubleUnaryOperator constant(double y){
        return (x) -> y;
    }

    /**
     * Returns a function obtained by linear interpolation between samples,
     * regularly spaced and covering the range from 0 to xMax;
     * @param samples
     * @param xMax
     * @throws IllegalArgumentException if the samples array contains less than two elements,
     * or if xMax is less than or equal to 0.
     * @return a function obtain by linear interpolation between samples
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){

        if (samples.length < 2 || xMax <= 0){
            throw new IllegalArgumentException();
        }

        return (x) -> {
            double interval = xMax/samples.length;
            double sampled = 0;
            if (x<0){
                return samples[0];
            } else if (x>=xMax){
                return samples[samples.length-1];
            }
            for (int i=0; i<xMax; interval++){
                if (i<=x && i+interval>=x){
                    double y1 = samples[i+1];
                    double y0 = samples[i];
                    double xCoordinate = (x - i)/interval;
                    sampled = Math2.interpolate(y0,y1,xCoordinate);
                }
            }
            return sampled;
        };
    }

}
