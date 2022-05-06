package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;


import java.io.IOException;
//TODO constate pour 256
/**
 * manages the display and interaction with the basemap
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public final class BaseMapManager {

    private TileManager tileManager;
    private final ObjectProperty<MapViewParameters> property;
    private final WaypointsManager waypointsManager;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane pane;


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

    public Pane pane() {
        return pane;
    }

    private void paneDraw() {

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double x = property.get().xTopLeft();
        double y = property.get().yTopLeft();

        for (int i = 0; i < canvas.getWidth() + 256; i += 256) {
            for (int j = 0; j < canvas.getHeight() + 256; j += 256) {
                try {
                    TileManager.TileId tileID =
                            new TileManager.TileId(property.get().zoomLevel(), Math.floorDiv((int) (i + x), 256), Math.floorDiv((int) (j + y), 256));
                    gc.drawImage(tileManager.imageForTileAt(tileID), i - x % 256, j - y % 256);
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

        //TODO magic numbers nommer 8 et 18
        //TODO regler problème de la mouse avec /26
        pane.setOnScroll(e -> {
            int newZoom = Math2.clamp(8, property.get().zoomLevel() + (int) e.getDeltaY() / 26, 19);

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