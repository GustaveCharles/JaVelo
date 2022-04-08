package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * collect all the information relating to a route edge
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint,
                   PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     *
     * @param graph the current graph
     * @param edgeId the edge we want to have the information for
     * @param fromNodeId the first node of the edge
     * @param toNodeId the last node of the edge
     * @return returns an instance of Edge whose attributes fromNodeId and toNodeId are those given,
     * the others being those of the identity edge edgeId in the graph Graph.
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId),
                graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     *computes the position closest to a given point along th edge
     * @param point the given point
     * @return returns the position along the edge, in meters,
     * that is closest to the given point
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(),
                toPoint.n(), point.e(), point.n());
    }

    /**
     * from a position in decimals computes a point in Swiss coordinates
     * @param position the given position on the edge
     * @return returns the point at the given position on the edge, expressed in meters
     */
    public PointCh pointAt(double position){
        double ePosition = Math2.interpolate(fromPoint.e(), toPoint.e(), position/length);
        double nPosition = Math2.interpolate(fromPoint.n(), toPoint.n(), position/length);
        return new PointCh(ePosition, nPosition);
    }

    /**
     *transforms the attribute from a DoubleUnaryOperator to a double
     * @param position the given position on the edge
     * @return returns the altitude, in meters, at the given position on the edge
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }
}
