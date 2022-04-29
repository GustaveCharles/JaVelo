package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public final class RouteBean {
    private final RouteComputer routeComputer;
    private ReadOnlyObjectProperty<ElevationProfile> elevationProfile

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
    }

    // J'ai un doute sur le fait
    private ObjectProperty<ObservableList<Waypoint>> waypoints() {
    }

    private ReadOnlyObjectProperty<Route> route() {
    }

    private ObjectProperty<DoubleProperty> highlightedPosition() {
    }


}
