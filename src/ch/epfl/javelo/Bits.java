package ch.epfl.javelo;

public final class Bits {
    private Bits () {}

    public static int extractSigned(int value, int start, int length){
        if (start + length >= 31){
            throw new IllegalArgumentException();
        }
        int shift1 = value << (32-(start+length));
        int shift2 = shift1 >> (32-length);

        return shift2;
    }

    public static int extractUnsigned(int value, int start, int length){
        if (start + length >= 31){
            throw new IllegalArgumentException();
        }
        int shift1 = value << (32-(start+length));
        int shift2 = shift1 >>> (32-length);

        return shift2;
    }
}