package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;
/**
 * manages the display of the “annotated” map,
 * i.e. the base map above which the route and waypoints are superimposed
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class AnnotatedMapManager {

    private final RouteBean routeBean;
    private final StackPane mainPane;
    private final SimpleObjectProperty<Point2D> point2DProperty;
    private final DoubleProperty mouseProperty;
    private final SimpleObjectProperty<MapViewParameters> mapViewParametersP;

    public final static int MAX_DISTANCE_PIXELS = 15;
    public final static MapViewParameters MAP_VIEW_PARAMETERS =
            new MapViewParameters(12, 543200, 370650);

    /**
     * creates a basemap manager (BaseMapManager),
     * a waypoints manager (WaypointsManager) and a route manager (RouteManager)
     *
     * @param graph the road network graph
     * @param tileManager the OpenStreetMap tile manager
     * @param routeBean the route bean
     * @param stringConsumer an “error consumer” to report an error
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> stringConsumer) {

        this.routeBean = routeBean;
        this.mouseProperty = new SimpleDoubleProperty(Double.NaN);
        this.point2DProperty = new SimpleObjectProperty<>(Point2D.ZERO);
        this.mapViewParametersP = new SimpleObjectProperty<>(MAP_VIEW_PARAMETERS);

        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersP);
        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParametersP, routeBean.waypointsProperty(), stringConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);

        this.mainPane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        mainPane.getStylesheets().add("map.css");

        handler();
    }

    /**
     *
     * @return returns the the panel containing the annotated map
     */
    public Pane pane() {
        return mainPane;
    }

    /**
     *
     * @return returning the property containing the position of
     * the mouse pointer along the route
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mouseProperty;
    }

    /**
     * manages the property containing the position of the mouse pointer along the route
     */
    private void handler() {
        mouseProperty.bind(Bindings.createDoubleBinding(
                () -> {
                    if (routeBean.route() != null && point2DProperty.get() != null) {
                        Point2D point2DMouse = point2DProperty.get();
                        PointWebMercator pointWebMercator = mapViewParametersP.get().pointAt(point2DMouse.getX(), point2DMouse.getY());
                        PointCh pointCh = pointWebMercator.toPointCh();
                        if (pointCh == null) {
                            return Double.NaN;
                        }

                        RoutePoint point = routeBean.route().pointClosestTo(pointCh);

                        double x = mapViewParametersP.get().viewX(PointWebMercator.ofPointCh(point.point()));
                        double y = mapViewParametersP.get().viewY(PointWebMercator.ofPointCh(point.point()));
                        Point2D point2D1 = new Point2D(x, y);

                        if (Math2.norm(point2DMouse.getX() - point2D1.getX(),
                                point2DMouse.getY() - point2D1.getY()) <= MAX_DISTANCE_PIXELS) {
                            return point.position();
                        } else return Double.NaN;
                    } else return Double.NaN;
                },
                point2DProperty, routeBean.routeProperty(), mapViewParametersP
        ));


        mainPane.setOnMouseMoved(e -> point2DProperty.set(new Point2D(e.getX(), e.getY())));

        mainPane.setOnMouseExited(e -> point2DProperty.set(null));
    }
}
