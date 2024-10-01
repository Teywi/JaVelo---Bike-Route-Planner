package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * BaseMapManager draws the map and the waypoints and manages the interactions with the map
 * (scroll and drag)
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class BaseMapManager {

    private static final int TILE_SIZE = 256;
    private static final int MIN_ZOOM = 8;
    private static final int MAX_ZOOM = 19;
    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> property;
    private final WaypointsManager waypointsManager;
    private final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private double lastX;
    private double lastY;
    private int zoom;
    private boolean isDragging;

    /**
     * Constructor of BaseMapManager
     *
     * @param tileManager tile manager to get the tiles
     * @param waypointsManager waypoint manager
     * @param property parameters of the displayed map
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property){
        this.tileManager = tileManager;
        this.property = property;
        this.waypointsManager = waypointsManager;

        redrawNeeded = true;
        zoom = property.get().zoomLevel();

        canvas = new Canvas();
        pane = new Pane();

        pane.getChildren().add(canvas);

        addBindings();
        addListeners();
    }

    /**
     * Pane of the map
     *
     * @return pane of the map
     */
    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        drawOnCanvas();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void drawOnCanvas(){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        MapViewParameters mapViewParameters = property.get();
        Point2D point = mapViewParameters.topLeft();
        int zoom = mapViewParameters.zoomLevel();

        for (int i = 0; i <= Math.ceil(canvas.getWidth()/TILE_SIZE); i++) {
            for (int j = 0; j <= Math.ceil(canvas.getHeight()/TILE_SIZE); j++) {
                try {
                    graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(zoom,
                            (int)(point.getX()/TILE_SIZE) + i, (int)(point.getY()/TILE_SIZE) + j)),
                            -(point.getX()%TILE_SIZE) + TILE_SIZE*i, -(point.getY()%TILE_SIZE) + TILE_SIZE*j);

                }catch (IOException ignored){}
            }
        }
    }

    private void addListeners(){
        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            if (newS != null){
                newS.addPreLayoutPulseListener(this::redrawIfNeeded);
            }
        });

        canvas.heightProperty().addListener(e -> redrawOnNextPulse());
        canvas.widthProperty().addListener(e -> redrawOnNextPulse());

        property.addListener(e -> redrawOnNextPulse());

        pane.setOnMousePressed(e -> {
            isDragging = false;
            lastX = e.getX();
            lastY = e.getY();
        });

        pane.setOnMouseDragged(e -> {
            isDragging = true;

            MapViewParameters mapView = property.get();
            double newTopLeftX = mapView.x() + (lastX - e.getX());
            double newTopLeftY = mapView.y() + (lastY - e.getY());

            property.set(property.get().withMinXY(newTopLeftX, newTopLeftY));

            lastX = e.getX();
            lastY = e.getY();
        });


        pane.setOnMouseClicked(e -> {
            if (!isDragging){
                waypointsManager.addWaypoint(e.getX(), e.getY());
            }
        });

        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            MapViewParameters mapView = property.get();
            double mouseX = e.getX();
            double mouseY = e.getY();
            double delta = e.getDeltaY();

            PointWebMercator point = mapView.pointAt(mouseX, mouseY);
            zoom = Math2.clamp(MIN_ZOOM, (int)Math.signum(delta) + mapView.zoomLevel(), MAX_ZOOM);

            property.set(new MapViewParameters(zoom, point.xAtZoomLevel(zoom) - mouseX, point.yAtZoomLevel(zoom) - mouseY));
        });
    }

    private void addBindings(){
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }
}
