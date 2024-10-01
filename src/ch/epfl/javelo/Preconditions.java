package ch.epfl.javelo;

/**
 * Preconditions
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Checks if the argument given is adequate.
     *
     * @param shouldBeTrue the conditions that the argument must respect
     * @throws IllegalArgumentException if the argument does not respect the conditions
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }

}
