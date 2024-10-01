package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * AnnotatedMapManager displays the waypoints and the route
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class AnnotatedMapManager {

    private static final int MAX_PIXEL_DISTANCE = 15;
    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> property;
    private final Pane mainPane;
    private final ObjectProperty<Point2D> mouseOnScene;
    private final DoubleProperty mousePosition;
    private boolean isOnPane;

    /**
     * Constructor of AnnotatedMapManager
     *
     * @param graph graph
     * @param tileManager tile manager
     * @param routeBean proprieties of the route
     * @param stringConsumer error consumer
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> stringConsumer){

        this.routeBean = routeBean;

        property = new SimpleObjectProperty<>(new MapViewParameters(12, 543200, 370650));
        mouseOnScene = new SimpleObjectProperty<>();
        mousePosition = new SimpleDoubleProperty();

        WaypointsManager waypointsManager = new WaypointsManager(graph, property, routeBean.getWaypointsProperty(), stringConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, property);
        RouteManager routeManager = new RouteManager(routeBean, property);

        mainPane = new StackPane(baseMapManager.pane(), routeManager.pane(),
                        waypointsManager.pane());
        mainPane.getStylesheets().add("map.css");

        addListeners();
    }

    /**
     * Pane composed of map pane, waypoints pane and route pane
     *
     * @return pane of map pane, waypoints pane and route pane
     */
    public Pane pane(){
        return mainPane;
    }

    /**
     * Position of the mouse on the route
     *
     * @return propriety of the position of the mouse on the route, NaN if the mouse is not on the route
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        return mousePosition;
    }

    private double updateHighlightedPosition(){
        if (!isOnPane){
            return Double.NaN;
        }

        if (mouseOnScene.get() != null && routeBean.getRoute() != null){

            MapViewParameters mapView = property.get();

            PointWebMercator point = mapView.pointAt(mouseOnScene.get().getX(), mouseOnScene.get().getY());
            PointCh pointCh = point.toPointCh();

            if (pointCh != null){
                RoutePoint routePoint = routeBean.getRoute().pointClosestTo(pointCh);
                PointWebMercator pointOnRoute = PointWebMercator.ofPointCh(routePoint.point());

                double pixelDistance =
                        Math2.norm(mapView.viewX(point) - mapView.viewX(pointOnRoute),
                                mapView.viewY(point) - mapView.viewY(pointOnRoute));

                if (pixelDistance <= MAX_PIXEL_DISTANCE){
                    return routePoint.position();
                }
            }
        }

        return Double.NaN;
    }

    private void addListeners(){
        mousePosition.bind(Bindings.createDoubleBinding(this::updateHighlightedPosition,
                mouseOnScene, property, routeBean.getRouteProperty()));

        mainPane.setOnMouseMoved(e -> {
            isOnPane = true;
            mouseOnScene.set(new Point2D(e.getX(), e.getY()));
        });

        mainPane.setOnMouseExited(e -> isOnPane = false);
    }
}

