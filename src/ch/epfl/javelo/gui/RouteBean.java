package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * RouteBean regroups the proprieties of the waypoints and of the route
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class RouteBean {
    private final static int MAX_CAPACITY = 100;
    private final static float LOAD_FACTOR = 0.75f;
    private final static int MAX_STEP_LENGTH = 5;
    private final ObservableList<Waypoint> waypointsProperty;
    private final ObjectProperty<Route> routeProperty;
    private final DoubleProperty highlightedPositionProperty;
    private final ObjectProperty<ElevationProfile> elevationProfileProperty;
    private final RouteComputer routeComputer;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> cacheMemory;

    /**
     * Constructor of RouteBean
     *
     * @param routeComputer route computer
     */
    public RouteBean(RouteComputer routeComputer){
        this.routeComputer = routeComputer;
        cacheMemory = new LinkedHashMap<>(MAX_CAPACITY, LOAD_FACTOR, true);

        routeProperty = new SimpleObjectProperty<>();
        elevationProfileProperty = new SimpleObjectProperty<>();
        highlightedPositionProperty = new SimpleDoubleProperty();

        waypointsProperty = FXCollections.observableArrayList();

        waypointsProperty.addListener((Observable o) -> routeProperty.set(computeRoute()));

        routeProperty.addListener(e -> elevationProfileProperty.set(computeElevation()));
    }

    /**
     * Highlighted position propriety
     *
     * @return highlighted position propriety
     */
    public DoubleProperty getHighlightedPositionProperty(){
        return highlightedPositionProperty;
    }

    /**
     * Highlighted position
     *
     * @return highlighted position
     */
    public double getHighlightedPosition(){
        return highlightedPositionProperty.get();
    }

    /**
     * Setter of the highlighted position
     *
     * @param newValue new highlighted position
     */
    public void setHighlightedPositionProperty(double newValue){
        if ( newValue != highlightedPositionProperty.get()){
            highlightedPositionProperty.set(newValue);
        }
    }

    /**
     * Best Route property
     *
     * @return best route property
     */
    public ReadOnlyObjectProperty<Route> getRouteProperty(){
        return routeProperty;
    }

    /**
     * Best Route
     *
     * @return best route
     */
    public Route getRoute(){
        return routeProperty.get();
    }

    /**
     * Elevations propriety of the best route
     *
     * @return elevations propriety of the best route
     */
    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty(){
        return elevationProfileProperty;
    }

    /**
     * Elevations of the best route
     *
     * @return elevations of the best route
     */
    public ElevationProfile getElevationProfile(){
        return elevationProfileProperty.get();
    }

    /**
     * Observable list of the waypoints
     * @return observable list of the waypoints
     */
    public ObservableList<Waypoint> getWaypointsProperty() {
        return waypointsProperty;
    }

    /**
     * Index of the segment of the route at a given position
     *
     * @param position position on the route
     * @return index of the segment of the route at a given position
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = getRoute().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypointsProperty.get(i).nodeId();
            int n2 = waypointsProperty.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    private ElevationProfile computeElevation(){
        return (waypointsProperty == null || waypointsProperty.size() < 2 || routeProperty.get() == null) ?
                null :
                ElevationProfileComputer.elevationProfile(routeProperty.get(), MAX_STEP_LENGTH);
    }

    private Route computeRoute(){
        List<Route> multiRoute = new ArrayList<>();

        if (waypointsProperty == null || waypointsProperty.size() < 2 ){
            return null;
        }

        for (int i = 0; i < waypointsProperty.size() - 1; i++) {
            Pair<Integer, Integer> nodePair = new Pair<>(waypointsProperty.get(i).nodeId(), waypointsProperty.get(i + 1).nodeId());

            if (Objects.equals(nodePair.getKey(), nodePair.getValue())){
                return null;
            }
            if (!cacheMemory.containsKey(nodePair)) {
                if (cacheMemory.size() >= MAX_CAPACITY) {

                    Iterator<Map.Entry<Pair<Integer, Integer>, Route>> it = cacheMemory.entrySet().iterator();
                    it.next();
                    it.remove();
                }

                cacheMemory.put(nodePair, routeComputer.bestRouteBetween(nodePair.getKey(), nodePair.getValue()));
            }
            if (cacheMemory.get(nodePair) == null){
                return null;
            }

            multiRoute.add(cacheMemory.get(nodePair));
        }

        return new MultiRoute(multiRoute);
    }
}
