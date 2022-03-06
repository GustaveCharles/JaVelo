package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public record GraphEdges (ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations){

    private static final int OFFSET_INVERT = 0;
    private static final int OFFSET_LENGTH = OFFSET_INVERT + 4;
    private static final int OFFSET_ELEVATION= OFFSET_LENGTH + 2;
    private static final int OFFSET_ATTRIBUTES = OFFSET_ELEVATION + 2;
    private static final int EDGE_INTS = OFFSET_ATTRIBUTES + 2;


    public boolean isInverted(int edgeId){

        int inv = edgesBuffer.getInt(edgeId);

        if(inv <0) return true;

        return false;
    }

    public int targetNodeId(int edgeId){

        if(isInverted(edgeId)){
            int targetNode = edgesBuffer.getInt(OFFSET_INVERT + edgeId*EDGE_INTS);
            return  ~targetNode;
        }else{
            return edgesBuffer.getInt(OFFSET_INVERT + edgeId*EDGE_INTS);
        }
    }

    public double length(int edgeId){

        return Q28_4.asDouble(edgesBuffer.getShort(OFFSET_LENGTH + edgeId));
    }


    public double elevationGain(int edgeId){

        return Q28_4.asDouble(edgesBuffer.getShort(OFFSET_ELEVATION + edgeId*EDGE_INTS));
    }

    public boolean hasProfile(int edgeId){

        int ProfilType = Bits.extractUnsigned(profileIds.get(edgeId*32), 29,2);

        if(ProfilType == 0) return false;

        return true;
    }

    private int profileType(int edgeId){
        return Bits.extractUnsigned(profileIds.get(edgeId*32), 29,2);
    }

    public float[] profileSamples(int edgeId){

        if(!hasProfile(edgeId)) return new float[0];

        int firstSampleIndex = Bits.extractSigned(profileIds.get(edgeId*32),0,30);
        double edgeLength = length(edgeId);
        int nbSamplesOnEdge = (int) ( 1 + Math.ceil(edgeLength));
        float[] profileSamples = new float[nbSamplesOnEdge];

        float firstSampleValue = Short.toUnsignedInt(elevations.get(firstSampleIndex));
        profileSamples[0] = firstSampleValue;

        if(profileType(edgeId) == 2){
            for(int i = 1; i<=Math.round(nbSamplesOnEdge/2); ++i){
                float otherSampleValues = Bits.extractSigned(elevations.get(firstSampleIndex +i), 0,8);
                profileSamples[i] = firstSampleValue + otherSampleValues;
            }
        }else if(profileType(edgeId) == 3){
            for(int i = 1; i<=Math.round(nbSamplesOnEdge/4); ++i){
                float otherSampleValues = Bits.extractSigned(elevations.get(firstSampleIndex +i), 0,4);
                profileSamples[i] = firstSampleValue + otherSampleValues;
            }
        }

        if(isInverted(edgeId)){
            inverter(profileSamples);
            return profileSamples;
        }else
            return profileSamples;
    }

    private float[] inverter(float[] invert){
        float[] inverted = new float[invert.length];
        for(int i=0; i<invert.length; ++i){
            for(int j= invert.length-1; j>0; --j){
                inverted[i]=invert[j];
            }
        }
        return inverted;
    }

    public int attributesIndex(int edgeId){
        return 1;
    }

}
