package ch.epfl.javelo;

import ch.epfl.javelo.Preconditions;

/**
 * Extraction of a bit
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public final class Bits {

    private static final int VECTOR_SIZE = Integer.SIZE;

    private Bits() {
    }

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at the index bit start,
     * which it interprets as a two's complement signed value
     *
     * @param value  the number in binary representation
     * @param start  the position of the beginning of the sequence
     * @param length the length of the sequence we want to extract
     * @return the extracted signed bit
     * @throws IllegalArgumentException if the range is invalid, meaning that either : the range,
     *                                  the start or the length is negative, or the range is greater than 32.
     */
    public static int extractSigned(int value, int start, int length) {
        int range = start + length;
        Preconditions.checkArgument((range >= 0 && range <= VECTOR_SIZE && start >= 0 && start < VECTOR_SIZE && length >= 0
                && length <= VECTOR_SIZE));
        int shift1 = value << (VECTOR_SIZE - (range));
        return shift1 >> (VECTOR_SIZE - length);
    }

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at the index bit start,
     * which it interprets in an unsigned way.
     *
     * @param value  the number in binary representation
     * @param start  the position of the beginning of the sequence
     * @param length the length of the sequence we want to extract
     * @return the extracted unsigned bit
     * @throws IllegalArgumentException if the range is invalid, meaning that either : the range, the start or the
     *                                  length is negative, or the range is greater or equal to 32.
     */
    public static int extractUnsigned(int value, int start, int length) {
        int range = start + length;
        Preconditions.checkArgument((range >= 0 && range <= VECTOR_SIZE && start >= 0 && start < VECTOR_SIZE &&
                length >= 0 && length < VECTOR_SIZE));
        int shift1 = value << (VECTOR_SIZE - (start + length));
        return shift1 >>> (VECTOR_SIZE - length);
    }
}