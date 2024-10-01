package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Edge
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     * Instantiate an Edge from two nodes
     *
     * @param graph      graph where the edge is stored
     * @param edgeId     identity of the edge
     * @param fromNodeId identity of the first node of the edge
     * @param toNodeId   identity of the last node of the edge
     * @return Edge between fromNodeId to toNodeId
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Return closest position on the edge to the given point
     *
     * @param point point close to the edge
     * @return closest position on the edge to the given point
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * Return the point on the edge at the given position
     *
     * @param position position on the edge
     * @return the point on the edge at the given position
     */
    public PointCh pointAt(double position) {
        double pointE = Math2.interpolate(fromPoint.e(),toPoint.e(), position/length);
        double pointN = Math2.interpolate(fromPoint.n(), toPoint.n(), position/length);

        return new PointCh(pointE, pointN);
    }

    /**
     * Return the elevation on the edge at the given position in meters
     *
     * @param position position on the edge
     * @return elevation on the edge at the given position in meters
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
