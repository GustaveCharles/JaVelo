package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * represents a point in the Web Mercator system
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public record PointWebMercator(double x, double y) {

    /**
     * @throws IllegalArgumentException if the coordinates x and y are not in the interval [0,1]
     */

    public PointWebMercator {

        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /**
     * returns the point whose coordinates are x and y at the zoom level zoomLevel
     *
     * @param zoomLevel the zoomed level
     * @param x         the x coordinates at the zoomed level zoomLevel
     * @param y         the x coordinates at the zoomed level zoomLevel
     * @return returns the coordinates in the WebMercator system in the interval [0,1]
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        double X = Math.scalb(x, -8 - zoomLevel);
        double Y = Math.scalb(y, -8 - zoomLevel);
        return new PointWebMercator(X, Y);
    }

    /**
     * returns the Web Mercator point corresponding to the point in the given Swiss coordinate system
     * (from Ch1903 coordinates, to WGS84 coordinates to WebMercator coordinates)
     *
     * @param pointCh the coordinates in the Ch1903 system
     * @return returns the coordinates in the WebMercator system in the interval [0,1]
     */

    public static PointWebMercator ofPointCh(PointCh pointCh) {
        double X = WebMercator.x(pointCh.lon());
        double Y = WebMercator.y(pointCh.lat());

        return new PointWebMercator(X, Y);
    }

    /**
     * computes the algorithm to zoom a coordinate at a certain zoom level
     *
     * @param zoomLevel
     * @return returns the x coordinate at the given zoom level
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(this.x, 8 + zoomLevel);
    }

    /**
     * computes the algorithm to zoom a coordinate at a certain zoom level
     *
     * @param zoomLevel
     * @return returns the y coordinate at the given zoom level
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(this.y, 8 + zoomLevel); }

    /**
     * @return returns the longitude of the point in the WGS84 system in radians
     */
    public double lon() {
        return WebMercator.lon(this.x);
    }

    /**
     * @return returns the latitude of the point in the WGS84 system in radians
     */
    public double lat() {
        return WebMercator.lat(this.y);
    }

    /**
     * from Web Mercator coordinates to Ch1903 coordinates
     *
     * @return returns the Ch1903 coordinates if they are in the SwissBounds, returns null otherwise
     */
    public PointCh toPointCh() {
        double e = Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(), lat());
        if (!SwissBounds.containsEN(e, n)) {
            return null;
        }
        return new PointCh(e, n);
    }
}