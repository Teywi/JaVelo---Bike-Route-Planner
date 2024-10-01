package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * RoutePoint
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    //Nonexistent point
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Return point with position shifted by the given difference
     *
     * @param positionDifference difference of position
     * @return the initial point shifted by positionDifference
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        if (positionDifference == 0){
            return this;
        }

        return new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * Return this if its reference distance is lower or equal that the reference distance of the given RoutePoint, that otherwise
     *
     * @param that given RoutePoint
     * @return this if its reference distance is lower or equal that the reference distance of the given RoutePoint, that otherwise
     */
    public RoutePoint min(RoutePoint that){
        return this.distanceToReference <= that.distanceToReference ? this : that;
    }

    /**
     * Return this if its reference distance is lower or equal that the reference distance of the given RoutePoint, a new RoutePoint with the given attributes otherwise
     *
     * @param thatPoint point of the new RoutePoint
     * @param thatPosition position of the new RoutePoint
     * @param thatDistanceToReference reference distance of the new RoutePoint
     * @return this if its reference distance is lower or equal that the reference distance of the given RoutePoint, a new RoutePoint with the given attributes otherwise
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        if (this.distanceToReference <= thatDistanceToReference){
            return this;
        }

        return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}
