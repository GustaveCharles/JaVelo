package ch.epfl.javelo;

import static java.lang.Math.fma;

public final class Math2 {

    private Math2 () {}

    /**
     *
     * @param x
     * @param y
     * @return the ceil of the division of x by y
     */
    public static int ceilDiv(int x, int y){
        if (x<0 || y == 0 || y<0){
            throw new IllegalArgumentException(); //
        } else {
            return (x+y-1)/y;
        }
    }

    public static double interpolate(double y0, double y1, double x){
        return fma(y1-y0,x,y0);
    }

    /**
     * Limit a certain value (an integer) in a range between a maximum and a minimum
     * @param min minimum value in the range
     * @param v value evaluated
     * @param max minimum value in the range
     * @return a value between min and max
     */
    public static int clamp(int min, int v, int max){
        if (min>max){
            throw new IllegalArgumentException();
        }
        if (v<min){
            return min;
        }
        else if (v>max){
            return max;
        }
        else{
            return v;
        }
    }
    /**
     * Limit a certain value (a double) in a range between a maximum and a minimum
     * @param min minimum value in the range
     * @param v value evaluated
     * @param max minimum value in the range
     * @return a value between min and max
     */
    public static double clamp(double min, double v, double max){
        if (min>max){
            throw new IllegalArgumentException();
        }
        if (v<min){
            return min;
        }
        else if (v>max){
            return max;
        }
        else{
            return v;
        }
    }

    /**
     *
     * @param x
     * @return the inverse hyperbolic sine of x
     */
    public static double asinh(double x){
        return Math.log(x+Math.sqrt(Math.pow(x,2) + 1));
    }

    /**
     *
     * @param uX x-coordinate of vector U
     * @param uY y-coordinate of vector U
     * @param vX x-coordinate of vector V
     * @param vY y-coordinate of vector V
     * @return the dot product of U and V
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return fma(uX, vX, uY*vY);
    }

    /**
     *
     * @param uX x-coordinate of vector U
     * @param uY y-coordinate of vector U
     * @return the squared norm of a vector U
     */
    public static double squaredNorm(double uX, double uY){
        return Math.pow(uX,2) + Math.pow(uY, 2);
    }

    /**
     *
     * @param uX x-coordinate of vector U
     * @param uY y-coordinate of vector U
     * @return the norm of a vector U
     */
    public static double norm(double uX, double uY){
        return Math.sqrt(Math.pow(uX,2) + Math.pow(uY, 2));
    }

    /**
     *
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
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){

        double uX = pX-aX;
        double uY = pY-aY;
        double vX = bX-aX;
        double vY = bY-aY;

        return dotProduct(uX, uY, vX, vY)/norm(vX, vY);
    }
}