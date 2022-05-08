package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

public final class ElevationProfileManager {
    private final Pane pane;
    private final BorderPane borderPane;
    private final VBox vBox;

    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ObjectProperty<Double> highlightedProperty) {
        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();
        this.elevationProfileProperty = new SimpleObjectProperty<ElevationProfile>();
        this.highlightedProperty = new SimpleObjectProperty<>();
//
        affine = new Affine();

        this.path = new Path();
        path.setId("grid");
        this.polygone = new Polygon();
        polygone.setId("profile");
        this.line = new Line();

        //y'en a peut etre plus
        this.textGroup_1 = new Text();
        this.textGroup_2 = new Text();
        textGroup_1.getStyleClass().setAll("grid_label", "horizontal");
        textGroup_2.getStyleClass().setAll("grid_label", "vertical");



        vBox = new VBox();
        pane = new Pane();

        borderPane = new BorderPane();
    }

    public Pane pane() {
        return pane;
    }

    public ReadOnlyObjectProperty<Integer> mousePositionOnProfileProperty() {
    }

    private ObjectProperty<Transform> screenToWorldProperty() {
        affine.appendTranslation();
    }

    private Transform worldToScreenProperty() {
        try {
            return screenToWorld.get().createInverse();
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createPolygon() {

    }

    public void grid() {

    }
}
