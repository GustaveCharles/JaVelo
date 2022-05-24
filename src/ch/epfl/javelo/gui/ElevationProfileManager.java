package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.Group;
import javafx.scene.text.Text;

/**
 * Represents a manager for the elevation profile
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */

public final class ElevationProfileManager {
    public final static int MIN_HORIZONTAL_SPACING = 25;
    public final static int MIN_VERTICAL_SPACING = 50;
    private static final int TO_KILOMETERS = 1000;
    private static final int[] POS_STEPS =
            {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private final Path path;
    private final Polygon polygon;
    private final Line line;
    private final Group group;
    private final Text textVbox;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;
    private final Insets insets;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedProperty;
    private final ObjectProperty<Rectangle2D> rectangle2DProperty;
    private final DoubleProperty mousePosition;
    private static final int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};
    private static final String font = "Avenir";
    private static final int fontSize = 10;
    private static final String gridLabel = "grid_label";
    private static final String[] gridOrientations = {"vertical", "horizontal"};
    private final Pane elevationPane;
    private final Pane statisticsPane;


    /**
     * Manages the display and interaction with the profile of the route
     *
     * @param elevationProfileProperty a read-only property containing the profile to display, it contains null if there is no such profile
     * @param highlightedProperty      a read-only property containing the position along the profile to be highlighted,
     *                                 it contains Nan if there is no such position
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedProperty) {
        this.screenToWorld = new SimpleObjectProperty<>();
        this.worldToScreen = new SimpleObjectProperty<>();
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedProperty = highlightedProperty;
        this.mousePosition = new SimpleDoubleProperty();
        this.path = new Path();
        this.polygon = new Polygon();
        this.line = new Line();
        this.group = new Group();
        this.textVbox = new Text();
        this.rectangle2DProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        VBox vBox = new VBox(textVbox);
        elevationPane = new Pane(path, polygon, line, group);
        statisticsPane = new BorderPane(elevationPane, null, null, vBox, null);
        insets = new Insets(10, 10, 20, 40);
        path.setId("grid");
        polygon.setId("profile");
        vBox.setId("profile_data");
        statisticsPane.getStylesheets().setAll("elevation_profile.css");
        createRectangle();

        elevationPane.widthProperty().addListener((p, o, n) -> {
            if (elevationProfileProperty.get() != null)
                displayElevation();
        });

        elevationPane.heightProperty().addListener((p, o, n) -> {
            if (elevationProfileProperty.get() != null)
                displayElevation();

        });

        elevationPane.setOnMouseMoved(e -> {
            if (rectangle2DProperty.get().contains(new Point2D(e.getX(), e.getY()))) {
                Point2D point2D = screenToWorld.get().transform(e.getX(), e.getY());
                mousePosition.set(point2D.getX());
            } else {
                mousePosition.set(Double.NaN);
            }
        });

        elevationPane.setOnMouseExited(e -> {
            if (!rectangle2DProperty.get().contains(new Point2D(e.getX(), e.getY()))) {
                mousePosition.set(Double.NaN);
            }
        });

        elevationProfileProperty.addListener((p, o, n) -> displayElevation());
    }

    /**
     * A getter for the pane
     *
     * @return the border pane
     */
    public Pane pane() {
        return statisticsPane;
    }

