package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import static java.lang.Math.hypot;
import static java.lang.Math.pow;

/**
 * Represents a point in the Swiss coordinate system
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public record PointCh(double e, double n) {

    /**
     * @throws IllegalArgumentException if the coordinates e and n are not included in the swiss territory
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Returns the square of the distance in meters separating the receiver (this) from the argument that
     *
     * @param that argument
     * @return a square distance in meters separating two points
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e - this.e, that.n - this.n);
    }

    /**
     * Returns the distance in meters separating the receiver (this) from the argument that
     *
     * @param that argument
     * @return a distance in meters separating two points
     */
    public double distanceTo(PointCh that) {
        return Math2.norm(that.e - this.e, that.n - this.n);
    }

    /**
     * Returns the longitude of the point, in the WGS84 system, in radians
     *
     * @return a longitude of a point
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Returns the latitude of the point, in the WGS84 system, in radians
     *
     * @return a latitude of a point
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}