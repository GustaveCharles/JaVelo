package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedProperty;
    private final ObjectProperty<Rectangle2D> rectangle2DProperty;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedProperty) {
        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();
        this.elevationProfileProperty = new SimpleObjectProperty<ElevationProfile>();
        this.highlightedProperty = highlightedProperty;
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

        this.rectangle2DProperty = new SimpleObjectProperty<>();

        rectangle2DProperty.bind(Bindings.createObjectBinding(() -> {

                    Rectangle2D r = new Rectangle2D(insets.getLeft(), insets.getTop(),
                            pane.getWidth() - insets.getLeft() - insets.getRight(),
                            pane.getHeight() - insets.getBottom() - insets.getTop());

                    return r;
                },pane.widthProperty(),pane.heightProperty()

        ));

        line.startYProperty().bind(Bindings.select(rectangle2DProperty,"minY"));
        line.endYProperty().bind(Bindings.select(rectangle2DProperty,"maxY"));

        line.visibleProperty().bind(
                highlightedProperty.greaterThanOrEqualTo(0)
        );

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            Point2D pd = worldToScreen.get().transform(highlightedProperty.get(),0);
            return pd.getX();
        },line.layoutXProperty()));

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

        int x_steps = 0;
        int y_steps = 0;

        for (int i : POS_STEPS) {
            if (worldToScreen.get().deltaTransform(i, 0).getX() >= 50) {
                x_steps = i;
                break;
            }
            if (worldToScreen.get().deltaTransform(POS_STEPS[POS_STEPS.length - 1], 0).getX() < 50) {
                x_steps = POS_STEPS[POS_STEPS.length - 1];
            }
        }

        for (int i : ELE_STEPS) {
            if (worldToScreen.get().deltaTransform(0, i).getY() >= 25) {
                y_steps = i;
                break;
            }
            if (worldToScreen.get().deltaTransform(0, ELE_STEPS[ELE_STEPS.length - 1]).getY() < 25) {
                y_steps = ELE_STEPS[ELE_STEPS.length - 1];
            }
        }

        for (int i = x_steps; i < elevationProfileProperty.get().length(); i = i + x_steps) {
            PathElement horizontalLineStart = new MoveTo(0, i);
            PathElement horizontalLineEnd = new LineTo(pane.getWidth(), i);
            path.getElements().addAll(horizontalLineStart, horizontalLineEnd);
        }

        double elevation = elevationProfileProperty.get().maxElevation() - elevationProfileProperty.get().minElevation();
        for (int i = y_steps; i < elevation; i = i + y_steps) {
            PathElement verticalLineStart = new MoveTo(i, 0);
            PathElement verticalLineEnd = new LineTo(i, pane.getHeight());
            path.getElements().addAll(verticalLineStart, verticalLineEnd);
        }
    }


    private void createPolygon() {
        rectangle2DProperty.get().
    }

    public void grid() {

    }
}
