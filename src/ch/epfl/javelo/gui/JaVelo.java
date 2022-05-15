package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

//TODO est-ce qu'il faut nommer des variables pour la height et la width (800 et 600)
//TODO implémenter ma classe ErrorManager

public final class JaVelo extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);
        CityBikeCF costFunction = new CityBikeCF(graph);
        Consumer<String> errorConsumer = new ErrorConsumer();
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(routeComputer);
        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);
        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);

        if (routeBean.elevationProfileProperty().get().length() != 0) {
            ElevationProfileManager elevationProfileComputer = new ElevationProfileManager(routeBean.elevationProfileProperty(),
                    routeBean.highlightedPositionProperty());

            splitPane.getItems().add(elevationProfileComputer.pane());


            //TODO faire du conditional binding
            // au lieu d'avoir des if les faire dans le bind
            if (annotatedMapManager.mousePositionOnRouteProperty().getValue() >= 0) {
                routeBean.highlightedPositionProperty().bind(annotatedMapManager.mousePositionOnRouteProperty());
            } else {
                routeBean.highlightedPositionProperty().bind(elevationProfileComputer.mousePositionOnProfileProperty());
            }

        }

        Menu menu = new Menu("Fichier");
        MenuItem menuItemExport = new MenuItem("Exporter GPX");
        menuItemExport.disableProperty().setValue(routeBean.elevationProfileProperty().get().length() == 0);
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
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.show();
        //
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) {
            System.out.println(s);
        }
    }
}
