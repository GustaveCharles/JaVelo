package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;
import java.util.List;

/**
 * Represents a route planner
 */
public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Constructs a route planner for the given graph and cost function.
     *
     * @param graph        a graph
     * @param costFunction a function that returns a multiplicative factor for the length of a route based on different parameters
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Uses the A* algorithm to compute the minimum total cost route from identity node startNodeId to identity node endNodeId in the graph
     * passed to the constructor, or null if no route exists. If multiple minimum total cost routes exist,
     * bestRouteBetween returns any of them.
     *
     * @param startNodeId identity of the starting node
     * @param endNodeId   identity of the last node
     * @return the best cycling route between 2 nodes
     * @throws IllegalArgumentException if the start and end node are the same
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        int nbOfNode = graph.nodeCount();
        float[] distance = new float[nbOfNode];
        int[] predecessor = new int[nbOfNode];
        PriorityQueue<WeightedNode> exploringNode = new PriorityQueue<>();
        List<Edge> edges = new ArrayList<>();
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;

        exploringNode.add(new WeightedNode(startNodeId, distance[startNodeId]));

        while (exploringNode.size() != 0) {
            int nodeId = exploringNode.remove().nodeId;
            if (distance[nodeId] == Float.NEGATIVE_INFINITY) {
                continue;
            }
            if (nodeId == endNodeId) {
                int currentNode = endNodeId;
                while (currentNode != startNodeId) {
                    edges.add(Edge.of(graph, getEdgeId(predecessor[currentNode], currentNode), predecessor[currentNode], currentNode));
                    currentNode = predecessor[currentNode];
                }
                Collections.reverse(edges);
                return new SingleRoute(edges);
            }
            int numberOfOutgoingEdges = graph.nodeOutDegree(nodeId);
            for (int edgeIndex = 0; edgeIndex < numberOfOutgoingEdges; edgeIndex++) {
                int outgoingEdgeId = graph.nodeOutEdgeId(nodeId, edgeIndex);
                int N2 = graph.edgeTargetNodeId(outgoingEdgeId);
                if (distance[N2] == Float.NEGATIVE_INFINITY) {
                    continue;
                }
                float d = (float) (distance[nodeId] +
                        graph.edgeLength(outgoingEdgeId) * costFunction.costFactor(nodeId, outgoingEdgeId));
                if (d < distance[N2]) {
                    distance[N2] = d;
                    PointCh N2PointCh = graph.nodePoint(N2);
                    PointCh endPointCh = graph.nodePoint(endNodeId);
                    float distanceCrowFlies = (float) endPointCh.distanceTo(N2PointCh);
                    predecessor[N2] = nodeId;
                    exploringNode.add(new WeightedNode(N2, d + distanceCrowFlies));
                }
            }
            distance[nodeId] = Float.NEGATIVE_INFINITY;
        }
        return null;
    }

    /**
     * Computes the edge identity of a node which points to the next node (nodeAfter) by searching through the
     * outgoing edges of this node
     *
     * @param nodeBefore the first node
     * @param nodeAfter  the second node
     * @return an edge identity
     */
    private int getEdgeId(int nodeBefore, int nodeAfter) {
        int nbOfEdges = graph.nodeOutDegree(nodeBefore);
        for (int edgeIndex = 0; edgeIndex < nbOfEdges; edgeIndex++) {
            int outgoingEdgeId = graph.nodeOutEdgeId(nodeBefore, edgeIndex);
            int node = graph.edgeTargetNodeId(outgoingEdgeId);
            if (node == nodeAfter) {
                return outgoingEdgeId;
            }
        }
        return -1;
    }
}


