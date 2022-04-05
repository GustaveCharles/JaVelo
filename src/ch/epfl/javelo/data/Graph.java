package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.data.GraphSectors.Sector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph { //classe final pour immuabilité

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;
//TODO regarder si le load from est ok
    /**
     * constructs the graph with the given nodes, sectors, edges and attribute sets
     * @param nodes the given GraphNodes
     * @param sectors the given graphSectors
     * @param edges the given GraphEdges
     * @param attributeSets the given AttributeSets
     */
   public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){

        this.sectors = sectors;
        this.nodes = nodes;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     *
     * @param basePath
     * @return
     * @throws IOException
     */
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
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
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
        LongBuffer buffer;
        try (FileChannel channel = FileChannel.open(attributePath)){
            buffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
        }

        List<AttributeSet> list1 = new ArrayList<>();
        for(int i= 0; i< buffer.capacity(); ++i){
            list1.add(new AttributeSet(buffer.get(i)));
        }

        return new Graph(nodes,sectors,edges,list1);
    }

    /**
     *
     * @return returns the total number of nodes in the graph
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     * computes the Swiss coordinates of a node point
     * @param nodeId the given node
     * @return returns the position of the given identity node
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId),nodes.nodeN(nodeId));
    }

    /**
     * computes the number of edges exiting the given identity node
     * @param nodeId the given node
     * @return returns the number of edges exiting the given identity node
     */
    public int nodeOutDegree(int nodeId){
       return nodes.outDegree(nodeId);
    }

    /**
     * computes the identity of the edgeIndex-th edge outgoing from the identity node nodeId
     * @param nodeId the given node
     * @param edgeIndex the Index of the first node of the edge
     * @return returns the identity of the edgeIndex-th edge outgoing from the identity node nodeId
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId,edgeIndex);
    }

    /**
     *
     * @param point
     * @param searchDistance
     * @return returns the identity of the node closest to the given point,
     * at the given maximum distance (in meters), or -1 if no node matches these criteria
     */
    public int nodeClosestTo(PointCh point, double searchDistance){
        double distance1 = searchDistance * searchDistance;

        int nodeClosestTo = -1;
        List<Sector> sectorsInRange= sectors.sectorsInArea(point,searchDistance);

        for(Sector sector: sectorsInRange){
            for(int nodeCompare=sector.startNodeId(); nodeCompare<sector.endNodeId(); ++nodeCompare){
                double distance2 = point.squaredDistanceTo(nodePoint(nodeCompare));

                if(distance2<distance1){
                    distance1 = distance2;
                    nodeClosestTo = nodeCompare;
                }
            }
        }
        return nodeClosestTo;
    }

    /**
     *
     * @param edgeId
     * @return returns the identity of the destination node of the edge with the given identity
     */
    public int edgeTargetNodeId(int edgeId){
       return edges.targetNodeId(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return returns true iff the given identity edge goes
     * in the opposite direction of the OSM channel it comes from
     */
    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return returns the set of OSM attributes attached to the given identity edge
     */
    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     *
     * @param edgeId
     * @return returns the length, in meters, of the given identity edge
     */
    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return returns the total positive elevation of the edge with the given identity
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return returns the profile along the edge with the given identity,
     * in the form of a function, or Nan if the edge does not have a profile
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        return edges.hasProfile(edgeId) ? Functions.sampled(edges.profileSamples(edgeId),edges.length(edgeId)):
               Functions.constant(Double.NaN);
    }

}
