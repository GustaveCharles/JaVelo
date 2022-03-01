package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {

    private Functions() {}

    public static DoubleUnaryOperator constant(double y){
        return (x) -> y;
    }

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
