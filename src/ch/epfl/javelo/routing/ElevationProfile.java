package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;

import java.util.DoubleSummaryStatistics;

/**
 * represents the longitudinal profile of a single or multiple route.
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public final class ElevationProfile {

    private double length;
    private float[] samples;
    private DoubleSummaryStatistics s = new DoubleSummaryStatistics();

    /**
     * Constructs the profile of a route of length "length"
     * and whose elevation samples are contained in elevationSamples. Moreover, create a copy of the attribute in
     * order to respect immutability
     * @param length length (in meters)
     * @param elevationSamples sample of elevations uniformly distributed along the route
     * @throws IllegalArgumentException if the length is negative or zero, or if the array of samples
     * contains less than 2 elements
     */
    public ElevationProfile(double length, float[] elevationSamples){
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

    /**
     * Getter for the length
     * @return the length (in meters)
     */
    public double length(){
        return length;
    }

    /**
     * Returns the minimum altitude of the profile, in meters,
     */
    public double minElevation(){
        return s.getMin();
    }

    /**
     * Returns the maximum altitude of the profile, in meters
     */
    public double maxElevation(){
        return s.getMax();
    }

    /**
     * Returns the total elevation gain of the profile, in meters
     */
    public double totalAscent(){
        float totalAscent = 0;
        for (int i = 0; i< samples.length - 1; i++){
            float difference = samples[i+1] - samples[i];
            if (difference >= 0){
                totalAscent += difference;
            }
        }
        return totalAscent;
    }

    /**
     * Returns the total negative elevation of the profile, in meters
     */
    public double totalDescent(){
        float totalDescent = 0;
        for (int i = 0; i< samples.length - 1; i++){
            float difference = samples[i+1] - samples[i];
            if (difference <= 0){
                totalDescent += difference;
            }
        }
        return Math.abs(totalDescent);
    }

    /**
     * Compute the altitude of the profile at the given position
     * @param position position on the edge
     * @return the altitude of the profile at the given position
     */
    public double elevationAt(double position){
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
