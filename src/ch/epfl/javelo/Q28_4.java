package ch.epfl.javelo;

import static java.lang.Math.scalb;

public final class Q28_4 {

    private Q28_4 () {}

    public static int ofInt(int i){
        return i << 4;
    }
    public static double asDouble(int q28_4){
        return scalb(q28_4, -4);
    }

    public static float asFloat(int q28_4) {
        return scalb(q28_4, -4);
    }

}
