package ch.epfl.javelo.projection;

/**
 * SwissBounds.
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class SwissBounds {
    private SwissBounds() {
    }

    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Checks if the coordinates are in the Swiss bounds
     *
     * @param e the coordinates on the x-axis
     * @param n the coordinates on the y-axis
     * @return true if the coordinates are in the Swiss bounds, false otherwise
     */
    public static boolean containsEN(double e, double n) {
        return e >= MIN_E && e <= MAX_E && n >= MIN_N && n <= MAX_N;
    }
}