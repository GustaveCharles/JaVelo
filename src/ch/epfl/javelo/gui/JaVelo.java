package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.Observable;
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
import java.io.UncheckedIOException;
import java.nio.file.Path;

//TODO est-ce qu'il faut nommer des variables pour la height et la width (800 et 600)

public final class JaVelo extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CityBikeCF costFunction = new CityBikeCF(graph);

        RouteComputer routeComputer = new RouteComputer(graph, costFunction);

        RouteBean routeBean = new RouteBean(routeComputer);

        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorManager::displayError);

        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());
        BorderPane profilePane = elevationProfileManager.pane();

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        routeBean.elevationProfileProperty().addListener((e, oV, nV) -> {
            if (oV == null & nV != null) {
                splitPane.getItems().add(1, profilePane);
            }
            if (nV == null & oV != null) {
                splitPane.getItems().remove(1);
            }
        });

        //vérifier ligne en dessous
        SplitPane.setResizableWithParent(profilePane, false);

        Menu menu = new Menu("Fichier");
        MenuItem menuItemExport = new MenuItem("Exporter GPX");
        menuItemExport.disableProperty().setValue(routeBean.elevationProfileProperty() == null);
        menu.getItems().add(menuItemExport);
        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);
        splitPane.getItems().add(menuBar);

        menuItemExport.setOnAction(e -> {
            if (!menuItemExport.isDisable()) {
                try {
                    GpxGenerator.writeGpx("javelo.gpx", routeBean.routeProperty().get(), routeBean.elevationProfileProperty().get());
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
        });
        Pane errorManagerPane = errorManager.pane();

        StackPane stackPane = new StackPane(menuBar, splitPane, errorManagerPane);

        stackPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(stackPane));
        primaryStage.show();


        System.out.println(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0).get());
        routeBean.highlightedPositionProperty().bind(
                Bindings
                        .when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                        .then(annotatedMapManager.mousePositionOnRouteProperty())
                        .otherwise(elevationProfileManager.mousePositionOnProfileProperty())
        );


    }


}
