package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
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
    private final Insets insets;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedProperty;
    private final ObjectProperty<Rectangle2D> rectangle2DProperty;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightedProperty) {
        screenToWorld = new SimpleObjectProperty<>();
        worldToScreen = new SimpleObjectProperty<>();
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightedProperty = highlightedProperty;


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
        BorderPane.setMargin(pane,insets);
        this.rectangle2DProperty = new SimpleObjectProperty<>(Rectangle2D.EMPTY);

        rectangle2DProperty.bind(Bindings.createObjectBinding(() -> {

                    return new Rectangle2D(insets.getLeft(), insets.getTop(),
                            Math.max(0,pane.getWidth() - insets.getLeft() - insets.getRight()),
                            Math.max(0,pane.getHeight() - insets.getBottom() - insets.getTop()));
                },pane.widthProperty(),pane.heightProperty()

        ));
        //pane.setBackground(Background.fill(Color.BLUE));
        //borderPane.setBackground(Background.fill(Color.RED));
        //besoin des deux ou 1 des deux c'est bon?
        pane.widthProperty().addListener(nV -> {
            createTransformation();
            createPolygon();
            createLine();
            createGrid();
            createBox();
        });
        pane.heightProperty().addListener(e -> {
            createTransformation();
            createPolygon();
            createLine();
            createGrid();
            createBox();
        });

        //pane.setBorder(new Border(new BorderStroke(Color.BLACK,
        //      BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //pas sur
        //elevationProfileProperty.addListener(nV->
        //   createPolygon()   );

        //  pane.setOnMouseMoved(() -> return mousePositionOnProfileProperty());
        // pane.setOnMouseExited(e -> mousePositionOnProfileProperty());
    }

    public Pane pane() {
        return borderPane;
    }

    //public ReadOnlyObjectProperty<Integer> mousePositionOnProfileProperty() {
    //}

    private void createTransformation() {
        Affine transformation = new Affine();

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
        System.out.println(worldToScreen.get().transform(0,663));

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

    private void createLabel(){
        //horizontal = position = group1      vertical = altitude = group2

        textGroup_1.setTextOrigin(VPos.TOP);
        textGroup_1.setFont(Font.font("Avenir", 10));
        textGroup_1.setLayoutX(-textGroup_1.prefWidth(0)/2);

        textGroup_2.setTextOrigin(VPos.CENTER);
        textGroup_2.setFont(Font.font("Avenir", 10));
        textGroup_2.setLayoutX(-textGroup_2.prefWidth(0) -2);

    }

    private void createBox(){

        textVbox.setText("Longueur : %.1f km".formatted(elevationProfileProperty.get().length()*0.001) +
                "     Montée : %.0f m".formatted(elevationProfileProperty.get().totalAscent()) +
                "     Descente : %.0f m".formatted(elevationProfileProperty.get().totalDescent()) +
                "     Altitude : de %.0f m à %.0f m".formatted(
                        elevationProfileProperty.get().minElevation(),
                        elevationProfileProperty.get().maxElevation()
                ));

    }


    private void createPolygon() {
        polygone.getPoints().clear();

        System.out.println("minX" + rectangle2DProperty.get().getMinX());
        System.out.println("maxX" + rectangle2DProperty.get().getMaxX());
        //System.out.println("width" + rectangle2DProperty.get().getWidth());


        for(int i=(int)rectangle2DProperty.get().getMinX(); i<=rectangle2DProperty.get().getMaxX() ; ++i){
            Point2D point2Dd = screenToWorld.get().transform(i,0);
            double point = elevationProfileProperty.get().elevationAt(point2Dd.getX());
            Point2D pd2=  worldToScreen.get().transform(0,point);
            //Math2.clamp(); --post 1551
            polygone.getPoints().add((double)i);
            polygone.getPoints().add(pd2.getY());
        }
        polygone.getPoints().add(rectangle2DProperty.get().getMaxX());
        polygone.getPoints().add(rectangle2DProperty.get().getMaxY());
        polygone.getPoints().add(rectangle2DProperty.get().getMinX());
        polygone.getPoints().add(rectangle2DProperty.get().getMaxY());



    }
    private void createRectangle(){

        //   rectangle2DProperty.set(new Rectangle2D(insets.getLeft(), insets.getTop(),
        //           Math.max(0,pane.getWidth() - insets.getLeft() - insets.getRight()),
        //         Math.max(0,pane.getHeight() - insets.getBottom() - insets.getTop())));

        //le rectangle il a un bleme - cette partie est bien faite?

    }

    private void createLine(){
        line.startYProperty().bind(Bindings.select(rectangle2DProperty,"minY"));
        line.endYProperty().bind(Bindings.select(rectangle2DProperty,"maxY"));

        //visibleProperty.bind
        line.visibleProperty().addListener(e -> {
            if(  highlightedProperty.greaterThanOrEqualTo(0).get() )
                line.setVisible(true);
        });

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            Point2D pd = worldToScreen.get().transform(highlightedProperty.get(),0);
            return pd.getX();
        },line.layoutXProperty()));
    }


}
