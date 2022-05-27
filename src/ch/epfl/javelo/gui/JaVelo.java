package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PipedReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;

//TODO est-ce qu'il faut nommer des variables pour la height et la width (800 et 600)

public final class JaVelo extends Application {
    private final static String PATH_GRAPH = "javelo-data";
    private final static String PATH_CACHE = "osm-cache";
    private final static String SERVER_NAME = "tile.openstreetmap.org";
    private final static String MENU_NAME = "Fichier";
    private final static String EXPORT_NAME = "Exporter GPX";
    private final static String GPX_FILE_NAME = "javelo.gpx";
    private final static String MAP_STYLE = "map.css";
    private final static String PANE_NAME = "JaVelo";
    private final static int PANE_WIDTH = 800;
    private final static int PANE_HEIGHT = 600;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of(PATH_GRAPH));
        Path cacheBasePath = Path.of(PATH_CACHE);

        TileManager tileManager =
                new TileManager(cacheBasePath, SERVER_NAME);

        CityBikeCF costFunction = new CityBikeCF(graph);

        RouteComputer routeComputer = new RouteComputer(graph, costFunction);

        RouteBean routeBean = new RouteBean(routeComputer);

        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean,
                errorManager::displayError);

        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(routeBean.elevationProfileProperty(),
                routeBean.highlightedPositionProperty());
        Pane profilePane = elevationProfileManager.pane();

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        SplitPane.setResizableWithParent(profilePane, false);
        Menu menu = new Menu(MENU_NAME);
        MenuItem exportGPXItem = new MenuItem(EXPORT_NAME);
        menu.getItems().add(exportGPXItem);
        exportGPXItem.disableProperty().set(true);
        MenuBar menuBar = new MenuBar(menu);

        routeBean.routeProperty().addListener((p, o, n) -> {
            exportGPXItem.disableProperty().set(p.getValue() == null);
        });

        exportGPXItem.setOnAction(e -> {
            if (!exportGPXItem.isDisable()) {
                try {
                    GpxGenerator.writeGpx(GPX_FILE_NAME, routeBean.route(), routeBean.elevationProfileProperty().get());
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        });

        routeBean.elevationProfileProperty().addListener((e, oV, nV) -> {
            if (oV == null & nV != null) {
                splitPane.getItems().add(1, profilePane);
            }
            if (nV == null & oV != null) {
                splitPane.getItems().remove(1);
            }
        });

        Pane errorManagerPane = errorManager.pane();

        StackPane stackPane = new StackPane(splitPane, errorManagerPane);
        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(stackPane);
        borderPane.setTop(menuBar);

        borderPane.getStylesheets().add(MAP_STYLE);
        primaryStage.setMinWidth(PANE_WIDTH);
        primaryStage.setMinHeight(PANE_HEIGHT);
        primaryStage.setTitle(PANE_NAME);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();

        routeBean.highlightedPositionProperty().bind(
                Bindings
                        .when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                        .then(annotatedMapManager.mousePositionOnRouteProperty())
                        .otherwise(elevationProfileManager.mousePositionOnProfileProperty())
        );
    }
}
