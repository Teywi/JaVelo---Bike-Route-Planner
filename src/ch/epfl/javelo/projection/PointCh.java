package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * PointCh
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record PointCh(double e, double n) {

    /**
     * Compact constructor of PointCh
     *
     * @throws IllegalArgumentException if the point isn't in the Swiss bounds
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Squared distance between two points
     *
     * @param that point from which we want to know the squared distance
     * @return distance between the two points squared
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e - this.e, that.n - this.n);
    }

    /**
     * Distance between two points
     *
     * @param that point from which we want to know the distance
     * @return distance between the points
     */
    public double distanceTo(PointCh that) {
        //TODO return Math2.norm(that.e - this.e, that.n - this.n);
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * Longitude of the point
     *
     * @return longitude of the point
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Latitude of the point
     *
     * @return latitude of the point
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}

