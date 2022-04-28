package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
        //pane.getChildren().add(canvas);

        redrawNeeded = false;

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();
        //canvas.setOnMousePressed(e->redrawOnNextPulse());
        canvas.setOnMouseClicked(e -> waypointsManager.addWaypoint(e.getX(),e.getY()));
    }

    public Pane pane() {
        return pane;
    }

    private void paneDraw() {

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        GraphicsContext gc = canvas.getGraphicsContext2D();

        //canvas.heightProperty().addListener(o -> System.out.printf("New canvas height: %.2f\n", canvas.getHeight()));
        //canvas.widthProperty().addListener(o -> System.out.printf("New canvas width: %.2f\n", canvas.getWidth()));

        canvas.heightProperty().addListener(o -> redrawOnNextPulse());
        canvas.widthProperty().addListener(o -> redrawOnNextPulse());

        double x = property.get().xTopLeft();
        double y = property.get().yTopLeft();

        for (int i = 0; i < canvas.getWidth() + 256; i += 256) {
            for (int j = 0; j < canvas.getHeight() + 256; j += 256) {
                try {
                    TileManager.TileId tileID =
                            new TileManager.TileId(property.get().zoomLevel(), Math.floorDiv((int) (i + x), 256), Math.floorDiv((int) (j + y), 256));
                    gc.drawImage(tileManager.imageForTileAt(tileID), i - x % 256, j - y % 256);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

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
