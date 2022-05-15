package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import javafx.beans.property.DoubleProperty;
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

    //le throws exception est bien??
    //MapViewParameters c'est bien??
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> stringConsumer) throws Exception {

        this.graph = Graph.loadFrom(Path.of("lausanne"));
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.stringConsumer = stringConsumer;

        SimpleObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(MAP_VIEW_PARAMETERS);

        this.routeManager = new RouteManager(routeBean, mapViewParametersP);
        this.waypointsManager = new WaypointsManager(graph, mapViewParametersP, routeBean.waypointsProperty(), stringConsumer);
        this.baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);


        this.mainPane =
                new StackPane(baseMapManager.pane(),
                        routeManager.pane(), waypointsManager.pane());
        mainPane.getStylesheets().add("map.css");


        mapViewParametersP.addListener(e -> {
            //           mouseProperty.set();
        });

        routeBean.routeProperty().addListener(e -> {
            //   mouseProperty.set();
        });

        mainPane.setOnMouseMoved(e -> {
            if (mainPane.contains(point2DProperty.get())) {
                mouseProperty.set(e.getX());
                point2DProperty.set(new Point2D(e.getX(), e.getY()));
            } else {
                mouseProperty.set(Double.NaN);
            }
        });

        mainPane.setOnMouseExited(e -> {
            if (!mainPane.contains(point2DProperty.get())) {
                mouseProperty.set(Double.NaN);
            }
        });
    }

    public Pane pane() {
        return mainPane;
    }

    //retourne en lecture seule ou pas?
    public DoubleProperty mousePositionOnRouteProperty() {
        return mouseProperty;
    }
}
