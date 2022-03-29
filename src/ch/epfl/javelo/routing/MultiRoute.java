package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import jdk.management.jfr.RecordingInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MultiRoute implements Route {

    private final List<Route> segments;

    MultiRoute(List<Route> segments){
        Preconditions.checkArgument(segments.size()!=0);
        this.segments=List.copyOf(segments);
    }

    @Override
    public int indexOfSegmentAt(double position) {
        double clampedPosition = Math2.clamp(0,position,length()), length = 0;
        int index=0;

       for(Route segment:segments){
           length += segment.length();
           if(length<clampedPosition){
               index=segment.indexOfSegmentAt(segment.length())+1;
               clampedPosition -= length;
           }else{
               index = segment.indexOfSegmentAt(clampedPosition)+1;
           }
       }
       return index;
    }

    @Override
    public double length() {
        double length=0;

        for (Route segment: segments){
            //for(Edge edge: segment.edges()){
              //  length +=edge.length();
           // }
            length += segment.length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();

        for (Route segment: segments){
            for(Edge edge:segment.edges()){
                edges.add(edge);
            }
        }
        return edges;
    }

    @Override
    public List<PointCh> points() {
        HashSet<PointCh> pointChList = new HashSet<>();

        for(Route segment: segments){
            pointChList.addAll(segment.points());

        }
        for(PointCh point : pointChList){
            if(pointChList.contains(point)){
                pointChList.remove(point);
            }
        }
        return new ArrayList<>(pointChList);
    }

    @Override
    public PointCh pointAt(double position) {
        double clampedPosition = Math2.clamp(0,position,length());
        PointCh pointAt = new PointCh(0,0);

        for(Route segment : segments){
            double length = segment.length();

            if(clampedPosition>length){
                clampedPosition -= length;
            }else{
                pointAt = segment.pointAt(clampedPosition);

            }
        }
        return pointAt;
    }

    @Override
    public double elevationAt(double position) {
        double clampedPosition = Math2.clamp(0,position,length());
        double elevationAt = 0;

        for(Route segment : segments){
            double length = segment.length();

            if(clampedPosition>length){
                clampedPosition -= length;
            }else{
                elevationAt = segment.elevationAt(clampedPosition);
            }
        }
        return elevationAt;
    }

    @Override
    public int nodeClosestTo(double position) {
        double clampedPosition = Math2.clamp(0,position,length());
        int nodeClosestTo = 0;

        for(Route segment : segments){
            double length = segment.length();

            if(clampedPosition>length){
                clampedPosition -= length;
            }else{
                nodeClosestTo = segment.nodeClosestTo(clampedPosition);
            }
        }
        return nodeClosestTo;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint pointCompare =  RoutePoint.NONE;
        double pastLength=0;

        for(Route segment: segments){
            pastLength += segment.length();
            pointCompare = pointCompare.min(segment.pointClosestTo(point).withPositionShiftedBy(pastLength));
        }
        return pointCompare;
    }
}
