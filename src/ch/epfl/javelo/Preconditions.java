package ch.epfl.javelo;

/**
 * Preconditions of any method
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Check one or multiple conditions of a method before performing it
     *
     * @param shouldBeTrue the boolean value
     * @throws IllegalArgumentException if its argument is false and does nothing otherwise
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
