package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * SingleRoute
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class SingleRoute implements Route {

    private final List<Edge> edges;
    private final double[] positionOnRoute;

    /**
     * Constructor of class SingleRoute
     *
     * @param edges List of Edge
     * @throws IllegalArgumentException if edges is empty
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());

        this.edges = List.copyOf(edges);

        double[] positionOnRoute = new double[edges.size() + 1];
        double position = 0;

        positionOnRoute[0] = 0.0;

        for (int i = 0; i < edges.size(); i++) {
            position += edges.get(i).length();
            positionOnRoute[i + 1] = position;
        }
        this.positionOnRoute = positionOnRoute;
    }

    /**
     * @inheritdoc
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * @inheritdoc
     */
    @Override
    public double length() {
        return positionOnRoute[edges.size()];
    }

    /**
     * @inheritdoc
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * @inheritdoc
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> allPoints = new ArrayList<>();

        for (Edge edge : edges) {
            allPoints.add(edge.fromPoint());
        }

        allPoints.add(edges.get(edges().size() - 1).toPoint());

        return allPoints;
    }

    /**
     * @inheritdoc
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        int edgeIndex = Arrays.binarySearch(positionOnRoute, position);

        if (edgeIndex >= 0) {
            return edgeIndex == edges().size() ?
                    edges.get(edgeIndex - 1).toPoint() :
                    edges.get(edgeIndex).fromPoint();
        }

        edgeIndex = -edgeIndex - 2;

        return edges.get(edgeIndex).pointAt(position - positionOnRoute[edgeIndex]);
    }

    /**
     * @inheritdoc
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        int edgeIndex = Arrays.binarySearch(positionOnRoute, position);

        if (edgeIndex >= 0) {
            return edgeIndex == edges().size() ?
                    edges.get(edgeIndex - 1).elevationAt(edges.get(edgeIndex - 1).length()) :
                    edges.get(edgeIndex).elevationAt(0);
        }

        edgeIndex = -edgeIndex - 2;

        return edges.get(edgeIndex).elevationAt(position - positionOnRoute[edgeIndex]);
    }

    /**
     * @inheritdoc
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        int edgeIndex = Arrays.binarySearch(positionOnRoute, position);

        if (edgeIndex >= 0) {
            return edgeIndex == edges().size() ?
                    edges.get(edgeIndex - 1).toNodeId() :
                    edges.get(edgeIndex).fromNodeId();
        }

        edgeIndex = -edgeIndex - 2;

        return (position - positionOnRoute[edgeIndex]) <= edges.get(edgeIndex).length() / 2 ?
                edges.get(edgeIndex).fromNodeId() :
                edges.get(edgeIndex).toNodeId();
    }

    /**
     * @inheritdoc
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        int i = 0;

        for (Edge edge : edges) {
            double projectionLength = edge.positionClosestTo(point);
            projectionLength = Math2.clamp(0, projectionLength, edge.length());
            PointCh pointOnEdge = edge.pointAt(projectionLength);
            closestPoint = closestPoint.min(pointOnEdge, positionOnRoute[i] + projectionLength, point.distanceTo(pointOnEdge));
            i++;
        }

        return closestPoint;
    }
}
