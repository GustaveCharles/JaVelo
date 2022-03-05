package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;

import java.nio.IntBuffer;

public record GraphNodes (IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;


    public int count(){

        return buffer.capacity() / 3;
    }

   public double nodeE(int nodeId){

       return buffer.get(OFFSET_E +3*nodeId);
   }

    public double nodeN(int nodeId){

        return buffer.get(OFFSET_N +3*nodeId);
    }

   public int outDegree(int nodeId){

        int number3 =  buffer.get(OFFSET_OUT_EDGES + 3*nodeId);
       return Bits.extractUnsigned(number3,27,4);
    }

    public int edgeId(int nodeId, int edgeIndex){

        int number3 =  buffer.get(OFFSET_OUT_EDGES + 3*nodeId);
        int index1 = Bits.extractUnsigned(number3,0,28);

        return index1 + edgeIndex;
    }

}
