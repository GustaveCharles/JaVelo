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

    private final ObjectProperty<MapViewParameters> mapParameters;
    private final Consumer<String> stringConsumer;
    private final Graph graph;
    private final ObservableList<Waypoint> listOfWayPoint;
    private final static int RADIUS = 500;
    private final Pane pane;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapParameters, ObservableList<Waypoint> listOfWayPoint, Consumer<String> stringConsumer) {
        this.graph = graph;
        this.mapParameters = mapParameters;
        this.listOfWayPoint = listOfWayPoint;
        this.stringConsumer = stringConsumer;
        pane = new Pane();
        pane.setPickOnBounds(false);
        updatePane();
        listOfWayPoint.addListener((Observable o) -> updatePane());
        mapParameters.addListener((Observable o) -> updatePane());
    }

    public Pane pane() {
        return pane;
    }

    public void addWaypoint(double x, double y) {
        if (isWaypointClosest(x, y) != null) {
            listOfWayPoint.add(isWaypointClosest(x, y));
        }
    }

    private Waypoint isWaypointClosest(double x, double y) {
        PointCh point = mapParameters.getValue().pointAt(x, y).toPointCh();
        int closestPointId = graph.nodeClosestTo(point, RADIUS);
        if (closestPointId != -1) {
            return new Waypoint(point, closestPointId);
        } else {
            stringConsumer.accept("No road nearby!");
        }
        return null;
    }


    private Group createSVGPath(int i) {
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
        positionGroup(listOfWayPoint.get(i), wayPointGroup);
        pane.getChildren().add(wayPointGroup);
        return wayPointGroup;
    }

    private void handler(int i, Group wayPointGroup) {
        ObjectProperty<Point2D> initialPoint = new SimpleObjectProperty<>();

        wayPointGroup.setOnMousePressed(e -> initialPoint.setValue(new Point2D(e.getX(), e.getY())));

        wayPointGroup.setOnMouseReleased(e1 -> {
            if (e1.isStillSincePress()) {
                listOfWayPoint.remove(i);
            } else {
                Point2D point = new Point2D(wayPointGroup.getLayoutX(), wayPointGroup.getLayoutY())
                        .add(e1.getX(), e1.getY()).subtract(initialPoint.get());
                Waypoint w = isWaypointClosest(point.getX(), point.getY());
                if (w != null) {
                    positionGroup(w, wayPointGroup);
                    listOfWayPoint.set(i, w);
                } else {
                    wayPointGroup.setLayoutX(initialPoint.get().getX());
                    wayPointGroup.setLayoutY(initialPoint.get().getY());
                    updatePane();
                }
            }
        });

        wayPointGroup.setOnMouseDragged(e1 -> {
            Point2D newPoint = new Point2D(wayPointGroup.getLayoutX(), wayPointGroup.getLayoutY())
                    .add(e1.getX(), e1.getY()).subtract(initialPoint.get());
            wayPointGroup.setLayoutX(newPoint.getX());
            wayPointGroup.setLayoutY(newPoint.getY());
        });
    }

    private void updatePane() {
        pane.getChildren().clear();
        for (int i = 0; i < listOfWayPoint.size(); i++) {
            Group wayPointGroup = createSVGPath(i);
            handler(i, wayPointGroup);
        }
    }

    private void positionGroup(Waypoint w, Group wayPointGroup) {
        wayPointGroup.setLayoutX(mapParameters.get().viewX((PointWebMercator.ofPointCh(w.crossingPosition()))));
        wayPointGroup.setLayoutY(mapParameters.get().viewY((PointWebMercator.ofPointCh(w.crossingPosition()))));
    }
}
