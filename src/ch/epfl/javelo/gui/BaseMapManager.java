package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;

/**
 * manages the display and interaction with the basemap
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public final class BaseMapManager {

    private TileManager tileManager;
    private ObjectProperty<TileManager.TileId> property = new SimpleObjectProperty<>();
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane pane;

    public BaseMapManager(TileManager tileManager,/*gestionnaire des points de passage de WaysPointManager*/, SimpleObjectProperty<TileManager.TileId> property) {
        this.tileManager = tileManager;
        this.property = property;

        redrawNeeded = false;
         canvas = new Canvas();
         pane = new Pane();

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    public Pane paneDraw() throws IOException {

        int canvasX = canvas.widthProperty().bind(pane.widthProperty());
        int canvasY = canvas.widthProperty().bind(pane.heightProperty());

        //pane.getChildren().add(canvas);

        GraphicsContext gc = getGraphicsContext2D();
        gc.drawImage(tileManager.imageForTileAt(property.get()),canvasX,canvasY);

        return new Pane(gc.getCanvas());
    }

    private void draw(){


    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // … à faire : dessin de la carte
        draw();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
