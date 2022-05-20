package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a multiple route, composed of a sequence
 * of contiguous routes called segments
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public final class MultiRoute implements Route {

    private final List<Route> segments;

    /**
     * builds a multiple route made up of the given segments
     *
     * @param segments the given segments
     * @throws IllegalArgumentException if the size of the segments is equal to 0
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(segments.size() != 0);
        this.segments = List.copyOf(segments);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public int indexOfSegmentAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length()), length;
        int index = 0;

        for (Route segment : segments) {
            length = segment.length();
            if (length < clampedPosition) {
                index += segment.indexOfSegmentAt(segment.length()) + 1;
                clampedPosition -= length;
            } else {
                index += segment.indexOfSegmentAt(clampedPosition);
                break;
            }
        }
        return index;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public double length() {
        double length = 0;

        for (Route segment : segments) {
            length += segment.length();
        }
        return length;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();

        for (Route segment : segments) {
            edges.addAll(segment.edges());
        }
        return edges;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<PointCh> points() {
        List<PointCh> pointChList = new ArrayList<>();

        for (Route segment : segments) {
            pointChList.addAll(segment.points());
            pointChList.remove(pointChList.size() - 1);
        }

        List<PointCh> intermediateList = segments.get(segments.size() - 1).points();
        pointChList.add(intermediateList.get(intermediateList.size() - 1));
        return pointChList;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public PointCh pointAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length());
        PointCh pointAt = segments.get(0).pointAt(position);

        for (Route segment : segments) {
            double length = segment.length();

            if (clampedPosition > length) {
                clampedPosition -= length;
            } else {
                pointAt = segment.pointAt(clampedPosition);
                break;
            }
        }
        return pointAt;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public double elevationAt(double position) {
        double clampedPosition = Math2.clamp(0, position, length()), elevationAt = 0;

        for (Route segment : segments) {
            double length = segment.length();

            if (clampedPosition > length) {
                clampedPosition -= length;
            } else {
                elevationAt = segment.elevationAt(clampedPosition);
                break;
            }
        }
        return elevationAt;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public int nodeClosestTo(double position) {
        double clampedPosition = Math2.clamp(0, position, length());
        int nodeClosestTo = 0;

        for (Route segment : segments) {
            double length = segment.length();

            if (clampedPosition > length) {
                clampedPosition -= length;
            } else {
                nodeClosestTo = segment.nodeClosestTo(clampedPosition);
                break;
            }
        }
        return nodeClosestTo;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointCompare = RoutePoint.NONE;
        double pastLength = 0;

        for (Route segment : segments) {
            pointCompare = pointCompare.min(segment.pointClosestTo(point).withPositionShiftedBy(pastLength));
            pastLength += segment.length();
        }
        return pointCompare;
    }
}
