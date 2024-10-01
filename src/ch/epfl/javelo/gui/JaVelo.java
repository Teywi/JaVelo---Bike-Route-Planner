package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * JaVelo is the main class where the program can be executed
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class JaVelo extends Application {

    private SplitPane splitPane;
    private RouteBean routeBean;
    private ElevationProfileManager elevationProfileManager;
    private AnnotatedMapManager annotatedMapManager;
    private MenuItem gpx;

    /**
     * Launch the application
     *
     * @param args arguments
     */
    public static void main(String[] args) { launch(args); }

    /**
     * Displays all the panes on the graphic interface and the menu
     *
     * @param primaryStage main stage where all the panes and the menu are displayed
     * @throws Exception if usage of the graph flow after closure
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"), "tile.openstreetmap.org");
        routeBean = new RouteBean(new RouteComputer(graph, new CityBikeCF(graph)));
        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;

        annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);
        splitPane.getItems().add(annotatedMapManager.pane());

        elevationProfileManager = new ElevationProfileManager(routeBean.getElevationProfileProperty(),
                routeBean.getHighlightedPositionProperty());
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);

        StackPane stackPane = new StackPane(splitPane, errorManager.pane());

        Menu file = new Menu("Fichier");

        gpx = new MenuItem("Export GPX");
        gpx.disableProperty().bind(Bindings.createBooleanBinding(() -> routeBean.getRoute() == null,
                routeBean.getRouteProperty()));

        file.getItems().add(gpx);

        MenuBar menu = new MenuBar(file);

        BorderPane mainPane = new BorderPane();
        mainPane.getStylesheets().add("map.css");
        mainPane.setCenter(stackPane);
        mainPane.setTop(menu);

        addListeners();

        Scene scene = new Scene(mainPane);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addListeners(){
        gpx.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.getRoute(), routeBean.getElevationProfile());
            } catch (IOException ignored) {}
        });

        annotatedMapManager.mousePositionOnRouteProperty().addListener(e ->
                routeBean.setHighlightedPositionProperty(annotatedMapManager.mousePositionOnRouteProperty().get()));

        elevationProfileManager.mousePositionOnProfileProperty().addListener(f ->
                routeBean.setHighlightedPositionProperty(elevationProfileManager.mousePositionOnProfileProperty().get()));

        routeBean.getRouteProperty().addListener((r, oldR, newR) -> {
            if (oldR == null && newR != null){
                splitPane.getItems().add(elevationProfileManager.pane());
            }
            else if (oldR != null && newR == null){
                splitPane.getItems().remove(elevationProfileManager.pane());
            }
        });
    }
}
