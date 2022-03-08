package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
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

        int ProfilType = Bits.extractUnsigned(profileIds.get(edgeId), 29,2);

        if(ProfilType == 0) return false;

        return true;
    }

    private int profileType(int edgeId){
        int type =  Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
        return type;
    }

    public float[] profileSamples(int edgeId){

        if(!hasProfile(edgeId)) return new float[0];

        int firstSampleIndex = Bits.extractSigned(profileIds.get(edgeId),0,30);

        double edgeLength = length(edgeId);
        int nbSamplesOnEdge = (int) ( 1 + Math.ceil(edgeLength/2)); //utiliser ceildiv
        float[] profileSamples = new float[nbSamplesOnEdge];
        Math2.ceilDiv((int) edgeLength,Q28_4.ofInt(2)); //definir 2 comme constante final static

        int sum=1;
        float firstSampleValue = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSampleIndex)));
        float NewSampleValue = firstSampleValue;
        profileSamples[0] = firstSampleValue;


        if(profileType(edgeId) == 2){
            for(int i = 1; i<=Math.ceil(nbSamplesOnEdge/2); ++i){
                short otherSampleValues = elevations.get(firstSampleIndex +i);
                for(int j=1; j>=0;--j) {
                    float difference = Q28_4.asFloat(Bits.extractSigned(otherSampleValues,j*8,8));
                    if(difference!=0) {
                        NewSampleValue+=difference;
                        profileSamples[sum] = NewSampleValue;
                        ++sum;
                    }
                }
            }


        }else if(profileType(edgeId) == 3){
            for(int i = 1; i<=Math.ceil(nbSamplesOnEdge/4)+1; ++i){//pas sur du +1
                short otherSampleValues = elevations.get(firstSampleIndex +i);
                for(int j=3; j>=0;--j){
                    float difference = Q28_4.asFloat(Bits.extractSigned(otherSampleValues,j*4,4));
                    if(difference!=0){
                         NewSampleValue+= difference;
                        profileSamples[sum] = NewSampleValue;
                        ++sum;
                    }
                }
            }
        }

        if(isInverted(edgeId)){
           return inverter(profileSamples);

        }else
            return profileSamples;
    }

    private float[] inverter(float[] invert){

        int j=invert.length;
        float[] inverted = new float[j];

            for(int i =0; i<=j;++i){
                inverted[i]=invert[j-1];
                j-=1;
            }



        return inverted;
    }

    public int attributesIndex(int edgeId){
        return edgesBuffer.getShort(OFFSET_ATTRIBUTES + edgeId*EDGE_INTS);
    }

}
