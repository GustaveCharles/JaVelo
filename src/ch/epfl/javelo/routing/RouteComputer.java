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

    public Route bestRouteBetween(int startNodeId, int endNodeId){
        Preconditions.checkArgument(startNodeId != endNodeId);
        int nbOfNode = graph.nodeCount();
        double[] distance = new double[nbOfNode];
        int[] predecessor = new int[nbOfNode];
        Set<Integer> exploringNode = new HashSet<>();
        exploringNode.add(startNodeId);
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;

        while (exploringNode.size()!= 0){
            int N = getIndexOfMinValue(exploringNode, distance);
            exploringNode.remove(N);
            if (N == endNodeId){break;}
            int numberOfOutgoingEdges = graph.nodeOutDegree(N);
            for (int edgeIndex=0; edgeIndex<numberOfOutgoingEdges; edgeIndex++){
                int outgoingEdgeId = graph.nodeOutEdgeId(N, edgeIndex);
                int N2 = graph.edgeTargetNodeId(outgoingEdgeId);
                double d = distance[N] + graph.edgeLength(outgoingEdgeId)*costFunction.costFactor(N,outgoingEdgeId);
                if (d<distance[N2]){
                    distance[N2] = d;
                    predecessor[N2] = N;
                    exploringNode.add(N2);
                }
            }
        }

        List<Edge> edges = new ArrayList<>();
        int currentNode = endNodeId;
        int predecessorNode = 0;
        while (currentNode != startNodeId){
            predecessorNode = predecessor[currentNode];
            int numberOfNodes = graph.nodeOutDegree(currentNode);
            for (int j = 0; j < numberOfNodes; j++) {
                int outgoingEdgeId = graph.nodeOutEdgeId(currentNode, j);
                int node = graph.edgeTargetNodeId(outgoingEdgeId);
                if (node == predecessorNode) {
                    edges.add(Edge.of(graph,outgoingEdgeId, predecessorNode, currentNode));
                }
            }
            currentNode = predecessorNode;
        }

        Collections.reverse(edges);

        return new SingleRoute(edges);
    }

    private static int getIndexOfMinValue(Set<Integer> exploringNode, double[] distance) {
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
}
