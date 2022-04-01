package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;

/**
 * represents the point on a route closest to a given reference point, which is around of the route.
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public record RoutePoint (PointCh point, double position, double distanceToReference){

    public static final RoutePoint NONE = new RoutePoint(null,NaN,POSITIVE_INFINITY); //faut utiliser none

    /**
     * Returns a point identical to the receiver (this) but whose position is offset by the given
     * difference, which can be positive or negative
     * @param positionDifference given difference
     * @return the shifted position
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(point,position+positionDifference,distanceToReference);
    }

    /**
     * Returns this if the distance to the reference is lower or equal to that, and that otherwise
     * @param that the other route point
     */
    public RoutePoint min(RoutePoint that){
        return this.distanceToReference<=that.distanceToReference ? this:
                that;
    }

    /**
     * Returns this if its distance to the reference is less than or equal to
     * thatDistanceToReference, and a new RoutePoint instance whose attributes
     * are the arguments passed to min otherwise.
     * @param thatPoint the other route point
     * @param thatPosition the other position
     * @param thatDistanceToReference the distance (in meters) between the point and the reference
     * @return this if the distance respects the conditions above
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return this.distanceToReference<=thatDistanceToReference ? this:
                new RoutePoint(thatPoint,thatPosition,thatDistanceToReference);
    }

}
