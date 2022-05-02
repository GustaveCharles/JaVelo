//package ch.epfl.javelo.gui;
//
//import ch.epfl.javelo.Preconditions;
//import ch.epfl.javelo.routing.ElevationProfile;
//import ch.epfl.javelo.routing.Route;
//import ch.epfl.javelo.routing.RouteComputer;
//import javafx.beans.Observable;
//import javafx.beans.property.DoubleProperty;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.ReadOnlyObjectProperty;
//import javafx.beans.property.SimpleDoubleProperty;
//import javafx.collections.ObservableList;
//
//public final class RouteBean {
//
//    //Lorsque la liste des points de passage ne contient pas au moins 2 éléments,
//    // ou s'il existe au moins une paire de points de passage entre lesquels aucun itinéraire
//    // ne peut être trouvé, alors ni l'itinéraire ni son profil n'existent, et les propriétés
//    // correspondantes contiennent null.
//
//
//    private final RouteComputer routeComputer;
//
//    public RouteBean(RouteComputer routeComputer) {
//        this.routeComputer = routeComputer;
//
//    }
//
//    private ObservableList<Waypoint> waypoints() {
//        Preconditions.checkArgument(waypoints().size() >= 2);
//        waypoints().addListener((Observable o) -> elevationProfile(), route());
//    }
//
//    DoubleProperty highlightedPosition = new SimpleDoubleProperty();
//
//    private ReadOnlyObjectProperty<ElevationProfile> elevationProfile(){}
//
//    private ReadOnlyObjectProperty<Route> route() {
//    }
//
//    public DoubleProperty highlightedPositionProperty() {
//        return highlightedPositionProperty();
//    }
//
//    public double highlightedPosition(){
//        return highlightedPositionProperty().getValue();
//    }
//
//    public void setHighlightedPosition(DoubleProperty newHighlightedPosition){
//        highlightedPositionProperty().setValue(newHighlightedPosition.getValue());
//    }
//
//    ObjectProperty<Route> r = (ObjectProperty<Route>) route();
//    ObjectProperty<ElevationProfile> elevation = (ObjectProperty<ElevationProfile>) elevationProfile();
//
//    private ReadOnlyObjectProperty<Route> getRouteProperty(){
//        return r;
//    }
//
//    private ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty(){
//        return elevation;
//    }
//
//}
//
//
