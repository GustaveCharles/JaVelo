package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Represents the map view parameters presented in the graphic interface
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public record MapViewParameters(int zoomLevel, double xTopLeft, double yTopLeft) {

    /**
     * Create a 2D-Point with the given x and y parameters
     *
     * @return a 2D geometric point
     */
    public Point2D topLeft() {
        return new Point2D(xTopLeft, yTopLeft);
    }

    /**
     * Create a new instance of MapViewParameters with different x and y coordinates
     *
     * @param newXTopLeft the new x-parameter
     * @param newYTopLeft the new y-parameter
     */
    public MapViewParameters withMinXY(double newXTopLeft, double newYTopLeft) {
        return new MapViewParameters(zoomLevel, newXTopLeft, newYTopLeft);
    }

    /**
     * Converts x and y coordinates in relation to the top-left corner of the map portion into an instance
     * of PointWebMercator
     *
     * @param xComparedToXTopLeft x-coordinate
     * @param yComparedToYTopLeft y-coordinate
     * @return an instance of PointWebMercator
     */
    public PointWebMercator pointAt(double xComparedToXTopLeft, double yComparedToYTopLeft) {
        return PointWebMercator.of(zoomLevel, xTopLeft + xComparedToXTopLeft, yTopLeft + yComparedToYTopLeft);
    }

    /**
     * Computes the corresponding x position expressed in relation to the top-left corner
     * of the map portion displayed on the screen.
     *
     * @param point the given point
     * @return the x-coordinate in the portion scale
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel) - xTopLeft;
    }

    /**
     * Computes the corresponding y position expressed in relation to the top-left corner
     * of the map portion displayed on the screen.
     *
     * @param point the given point
     * @return the y-coordinate in the portion scale
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel) - yTopLeft;
    }
}
