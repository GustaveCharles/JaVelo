package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

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
            if (N == endNodeId) {return routeCreator(startNodeId, endNodeId, predecessor);}
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

    private int getEdgeId(int nodeBefore, int nodeAfter){
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

    private SingleRoute routeCreator(int startNodeId, int endNodeId, int[] predecessor){
        List<Edge> edges = new ArrayList<>();
        int currentNode = endNodeId;
        while (currentNode != startNodeId){
            edges.add(Edge.of(graph, getEdgeId(predecessor[currentNode], currentNode), predecessor[currentNode], currentNode));
            currentNode = predecessor[currentNode];
        }
        Collections.reverse(edges);
        return new SingleRoute(edges);
    }
}