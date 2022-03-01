package ch.epfl.javelo.projection;

/**
 * contains the necessary constant to delimit the Swiss territory
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Jean Dupond (339364)
 */

public final class SwissBounds {

    private SwissBounds(){}


    public static final double MIN_E = 2485000,MAX_E = 2834000, MIN_N = 1075000,
            MAX_N = 1296000, WIDTH = Math.abs(MAX_E-MIN_E), HEIGHT=Math.abs(MAX_N-MIN_N);

    /**
     *  tests if a point is within the limits of the Swiss territory
     * @param e east swiss coordinate
     * @param n north swiss coordinate
     * @return returns true if it is indeed within the limits and false otherwise
     */
    public static boolean containsEN(double e, double n){
        if(e<MAX_E && e>MIN_E && n<MAX_N && n>MIN_N){
            return true;
        }
        else return false;

    }
}
