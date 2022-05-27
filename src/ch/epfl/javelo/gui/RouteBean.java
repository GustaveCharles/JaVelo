package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * Represents a JavaFX bean for a route
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class RouteBean {

    private final RouteComputer routeComputer;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<Route> route;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> map;
    public final static int MAX_LENGTH = 5;
    public final static int MAX_CAPACITY = 80;
    public final static int MIN_GROUP_SIZE = 2;

    /**
     * Builds a JavaFX bean grouping properties related to waypoints and the corresponding route
     *
     * @param routeComputer a route calculator, of RouteComputer type,
     *                     used to determine the best route connecting two waypoints.
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        map = new LinkedHashMap<>(MAX_CAPACITY);
        highlightedPosition = new SimpleDoubleProperty();
        route = new SimpleObjectProperty<>();
        waypoints = FXCollections.observableArrayList();
        elevationProfile = new SimpleObjectProperty<>();
        waypoints.addListener((Observable o) -> createRoute());
        createRoute();
    }

    /**
     * A getter for the route
     *
     * @return a read-only property which contains the route
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Creates a route connecting waypoints, returns null if there is no
     * road between two waypoints or there are less than 2 waypoints
     */
    private void createRoute() {
        if (waypoints.size() >= MIN_GROUP_SIZE) {
            List<Route> listRoute = new ArrayList<>();
            for (int i = 1; i < waypoints.size(); i++) {
                int point1 = waypoints.get(i - 1).closestJaVeloNode();
                int point2 = waypoints.get(i).closestJaVeloNode();
                if (!(point1 == point2)) {
                    if (!map.containsKey(new Pair<>(point1, point2))) {
                        Route r = routeComputer.bestRouteBetween(point1, point2);
                        if (r == null) {
                            setRouteAndElevation(null, null);
                            return;
                        }
                        map.put(new Pair<>(point1, point2), r);
                        listRoute.add(r);
                    } else {
                        listRoute.add(map.get(new Pair<>(point1, point2)));
                    }
                }
            }
            if (!listRoute.isEmpty()) {
                MultiRoute multiRoute = new MultiRoute(listRoute);
                setRouteAndElevation(multiRoute, ElevationProfileComputer.elevationProfile(multiRoute, MAX_LENGTH));
            } else {
                setRouteAndElevation(null, null);
            }
        } else {
            setRouteAndElevation(null, null);
        }
    }

    /**
     * Calculates in which segment/singleroute our position is in, ignoring empty segments
     *
     * @param position a position along the route
     * @return an index
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestJaVeloNode();
            int n2 = waypoints.get(i + 1).closestJaVeloNode();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * A setter for the route and the elevation profile
     *
     * @param multiRoute the route connecting every waypoint
     * @param ele        the elevation profile of the route
     */
    private void setRouteAndElevation(MultiRoute multiRoute, ElevationProfile ele) {
        route.set(multiRoute);
        elevationProfile.set(ele);
    }

    /**
     * A getter for the route
     *
     * @return a route
     */
    public Route route() {
        return route.get();
    }

    /**
     * A getter for the highlighted position
     *
     * @return a double property which contains the highlighted position
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * A getter for the highlighted position
     *
     * @return the highlighted position
     */
    public double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * A getter for the list of waypoints
     *
     * @return an observable list containing waypoints
     */
    public ObservableList<Waypoint> waypointsProperty() {
        return waypoints;
    }

    //todo commenter
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }
}


