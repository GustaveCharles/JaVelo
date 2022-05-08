package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public final class ElevationProfileManager {
    private final Pane pane;
    private final BorderPane borderPane;
    private final VBox vBox;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedProperty ){



        vBox = new VBox();
        pane = new Pane();

        borderPane = new BorderPane();
    }

    public Pane pane() {
        return pane;
    }

    public ReadOnlyObjectProperty<Integer> mousePositionOnProfileProperty(){

    }
}
