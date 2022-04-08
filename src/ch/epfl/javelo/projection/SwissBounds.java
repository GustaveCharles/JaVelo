package ch.epfl.javelo.projection;

/**
 * Contains the necessary constant to delimit the Swiss territory
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class SwissBounds {

    private SwissBounds() {
    }

    /**
     * The bounds of the Swiss territory represented by a rectangle with a maximum
     * or a minimum on the north, south, east and west sides of the rectangle
     */
    public static final double MIN_E = 2485000, MAX_E = 2834000, MIN_N = 1075000,
            MAX_N = 1296000;

    /**
     * The width and the height of the rectangle delimiting the Swiss territory
     */
    public static final double WIDTH = Math.abs(MAX_E - MIN_E), HEIGHT = Math.abs(MAX_N - MIN_N);

    /**
     * tests if a point is within the limits of the Swiss territory
     *
     * @param e east swiss coordinate
     * @param n north swiss coordinate
     * @return returns true if it is indeed within the limits and false otherwise
     */
    public static boolean containsEN(double e, double n) {
        return (e <= MAX_E && e >= MIN_E && n <= MAX_N && n >= MIN_N);
    }
}

