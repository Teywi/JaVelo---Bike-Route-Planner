package ch.epfl.javelo;

/**
 * Q28_4
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class Q28_4 {

    private static final int NUMBER_DECIMAL = 4;

    private Q28_4() {
    }

    /**
     * Convert an int i in Q32 to the Q28.4 representation
     *
     * @param i the initial int value in Q32
     * @return the initial value as an int in Q28.4
     */
    public static int ofInt(int i) {
        return i << NUMBER_DECIMAL;
    }

    /**
     * Convert an int in Q28.4 to a double in IEEE-754 representation
     *
     * @param q28_4 the initial value as an int in Q28.4
     * @return the initial value as a double in the IEEE-754 representation
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -NUMBER_DECIMAL);
    }

    /**
     * Convert an int in Q28.4 to a float in IEEE-754 representation
     *
     * @param q28_4 the initial value as an int in Q28.4
     * @return the initial value as a float in the IEEE-754 representation
     */
    public static float asFloat(int q28_4) {
        return Math.scalb((float) q28_4, -NUMBER_DECIMAL);
    }
}
