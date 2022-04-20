package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

public final class WaypointsManager {

    private final ObjectProperty<MapViewParameters> property;
    private final Consumer<String> stringConsumer;
    private final Graph graph;
    private final ObservableList<Waypoint> listOfWayPoint;
    private final Pane pane;

    WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property, ObservableList<Waypoint> listOfWayPoint, Consumer<String> stringConsumer) {
        this.graph = graph;
        this.property = property;
        this.listOfWayPoint = listOfWayPoint;
        this.stringConsumer = stringConsumer;
        pane = new Pane();

        for (int i = 0; i < listOfWayPoint.size(); i++) {
            Group wayPointGroup = new Group();
            wayPointGroup.getStyleClass().add("pin");
            SVGPath path1 = new SVGPath();
            path1.getStyleClass().add("pin_outside");
            path1.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            SVGPath path2 = new SVGPath();
            path2.getStyleClass().add("pin_inside");
            path2.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

            if (i == 0) {
                path1.getStyleClass().add("first");
            } else if (i == listOfWayPoint.size() - 1) {
                path1.getStyleClass().add("last");
            } else {
                path1.getStyleClass().add("middle");
            }

            wayPointGroup.setLayoutX(property.getValue().viewX((PointWebMercator.ofPointCh(
                    listOfWayPoint.get(i).crossingPosition()))));
            wayPointGroup.setLayoutY(property.getValue().viewY((PointWebMercator.ofPointCh(
                    listOfWayPoint.get(i).crossingPosition()))));

            wayPointGroup.getChildren().addAll(path1, path2);
            pane.getChildren().add(wayPointGroup);
        }

    }

    public Pane pane() {
        return pane;
    }

    public void addWaypoint(int x, int y) {

    }
}


