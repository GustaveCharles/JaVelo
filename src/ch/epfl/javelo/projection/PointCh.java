package ch.epfl.javelo.projection;

/**
 * Represents a point in the Swiss coordinate system
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

import ch.epfl.javelo.Preconditions;

/**
 * @throws IllegalArgumentException if the coordinates e and n are not included in the swiss territory
 */
public record PointCh(double e, double n) {
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Returns the square of the distance in meters separating the receiver (this) from the argument that
     *
     * @param that argument
     * @return distance in meters separating the receiver (this) from the argument that
     */
    public double squaredDistanceTo(PointCh that) {
        return Math.pow(Math.hypot(that.e - this.e, that.n - this.n), 2);
    }

    /**
     * Returns the distance in meters separating the receiver (this) from the argument that
     *
     * @param that arguement
     * @return the distance in meters separating the receiver (this) from the argument that
     */
    public double distanceTo(PointCh that) {
        return Math.hypot(that.e - this.e, that.n - this.n);
    }

    /**
     * Returns the longitude of the point, in the WGS84 system, in radians
     *
     * @return longitude of the point, in the WGS84 system, in radians
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Returns the latitude of the point, in the WGS84 system, in radians
     *
     * @return the latitude of the point, in the WGS84 system, in radians
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}