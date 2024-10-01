package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * ElevationProfileComputer
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class ElevationProfileComputer {
    private ElevationProfileComputer(){}

    /**
     * Return the ElevationProfile of the itinerary
     *
     * @param route itinerary from which we want the ElevationProfile
     * @param maxStepLength maximum length between 2 profiles
     * @throws IllegalArgumentException if maxSetLength is negative or 0
     * @return the ElevationProfile of the itinerary, with maxStepLength between two profiles
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);

        int firstNotNaN = -1;
        int lastNotNaN = -1;
        int firstNaN = -1;
        int lastNaN = -1;
        int numberProfile = (int)(1 + Math.ceil(route.length()/maxStepLength));

        float[] elevationProfile = new float[numberProfile];

        for (int i = 0; i < numberProfile; i++) {
            elevationProfile[i] = (float)route.elevationAt((i*route.length()/(numberProfile-1)));

            if (!Float.isNaN(elevationProfile[i])){
                firstNotNaN = (firstNotNaN == -1) ? i : firstNotNaN;
                lastNotNaN = i;
            }
        }

        if (firstNotNaN == -1){
            Arrays.fill(elevationProfile, 0);
        }
        else {
            Arrays.fill(elevationProfile, 0, firstNotNaN, elevationProfile[firstNotNaN]);

            if (lastNotNaN != numberProfile-1){
                Arrays.fill(elevationProfile, lastNotNaN+1, numberProfile, elevationProfile[lastNotNaN]);
            }
        }

        //Fill the gaps
        for (int i = 1; i < numberProfile - 1; i++) {
            if (Float.isNaN(elevationProfile[i])){
                if (!Float.isNaN(elevationProfile[i-1])){
                    firstNaN = i;
                }
                if (!Float.isNaN(elevationProfile[i+1])){
                    lastNaN = i;
                }
            }
            if ((firstNaN != -1) && (lastNaN != -1)){
                for (int j = firstNaN; j <= lastNaN ; j++) {
                    elevationProfile[j] = (float)Math2.interpolate(elevationProfile[firstNaN-1],
                            elevationProfile[lastNaN+1],
                            (j + 1 - firstNaN)/(lastNaN-firstNaN+2.0));
                }
                firstNaN = -1;
                lastNaN = -1;
            }
        }

        return new ElevationProfile(route.length(),elevationProfile);
    }
}