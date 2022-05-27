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

/**
 * manages the display and interaction with the basemap
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public final class BaseMapManager {

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mvpProperty;
    private final WaypointsManager waypointsManager;
    private boolean redrawNeeded;
    private final Canvas canvas;
    private final Pane pane;
    private final GraphicsContext gc;

    /**
     * height of a tile
     */
    private final static int TILE_HEIGHT = 256;
    /**
     * minimal zoom of JaVelo
     */
    private final static int MIN_ZOOM = 8;
    /**
     * maximal zoom of JaVelo
     */
    private final static int MAX_ZOOM = 19;

    /**
     * @param tileManager      the tile manager to use to get the tiles from the map
     * @param waypointsManager waypoint manager
     * @param property         a JavaFX property containing the parameters of the map displayed
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> property) {

        this.tileManager = tileManager;
        this.mvpProperty = property;
        this.waypointsManager = waypointsManager;
        this.canvas = new Canvas();
        this.pane = new Pane(canvas);

        canvas.sceneProperty().addListener((p, o, n) -> {
            assert o == null;
            n.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();
        canvas.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        handler();

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        this.gc = canvas.getGraphicsContext2D();
    }

    /**
     * @return returns the current pane
     */
    public Pane pane() {
        return pane;
    }

    private void paneDraw() {

        double x = mvpProperty.get().xTopLeft();
        double y = mvpProperty.get().yTopLeft();

        for (int i = 0; i < canvas.getWidth() + TILE_HEIGHT; i += TILE_HEIGHT) {
            for (int j = 0; j < canvas.getHeight() + TILE_HEIGHT; j += TILE_HEIGHT) {
                try {
                    TileManager.TileId tileID =
                            new TileManager.TileId(mvpProperty.get().zoomLevel(),
                                    Math.floorDiv((int) (i + x), TILE_HEIGHT),
                                    Math.floorDiv((int) (j + y), TILE_HEIGHT));
                    gc.drawImage(tileManager.imageForTileAt(tileID),
                            i - x % TILE_HEIGHT,
                            j - y % TILE_HEIGHT);
                } catch (IOException ignored) {
                }
            }
        }

    }

    private void handler() {

        ObjectProperty<Point2D> oldMousePositionProperty = new SimpleObjectProperty<>();

        pane.setOnMousePressed(e ->
                oldMousePositionProperty.set(new Point2D(e.getX(), e.getY()))
        );

        pane.setOnMouseDragged(e -> {
            Point2D scrolledDistance = oldMousePositionProperty.get().subtract(e.getX(), e.getY());

            Point2D newMousePosition = mvpProperty
                    .get().topLeft().add(scrolledDistance);

            mvpProperty
                    .set(mvpProperty
                            .get().withMinXY(newMousePosition.getX(), newMousePosition.getY()));

            oldMousePositionProperty.set(new Point2D(e.getX(), e.getY()));
            redrawOnNextPulse();
        });

        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {

            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(e.getDeltaY());

            int newZoom = Math2.clamp(MIN_ZOOM,
                    mvpProperty.get().zoomLevel() + zoomDelta,
                    MAX_ZOOM);

            PointWebMercator newCoordinates
                    = mvpProperty.get().pointAt(e.getX(), e.getY());

            double newX = newCoordinates.xAtZoomLevel(newZoom);
            double newY = newCoordinates.yAtZoomLevel(newZoom);

            mvpProperty.set(new MapViewParameters(newZoom,
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