package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

/**
 * Represents a route planner
 */
public final class RouteComputerOld {
    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Constructs a route planner for the given graph and cost function.
     *
     * @param graph        a graph
     * @param costFunction a function that returns a multiplicative factor for the length of a route based on different parameters
     */
    public RouteComputerOld(Graph graph, CostFunction costFunction) {
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
        int nbOfNode = graph.nodeCount();
        double[] distance = new double[nbOfNode];
        int[] predecessor = new int[nbOfNode];
        Set<Integer> exploringNode = new HashSet<>();
        exploringNode.add(startNodeId);
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;

        while (exploringNode.size() != 0) {
            int N = getIndexOfMinValue(exploringNode, distance);
            exploringNode.remove(N);
            if (N == endNodeId) {
                return routeCreator(startNodeId, endNodeId, predecessor);
            }
            int numberOfOutgoingEdges = graph.nodeOutDegree(N);
            for (int edgeIndex = 0; edgeIndex < numberOfOutgoingEdges; edgeIndex++) {
                int outgoingEdgeId = graph.nodeOutEdgeId(N, edgeIndex);
                int N2 = graph.edgeTargetNodeId(outgoingEdgeId);
                double d = distance[N] + graph.edgeLength(outgoingEdgeId) * costFunction.costFactor(N, outgoingEdgeId);
                if (d < distance[N2]) {
                    distance[N2] = d;
                    predecessor[N2] = N;
                    exploringNode.add(N2);
                }
            }
        }
        return null;
    }

    /**
     * Finds the node in exploring with the minimal distance from the starting node
     *
     * @param exploringNode list of node
     * @param distance      array who represents the distance of all nodes from the starting node
     * @return the index of the node with the minimal distance
     */
    private int getIndexOfMinValue(Set<Integer> exploringNode, double[] distance) {
        double minValue = Double.POSITIVE_INFINITY;
        int N = 0;
        for (Integer i : exploringNode) {
            if (distance[i] < minValue) {
                minValue = distance[i];
                N = i;
            }
        }
        return N;
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
        return 0;
    }

    /**
     * Creates a new SingleRoute composed of the edges connecting the best route
     *
     * @param startNodeId identity of the starting node
     * @param endNodeId   identity of the last node
     * @param predecessor an array of predecessor of each node in the route
     * @return an instance of SingleRoute
     */
    private SingleRoute routeCreator(int startNodeId, int endNodeId, int[] predecessor) {
        List<Edge> edges = new ArrayList<>();
        int currentNode = endNodeId;
        while (currentNode != startNodeId) {
            edges.add(Edge.of(graph, getEdgeId(predecessor[currentNode], currentNode), predecessor[currentNode], currentNode));
            currentNode = predecessor[currentNode];
        }
        Collections.reverse(edges);
        return new SingleRoute(edges);
    }
}