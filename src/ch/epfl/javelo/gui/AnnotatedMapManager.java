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


import java.nio.file.Path;
import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final Graph graph;
    private final TileManager tileManager;
    private final RouteBean routeBean;
    private final Consumer<String> stringConsumer;
    private final StackPane mainPane;
    private BaseMapManager baseMapManager;
    private RouteManager routeManager;
    private WaypointsManager waypointsManager;
    private SimpleObjectProperty<Point2D> point2DProperty;
    private DoubleProperty mouseProperty;

    public final static MapViewParameters MAP_VIEW_PARAMETERS =
            new MapViewParameters(12, 543200, 370650);

    //MapViewParameters c'est bien??
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> stringConsumer) {

        this.graph = graph;
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.stringConsumer = stringConsumer;
        double mouse = Double.NaN;
        this.mouseProperty = new SimpleDoubleProperty(mouse);
        Point2D point2D = Point2D.ZERO;
        this.point2DProperty = new SimpleObjectProperty<>(point2D);
        SimpleObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(MAP_VIEW_PARAMETERS);

        this.routeManager = new RouteManager(routeBean, mapViewParametersP);
        this.waypointsManager = new WaypointsManager(graph, mapViewParametersP, routeBean.waypointsProperty(), stringConsumer);
        this.baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);


        this.mainPane =
                new StackPane(baseMapManager.pane(),
                        routeManager.pane(), waypointsManager.pane());
        mainPane.getStylesheets().add("map.css");


        mouseProperty.bind(Bindings.createDoubleBinding(
                () -> {
                    if(routeBean.routeProperty().get() != null && point2DProperty.get() != null){
                        Point2D point2DMouse = point2DProperty.get();
                        PointWebMercator pointWebMercator = mapViewParametersP.get().pointAt(point2DMouse.getX(),point2DMouse.getY());
                        PointCh pointCh =  pointWebMercator.toPointCh();
                        RoutePoint point = routeBean.routeProperty().get().pointClosestTo(pointCh);

                        double x = mapViewParametersP.get().viewX(PointWebMercator.ofPointCh(point.point()));
                        double y = mapViewParametersP.get().viewY(PointWebMercator.ofPointCh(point.point()));
                        Point2D point2D1 = new Point2D(x,y);
                        if(Math2.norm(point2D.getX()-point2D1.getX(),point2D.getY()-point2D1.getY()) <= 15){
                            return x;
                        }else return Double.NaN;
                    }else return Double.NaN;
                },
                 point2DProperty,routeBean.routeProperty(),mapViewParametersP
        ));


        mainPane.setOnMouseMoved(e -> point2DProperty.set(new Point2D(e.getX(), e.getY())));

        mainPane.setOnMouseExited(e -> point2DProperty.set(null));


    }

    public Pane pane() {
        return mainPane;
    }

    //retourne en lecture seule ou pas?
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mouseProperty;
    }
}
