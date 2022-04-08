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
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * represents a non-existent point
     */
    public static final RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * computes a point identical to the receiver (this) but whose position is offset by the given
     * difference, which can be positive or negative
     *
     * @param positionDifference given difference
     * @return returns the shifted position
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }

    /**
     * checks the RoutePoint for which the distance is smaller
     *
     * @param that the other route point
     * @return Returns this if the distance to the reference is lower or equal to that, and that otherwise
     */
    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference <= that.distanceToReference ? this :
                that;
    }

    /**
     * checks the RoutePoint for which the distance is the smallest to the position and if the current
     * distance is bigger it creates a new RoutePoint with the other RoutePoint values
     *
     * @param thatPoint               the other route point
     * @param thatPosition            the other position
     * @param thatDistanceToReference the distance (in meters) between the point and the reference
     * @return Returns this if its distance to the reference is less than or equal to
     * thatDistanceToReference, and a new RoutePoint instance whose attributes
     * are the arguments passed to min otherwise.
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return this.distanceToReference <= thatDistanceToReference ? this :
                new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }

}
