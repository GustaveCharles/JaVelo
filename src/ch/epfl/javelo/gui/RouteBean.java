package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

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

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        route.addListener((Observable o) ->);

    }

    ElevationProfile getElevationProfile() {
        return ElevationProfileComputer.elevationProfile(route(), 5);
    }

    public ReadOnlyObjectProperty<Route> getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route.set(route);
    }

    private Route route() {
        return route.getValue();
    }

    //highlighted
    //routeProperty

    private Route createSingleRoute() {

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


