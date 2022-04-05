package ch.epfl.javelo;

import static java.lang.Math.fma;

/**
 * offers static methods for performing certain mathematical calculations
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
//TODO regarder si le triple if/else on peut le condenser en un ?: (opérateur ternaire)
// TODO voir quand utiliser squaredNorm : l'utiliser lorsqu 'on demande le point le plus proche
//  d'un certain segment
public final class Math2 {

    private Math2() {
    }

    /**
     * performs the ceil of the division of x by y
     *
     * @param x an int
     * @param y an int
     * @return the ceil of the division of x by y
     * @throws IllegalArgumentException if the x is negative, y equals 0 or y is negative
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(!(x < 0 || y == 0 || y < 0));
        return (x + y - 1) / y;
    }

    /**
     * uses the Math library fma method to perform an interpolation
     *
     * @param y0 coordinate
     * @param y1 coordinate
     * @param x  coordinate
     * @return returns the y coordinate of the point on the line
     * passing through (0,y0) and (1,y1) and the given x coordinate
     */
    public static double interpolate(double y0, double y1, double x) {
        return fma(y1 - y0, x, y0);
    }

    /**
     * Limit a certain value (an integer) in a range between a maximum and a minimum
     *
     * @param min minimum value in the range
     * @param v   value evaluated
     * @param max minimum value in the range
     * @return a value between min and max
     * @throws IllegalArgumentException if the minimum is greater than the maximum
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);

        if (v < min) {
            return min;
        } else if (v > max) {
            return max;
        } else {
            return v;
        }
    }

    /**
     * Limit a certain value (a double) in a range between a maximum and a minimum
     *
     * @param min minimum value in the range
     * @param v   value evaluated
     * @param max minimum value in the range
     * @return a value between min and max
     * @throws IllegalArgumentException if the minimum is greater than the maximum
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(min <= max);

        if (v < min) {
            return min;
        } else if (v > max) {
            return max;
        } else {
            return v;
        }
    }

    /**
     * computes the inverse hyperbolic sine of x
     *
     * @param x
     * @return the inverse hyperbolic sine of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(Math.pow(x, 2) + 1));
    }

    /**
     * computes the dot product of U and V
     *
     * @param uX x-coordinate of vector U
     * @param uY y-coordinate of vector U
     * @param vX x-coordinate of vector V
     * @param vY y-coordinate of vector V
     * @return the dot product of U and V
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return fma(uX, vX, uY * vY);
    }

    /**
     * computes the squared norm of a vector U
     *
     * @param uX x-coordinate of vector U
     * @param uY y-coordinate of vector U
     * @return the squared norm of a vector U
     */
    public static double squaredNorm(double uX, double uY) {
        return Math.pow(uX, 2) + Math.pow(uY, 2);
    }

    /**
     * computes the norm of a vector U
     *
     * @param uX x-coordinate of vector U
     * @param uY y-coordinate of vector U
     * @return the norm of a vector U
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(Math.pow(uX, 2) + Math.pow(uY, 2));
    }

    /**
     * @param aX x-coordinate of vector A
     * @param aY y-coordinate of vector A
     * @param bX x-coordinate of vector B
     * @param bY y-coordinate of vector B
     * @param pX x-coordinate of vector P
     * @param pY y-coordinate of vector P
     * @return the length of the projection of the vector going from point A (of coordinates aX and aY)
     * to point P (of coordinates pX and pY) on the vector going from point A to point B
     * (of components bY and bY)
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {

        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;

        return dotProduct(uX, uY, vX, vY) / norm(vX, vY);
    }
}