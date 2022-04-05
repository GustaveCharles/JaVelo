package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static ch.epfl.javelo.Bits.extractUnsigned;

/**
 * represents the array of all edges of the JaVelo graph
 * and gives all its characteristics
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int OFFSET_INVERT = 0;
    private static final int OFFSET_LENGTH = OFFSET_INVERT + 4;
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + 2;
    private static final int OFFSET_ATTRIBUTES = OFFSET_ELEVATION + 2;
    private static final int EDGE_INTS = OFFSET_ATTRIBUTES + 2;
    private static final int DIVISE = 2;


    private enum ProfileTypeEnum {
        PROFILE_TYPE_INEXISTANT(0),
        PROFILE_TYPE_1(1),
        PROFILE_TYPE_2(2),
        PROFILE_TYPE_3(3);

        int nombre;

        ProfileTypeEnum(int nombre) {
            this.nombre = nombre;
        }
    }

    /**
     * the default should never happen
     */
    private ProfileTypeEnum switch2(int nombre) {
        switch (nombre) {
            case 0:
                return ProfileTypeEnum.PROFILE_TYPE_INEXISTANT;
            case 1:
                return ProfileTypeEnum.PROFILE_TYPE_1;
            case 2:
                return ProfileTypeEnum.PROFILE_TYPE_2;
            case 3:
                return ProfileTypeEnum.PROFILE_TYPE_3;
            default:
                throw new IllegalArgumentException();
        }
    }


    /**
     * checks in which way the edge is going
     *
     * @param edgeId the given edge
     * @return returns true iff the edge with the given identity goes
     * in the opposite direction to the OSM channel it comes from
     */
    public boolean isInverted(int edgeId) {

        int inv = edgesBuffer.getInt(OFFSET_INVERT + edgeId * EDGE_INTS);

        return inv < 0;
    }

    /**
     * extracts from the edges buffer the identity of the destination node
     *
     * @param edgeId the given edge
     * @return returns the identity of the destination node of the given identity edge
     */
    public int targetNodeId(int edgeId) {
        int targetNode = edgesBuffer.getInt(OFFSET_INVERT + edgeId * EDGE_INTS);

        return (isInverted(edgeId)) ? ~targetNode :
                edgesBuffer.getInt(OFFSET_INVERT + edgeId * EDGE_INTS);
    }

    /**
     * extracts a short representation of two byte buffers and then converts it to decimal (double)
     *
     * @param edgeId the given edge
     * @return returns the length, in meters, of the given identity edge
     */
    public double length(int edgeId) {

        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_LENGTH + edgeId * EDGE_INTS)));
    }

    /**
     * extracts a short representation of two byte buffers and then converts it to decimal (double)
     *
     * @param edgeId the given edge
     * @return returns the positive elevation, in meters, of the edge with the given identity
     */
    public double elevationGain(int edgeId) {

        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_ELEVATION + edgeId * EDGE_INTS)));
    }

    /**
     * checks if the edge has a profile
     *
     * @param edgeId the given edge
     * @return returns true iff the given identity edge has a profile
     */
    public boolean hasProfile(int edgeId) {

        int ProfilType = extractUnsigned(profileIds.get(edgeId), 30, 2);

        return ProfilType != 0;
    }

    /**
     * @param edgeId the given edge
     * @return returns the array of samples of the profile
     * of the edge with the given identity, which is empty if the edge does not have a profile
     */
    public float[] profileSamples(int edgeId) {

        int profileType = extractUnsigned(profileIds.get(edgeId), 30, 2);
        int firstSampleIndex = Bits.extractSigned(profileIds.get(edgeId), 0, 30);
        int edgeLength = Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_LENGTH + edgeId * EDGE_INTS));

        int nbSamplesOnEdge = (1 + Math2.ceilDiv(edgeLength, Q28_4.ofInt(DIVISE)));
        float[] profileSamples = new float[nbSamplesOnEdge];

        float firstSampleValue = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSampleIndex)));
        float newSampleValue = firstSampleValue;
        profileSamples[0] = firstSampleValue;

        switch (switch2(profileType)) {

            case PROFILE_TYPE_INEXISTANT:
                return new float[0];

            case PROFILE_TYPE_1:
                int sum = 1;
                for (int i = 1; i <= Math.ceil(nbSamplesOnEdge); ++i) {
                    float newValue = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(firstSampleIndex + i), 0, 16));
                    if (sum < nbSamplesOnEdge) {
                        profileSamples[sum] = newValue;
                        ++sum;
                    }
                }
                return inverter(profileSamples, edgeId);

            case PROFILE_TYPE_2:
                type2and3(nbSamplesOnEdge, 2, firstSampleIndex, 1, 8, profileSamples, newSampleValue);
                return inverter(profileSamples, edgeId);

            case PROFILE_TYPE_3:
                type2and3(nbSamplesOnEdge, 3, firstSampleIndex, 3, 4, profileSamples, newSampleValue);
                return inverter(profileSamples, edgeId);

        }
        return new float[0];
    }

    private float[] inverter(float[] invert, int edgeID) {

        if (isInverted(edgeID)) {
            int j = invert.length;
            float[] inverted = new float[j];

            for (int i = 0; i < invert.length; ++i) {
                inverted[i] = invert[j - 1];
                j -= 1;
            }
            return inverted;
        } else {
            return invert;
        }

    }

    private float[] type2and3(int nbSamplesOnEdge, int delta, int firstSampleIndex, int forIndex, int lengthOfExtraction, float[] profileSamples, float NewSampleValue) {
        int sum = 1;
        for (int i = 1; i <= Math.ceil(nbSamplesOnEdge / delta); ++i) {
            short otherSampleValues = elevations.get(firstSampleIndex + i);
            for (int j = forIndex; j >= 0; --j) {
                float difference = Q28_4.asFloat(Bits.extractSigned(otherSampleValues, j * lengthOfExtraction, lengthOfExtraction));
                if (sum < nbSamplesOnEdge) {
                    NewSampleValue += difference;
                    profileSamples[sum] = NewSampleValue;
                    ++sum;
                }
            }
        }
        return profileSamples;
    }

    /**
     * extracts from the buffer a short composed of two bytes and then covnerts it to int
     *
     * @param edgeId the given edge
     * @return returns the identity of the attribute set attached to the given identity edge
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(OFFSET_ATTRIBUTES + edgeId * EDGE_INTS));
    }

}
