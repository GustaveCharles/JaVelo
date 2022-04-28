package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
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
        pane.setPickOnBounds(false);
        updatePane();
        listOfWayPoint.addListener((Observable o) -> updatePane());
        property.addListener((Observable o) -> updatePane());
    }

    public Pane pane() {
        return pane;
    }

    public void addWaypoint(double x, double y) {
        PointCh point = property.getValue().pointAt(x, y).toPointCh();
        int closestPointId = graph.nodeClosestTo(point, 1000);
        if (closestPointId != -1) {
            listOfWayPoint.add(new Waypoint(point, closestPointId));
        } else {
            stringConsumer.accept("No road nearby!");
        }
    }

    private void updatePane() {
        pane.getChildren().clear();
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

            wayPointGroup.setLayoutX(property.getValue().viewX((PointWebMercator.ofPointCh(
                    listOfWayPoint.get(i).crossingPosition()))));
            wayPointGroup.setLayoutY(property.getValue().viewY((PointWebMercator.ofPointCh(
                    listOfWayPoint.get(i).crossingPosition()))));

            ObjectProperty<Point2D> initialPoint = new SimpleObjectProperty<>();
            ObjectProperty<Point2D> initialCoordinates = new SimpleObjectProperty<>();

            wayPointGroup.setOnMousePressed(e -> {
                initialPoint.setValue(new Point2D(e.getX(), e.getY()));
                initialCoordinates.setValue(new Point2D(pane.getScene().getX(), pane.getScene().getY()));
            });

            int finalI = i;
            wayPointGroup.setOnMouseReleased(e1 -> {
                if (e1.isStillSincePress()) {
                    listOfWayPoint.remove(finalI);
                } else {
                    //if la position est bonne addWayPoint
                    //c quoi la diff entre relocate et addWaypoint
                    addWaypoint(e1.getX(), e1.getY());
                }
            });

            wayPointGroup.setOnMouseDragged(e1 -> {
                wayPointGroup.setLayoutX(e1.getSceneX() - initialPoint.getValue().getX());
                wayPointGroup.setLayoutY(e1.getSceneY() - initialPoint.getValue().getY());
            });

            pane.getChildren().add(wayPointGroup);
        }
    }
}


