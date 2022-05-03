package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import javax.swing.*;
import java.util.*;

public final class RouteBean {

    private final RouteComputer routeComputer;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<Route> route;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final static int MAX_LENGTH = 5;
    private HashMap<Pair<Waypoint, Waypoint>, Route> map;

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        map = new LinkedHashMap<>();
        highlightedPosition = new SimpleDoubleProperty();
        route = new SimpleObjectProperty<>();
        waypoints = FXCollections.observableArrayList();
        elevationProfile = new SimpleObjectProperty<>();
        waypoints.addListener((Observable o) -> createRoute());
    }

    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    private void createRoute() {
        //map
        if (waypoints.size() >= 2) {
            List<Route> listRoute = new ArrayList<>();
            for (int i = 1; i < waypoints.size(); i++) {
                int point1 = waypoints.get(i - 1).closestJaVeloNode();
                int point2 = waypoints.get(i).closestJaVeloNode();
                Route r = routeComputer.bestRouteBetween(point1, point2);
                if (r != null) {
                    listRoute.add(r);
                } else {
                    route.setValue(null);
                    elevationProfile.setValue(null);
                }
            }
            MultiRoute m = new MultiRoute(listRoute);
            route.setValue(m);
            elevationProfile.setValue(ElevationProfileComputer.elevationProfile(m, MAX_LENGTH));
        } else {
            route.setValue(null);
            elevationProfile.setValue(null);
        }
    }

    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

    public void setHighlightedPosition(double newHighlightedPosition) {
        highlightedPosition.setValue(newHighlightedPosition);
    }

    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    public double highlightedPosition() {
        return highlightedPosition.getValue();
    }


    public void addWaypoints(Waypoint waypoint) {
//        if (!map.containsKey(waypoint) && !waypoints.contains(waypoint)){
//            map.put()
//            waypoints.add(waypoint);
//        }
    }

    public ObservableList<Waypoint> waypointsProperty() {
        return waypoints;
    }
}


