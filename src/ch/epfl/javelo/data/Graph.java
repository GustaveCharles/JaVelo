package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){

        this.sectors = sectors;
        this.nodes = nodes;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }

    public static Graph loadFrom(Path basePath) throws IOException {

        Path nodesPath = basePath.resolve("nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel = FileChannel.open(nodesPath)){
            nodesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }
        GraphNodes nodes = new GraphNodes(nodesBuffer);

        Path sectorsPath = basePath.resolve("sectors.bin");
        ByteBuffer byteBufferSectors;
        try (FileChannel channel = FileChannel.open(sectorsPath)){
            byteBufferSectors = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }
        GraphSectors sectors = new GraphSectors(byteBufferSectors);

        Path edgesPath = basePath.resolve("edges.bin");
        ByteBuffer byteBufferEdges;
        try (FileChannel channel = FileChannel.open(edgesPath)){
            byteBufferEdges = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asReadOnlyBuffer();
        }

        Path edgesPath1 = basePath.resolve("profile_ids.bin");
        IntBuffer nodesBufferEdges;
        try (FileChannel channel = FileChannel.open(edgesPath1)){
            nodesBufferEdges = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asIntBuffer();
        }

        Path edgesPath2 = basePath.resolve("elevations.bin");
        ShortBuffer shortBuffer;
        try (FileChannel channel = FileChannel.open(edgesPath2)){
            shortBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asShortBuffer();
        }

        GraphEdges edges = new GraphEdges(byteBufferEdges,nodesBufferEdges,shortBuffer);

        Path attributePath = basePath.resolve("attributes.bin");
        List list;
        try (FileChannel channel = FileChannel.open(nodesPath)){
            list = Arrays.asList(channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).array());
        }

        return new Graph(nodes,sectors,edges,list);
    }

    public int nodeCount(){
        return nodes.count();
    }

    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId),nodes.nodeN(nodeId));
    }

    public int nodeOutDegree(int nodeId){
       return nodes.outDegree(nodeId);
    }

    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId,edgeIndex);
    }

    int nodeClosestTo(PointCh point, double searchDistance){
        List<GraphSectors.Sector> sectorsInRange= new ArrayList<>();
        sectorsInRange.addAll(sectors.sectorsInArea(point,searchDistance));

        int node = sectorsInRange.get(0).startNodeId();
        double distance1 = point.squaredDistanceTo(nodePoint(node));
        int nodeClosestTo = node;

        for(int i=0; i<sectorsInRange.size(); ++i){

            int node1 = sectorsInRange.get(i).startNodeId();

            for(int j=node1+1; j<sectorsInRange.get(i).endNodeId(); ++j){
                int nodeCompare = j;
                double distance2 = point.squaredDistanceTo(nodePoint(nodeCompare));

                if(distance2<distance1){
                    nodeClosestTo = nodeCompare;
                }
            }
            return nodeClosestTo;
        }
        return -1;
    }

    int edgeTargetNodeId(int edgeId){
       return edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edgeId);
    }

    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    public DoubleUnaryOperator edgeProfile(int edgeId){
       return edges.hasProfile(edgeId) ? Functions.sampled(edges.profileSamples(edgeId),edges.profileSamples(edgeId).length):
               Functions.constant(Double.NaN);
    }

}
