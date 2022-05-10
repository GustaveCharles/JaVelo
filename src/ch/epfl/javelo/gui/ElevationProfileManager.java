package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.Group;
import javafx.scene.text.Text;

public final class ElevationProfileManager {
    private final Pane pane;
    private final BorderPane borderPane;
    private final VBox vBox;
    private final Path path;
    private final Polygon polygone;
    private final Line line;
    private final Group group;
    private final Text textGroup_1, textGroup_2, textVbox;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;
    private final Affine transformation;
    private final Insets insets;
    private final ObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ObjectProperty<Double> highlightedProperty;
    private final ObjectProperty<Rectangle2D> rectangle2DProperty;

    public ElevationProfileManager(ObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ObjectProperty<Double> highlightedProperty) {
        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();
        this.elevationProfileProperty = new SimpleObjectProperty<ElevationProfile>();
        this.highlightedProperty = new SimpleObjectProperty<>();
//
        transformation = new Affine();

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

        this.group = new Group(textGroup_1, textGroup_2);

        this.textVbox = new Text();
        this.vBox = new VBox(textVbox);

        vBox.setId("profile_data");
        pane = new Pane(path, polygone, line, group);

        borderPane = new BorderPane(pane, null, null, vBox, null);
        borderPane.getStylesheets().setAll("elevation_profile.css");

        insets = new Insets(10, 10, 20, 40);

        Rectangle2D r = new Rectangle2D(insets.getLeft(), insets.getTop(),
                pane.getWidth() - insets.getLeft() - insets.getRight(),
                pane.getHeight() - insets.getBottom() - insets.getTop());

        this.rectangle2DProperty = new SimpleObjectProperty<>(r);

        Bindings.createObjectBinding(rectangle2DProperty, );
        line.setLayoutX(Bindings.createDoubleBinding(highlightedProperty, ));
    }

    public Pane pane() {
        return borderPane;
    }

    public ReadOnlyObjectProperty<Integer> mousePositionOnProfileProperty() {
    }

    private void createTransformation() {
        transformation.prependTranslation(-rectangle2DProperty.get().getMinX(), -rectangle2DProperty.get().getMinY());
        transformation.prependScale(elevationProfileProperty.get().length() / rectangle2DProperty.get().getWidth(),
                -(elevationProfileProperty.get().maxElevation() - elevationProfileProperty.get().minElevation()) / rectangle2DProperty.get().getHeight());
        transformation.prependTranslation(0, elevationProfileProperty.get().maxElevation());
        screenToWorld.setValue(transformation);

        try {
            worldToScreen.setValue(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    private void createGrid() {
        int[] POS_STEPS =
                {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
        int[] ELE_STEPS =
                {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};

        Path p = new Path();
        p.getElements().addAll(new MoveTo(rectangle2DProperty.get().getMinX(), rectangle2DProperty.get().getMinY()),
                new LineTo(rectangle2DProperty.get().getMaxX(), rectangle2DProperty.get().getMaxY()));
        //worldToScreen.get().deltaTransform(rectangle2DProperty.get().getMinX(), rectangle2DProperty.get().getMaxX())
    }


    private void createPolygon() {
        rectangle2DProperty.get().
    }

    public void grid() {

    }
}
