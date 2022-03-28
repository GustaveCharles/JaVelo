//package ch.epfl.javelo.routing;
//
//import ch.epfl.javelo.Preconditions;
//import ch.epfl.javelo.data.Graph;
//import ch.epfl.javelo.data.GraphNodes;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public final class RouteComputer {
//    private final Graph graph;
//    private final CostFunction costFunction;
//
//    RouteComputer(Graph graph, CostFunction costFunction){
//        this.graph = graph;
//        this.costFunction = costFunction;
//    }
//
//    // ATTENTION A L IMMUTABILITE
//
//    public Route bestRouteBetween(int startNodeId, int endNodeId){
//        Preconditions.checkArgument(startNodeId != endNodeId);
//
//        List<Double> nodeDistanceFromOrigin = new ArrayList<>();
//        //edegTargetNodeId : qui retourne l'identité du nœud destination de l'arête d'identité donnée,
//        //double edgeLength(int edgeId), qui retourne la longueur, en mètres, de l'arête d'identité donnée,
//        //int nodeOutDegree(int nodeId), qui retourne le nombre d'arêtes sortant du nœud d'identité donnée,
//        //int nodeOutEdgeId(int nodeId, int edgeIndex), qui retourne l'identité de la edgeIndex-ième arête sortant
//        // du nœud d'identité nodeId,
//        //int nodeOutDegree(int nodeId), qui retourne le nombre d'arêtes sortant du nœud d'identité donnée,
//
//        List<Integer> unvisitedNode = new ArrayList<>();
//        for (int i=startNodeId; i<=endNodeId; i++){
//            unvisitedNode.add(i);
//        }
//
//        for (int i=0; i<unvisitedNode.size(); i++){
//            int id = unvisitedNode.get(i);
//            double minEdgeLength = 0;
//            int numberOfOutgoingEdges = graph.nodeOutDegree(id);
//            double[] lengthOfEdges = new double[numberOfOutgoingEdges];
//            for (int j = 0; j<numberOfOutgoingEdges; j++){
//                int outgoingEdgeId = graph.nodeOutEdgeId(id, j);
//                lengthOfEdges[j] = graph.edgeLength(outgoingEdgeId);
//            }
//            //Arrays.sort(lengthOfEdges);
//            //minEdgeLength = lengthOfEdges[0]; en faisant ça on casse les index
//            //FAIRE TRES ATTENTION A LA MODIFICATION D UNE LISTE PDT SON PARCOURS
//            unvisitedNode.remove(i);
//        }
//    }
//}
