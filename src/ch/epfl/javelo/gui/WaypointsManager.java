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

/**
 * Manages display and interaction with waypoints
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */
public final class WaypointsManager {

    private final ObjectProperty<MapViewParameters> mapParameters;
    private final Consumer<String> stringConsumer;
    private final Graph graph;
    private final ObservableList<Waypoint> listOfWayPoint;
    /**
     * The search radius for nodeClosestTo method
     */
    private final static int RADIUS = 500;
    /**
     * The error message that will be display on the screen when there is no road nearby
     */
    private final static String ERROR_MESSAGE = "Aucune route à proximité !";
    /**
     * Identity name for the inner circle
     */
    private final static String PATH1_STYLE = "pin_outside";
    /**
     * SVG Path for the inner circle
     */
    private final static String PATH2_CONTENT = "M0-23A1 1 0 000-29 1 1 0 000-23";
    /**
     * Identity name for the marker outline
     */
    private final static String PATH2_STYLE = "pin_inside";
    /**
     * SVG Path for the marker outline
     */
    private final static String PATH1_CONTENT = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    /**
     * Identity name for the fist waypoint of the list
     */
    private final static String FIRST_WAYPOINT_ID = "first";
    /**
     * Identity name for the last waypoint of the list
     */
    private final static String LAST_WAYPOINT_ID = "last";
    /**
     * Identity name for the intermediate waypoint of the list
     */
    private final static String INTERMEDIATE_WAYPOINT_ID = "middle";
    /**
     * Identity name for the group of all waypoints
     */
    private final static String GROUP_ID = "pin";
    private final Pane pane;

    /**
     * Finds the closest nodes for the given waypoints and displays it on the map
     *
     * @param graph          the graph of nodes
     * @param mapParameters  parameters for the map view presented in the graphic interface
     * @param listOfWayPoint a list of waypoints
     * @param stringConsumer an error message
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapParameters,
                            ObservableList<Waypoint> listOfWayPoint, Consumer<String> stringConsumer) {
        this.graph = graph;
        this.mapParameters = mapParameters;
        this.listOfWayPoint = listOfWayPoint;
        this.stringConsumer = stringConsumer;
        pane = new Pane();
        pane.setPickOnBounds(false);
        updatePane();
        listOfWayPoint.addListener((Observable o) -> updatePane());
        mapParameters.addListener((p, o, n) -> updatePane());
    }

    /**
     * Getter for the pane
     *
     * @return the current pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * If there is a node at the (x,y) position then adds the given waypoint to the list
     *
     * @param x x-position on the pane
     * @param y y-position on the pane
     */
    public void addWaypoint(double x, double y) {
        if (isWaypointClosest(x, y) != null) {
            listOfWayPoint.add(isWaypointClosest(x, y));
        } else {
            stringConsumer.accept(ERROR_MESSAGE);
        }
    }

    /**
     * Returns a waypoints for the (x,y) position on the pane, if there is no node then returns null
     *
     * @param x x-position on the pane
     * @param y y-position on the pane
     * @return the given waypoint
     */
    private Waypoint isWaypointClosest(double x, double y) {
        PointCh point = mapParameters.get().pointAt(x, y).toPointCh();
        if (point == null) {
            return null;
        } else {
            int closestPointId = graph.nodeClosestTo(point, RADIUS);
            if (closestPointId != -1) {
                return new Waypoint(point, closestPointId);
            } else {
                return null;
            }
        }
    }

    /**
     * Creates SVG paths for waypoints, the first waypoint is in green, intermediate waypoints are in blue and the
     * last one is in red.
     *
     * @param i the index position in the list of waypoints
     * @return a group of svg paths
     */
    private Group createSVGPath(int i) {
        SVGPath path1 = new SVGPath();
        path1.getStyleClass().add(PATH1_STYLE);
        path1.setContent(PATH1_CONTENT);

        SVGPath path2 = new SVGPath();
        path2.getStyleClass().add(PATH2_STYLE);
        path2.setContent(PATH2_CONTENT);

        Group wayPointGroup = new Group(path1, path2);
        wayPointGroup.getStyleClass().add(GROUP_ID);

        if (i == 0) {
            wayPointGroup.getStyleClass().add(FIRST_WAYPOINT_ID);
        } else if (i == listOfWayPoint.size() - 1) {
            wayPointGroup.getStyleClass().add(LAST_WAYPOINT_ID);
        } else {
            wayPointGroup.getStyleClass().add(INTERMEDIATE_WAYPOINT_ID);
        }
        positionGroup(listOfWayPoint.get(i), wayPointGroup);
        pane.getChildren().add(wayPointGroup);
        return wayPointGroup;
    }

    /**
     * A handler that manages the removal of waypoints and their position across the map
     *
     * @param i             the index position in the list of waypoints
     * @param wayPointGroup a group for a waypoint
     */
    private void handler(int i, Group wayPointGroup) {
        ObjectProperty<Point2D> initialPoint = new SimpleObjectProperty<>();

        wayPointGroup.setOnMousePressed(e -> initialPoint.set(new Point2D(e.getX(), e.getY())));

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
                    stringConsumer.accept(ERROR_MESSAGE);
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

    /**
     * Updates the current pane based on changes over the list of waypoints
     */
    private void updatePane() {
        pane.getChildren().clear();
        for (int i = 0; i < listOfWayPoint.size(); i++) {
            Group wayPointGroup = createSVGPath(i);
            handler(i, wayPointGroup);
        }
    }

    /**
     * Positions a waypoint over the map
     *
     * @param w             a waypoint
     * @param wayPointGroup a group for a waypoint
     */
    private void positionGroup(Waypoint w, Group wayPointGroup) {
        wayPointGroup.setLayoutX(mapParameters.get().viewX((PointWebMercator.ofPointCh(w.crossingPosition()))));
        wayPointGroup.setLayoutY(mapParameters.get().viewY((PointWebMercator.ofPointCh(w.crossingPosition()))));
    }
}

