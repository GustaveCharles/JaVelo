package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;

public record RoutePoint (PointCh point, double position, double distanceToReference){

    public static final RoutePoint NONE = new RoutePoint(null,NaN,POSITIVE_INFINITY);

    public RoutePoint withPositionShiftedBy(double positionDifference){
        PointCh newPoint = new PointCh(this.point.e() + positionDifference, this.point.n() + positionDifference);
        return new RoutePoint(newPoint,this.position,this.distanceToReference);
    }

    public RoutePoint min(RoutePoint that){
        return this.distanceToReference<=that.distanceToReference ? this:
                that;
    }

    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return this.distanceToReference<=thatDistanceToReference ? this:
                new RoutePoint(thatPoint,thatPosition,thatDistanceToReference);
    }

}
