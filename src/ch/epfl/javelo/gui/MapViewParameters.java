package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;


/**
 * The class MapViewParameters represents the parameters of the map displayed on the graphic interface
 *
 * @param zoomLevel zoom level of the displayed map
 * @param x x-coordinate of the top left
 * @param y y-coordinate of the top left
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record MapViewParameters(int zoomLevel, double x, double y) {

    /**
     * The top left-point of the map displayed on the graphic interface
     *
     * @return top-left point of the map displayed on the graphic interface
     */
    public Point2D topLeft(){
        return new Point2D(x, y);
    }

    /**
     * New MapViewParameters with a different top-left point but same zoom level
     *
     * @param xPoint new x-coordinate of the top-left point
     * @param yPoint new y-coordinate of the top-left point
     * @return MapViewParameters with the top-left point given and the same zoom level as the receptor
     */
    public MapViewParameters withMinXY(double xPoint, double yPoint){
        return new MapViewParameters(zoomLevel, xPoint, yPoint);
    }

    /**
     * PointWebMercator at given coordinates compared to the map displayed on the graphic interface
     *
     * @param xPoint x-coordinate of the point
     * @param yPoint y-coordinate of the point
     * @return PointWebMercator at given coordinates compared to the map displayed on the graphic interface
     */
    public PointWebMercator pointAt(double xPoint, double yPoint){
        return PointWebMercator.of(zoomLevel, xPoint + x, yPoint + y);
    }

    /**
     * X-coordinate from a given PointWebMercator on the map displayed on the graphic interface
     *
     * @param point point given
     * @return X-coordinate from a given PointWebMercator on the map displayed on the graphic interface
     */
    public double viewX(PointWebMercator point){
        return (point.xAtZoomLevel(zoomLevel) - x);
    }

    /**
     * Y-coordinate from a given PointWebMercator on the map displayed on the graphic interface
     *
     * @param point point given
     * @return Y-coordinate from a given PointWebMercator on the map displayed on the graphic interface
     */
    public double viewY(PointWebMercator point){
        return (point.yAtZoomLevel(zoomLevel) - y);
    }
}
