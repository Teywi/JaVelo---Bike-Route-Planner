package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Waypoint represents a waypoint
 *
 * @param point position of the waypoint in the Swiss coordinate system
 * @param nodeId identity of the closest node from the waypoint
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record Waypoint(PointCh point, int nodeId) {
}
