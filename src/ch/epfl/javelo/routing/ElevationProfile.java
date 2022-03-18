package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;

import java.util.DoubleSummaryStatistics;

public final class ElevationProfile {

    private double length;
    private float[] samples;
    private DoubleSummaryStatistics s = new DoubleSummaryStatistics();

    ElevationProfile(double length, float[] elevationSamples){
        this.length = length;
        this.samples = new float[elevationSamples.length];

        if (length <= 0 || elevationSamples.length < 2){
            throw new IllegalArgumentException();
        } else {
            System.arraycopy(elevationSamples, 0, samples, 0, elevationSamples.length);
            for (float sample : samples){
                s.accept(sample);
            }
        }
    }

    public double length(){
        return length;
    }

    double minElevation(){
        return s.getMin();
    }

    double maxElevation(){
        return s.getMax();
    }

    double totalAscent(){
        float totalAscent = 0;
        for (int i = 0; i< samples.length - 1; i++){
            float difference = samples[i+1] - samples[i];
            if (difference >= 0){
                totalAscent += difference;
            }
        }
        return totalAscent;
    }

    double totalDescent(){
        float totalDescent = 0;
        for (int i = 0; i< samples.length - 1; i++){
            float difference = samples[i+1] - samples[i];
            if (difference <= 0){
                totalDescent += difference;
            }
        }
        return Math.abs(totalDescent);
    }

    double elevationAt(double position){
        if (position <= 0){
            return samples[0];
        }
        else if (position >= length){
            return samples[samples.length - 1];
        } else {
            return Functions.sampled(samples, length).applyAsDouble(position);
        }
    }
}
