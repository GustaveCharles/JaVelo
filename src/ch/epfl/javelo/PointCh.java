package ch.epfl.javelo;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.SwissBounds;

/**
 * @throws IllegalArgumentException if the coordinates e and n are included in the swiss territory
 */
public record PointCh(double e, double n) {
    public PointCh{
        if (!SwissBounds.containsEN(e,n)){
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param that
     *
     * @return
     */
    public double squaredDistanceTo(PointCh that){
        return Math.pow(Math.hypot(that.e - this.e,that.n - this.n), 2);
    }

    /**
     *
     * @param that
     * @return
     */
    public double distanceTo(PointCh that){
        return Math.hypot(that.e - this.e,that.n - this.n);
    }

    /**
     *
     * @return
     */
    public double lon(){return Ch1903.lon(e,n);}
    public double lat(){return Ch1903.lat(e,n);}
}