package ch.epfl.javelo.routing;

/**
 * represents a cost function
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public interface CostFunction {
    /**
     * computes the factor by which the length of the identity edge edgeId,
     * starting from the identity node nodeId, should be multiplied;
     * this factor must imperatively be greater than or equal to 1
     *
     * @param nodeId the given node
     * @param edgeId the given edge
     * @return returns the factor in form of a double. The factor can be positive infinity,
     * which expresses the fact that the edge cannot be taken at all.
     * This is equivalent to considering that the edge does not exist.
     */
    double costFactor(int nodeId, int edgeId);
}

