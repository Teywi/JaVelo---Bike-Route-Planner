package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * RouteManger displays the route and manages the interactions with the circle
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class RouteManager {

    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapViewProperty;
    private final Pane pane;
    private final Polyline polyline;
    private final Circle circle;

    /**
     * Constructor of RouteManager
     *
     * @param routeBean proprieties of the route
     * @param mapViewProperty proprieties of the displayed map
     */
    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> mapViewProperty){
        this.routeBean = routeBean;
        this.mapViewProperty = mapViewProperty;

        pane = new Pane();
        pane.setPickOnBounds(false);

        polyline = new Polyline();
        polyline.setId("route");
        pane.getChildren().add(polyline);
        polyline.setVisible(false);

        circle = new Circle(5);
        circle.setId("highlight");
        pane.getChildren().add(circle);
        circle.setVisible(false);

        addListeners();
    }

    /**
     * Pane of the route
     *
     * @return pane of route
     */
    public Pane pane(){
        return pane;
    }

    private void updatePolyline(){
        if (routeBean.getRoute() != null){
            ObservableList<Double> points = FXCollections.observableArrayList();

            for(PointCh point : routeBean.getRoute().points()){
                points.addAll(fromPointToCoords(point));
            }

            polyline.getPoints().setAll(points);
            polyline.setLayoutX(0);
            polyline.setLayoutY(0);
        }
    }

    private List<Double> fromPointToCoords(PointCh pointCh){
        List<Double> coords = new ArrayList<>();
        MapViewParameters mapView = mapViewProperty.get();
        PointWebMercator point = PointWebMercator.ofPointCh(pointCh);

        coords.add(mapView.viewX(point));
        coords.add(mapView.viewY(point));

        return coords;
    }

    private void updateCircle(){
        if (routeBean.getRoute() !=  null && !Double.isNaN(routeBean.getHighlightedPosition())){
            MapViewParameters mapView = mapViewProperty.get();
            PointCh pointCh = routeBean.getRoute().pointAt(routeBean.getHighlightedPosition());
            PointWebMercator point = PointWebMercator.ofPointCh(pointCh);

            circle.setVisible(true);
            circle.setLayoutX(mapView.viewX(point));
            circle.setLayoutY(mapView.viewY(point));
        }
        else {
            circle.setVisible(false);
        }
    }

    private void addListeners(){
        circle.setOnMouseClicked(e -> {
            PointCh pointCh = routeBean.getRoute().pointAt(routeBean.getHighlightedPosition());

            int nodeClosestTo = routeBean.getRoute().nodeClosestTo(routeBean.getHighlightedPosition());
            int index = routeBean.indexOfNonEmptySegmentAt(routeBean.getHighlightedPosition());
            PointCh pointClosestTo = routeBean.getRoute().pointClosestTo(pointCh).point();

            routeBean.getWaypointsProperty().add(index + 1, new Waypoint(pointClosestTo, nodeClosestTo));
        });

        mapViewProperty.addListener((p, oldM, newM) -> {
            if (oldM == null || oldM.zoomLevel() != newM.zoomLevel()){
                mapViewProperty.set(newM);
                updatePolyline();
                updateCircle();
            }
            else {
                Point2D point = polyline.localToParent(oldM.x() - newM.x(), oldM.y() - newM.y());

                polyline.setLayoutX(point.getX());
                polyline.setLayoutY(point.getY());
                circle.setLayoutX(circle.getLayoutX() + oldM.x() - newM.x());
                circle.setLayoutY(circle.getLayoutY() + oldM.y() - newM.y());
            }
        });

        routeBean.getWaypointsProperty().addListener((Observable o) -> {
            circle.setVisible(false);

            if (routeBean.getRoute() == null){
                polyline.setVisible(false);
            }
            else {
                updateCircle();
                updatePolyline();
                polyline.setVisible(true);
            }
        });

        routeBean.getHighlightedPositionProperty().addListener(e -> updateCircle());
    }
}

