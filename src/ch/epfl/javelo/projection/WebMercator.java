package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

import static java.lang.Math.PI;

/**
 * converts between WGS 84 coordinates and Web Mercator coordinates and vice versa
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public final class WebMercator {

    private WebMercator() {
    }

    /**
     * converts from WGS84 to Web Mercator
     *
     * @param lon of WGS84 coordinate
     * @return returns the x value of the Web Mercator coordinate in a value between 0 and 1 ([0,1])
     */
    public static double x(double lon) {
        return (lon + PI) / (2 * PI);
    }

    /**
     * converts from WGS84 to Web Mercator
     *
     * @param lat of WGS84 coordinate
     * @return returns the y value of the Web Mercator coordinate in a value between 0 and 1 ([0,1])
     */

    public static double y(double lat) {
        return (PI - Math2.asinh(Math.tan(lat))) / (2 * PI);
    }

    /**
     * converts from Web Mercator to WGS 84
     *
     * @param x of Web Mercator coordinate
     * @return returns the longitude coordinate of the WGS 84 coordinates (in radians)
     */
    public static double lon(double x) {
        return 2 * PI * x - PI;
    }

    /**
     * converts from Web Mercator to WGS 84
     *
     * @param y of Web Mercator coordinate
     * @return returns the latitude coordinate of the WGS 84 coordinates (in radians)
     */

    public static double lat(double y) {
        return Math.atan(Math.sinh(PI - 2 * PI * y));
    }

}
