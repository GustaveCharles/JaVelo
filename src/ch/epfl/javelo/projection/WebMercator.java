package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * converts between WGS 84 coordinates and Web Mercator coordinates and vice versa
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public final class WebMercator {

    /**
     * private constructor, non instantiable
     */
    private WebMercator(){}

    /**
     * converts from WGS84 to Web Mercator
     * @param lon of WGS84 coordinate
     * @return returns the x value of the Web Mercator coordinate in a value between 0 and 1 ([0,1])
     */
    public static double x(double lon){


        double X = (lon + Math.PI)/(2*Math.PI);

        return X;
    }

    /**
     * converts from WGS84 to Web Mercator
     * @param lat of WGS84 coordinate
     * @return returns the y value of the Web Mercator coordinate in a value between 0 and 1 ([0,1])
     */

    public static double y(double lat){

        double Y = (Math.PI - Math2.asinh(Math.tan(lat))) / (2*Math.PI);

        return Y;
    }

    /**
     * converts from Web Mercator to WGS 84
     * @param x of Web Mercator coordinate
     * @return returns the longitude coordinate of the WGS 84 coordinates (in radians)
     */
    public static double lon(double x){

        double lon = 2*Math.PI*x - Math.PI;

        return lon;
    }

    /**
     * converts from Web Mercator to WGS 84
     * @param y of Web Mercator coordinate
     * @return returns the latitude coordinate of the WGS 84 coordinates (in radians)
     */

    public static double lat(double y){

        double lat = Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));

        return lat;
    }

}