package ch.epfl.javelo.routing;

public interface CostFunction {
    abstract double costFactor(int nodeId, int edgeId);
}

