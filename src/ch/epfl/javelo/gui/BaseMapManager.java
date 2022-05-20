package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;


import java.io.IOException;
//TODO regler problème de la mouse avec /26

/**
 * manages the display and interaction with the basemap
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> property;
    private final WaypointsManager waypointsManager;
    private boolean redrawNeeded;
    private final Canvas canvas;
    private final Pane pane;

    public final static int TILE_HEIGHT = 256;
    public final static int MIN_ZOOM = 8;
    public final static int MAX_ZOOM = 19;

    /**
     * @param tileManager      the tile manager to use to get the tiles from the map
     * @param waypointsManager waypoint manager
     * @param property         a JavaFX property containing the parameters of the map displayed
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {
        this.tileManager = tileManager;
        this.property = property;
        this.waypointsManager = waypointsManager;
        canvas = new Canvas();
        pane = new Pane(canvas);

        redrawNeeded = false;

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();
        canvas.heightProperty().addListener(o -> redrawOnNextPulse());
        canvas.widthProperty().addListener(o -> redrawOnNextPulse());
        handler();
    }

    /**
     * @return returns the current pane
     */
    public Pane pane() {
        return pane;
    }

    private void paneDraw() {

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double x = property.get().xTopLeft();
        double y = property.get().yTopLeft();

        for (int i = 0; i < canvas.getWidth() + TILE_HEIGHT; i += TILE_HEIGHT) {
            for (int j = 0; j < canvas.getHeight() + TILE_HEIGHT; j += TILE_HEIGHT) {
                try {
                    TileManager.TileId tileID =
                            new TileManager.TileId(property.get().zoomLevel(), Math.floorDiv((int) (i + x), TILE_HEIGHT), Math.floorDiv((int) (j + y), 256));
                    gc.drawImage(tileManager.imageForTileAt(tileID), i - x % TILE_HEIGHT, j - y % TILE_HEIGHT);
                } catch (IOException ignored) {
                }
            }
        }

    }

    private void handler() {

        ObjectProperty<Point2D> property1 = new SimpleObjectProperty<>();
        pane.setOnMousePressed(e ->
                property1.set(new Point2D(e.getX(), e.getY())));

        pane.setOnMouseDragged(e -> {
                    Point2D pd = new Point2D(property1.get().getX(), property1.get().getY());
                    Point2D pd1 = pd.subtract(e.getX(), e.getY());

                    property.setValue(property.get().
                            withMinXY(property.get().xTopLeft() + pd1.getX(),
                                    property.get().yTopLeft() + pd1.getY()));

                    //     property.set(
                    //   new MapViewParameters(property.get().zoomLevel(),
                    // property.get().xTopLeft() + pd1.getX(),
                    //property.get().yTopLeft() + pd1.getY()));

                    property1.set(new Point2D(e.getX(), e.getY()));
                    redrawOnNextPulse();
                }
        );

        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {

            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(e.getDeltaY());

            int newZoom = Math2.clamp(MIN_ZOOM, property.get().zoomLevel() + zoomDelta, MAX_ZOOM);

            PointWebMercator newCoordinates = property.get().pointAt(e.getX(), e.getY());

            // PointWebMercator newCoordinates = PointWebMercator.of(property.get().zoomLevel(),
            //       property.get().xTopLeft() + e.getX(),
            //     property.get().yTopLeft() + e.getY());

            double newX = newCoordinates.xAtZoomLevel(newZoom);
            double newY = newCoordinates.yAtZoomLevel(newZoom);

            property.set(new MapViewParameters(newZoom,
                    newX - e.getX(),
                    newY - e.getY()));

            redrawOnNextPulse();

        });

        pane.setOnMouseReleased(e -> {
                    if (e.isStillSincePress()) {
                        waypointsManager.addWaypoint(e.getX(), e.getY());
                    }
                }
        );
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        paneDraw();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}