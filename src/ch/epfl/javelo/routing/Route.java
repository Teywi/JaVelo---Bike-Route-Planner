package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import java.util.List;

/**
 * Route
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public interface Route {

    /**
     * Return the index of the segment in the List at the given position
     *
     * @param position position on the route
     * @return index of the segment in the List at the given position
     */
    int indexOfSegmentAt(double position);

    /**
     * Return the total length of the route
     *
     * @return length of the route
     */
    double length();

    /**
     * Return the List of Edge that creates the route
     *
     * @return List of Edge of the route
     */
    List<Edge> edges();

    /**
     * Return the List of PointCh of all end of edges on the route
     *
     * @return List of PointCh of all end of edges on the route
     */
    List<PointCh> points();

    /**
     * Return the PointCh at the given position on the route
     *
     * @param position position on the itinerary
     * @return PointCh at the given position on the itinerary
     */
    PointCh pointAt(double position);

    /**
     * Return the elevation at the given position on the route
     *
     * @param position position on the itinerary
     * @return elevation at the given position on the itinerary
     */
    double elevationAt(double position);

    /**
     * Return the identity of the closest node from the given position on the itinerary
     *
     * @param position position on the itinerary
     * @return identity of the closest node from the given position on the itinerary
     */
    int nodeClosestTo(double position);

    /**
     * Return the RoutePoint of the closest PointCh to point on the itinerary
     *
     * @param point position on the itinerary
     * @return RoutePoint of the closest PointCh to point on the itinerary
     */
    RoutePoint pointClosestTo(PointCh point);
}
