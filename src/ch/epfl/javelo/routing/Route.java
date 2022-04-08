package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * represents a route, either a single route consisting of various edges or a multi-route consisting
 * of various single/multi routes
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public interface Route {

    /**
     * calculates in which segment/singleroute our position is in
     *
     * @param position the position of a point in our route
     * @return returns the index of the segment of the route containing the given position
     */
    int indexOfSegmentAt(double position);

    /**
     * calculates the length of our route by combining the length of each edge
     *
     * @return returns the length of the route, in meters
     */
    double length();

    /**
     * adds all the edges of a route in a list
     *
     * @return returns all the edges of the route
     */
    List<Edge> edges();

    /**
     * adds all the points of the route in a list
     *
     * @return returns all the points located at the extremities of the edges of the route,
     * without duplicates
     */
    List<PointCh> points();

    /**
     * computes a binary search to find the index of the edge that contains the position and then
     * calls the pointAt function with this index and the position relative to the edge
     *
     * @param position the position of a point in our route
     * @return returns the point at the given position along the route in the Swiss coordinates system
     */
    PointCh pointAt(double position);

    /**
     * computes a binary search to find the index of the edge that contains the position and then
     * calls the elevationAt function with this index and the position relative to the edge
     *
     * @param position the position of a point in our route
     * @return returns the altitude at the given position along the route,
     * which can be NaN if the edge containing this position has no profile
     */
    double elevationAt(double position);

    /**
     * computes a binary search to find the index of the edge that contains the position and then
     * finds which node of the edge is the nearest
     *
     * @param position
     * @return returns the identity of the node belonging to the route and located closest
     * to the given position
     */
    int nodeClosestTo(double position);

    /**
     * compares the distance between the closest point to the given point on each edge and determines
     * the closest point on the route
     *
     * @param point the point in Swiss coordinates
     * @return returns the route point closest to the given reference point
     */
    RoutePoint pointClosestTo(PointCh point);
}
