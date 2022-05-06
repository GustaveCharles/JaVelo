package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

import static java.lang.Double.NaN;

public final class RouteBean {

    private final RouteComputer routeComputer;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<Route> route;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final static int MAX_LENGTH = 5;
    private final static int MAX_CAPACITY = 80;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> map;

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

    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    private void createRoute() {
        if (waypoints.size() >= 2) {
            List<Route> listRoute = new ArrayList<>();
            for (int i = 1; i < waypoints.size(); i++) {
                int point1 = waypoints.get(i - 1).closestJaVeloNode();
                int point2 = waypoints.get(i).closestJaVeloNode();
                if (!map.containsKey(new Pair<>(point1,point2))){
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
            MultiRoute m = new MultiRoute(listRoute);
            setRouteAndElevation(m, ElevationProfileComputer.elevationProfile(m, MAX_LENGTH));
        } else {
            setRouteAndElevation(null, null);
        }
    }

    private void setRouteAndElevation(MultiRoute m, ElevationProfile ele) {
        route.setValue(m);
        elevationProfile.setValue(ele);
    }

    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

    public void setHighlightedPosition(double newHighlightedPosition) {
        highlightedPosition.setValue((newHighlightedPosition <= route.get().length()) && (newHighlightedPosition >= 0) ?
                newHighlightedPosition : NaN);
    }

    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    public double highlightedPosition() {
        return highlightedPosition.getValue();
    }

    public void addWaypoints(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public ObservableList<Waypoint> waypointsProperty() {
        return waypoints;
    }
}


