package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.beans.Observable;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * WaypointsManager displays the waypoints and manages the interactions with the waypoints
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class WaypointsManager {

    private static final int SEARCH_DISTANCE = 500;
    private static final int NO_NODE_FOUND = -1;
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> property;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;
    private final Pane pane;
    private boolean isDragging;
    private double lastX;
    private double lastY;

    /**
     * Constructor of WaypointsManager
     *
     * @param graph graph
     * @param property parameters of the displayed map
     * @param initialPoints initial waypoints
     * @param errorConsumer error consumer
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property,
                            ObservableList<Waypoint> initialPoints, Consumer<String> errorConsumer){

        this.graph = graph;
        this.property = property;
        waypoints = initialPoints;
        this.errorConsumer = errorConsumer;

        isDragging = false;
        pane = new Pane();
        pane.setPickOnBounds(false);

        addListeners();
        updateGroups();

    }

    /**
     * Add a waypoint at a given position
     *
     * @param x x-coordinate of the waypoint
     * @param y y-coordinate of the waypoint
     */
    public void addWaypoint(double x, double y){
        int closestNodeId = closestNodeToWaypoint(x, y);

        if (closestNodeId == NO_NODE_FOUND){
            errorConsumer.accept("Aucune route à proximité !");
        }
        else {
            waypoints.add(new Waypoint(graph.nodePoint(closestNodeId), closestNodeId));
        }
    }

    private Group createGroup(String waypointType){
        Group group = new Group();
        group.getStyleClass().add("pin");
        group.getStyleClass().add(waypointType);

        SVGPath border = new SVGPath();
        border.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        border.getStyleClass().add("pin_outside");
        group.getChildren().add(border);

        SVGPath innerCircle = new SVGPath();
        innerCircle.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        innerCircle.getStyleClass().add("pin_outside ");
        group.getChildren().add(innerCircle);

        return group;
    }

    private void updateGroupPosition(Group group, Waypoint waypoint){
        MapViewParameters mapView = property.get();

        PointWebMercator point = PointWebMercator.ofPointCh(waypoint.point());
        double x = mapView.viewX(point);
        double y = mapView.viewY(point);

        group.setLayoutX(x);
        group.setLayoutY(y);
    }

    private void updateGroups(){
        List<Group> groupList = new ArrayList<>();
        Group groupToAdd;

        for (int i = 0; i < waypoints.size(); i++) {
            if (i == 0) {
                groupToAdd = createGroup("first");
            }
            else if (i == waypoints.size() - 1) {
                groupToAdd = createGroup("last");
            }
            else {
                groupToAdd = createGroup("middle");
            }

            groupAddListeners(groupToAdd);
            updateGroupPosition(groupToAdd, waypoints.get(i));

            groupList.add(groupToAdd);
        }

        pane.getChildren().setAll(groupList);
    }

    private int closestNodeToWaypoint(double x, double y){
        MapViewParameters mapView = property.get();

        PointWebMercator point = mapView.pointAt(x, y);
        PointCh pointAtClick = point.toPointCh();

        return graph.nodeClosestTo(pointAtClick, SEARCH_DISTANCE);
    }

    private void addListeners(){
        waypoints.addListener((Observable o) -> updateGroups());

        property.addListener((p, oldM, newM) -> {
            assert oldM == null;
            updateGroups();
        });
    }

    private void groupAddListeners(Group group){
        group.setOnMouseClicked(e -> {
            if (!isDragging){
                int index = pane.getChildren().indexOf(group);
                waypoints.remove(index);
            }
        });

        group.setOnMousePressed(e -> {
            lastX = e.getSceneX();
            lastY = e.getSceneY();
        });

        group.setOnMouseReleased(e -> {
            if (isDragging){
                int closestNodeId = closestNodeToWaypoint(group.getLayoutX(), group.getLayoutY());

                if (closestNodeId == NO_NODE_FOUND){
                    errorConsumer.accept("Aucune route à proximité !");
                    updateGroups();
                }
                else {
                    PointCh pointCh = graph.nodePoint(closestNodeId);
                    int index = pane.getChildren().indexOf(group);

                    waypoints.set(index, new Waypoint(pointCh, closestNodeId));
                }

                isDragging = false;
            }
        });

        group.setOnMouseDragged(e -> {
            isDragging = true;
            group.setLayoutX(group.getLayoutX() + (e.getSceneX() - lastX));
            group.setLayoutY(group.getLayoutY() + (e.getSceneY() - lastY));
            lastX = e.getSceneX();
            lastY = e.getSceneY();
        });
    }

    /**
     * Pane of the waypoints
     *
     * @return pane of the waypoints
     */
    public Pane pane(){
        return pane;
    }
}