    /**
     * A getter for the mouse position
     *
     * @return a read-only property which contains the mouse position
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePosition;
    }

    /**
     * Represents the conversion between the two coordinate systems,
     * namely from real coordinates to screen coordinates and vice versa.
     */
    private void createTransformation() {
        double elevationDifference = elevationProfileProperty.get().maxElevation() - elevationProfileProperty.get().minElevation();
        double rectangleHeight = rectangle2DProperty.get().getHeight();
        double rectangleWidth = rectangle2DProperty.get().getWidth();
        double length = elevationProfileProperty.get().length();
        double minXRectangle = rectangle2DProperty.get().getMinX();
        double minYRectangle = rectangle2DProperty.get().getMinY();
        double maxElevation = elevationProfileProperty.get().maxElevation();

        Affine transformation = new Affine();
        transformation.prependTranslation(-minXRectangle, -minYRectangle);
        transformation.prependScale(length / rectangleWidth,
                -elevationDifference / rectangleHeight);
        transformation.prependTranslation(0, maxElevation);
        screenToWorld.set(transformation);

        try {
            worldToScreen.set(screenToWorld.get().createInverse());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds a grid with an adapted scale for the x-axis and y-axis regarding
     * the value of the height and the length of the profile
     */
    private void createGrid() {
        group.getChildren().clear();
        path.getElements().clear();

        double minElevation = elevationProfileProperty.get().minElevation();
        double maxElevation = elevationProfileProperty.get().maxElevation();
        double length = elevationProfileProperty.get().length();

        int xStep = 0;
        int yStep = 0;

        //Searches for the smallest value guaranteeing that, on the screen, the horizontal lines are separated by
        //at least 25 JavaFX units (pixels). If no value guarantees this, the largest of all is used.

        for (int i : POS_STEPS) {
            if (worldToScreen.get().deltaTransform(i, 0).getX() >= MIN_VERTICAL_SPACING) {
                xStep = i;
                break;
            }
            if (worldToScreen.get().deltaTransform(POS_STEPS[POS_STEPS.length - 1], 0).getX() < MIN_VERTICAL_SPACING) {
                xStep = POS_STEPS[POS_STEPS.length - 1];
            }
        }

        //Searches for the smallest value guaranteeing that, on the screen, the vertical lines are separated by
        //at least 50 JavaFX units (pixels). If no value guarantees this, the largest of all is used.

        for (int i : ELE_STEPS) {
            if (worldToScreen.get().deltaTransform(0, -i).getY() >= MIN_HORIZONTAL_SPACING) {
                yStep = i;
                break;
            }
            if (worldToScreen.get().deltaTransform(0, ELE_STEPS[ELE_STEPS.length - 1]).getY() < MIN_HORIZONTAL_SPACING) {
                yStep = ELE_STEPS[ELE_STEPS.length - 1];
            }
        }

        //Creates horizontal lines

        double closestStepBound = Math2.ceilDiv((int) minElevation, yStep) * yStep;
        for (double i = closestStepBound; i < maxElevation; i += yStep) {
            Point2D startHorizontal = worldToScreen.get().transform(0, i);
            Point2D endHorizontal = worldToScreen.get().transform(length, i);
            path.getElements().addAll(new MoveTo(startHorizontal.getX(), startHorizontal.getY()),
                    new LineTo(endHorizontal.getX(), endHorizontal.getY()));

            Text textGroup_1 = new Text();
            textGroup_1.getStyleClass().setAll(gridLabel, gridOrientations[0]);
            textGroup_1.setTextOrigin(VPos.CENTER);
            textGroup_1.setFont(Font.font(font, fontSize));
            textGroup_1.setText(Integer.toString((int) i));
            textGroup_1.setX(startHorizontal.getX());
            textGroup_1.setY(startHorizontal.getY());
            textGroup_1.setLayoutX(-(textGroup_1.prefWidth(0) + 2));
            group.getChildren().add(textGroup_1);
        }

        // Creates vertical lines

        for (double i = 0; i < length; i += xStep) {
            Point2D startVertical = worldToScreen.get().transform(i, minElevation);
            Point2D endVertical = worldToScreen.get().transform(i, maxElevation);
            path.getElements().addAll(new MoveTo(startVertical.getX(), startVertical.getY()),
                    new LineTo(endVertical.getX(), endVertical.getY()));

            Text textGroup_2 = new Text();
            textGroup_2.getStyleClass().setAll(gridLabel, gridOrientations[1]);
            textGroup_2.setTextOrigin(VPos.TOP);
            textGroup_2.setFont(Font.font(font, fontSize));
            textGroup_2.setText(Integer.toString((int) (i / TO_KILOMETERS)));
            textGroup_2.setLayoutY(rectangle2DProperty.get().getMaxY());
            textGroup_2.setLayoutX(startVertical.getX() - textGroup_2.prefWidth(0) / 2);
            group.getChildren().add(textGroup_2);
        }
    }

    /**
     *displays the route statistics presented at the bottom of the panel
     */
    private void createBox() {
        //mettre une constante pour 0.001
        textVbox.setText("Longueur : %.1f km".formatted(elevationProfileProperty.get().length() / TO_KILOMETERS) +
                "     Montée : %.0f m".formatted(elevationProfileProperty.get().totalAscent()) +
                "     Descente : %.0f m".formatted(elevationProfileProperty.get().totalDescent()) +
                "     Altitude : de %.0f m à %.0f m".formatted(
                        elevationProfileProperty.get().minElevation(),
                        elevationProfileProperty.get().maxElevation()
                ));
    }


    private void createPolygon() {
        polygon.getPoints().clear();

        for (int i = (int) rectangle2DProperty.get().getMinX(); i <= rectangle2DProperty.get().getMaxX(); ++i) {
            Point2D point2Dd = screenToWorld.get().transform(i, 0);
            double point = elevationProfileProperty.get().elevationAt(point2Dd.getX());
            Point2D pd2 = worldToScreen.get().transform(0, point);
            //Math2.clamp(); --post 1551
            polygon.getPoints().add((double) i);
            polygon.getPoints().add(pd2.getY());
        }
        polygon.getPoints().add(rectangle2DProperty.get().getMaxX());
        polygon.getPoints().add(rectangle2DProperty.get().getMaxY());
        polygon.getPoints().add(rectangle2DProperty.get().getMinX());
        polygon.getPoints().add(rectangle2DProperty.get().getMaxY());
    }

    private void createRectangle() {
        rectangle2DProperty.bind(Bindings.createObjectBinding(() -> new Rectangle2D(insets.getLeft(), insets.getTop(),
                Math.max(0, elevationPane.getWidth() - insets.getLeft() - insets.getRight()),
                Math.max(0, elevationPane.getHeight() - insets.getBottom() - insets.getTop())), elevationPane.widthProperty(), elevationPane.heightProperty()
        ));
    }

    private void createLine() {
        line.startYProperty().bind(Bindings.select(rectangle2DProperty, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle2DProperty, "maxY"));

        line.visibleProperty().bind(
                highlightedProperty.greaterThanOrEqualTo(0)
        );

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            Point2D p2d = worldToScreen.get().transform(highlightedProperty.get(), 0);
            return p2d.getX();
        }, highlightedProperty, worldToScreen));
    }

    private void displayElevation() {
        if (elevationProfileProperty.get() != null) {
            createTransformation();
            createPolygon();
            createLine();
            createGrid();
            createBox();
        }
    }
}