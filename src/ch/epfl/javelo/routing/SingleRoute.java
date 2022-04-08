package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * represents a simple route, connecting a starting point to an ending point,
 * without an intermediate waypoint
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */


public final class SingleRoute implements Route {

    private final List<Edge> edges;
    private final double[] routePositions;

    /**
     * @throws IllegalArgumentException if the size of the edge is equal to 0
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(edges.size() != 0);
        this.edges = List.copyOf(edges);
        routePositions = positionArray();

    }

    @Override
    /**
     * {@inheritDoc}
     */
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public double length() {
        double routeLength = 0;

        for (Edge edges : edges()) {
            routeLength += edges.length();
        }
        return routeLength;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<Edge> edges() {
        return List.copyOf(edges);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<PointCh> points() {

        List<PointCh> pointChList = new ArrayList<>();
        pointChList.add(edges.get(0).fromPoint());

        for (Edge edge : this.edges) {
            pointChList.add(edge.toPoint());
        }
        return List.copyOf(pointChList);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public PointCh pointAt(double position) {

        double clampedPosition = Math2.clamp(0, position, length());
        int indexOfEdge = binarySearch(clampedPosition);
        double edgePosition = calculatePosition(indexOfEdge, clampedPosition);

        return edges.get(indexOfEdge).pointAt(edgePosition);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public double elevationAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length());
        int indexOfEdge = binarySearch(clampedPosition);
        double edgePosition = calculatePosition(indexOfEdge, clampedPosition);

        return edges.get(indexOfEdge).elevationAt(edgePosition);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public int nodeClosestTo(double position) {
        double clampedPosition = Math2.clamp(0, position, length());
        int indexOfEdge = binarySearch(clampedPosition);

        double position1 = clampedPosition - routePositions[indexOfEdge], position2 = routePositions[indexOfEdge + 1] - clampedPosition;

        return position1 <= position2 ? edges.get(indexOfEdge).fromNodeId() :
                edges.get(indexOfEdge).toNodeId();

    }

    @Override
    /**
     * {@inheritDoc}
     */
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointCompare = RoutePoint.NONE;

        for (Edge edge : edges) {
            double pointPositionOnEdge = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            double pointPositionOnRoute = pointPositionOnEdge + routePositions[edges.indexOf(edge)];
            pointCompare = pointCompare.min(edge.pointAt(pointPositionOnEdge),
                    pointPositionOnRoute, point.distanceTo(edge.pointAt(pointPositionOnRoute)));
        }
        return pointCompare;
    }


    /**
     * an array containing the position of each node of the single route
     *
     * @return an array whose index is that of a node of the route,
     * and the value is the position of this node along the route, in meters
     */
    private double[] positionArray() {
        double length = 0;
        double[] routePositions = new double[edges.size() + 1];
        routePositions[0] = 0.;

        for (int i = 0; i < edges.size(); ++i) {
            length += edges.get(i).length();
            routePositions[i + 1] = length;
        }
        return routePositions;
    }

    /**
     * computes a binary search in an array
     *
     * @param position the position of the point which we want to do the search on
     * @return returns the index of the edge relative to the full route in which our point is
     */
    private int binarySearch(double position) {
        int dichValue = Arrays.binarySearch(routePositions, position);
        return dichValue < 0 ? Math2.clamp(0, -dichValue - 2, edges.size() - 1) :
                Math2.clamp(0, dichValue, edges.size() - 1);
    }

    /**
     * calculates de position of the given position relative to the edge
     *
     * @param dichValue the index of the edge in which the position is on
     * @param position  the position relative to all the route
     * @return returns a position on the edge
     */
    private double calculatePosition(int dichValue, double position) {
        return dichValue < 0 ? (position - routePositions[-dichValue - 2]) :
                position - routePositions[dichValue];
    }
}

