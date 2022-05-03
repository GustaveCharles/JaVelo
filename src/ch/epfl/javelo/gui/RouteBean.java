package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RouteBean {

    //Lorsque la liste des points de passage ne contient pas au moins 2 éléments,
    // ou s'il existe au moins une paire de points de passage entre lesquels aucun itinéraire
    // ne peut être trouvé, alors ni l'itinéraire ni son profil n'existent, et les propriétés
    // correspondantes contiennent null.

    private final RouteComputer routeComputer;
    private final DoubleProperty highlightedPosition = new SimpleDoubleProperty();
    private ObjectProperty<Route> route = new SimpleObjectProperty<>();
    private ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();
    private ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty();
    private final static int MAX_LENGTH = 5;

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        waypoints.addListener((Observable o) -> createSingleRoute());
    }

    ElevationProfile getElevationProfile() {
        return ElevationProfileComputer.elevationProfile(route.getValue(), MAX_LENGTH);
    }

    public ReadOnlyObjectProperty<Route> getRoute() {
        return route;
    }

    //highlighted
    //routeProperty

    private Route createSingleRoute() {
        record nodePair(int nodeId1, int nodeId2) {
        }
        List<Route> listRoute = new ArrayList<>();
        for (int i = 1; i < waypoints.size(); i++) {
            int point1 = waypoints.get(i - 1).closestJaVeloNode();
            int point2 = waypoints.get(i).closestJaVeloNode();
            Route r = routeComputer.bestRouteBetween(point1, point2);
            listRoute.add(r);
        }
        return new MultiRoute(listRoute);
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

    public void addWayPoints(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    //Lorsque la liste des points de passage ne contient pas au moins 2 éléments,
    // ou s'il existe au moins une paire de points de passage entre lesquels aucun itinéraire
    // ne peut être trouvé, alors ni l'itinéraire ni son profil n'existent, et les propriétés
    // correspondantes contiennent null.

}


