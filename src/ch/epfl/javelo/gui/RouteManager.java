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
//todo bien de faire foreach? faire un stream?

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
    private final ObjectProperty<MapViewParameters> mvpProperty;
    private List<PointCh> routeNodes;
    private final List<Double> routeNodesDouble;

    private final static int CIRCLE_RADIUS = 5;

    /**
     * @param routeBean   the route bean
     * @param mvpProperty a JavaFX property, read-only, containing the parameters of the map displayed,
     */
    public RouteManager(RouteBean routeBean, SimpleObjectProperty<MapViewParameters> mvpProperty) {
        this.line = new Polyline();
        this.routeBean = routeBean;
        this.circle = new Circle(CIRCLE_RADIUS);
        this.mvpProperty = mvpProperty;

        this.routeNodes = new ArrayList<>();
        this.routeNodesDouble = new ArrayList<>();

        pane = new Pane(line, circle);
        pane.setPickOnBounds(false);

        line.setId("route");
        circle.setId("highlight");

        routeBean.highlightedPositionProperty().addListener((p, o, n) -> {
                    setCircle();
                    visibleProperty();
                }
        );

        routeBean.routeProperty().addListener((p, o, n) -> {
                    visibleProperty();
                    rebuildWhenRouteChanges(o, n);
                }
        );

        mvpProperty.addListener((p, o, n) -> rebuildWhenMvpChanges(o, n));

        handler();
    }

    /**
     * //rebuild line when route changes and
     * rebuild disk when map settings change (xtopleft ytopleft)
     *
     * @param oV old route
     * @param nV new route
     */
    private void rebuildWhenRouteChanges(Route oV, Route nV) {
        if (oV != nV && nV != null) {
            pointsSequence();
            line.setLayoutX(-mvpProperty.get().xTopLeft());
            line.setLayoutY(-mvpProperty.get().yTopLeft());

            if (Double.isNaN(routeBean.highlightedPosition())) {
                circle.setVisible(false);
            } else {
                setCircle();
            }
        }
    }

    /**
     * reposition line when card dragged and rebuild line when zooming again
     * and rebuild disk when map settings change (zoom)
     *
     * @param oV old route
     * @param nV new route
     */
    private void rebuildWhenMvpChanges(MapViewParameters oV, MapViewParameters nV) {
        if (oV.zoomLevel() != nV.zoomLevel() && routeBean.route() != null) {

            pointsSequence();
            line.setLayoutX(-nV.xTopLeft());
            line.setLayoutY(-nV.yTopLeft());

            if (Double.isNaN(routeBean.highlightedPosition())) {
                circle.setVisible(false);
            } else {
                setCircle();
            }
        }
        if (oV.zoomLevel() == nV.zoomLevel()) {

            line.setLayoutX(-nV.xTopLeft());
            line.setLayoutY(-nV.yTopLeft());

            circle.setVisible(false);
        }
    }

    private void setCircle() {
        if (routeBean.route() != null && !Double.isNaN(routeBean.highlightedPosition())) {
            PointWebMercator point = PointWebMercator.
                    ofPointCh(routeBean.route()
                            .pointAt(routeBean.highlightedPosition()));

            circle.setCenterX(mvpProperty.get().viewX(point));
            circle.setCenterY(mvpProperty.get().viewY(point));
        }


    }

    /**
     * make the line invisible when route is null
     * make the line visible when the route is not null
     * make the disk invisible when route is null
     * make the disk visible when route is non-zero
     */
    private void visibleProperty() {
        if ((routeBean.route() == null)) {
            line.setVisible(false);
            circle.setVisible(false);

        } else if (routeBean.highlightedPositionProperty().get() == 0 || Double.isNaN(routeBean.highlightedPosition())) {
            circle.setVisible(false);
        } else {
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
                    PointCh point = mvpProperty.get().pointAt(point2D.getX(), point2D.getY()).toPointCh();
                    int closestPointId = routeBean.route()
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
    //TODO stream
    private void pointsSequence() {

        routeNodes.clear();
        routeNodesDouble.clear();
        line.getPoints().clear();

        routeNodes = new ArrayList<>(routeBean.route().points());
        routeNodes.forEach(o -> {
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(o);
            routeNodesDouble.add(pointWebMercator.xAtZoomLevel(mvpProperty.get().zoomLevel()));
            routeNodesDouble.add(pointWebMercator.yAtZoomLevel(mvpProperty.get().zoomLevel()));

        });

        line.getPoints().addAll(routeNodesDouble);

    }

}



