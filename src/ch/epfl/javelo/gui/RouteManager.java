package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {

    private final Pane pane;
    private Polyline line;
    private final Circle circle;
    private RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> property;
    private final Consumer<String> errors;
    private List<PointCh> routeNodes;
    private List<Double> routeNodesDouble;

    public RouteManager(RouteBean routeBean, SimpleObjectProperty<MapViewParameters> property, Consumer<String> errors) {
        this.line = new Polyline();
        this.routeBean = routeBean;
        this.circle = new Circle(5);
        this.property = property;
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


        routeBean.routeProperty().addListener((e, oV, nV) -> {

            if ((nV == null)) {
                System.out.println("coucou");
                line.setVisible(false);
                circle.setVisible(false);
            }


            if (e != null || oV != nV) {

                pointsSequence();
                line.setLayoutX(-property.get().xTopLeft());
                line.setLayoutY(-property.get().yTopLeft());

                routeBean.setHighlightedPosition(1000);

                PointWebMercator point = PointWebMercator.
                        ofPointCh(routeBean.routeProperty().get().pointAt(routeBean.getHighlightedPosition()));

                circle.setCenterX(point.xAtZoomLevel(property.get().zoomLevel()) - property.get().xTopLeft());
                circle.setCenterY(point.yAtZoomLevel(property.get().zoomLevel()) - property.get().yTopLeft());


                line.setVisible(true);
                circle.setVisible(true);
            }
        });

        property.addListener((e, oV, nV) -> {
            if (oV.zoomLevel() != nV.zoomLevel() && e!=null) {

                pointsSequence();
                line.setLayoutX(-nV.xTopLeft());
                line.setLayoutY(-nV.yTopLeft());
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
                    PointCh point = property.getValue().pointAt(point2D.getX(), point2D.getY()).toPointCh();
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
            routeNodesDouble.add(pointWebMercator.xAtZoomLevel(property.get().zoomLevel()));
            routeNodesDouble.add(pointWebMercator.yAtZoomLevel(property.get().zoomLevel()));

        });

        line.getPoints().addAll(routeNodesDouble);

    }

}



