package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * represents the array of all nodes of the JaVelo graph
 * and gives all its characteristics
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    private static final int EXTRACT_INT_3_START = 0;
    private static final int EXTRACT_EDGE_ID = EXTRACT_INT_3_START + 28;
    private static final int EXTRACT_OUT_DEGREE = 32 - EXTRACT_EDGE_ID;




    /**
     * at each NODE_INTS there is a new node, so we divide de total number of nodes
     * by NODE_INTS
     *
     * @return returns the total number of nodes
     */
    public int count() {

        return buffer.capacity() / NODE_INTS;
    }

    /**
     * we extract the int value off the buffer and then we convert it to decimal
     *
     * @param nodeId the node which we want to know its E coordinate
     * @return returns the E coordinate of the given identity node
     */
    public double nodeE(int nodeId) {

        return Q28_4.asDouble(buffer.get(OFFSET_E + NODE_INTS * nodeId));
    }

    /**
     * we extract the int value off the buffer and then we convert it to decimal
     *
     * @param nodeId the node which we want to know its N coordinate
     * @return returns the N coordinate of the given identity node
     */
    public double nodeN(int nodeId) {

        return Q28_4.asDouble(buffer.get(OFFSET_N + NODE_INTS * nodeId));
    }

    /**
     * we extract the int representation off the buffer
     * and then we select which bits we want the using an unsigned extraction
     *
     * @param nodeId the given identity node
     * @return returns the number of edges outgoing from the given identity node
     */
    public int outDegree(int nodeId) {

        int number3 = buffer.get(OFFSET_OUT_EDGES + NODE_INTS * nodeId);
        return Bits.extractUnsigned(number3, EXTRACT_EDGE_ID, EXTRACT_OUT_DEGREE);
    }

    /**
     * we extract the int representation off the buffer
     * and then we select which bits we want the using an unsigned extraction.
     * As we already know the index of the first edge, we add it to the index of the extracted edge
     *
     * @param nodeId the given node
     * @param edgeIndex the Index of the first node of the edge
     * @return returns the identity of the edgeIndex-th edge outgoing from the identity node nodeId
     */
    public int edgeId(int nodeId, int edgeIndex) {

        int number3 = buffer.get(OFFSET_OUT_EDGES + NODE_INTS * nodeId);
        int index1 = Bits.extractUnsigned(number3, EXTRACT_INT_3_START, EXTRACT_EDGE_ID);

        return index1 + edgeIndex;
    }

}
