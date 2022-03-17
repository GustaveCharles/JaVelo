package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.GraphNodes;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return Edge(fromNodeId, toNodeId, );
    }

    public double positionClosestTo(PointCh point){
        double aX = fromPoint.e();
        double aY = fromPoint.n();
        double bX = toPoint.e();
        double bY = toPoint.n();
        double pX = point.e();
        double pY = point.n();

        return Math2.projectionLength(aX, aY, bX, bY, pX, pY);
    }

    public PointCh pointAt(double position){
        double ePosition = Math2.interpolate(toPoint.e(), fromPoint.e(), position/length);
        double nPosition = Math2.interpolate(toPoint.n(), fromPoint.n() , position/length);
        return new PointCh(ePosition, nPosition);
    }

    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }
}
