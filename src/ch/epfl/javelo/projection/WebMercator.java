
package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

public final class WebMercator {

    /**
     * private constructor, non instantiable
     */
    private WebMercator(){}

    /**
     * passage de WGS84 à Web Mercator
     * @param lon
     * @return
     */
    public static double x(double lon){


        double X = (lon + Math.PI)/(2*Math.PI);

        return X;
    }

    public static double y(double lat){

        double Y = (Math.PI - Math2.asinh(Math.tan(lat)));

        return Y;
    }

    /**
     * passage de Web Mercator a WGS84
     * @param x
     * @return
     */
    public static double lon(double x){

        double lon = 2*Math.PI*x - Math.PI;

        return Math.round(Math.toDegrees(lon)); // verifier avec un assistant si c est bon de faire math.round
                                                // et math.todegree
    }

    public static double lat(double y){

        double lat = Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));

        return Math.round(Math.toDegrees(lat));
    }

}