package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

/**
 * Represents the longitudinal profile of a single or multiple route.
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class ElevationProfile {

    private final double length;
    private final float[] samples;
    private final DoubleSummaryStatistics s = new DoubleSummaryStatistics();
    private static double totalAscent;
    private static double totalDescent;

    /**
     * Builds the profile of a route and whose elevation samples are contained in the array elevationSamples.
     *
     * @param length           length (in meters)
     * @param elevationSamples sample of elevations uniformly distributed along the route
     * @throws IllegalArgumentException if the length is negative or zero, or if the array of samples
     *                                  contains less than 2 elements
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        this.length = length;
        this.samples = new float[elevationSamples.length];
        Preconditions.checkArgument(!(length <= 0 || elevationSamples.length < 2));
        System.arraycopy(elevationSamples, 0, samples, 0, elevationSamples.length);
        for (float sample : samples) {
            s.accept(sample);
        }

        totalAscent = 0;
        totalDescent = 0;

        for (int i = 0; i < samples.length - 1; i++) {
            float difference = samples[i + 1] - samples[i];
            if (difference > 0) {
                totalAscent += difference;
            } else {
                totalDescent += difference;
            }
        }
    }

    /**
     * Getter for the length of the profile
     *
     * @return a length (in meters)
     */
    public double length() {
        return length;
    }

    /**
     * Uses the DoubleSummaryStatistics getMin method to compute the minimal altitude of a given profile in meters
     *
     * @return the minimum altitude of the profile
     */
    public double minElevation() {
        return s.getMin();
    }

    /**
     * Uses the DoubleSummaryStatistics getMax method to compute the maximal altitude of a given profile in meters
     *
     * @return the maximum altitude of the profile
     */
    public double maxElevation() {
        return s.getMax();
    }

    /**
     * Computes the total elevation gain of a given profile in meters.
     * The total positive slope is equal to the sum of all the positive differences between a sample and its predecessor.
     *
     * @return the total elevation gain of the profile
     */
    public double totalAscent() {
        return Math.abs(totalAscent);
    }

    /**
     * Computes the total negative elevation of a given profile in meters.
     * The total negative elevation is equal to the sum of all the negative differences between a sample and its predecessor.
     *
     * @return the total elevation gain of the profile (positive or zero)
     */
    public double totalDescent() {
        return Math.abs(totalDescent);
    }

    /**
     * Computes the altitude of the profile at the given position in meters
     *
     * @param position an x-axis position on the edge
     * @return the altitude at the given position
     */
    public double elevationAt(double position) {
        return Functions.sampled(samples, length).applyAsDouble(position);
    }
}


