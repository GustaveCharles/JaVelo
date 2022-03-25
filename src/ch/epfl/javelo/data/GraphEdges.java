package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import static ch.epfl.javelo.Bits.extractSigned;
import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Q28_4.asFloat;

public record GraphEdges (ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations){

    private static final int OFFSET_INVERT = 0;
    private static final int OFFSET_LENGTH = OFFSET_INVERT + 4;
    private static final int OFFSET_ELEVATION= OFFSET_LENGTH + 2;
    private static final int OFFSET_ATTRIBUTES = OFFSET_ELEVATION + 2;
    private static final int EDGE_INTS = OFFSET_ATTRIBUTES + 2;
    private static final int DIVISE = 2;


    public boolean isInverted(int edgeId){

        int inv = edgesBuffer.getInt(OFFSET_INVERT + edgeId*EDGE_INTS);

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

        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_LENGTH + edgeId*EDGE_INTS)));
    }


    public double elevationGain(int edgeId){

        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_ELEVATION + edgeId*EDGE_INTS)));
    }

    public boolean hasProfile(int edgeId){

        int ProfilType = extractUnsigned(profileIds.get(edgeId), 30,2);

        if(ProfilType == 0) return false;

        return true;
    }

    private int profileType(int edgeId){
        int type =  extractUnsigned(profileIds.get(edgeId), 30, 2);
        return type;
    }

    public float[] profileSamples(int edgeId){

        if(!hasProfile(edgeId)) return new float[0];

        int profileType = profileType(edgeId);
        int firstSampleIndex = Bits.extractSigned(profileIds.get(edgeId),0,30);
        int edgeLength = Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_LENGTH + edgeId*EDGE_INTS));

        int nbSamplesOnEdge = ( 1 + Math2.ceilDiv( edgeLength,Q28_4.ofInt(DIVISE)));
        float[] profileSamples = new float[nbSamplesOnEdge];

        float firstSampleValue = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSampleIndex)));
        float NewSampleValue = firstSampleValue;
        profileSamples[0] = firstSampleValue;

        int delta =0, forIndex = 0,lengthOfExtraction =0,sum=1;

        if( profileType == 1){
            for(int i = 1; i<=Math.ceil(nbSamplesOnEdge); ++i) {
                float newValue = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(firstSampleIndex + i), 0, 16));
                if (sum < nbSamplesOnEdge) {
                    profileSamples[sum] = newValue;
                    ++sum;
                }
            }

            }else if(profileType == 2){
            delta = 2;
            forIndex = 1;
            lengthOfExtraction = 8;

        }else if(profileType == 3){
            delta = 3;
            forIndex = 3;
            lengthOfExtraction = 4;
        }

        if(profileType == 2 || profileType ==3){
            for(int i = 1; i<=Math.ceil(nbSamplesOnEdge/delta); ++i){
                short otherSampleValues = elevations.get(firstSampleIndex +i);
                for(int j=forIndex; j>=0;--j) {
                    float difference = Q28_4.asFloat(Bits.extractSigned(otherSampleValues, j * lengthOfExtraction, lengthOfExtraction));
                    if (sum<nbSamplesOnEdge) {
                        NewSampleValue += difference;
                        profileSamples[sum] = NewSampleValue;
                        ++sum;
                    }
                }
            }
        }

        return inverter(profileSamples,edgeId);
    }

    private float[] inverter(float[] invert,int edgeID){

        if(isInverted(edgeID)){
            int j=invert.length;
            float[] inverted = new float[j];

            for(int i =0; i< invert.length;++i){
                inverted[i]=invert[j-1];
                j-=1;
            }
            return inverted;
        }else{
            return invert;
        }

    }


    public int attributesIndex(int edgeId){
        return Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_ATTRIBUTES + edgeId*EDGE_INTS));
    }

}
