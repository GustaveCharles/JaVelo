package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * manages the display of the route and part of the interaction with it
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class RouteManager {

    private final Pane pane;
    private final Polyline line;
    private final Circle circle;
    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapParameters;
    private List<PointCh> routeNodes;
    private final List<Double> routeNodesDouble;
    //TODO public ou private la constante?
    //TODO problème lorsque la distance entre les points verts et rouge sont plus petits que highlited property

    /**
     * @param routeBean     the route bean
     * @param mapParameters a JavaFX property, read-only, containing the parameters of the map displayed,
     */
    public RouteManager(RouteBean routeBean, SimpleObjectProperty<MapViewParameters> mapParameters) {
        this.line = new Polyline();
        this.routeBean = routeBean;
        this.circle = new Circle(5);
        this.mapParameters = mapParameters;

        this.routeNodes = new ArrayList<>();
        this.routeNodesDouble = new ArrayList<>();

        pane = new Pane(line, circle);
        pane.setPickOnBounds(false);


        line.setId("route");
        circle.setId("highlight");

        //si la propriété route de routeBean contient null

        line.setVisible(false);
        circle.setVisible(false);

        routeBean.highlightedPositionProperty().addListener(e -> {
            setCircle();
            visibleProperty();
                }
        );

        routeBean.routeProperty().addListener(e ->
                visibleProperty());

        routeBean.routeProperty().addListener((e, oV, nV) ->
                rebuildWhenRoute(oV, nV));

        mapParameters.addListener((e, oV, nV) ->
                rebuildWhenZoom(oV, nV));

        handler();

    }

    /**
     * //rebuild line when route changes and
     * rebuild disk when map settings change (xtopleft ytopleft)
     *
     * @param oV old route
     * @param nV new route
     */
    private void rebuildWhenRoute(Route oV, Route nV) {
        if (oV != nV && nV != null) {
            pointsSequence();
            line.setLayoutX(-mapParameters.get().xTopLeft());
            line.setLayoutY(-mapParameters.get().yTopLeft());

            PointWebMercator point = PointWebMercator.
                    ofPointCh(routeBean.routeProperty().get().pointAt(routeBean.highlightedPosition()));

            circle.setCenterX(mapParameters.get().viewX(point));
            circle.setCenterY(mapParameters.get().viewY(point));
        }
    }

    /**
     * reposition line when card dragged and rebuild line when zooming again
     * and rebuild disk when map settings change (zoom)
     *
     * @param oV old route
     * @param nV new route
     */
    private void rebuildWhenZoom(MapViewParameters oV, MapViewParameters nV) {
        if (oV.zoomLevel() != nV.zoomLevel() && routeBean.routeProperty().get() != null) {

            pointsSequence();
            line.setLayoutX(-nV.xTopLeft());
            line.setLayoutY(-nV.yTopLeft());

            PointWebMercator point = PointWebMercator.
                    ofPointCh(routeBean.routeProperty().get().pointAt(routeBean.highlightedPosition()));

            circle.setCenterX(point.xAtZoomLevel(nV.zoomLevel()) - nV.xTopLeft());
            circle.setCenterY(point.yAtZoomLevel(nV.zoomLevel()) - nV.yTopLeft());

        }
        if (oV.zoomLevel() == nV.zoomLevel()) {

            line.setLayoutX(line.getLayoutX() + oV.xTopLeft() - nV.xTopLeft());
            line.setLayoutY(line.getLayoutY() + oV.yTopLeft() - nV.yTopLeft());

            circle.setCenterX(circle.getCenterX() + oV.xTopLeft() - nV.xTopLeft());
            circle.setCenterY(circle.getCenterY() + oV.yTopLeft() - nV.yTopLeft());
        }
    }

    private void setCircle() {
        if(routeBean.routeProperty().get() != null && !Double.isNaN(routeBean.highlightedPosition())) {
            PointWebMercator point = PointWebMercator.
                    ofPointCh(routeBean.routeProperty().get()
                            .pointAt(routeBean.highlightedPosition()));

            circle.setCenterX(mapParameters.get().viewX(point));
            circle.setCenterY(mapParameters.get().viewY(point));
        }


    }

    /**
     * make the line invisible when route is null
     * make the line visible when the route is not null
     * make the disk invisible when route is null
     * make the disk visible when route is non-zero
     */
    private void visibleProperty() {
        if ((routeBean.routeProperty().get() == null)) {
            line.setVisible(false);
            circle.setVisible(false);

        } else if(routeBean.highlightedPositionProperty().get() == 0){
                circle.setVisible(false);
                line.setVisible(true);
            }

        else {
            line.setVisible(true);
            circle.setVisible(true);
        }
    }

    /**
     * @return returns the current pane
     */
    public Pane pane() {
        return pane;
    }

    private void handler() {

        circle.setOnMouseClicked(e -> {
                    Point2D point2D = circle.localToParent(e.getX(), e.getY());
            PointCh point = mapParameters.get().pointAt(point2D.getX(), point2D.getY()).toPointCh();
            int closestPointId = routeBean.routeProperty().get()
                    .nodeClosestTo(routeBean.highlightedPosition());

                    int index = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + 1;
                    Waypoint waypoint = new Waypoint(point, closestPointId);

                        routeBean.waypointsProperty().add(index, waypoint);

                }
        );
    }

    /**
     * adds all the points to the polyline
     */
    private void pointsSequence() {

        routeNodes.clear();
        routeNodesDouble.clear();
        line.getPoints().clear();

        routeNodes = new ArrayList<>(routeBean.routeProperty().get().points());
        routeNodes.forEach(o -> {
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(o);
            routeNodesDouble.add(pointWebMercator.xAtZoomLevel(mapParameters.get().zoomLevel()));
            routeNodesDouble.add(pointWebMercator.yAtZoomLevel(mapParameters.get().zoomLevel()));

        });

        line.getPoints().addAll(routeNodesDouble);

    }

}



