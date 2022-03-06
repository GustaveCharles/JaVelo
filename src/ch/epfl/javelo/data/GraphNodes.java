package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

public record GraphNodes (IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;


    public int count(){

        return buffer.capacity() / NODE_INTS;
    }

   public double nodeE(int nodeId){

       return Q28_4.asDouble(buffer.get(OFFSET_E +NODE_INTS*nodeId));
   }

    public double nodeN(int nodeId){

        return Q28_4.asDouble(buffer.get(OFFSET_N +NODE_INTS*nodeId));
    }

   public int outDegree(int nodeId){

        int number3 =  buffer.get(OFFSET_OUT_EDGES + NODE_INTS*nodeId);
       return Bits.extractUnsigned(number3,28,4);
    }

    public int edgeId(int nodeId, int edgeIndex){

        int number3 =  buffer.get(OFFSET_OUT_EDGES + NODE_INTS*nodeId);
        int index1 = Bits.extractUnsigned(number3,0,28);

        return index1 + edgeIndex;
    }

}
