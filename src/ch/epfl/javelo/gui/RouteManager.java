package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {

    private final Pane pane;
    private final Polyline line;
    private final Circle circle;
    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapParameters;
    private final Consumer<String> errors;
    private List<PointCh> routeNodes;
    private final List<Double> routeNodesDouble;

    public RouteManager(RouteBean routeBean, SimpleObjectProperty<MapViewParameters> mapParameters, Consumer<String> errors) {
        this.line = new Polyline();
        this.routeBean = routeBean;
        this.circle = new Circle(5);
        this.mapParameters = mapParameters;
        this.errors = errors;

        this.routeNodes = new ArrayList<>();
        this.routeNodesDouble = new ArrayList<>();

        pane = new Pane(line, circle);
        pane.setPickOnBounds(false);


        line.setId("route");
        circle.setId("highlight");

        //si la propriété route de routeBean contient null

        line.setVisible(false);
        circle.setVisible(false);

        routeBean.highlightedPositionProperty().addListener( e -> {
            PointWebMercator point = PointWebMercator.
                    ofPointCh(routeBean.routeProperty().get().pointAt(routeBean.getHighlightedPosition()));

            circle.setCenterX(point.xAtZoomLevel(mapParameters.get().zoomLevel()) - mapParameters.get().xTopLeft());
            circle.setCenterY(point.yAtZoomLevel(mapParameters.get().zoomLevel()) - mapParameters.get().yTopLeft());
        });

        //rendre invisible la ligne lorsque lorsque itinéraire est nul
        //rendre visible la ligne lorsque l'itinéraire est pas nul
        // rendre invisible le disque lorsque itinéraire est nul
        //rendre visible le disque lorsque itinéraire est non nul
        routeBean.routeProperty().addListener(e -> {

            if ((routeBean.routeProperty().get() == null)) {
                line.setVisible(false);
                circle.setVisible(false);
            }

            if (routeBean.routeProperty().get() != null) {

                line.setVisible(true);
                circle.setVisible(true);
            }
        });

        //reconstruire ligne lorsque itinéraire change
        // reconstruire le disque lorsque les paramètres de la carte changent (xtopleft ytopleft)
        routeBean.routeProperty().addListener((e,oV,nV) -> {
            if(oV != nV && e!=null){
                pointsSequence();
                line.setLayoutX(-mapParameters.get().xTopLeft());
                line.setLayoutY(-mapParameters.get().yTopLeft());

                routeBean.setHighlightedPosition(1000);

                PointWebMercator point = PointWebMercator.
                        ofPointCh(routeBean.routeProperty().get().pointAt(routeBean.getHighlightedPosition()));

                circle.setCenterX(point.xAtZoomLevel(mapParameters.get().zoomLevel()) - mapParameters.get().xTopLeft());
                circle.setCenterY(point.yAtZoomLevel(mapParameters.get().zoomLevel()) - mapParameters.get().yTopLeft());
            }
        });

        //repositionner ligne lorsque la carte glissée
        //reconstruire ligne lors de nouveau zoom
        // reconstruire le disque lorsque les paramètres de la carte changent (zoom)

        mapParameters.addListener((e, oV, nV) -> {

            if (oV.zoomLevel() != nV.zoomLevel() && e!=null) {

                pointsSequence();
                line.setLayoutX(-nV.xTopLeft());
                line.setLayoutY(-nV.yTopLeft());

                PointWebMercator point = PointWebMercator.
                        ofPointCh(routeBean.routeProperty().get().pointAt(routeBean.getHighlightedPosition()));

                circle.setCenterX(point.xAtZoomLevel(nV.zoomLevel()) - nV.xTopLeft());
                circle.setCenterY(point.yAtZoomLevel(nV.zoomLevel()) - nV.yTopLeft());

            }
            if (oV.zoomLevel() == nV.zoomLevel()) {

                line.setLayoutX(line.getLayoutX() + oV.xTopLeft() - nV.xTopLeft());
                line.setLayoutY(line.getLayoutY() + oV.yTopLeft() - nV.yTopLeft());

                circle.setCenterX(circle.getCenterX() + oV.xTopLeft() - nV.xTopLeft());
                circle.setCenterY(circle.getCenterY() + oV.yTopLeft() - nV.yTopLeft());
            }

        });

        handler();

    }


    public Pane pane() {
        return pane;
    }

    private void handler() {

        circle.setOnMouseClicked(e -> {
                    Point2D point2D = circle.localToParent(e.getX(), e.getY());
                    PointCh point = mapParameters.getValue().pointAt(point2D.getX(), point2D.getY()).toPointCh();
                    int closestPointId = routeBean.routeProperty().get()
                            .nodeClosestTo(routeBean.highlightedPosition());
                    if (closestPointId != -1) {
                        int index= routeBean.routeProperty().get()
                                .indexOfSegmentAt(routeBean.highlightedPosition())+1;
                        routeBean.waypointsProperty().add(index,new Waypoint(point,closestPointId));

                    } else {
                        errors.accept("Un point de passage est déjà présent à cet endroit !");
                    }
                }
        );
    }


    private void pointsSequence() {

        routeNodes.clear();
        routeNodesDouble.clear();
        line.getPoints().clear();

        routeNodes = new ArrayList<>(routeBean.routeProperty().get().points());
        routeNodes.forEach(o -> {
            PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(o);
            routeNodesDouble.add(pointWebMercator.xAtZoomLevel(mapParameters.get().zoomLevel()));
            routeNodesDouble.add(pointWebMercator.yAtZoomLevel(mapParameters.get().zoomLevel()));

        });

        line.getPoints().addAll(routeNodesDouble);

    }

}



