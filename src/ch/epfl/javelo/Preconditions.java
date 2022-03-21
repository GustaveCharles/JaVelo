package ch.epfl.javelo;

/**
 * Preconditions of any method
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class Preconditions {

    /** private constructor to make the class non-instantiable
     *
     */

    private Preconditions() {}

    /** throws an exception if its argument is false and does nothing otherwise
     *
     * @param shouldBeTrue the boolean value
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
