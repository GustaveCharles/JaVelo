package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.DoubleSummaryStatistics;

public final class ElevationProfile {

    private final double length;
    private final float[] elevationSamples = {};

    ElevationProfile(double length, float[] elevationSamples){
        this.length = length;

        if (length <= 0 || elevationSamples.length < 2){
            throw new IllegalArgumentException();
        }

        System.arraycopy(elevationSamples, 0, this.elevationSamples, 0, elevationSamples.length);
    }

    public double length(){
        return length;
    }

    double minElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();

        for (float elevationSample : elevationSamples) {
            s.accept(elevationSample);
        }
        return s.getMin();
    }

    double maxElevation(){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float elevationSample : elevationSamples) {
            s.accept(elevationSample);
        }
        return s.getMax();
    }

    double totalAscent(){
        float totalAscent = 0;
        for (int i=0; i<elevationSamples.length - 1; i++){
            float difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference >= 0){
                totalAscent += difference;
            }
        }
        return totalAscent;
    }

    double totalDescent(){
        float totalDescent = 0;
        for (int i=0; i<elevationSamples.length - 1; i++){
            float difference = elevationSamples[i+1] - elevationSamples[i];
            if (difference <= 0){
                totalDescent += difference;
            }
        }
        return - totalDescent;
    }

    double elevationAt(double position){

        if (position <= 0){
            return elevationSamples[0];
        }
        else if (position >= length){
            return elevationSamples[elevationSamples.length - 1];
        } else {
            return Functions.sampled(elevationSamples, length).applyAsDouble(position);
        }
    }

}
