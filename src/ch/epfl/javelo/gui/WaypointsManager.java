package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
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
        pane.setPickOnBounds(false);
        updatePane();
        listOfWayPoint.addListener((Observable o) -> updatePane());
//      property.addListener((Observable o) -> );
    }

    public Pane pane() {
        return pane;
    }

    public void addWaypoint(int x, int y) {
        PointCh point = property.getValue().pointAt(x, y).toPointCh();
        int closestPointId = graph.nodeClosestTo(point, 1000);

        pane.setOnMouseDragged(e1 -> {
            //deplacement
        });
        pane.setOnMouseReleased(e2 -> {
            if (e2.isStillSincePress()) {
                if (closestPointId != -1) {
                    listOfWayPoint.add(new Waypoint(point, closestPointId));
                } else {
                    stringConsumer.accept("No road nearby!");
                }
            } else {
                //delete
                //
            }
        });
    }

    private void updatePane() {
        for (int i = 0; i < listOfWayPoint.size(); i++) {
            SVGPath path1 = new SVGPath();
            path1.getStyleClass().add("pin_outside");
            path1.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            SVGPath path2 = new SVGPath();
            path2.getStyleClass().add("pin_inside");
            path2.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

            Group wayPointGroup = new Group(path1, path2);
            wayPointGroup.getStyleClass().add("pin");

            if (i == 0) {
                wayPointGroup.getStyleClass().add("first");
            } else if (i == listOfWayPoint.size() - 1) {
                wayPointGroup.getStyleClass().add("last");
            } else {
                wayPointGroup.getStyleClass().add("middle");
            }

            relocate(wayPointGroup, i);
            pane.getChildren().add(wayPointGroup);
            setEvent(wayPointGroup, i);
        }
    }

    private void relocate(Group wayPointGroup, int index) {
        wayPointGroup.setLayoutX(property.getValue().viewX((PointWebMercator.ofPointCh(
                listOfWayPoint.get(index).crossingPosition()))));
        wayPointGroup.setLayoutY(property.getValue().viewY((PointWebMercator.ofPointCh(
                listOfWayPoint.get(index).crossingPosition()))));
    }

    private void setEvent(Group wayPointGroup, int index) {
        wayPointGroup.setOnMouseClicked(e -> {
            listOfWayPoint.remove(index);
        });

        wayPointGroup.setOnMouseDragged(e1 -> {
            relocate(wayPointGroup, index);
        });
    }
}


