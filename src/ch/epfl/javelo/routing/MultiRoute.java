package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiRoute
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class MultiRoute implements Route{

    private final List<Route> segments;

    /**
     * Constructor of MultiRoute
     *
     * @param segments list of all the Route representing the MultiRoute
     * @throws IllegalArgumentException if segments is empty
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int indexOfSegmentAt(double position) {
        int i = 0;
        double totalLength = 0;

        for(Route segment : segments){
            i += segment.indexOfSegmentAt(position - totalLength);
            totalLength += segment.length();

            if (position <= totalLength){
                return i;
            }
            i++;
        }

        return i-1;
    }

    /**
     * @inheritdoc
     */
    @Override
    public double length() {
        double length = 0;

        for(Route segment : segments){
            length += segment.length();
        }

        return length;
    }

    /**
     * @inheritdoc
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges = new ArrayList<>();

        for(Route segment : segments){
            edges.addAll(segment.edges());
        }

        return edges;
    }

    /**
     * @inheritdoc
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        PointCh lastPoint = null;

        for(Route segment : segments){
            points.addAll(segment.points());
            lastPoint = points.get(points.size() - 1);
            points.remove(points.size()-1);
        }

        if (!(points.get(0).e() == lastPoint.e() && points.get(0).n() == lastPoint.n())){
            points.add(lastPoint);
        }

        return points;
    }

    /**
     * @inheritdoc
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length());
        double totalLength = 0;

        for (Route segment : segments){
            totalLength += segment.length();

            if (position <= totalLength){
                return segment.pointAt(position - totalLength + segment.length());
            }
        }

        return null; //should never do it
    }

    /**
     * @inheritdoc
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length());
        double totalLength = 0;

        for (Route segment : segments){
            totalLength += segment.length();

            if (position <= totalLength){
                return segment.elevationAt(position - totalLength + segment.length());
            }
        }

        return 0; //should never do it
    }

    /**
     * @inheritdoc
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length());
        double totalLength = 0;

        for (Route segment : segments){
            totalLength += segment.length();

            if (position <= totalLength){
                return segment.nodeClosestTo(position - totalLength + segment.length());
            }
        }
        return 0; //should never do it
    }

    /**
     * @inheritdoc
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        RoutePoint segmentPoint;

        double totalLength = 0;

        for (Route segment : segments) {
            segmentPoint = segment.pointClosestTo(point);
            if (closestPoint.distanceToReference() > segmentPoint.distanceToReference()) {
                closestPoint = closestPoint
                        .min(segmentPoint)
                        .withPositionShiftedBy(totalLength);
            }
            totalLength += segment.length();
        }

        return closestPoint;
    }
}
