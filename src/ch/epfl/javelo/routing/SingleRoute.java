package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SingleRoute implements Route{

    private final List<Edge> edges;
    private final double[] routePositions;

    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(edges.size()!=0);
        this.edges = List.copyOf(edges);
         routePositions = positionArray();

    }


    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        double routeLength =0;

        for(Edge edges : edges()){
            routeLength+=edges.length();
        }
        return routeLength;
    }

    @Override
    public List<Edge> edges() {
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> pointChList = new ArrayList<>();
        pointChList.add(edges.get(0).fromPoint());
        for(Edge edge: this.edges){
            pointChList.add(edge.toPoint());
        }
        return pointChList;
    }

    @Override
    public PointCh pointAt(double position) {
        double clampedPosition = checkPosition(position);
        int dichValue = binarySearch(routePositions,clampedPosition);
        double edgePosition = calculatePosition(dichValue,routePositions,clampedPosition);

        return edges.get(dichValue).pointAt(edgePosition);
    }

    @Override
    public double elevationAt(double position) {
        double clampedPosition = checkPosition(position);
        int dichValue = binarySearch(routePositions,clampedPosition);
        double edgePosition = calculatePosition(dichValue,routePositions,clampedPosition);

        return edges.get(dichValue).elevationAt(edgePosition);
    }

    @Override
    public int nodeClosestTo(double position) { //marche pas
        double clampedPosition = checkPosition(position);
        int dichValue = binarySearch(routePositions,clampedPosition);
        double edgePosition = calculatePosition(dichValue,routePositions,clampedPosition);

        double position1 = clampedPosition- routePositions[dichValue],position2 = routePositions[dichValue+1]-clampedPosition;

        return position1<=position2 ? edges.get(dichValue).fromNodeId():
                edges.get(dichValue).toNodeId();

    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint firstComparison =  RoutePoint.NONE;

        for(int i=0; i<edges.size();++i){
           double longestPositionOnEdge = Math2.clamp(0,edges.get(i).positionClosestTo(point),edges.get(i).length());
           double distanceFromPointToEdge = longestPositionOnEdge + routePositions[edges.indexOf(edges.get(i))]; //edges.get(i).positionClosestTo(point)-longestPositionOnEdge
            firstComparison = firstComparison.min(edges.get(i).pointAt(longestPositionOnEdge),distanceFromPointToEdge,point.distanceTo(edges.get(i).pointAt(longestPositionOnEdge)));
        }
        return firstComparison;
    }


    private double checkPosition(double position){
        return Math2.clamp(0,position,length());
    }

    private double[] positionArray(){
        double length =0;
        double[] routePositions = new double[edges.size()+1];
        routePositions[0] = 0.;

        for (int i=0; i< edges.size(); ++i) {
            length += edges.get(i).length();
            routePositions[i+1] = length;
        }
        return routePositions;
    }

    private int binarySearch(double[] routePositions, double position){
        int dichValue = Arrays.binarySearch(routePositions,position);
        return dichValue<0 ? Math2.clamp(0,-dichValue-2,edges.size()-1):
            Math2.clamp(0,dichValue,edges.size()-1);
    }

    private double calculatePosition(int dichValue,double[] routePositions, double position){
        return dichValue<0 ? (position - routePositions[-dichValue-2]):
                position - routePositions[dichValue];
    }
}

