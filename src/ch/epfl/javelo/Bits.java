package ch.epfl.javelo;

public final class Bits {
    private Bits() {
    }

    public static int extractSigned(int value, int start, int length) {
        int range = start + length;

        if (range >= 0 & range <= 31) {
            int shift1 = value << (32 - (start + length));
            int shift2 = shift1 >> (32 - length);
            return shift2;
        } else {
            throw new IllegalArgumentException("Select a valid range (between 0 and 31 (included)");
        }
    }

    public static int extractUnsigned(int value, int start, int length) {
        int range = start + length;

        if (range >= 0 & range < 31) {
            int shift1 = value << (32 - (start + length));
            int shift2 = shift1 >> (32 - length);
            return shift2;
        } else {
            throw new IllegalArgumentException("Select a valid range (between 0 and 31)");
        }
    }

}