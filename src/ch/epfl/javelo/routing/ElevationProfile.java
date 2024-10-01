package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;

/**
 * ElevationProfile
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class ElevationProfile {

    private final double length;
    private final float[] elevationSamples;
    private final DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
    private final double totalAscent;
    private final double totalDescent;

    /**
     * Constructor of ElevationProfile
     *
     * @param length           length of the profile
     * @param elevationSamples elevations of the profile
     * @throws IllegalArgumentException if the length is negative or there are less than 2 elevations
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);

        this.length = length;
        this.elevationSamples = Arrays.copyOf(elevationSamples, elevationSamples.length);

        for (float elevationSample : elevationSamples) {
            statistics.accept(elevationSample);
        }

        //Computation of the totalAscent
        double totalAscentTemp = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            if (elevationSamples[i] > elevationSamples[i - 1]) {
                totalAscentTemp += (elevationSamples[i] - elevationSamples[i - 1]);
            }
        }
        totalAscent = totalAscentTemp;

        //Computation of the totalDescent
        double totalDescentTemp = 0;
        for (int i = 1; i < elevationSamples.length; i++) {
            if (elevationSamples[i] < elevationSamples[i - 1]) {
                totalDescentTemp += (elevationSamples[i - 1] - elevationSamples[i]);
            }
        }
        totalDescent = totalDescentTemp;

    }

    /**
     * Return the length of the profile
     *
     * @return length of the profile in meters
     */
    public double length() {
        return length;
    }

    /**
     * Return the minimal elevation of the profile
     *
     * @return minimal elevation of the profile in meters
     */
    public double minElevation() {
        return statistics.getMin();
    }

    /**
     * Return the maximum elevation of the profile
     *
     * @return maximum elevation of the profile in meters
     */
    public double maxElevation() {
        return statistics.getMax();
    }

    /**
     * Return total ascent of the profile
     *
     * @return total ascent of the profile in meters
     */
    public double totalAscent() {
        return totalAscent;
    }

    /**
     * Return total descent of the profile (value returned is positive)
     *
     * @return total descent of the profile in meters
     */
    public double totalDescent() {
       return totalDescent;
    }

    /**
     * Return elevation of the profile at the given position
     *
     * @param position position on the profile
     * @return elevation of the profile at the given position in meters
     */
    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }
}
