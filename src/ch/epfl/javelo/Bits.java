package ch.epfl.javelo;

/**
 * Extraction of a bit
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public final class Bits {

    /**
     * Private constructor of Bits in order to keep the class not instantiable
     */
    private Bits() {
    }

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at the index bit start,
     * which it interprets as a two's complement signed value
     * @param value the number in binary representation
     * @param start the position of the beginning of the sequence
     * @param length the length of the sequence we want to extract
     * @throws IllegalArgumentException if the range is invalid, meaning that either : the range,
     * the start or the length is negative, or the range is greater than 32.
     * @return the extracted signed bit
     */
    public static int extractSigned(int value, int start, int length) {
        int range = start + length;
        Preconditions.checkArgument((range >= 0 && range <= 32 && start>=0 && start <= 31 && length>=0 && length <= 32));
        int shift1 = value << (32 - (range));
        return shift1 >> (32 - length);
    }

    /**
     * Does the same thing as the previous method,
     * with two differences: on the one hand, the extracted value is interpreted in an unsigned way,
     * and on the other hand, the IllegalArgumentException exception is also thrown if length is 32.
     * @param value the number in binary representation
     * @param start the position of the beginning of the sequence
     * @param length the length of the sequence we want to extract
     * @throws IllegalArgumentException if the range is invalid, meaning that either : the range,
     *      * the start or the length is negative, or the range is greater or equal to 32.
     * @return the extracted unsigned bit
     */
    public static int extractUnsigned(int value, int start, int length) {
        int range = start + length;
        Preconditions.checkArgument((range >= 0 && range <= 32 && start>=0 && start <= 31 && length>=0 && length < 32));
        int shift1 = value << (32 - (start + length));
        return shift1 >>> (32 - length);
    }
}