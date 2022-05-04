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
    private Polyline line;
    private final Circle circle;
    private RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> property;
    private final Consumer<String> errors;
    private List<PointCh> routeNodes;
    private List<Double> routeNodesDouble;

    public RouteManager(RouteBean routeBean, SimpleObjectProperty<MapViewParameters> property, Consumer<String> errors) {
        //ajouter les points lors de la construction de la polyline
        this.line = new Polyline();
        this.routeBean = routeBean;
        //est ce qu il faut mettre des parametrees centrex et centrey a la construction?
        this.circle = new Circle(5);
        this.property = property;
        this.errors = errors;

         this.routeNodes = new ArrayList<>();
         this.routeNodesDouble = new ArrayList<>();

        pane = new Pane(line, circle);
        // pane.getChildren().add(line);
        //pane.getChildren().add(circle);
        pane.setPickOnBounds(false);

        line.setId("route");
        circle.setId("highlight");

        //si la propriété route de routeBean contient null
        if (routeBean.routeProperty() == null) {
            line.setVisible(false);
            circle.setVisible(false);
        }

        line.setLayoutX(line.getLayoutX() - property.get().xTopLeft());
        line.setLayoutY(line.getLayoutY() - property.get().yTopLeft());

        if(routeBean.routeProperty().get() != null){
          //  pointsSequence();
        }
        routeBean.routeProperty().addListener(e -> {
            //rendre visible invisible disque et ligne
        });
        property.addListener(e -> {
            //rendre visible invisible disque et ligne
        });

        property.addListener((e,oV,nV) -> {
            if(oV.zoomLevel() != nV.zoomLevel()){
             //   pointsSequence();
            }

            line.setLayoutX(-oV.topLeft().getX());
            line.setLayoutX(-oV.topLeft().getY());
        });

//        circle.visibleProperty().addListener((e, oV, nV) -> {
//            if (nV != oV) {
//
//            } else {
//                circle.setCenterX(routeBean.highlightedPositionProperty().get());
//                circle.setCenterY(routeBean.highlightedPosition.get().getY);
//            }
//            if (e.getValue() == true) {
//                circle.setVisible(true);
//            }
//        });

        line.visibleProperty().addListener((e, oV, nV) -> {

            // if(property.get().zoomLevel() == ){

            // }
        });
        handler();

    }


    public Pane pane() {
        return pane;
    }

    private void handler() {

        circle.setOnMouseClicked(e -> {
                    Point2D pd = circle.localToParent(e.getX(), e.getY());
                    PointCh point = property.getValue().pointAt(pd.getX(), pd.getY()).toPointCh();
                    int closestPointId = routeBean.routeProperty().get().nodeClosestTo(routeBean.highlightedPosition());

                    if (closestPointId != -1) {
                        routeBean.addWaypoints(new Waypoint(point, closestPointId));
                    } else {
                        errors.accept("Un point de passage est déjà présent à cet endroit !");
                    }
                }
        );
    }


    private void pointsSequence() {
        System.out.println("coucou");

        routeNodes = new ArrayList<>(routeBean.routeProperty().get().points());

        routeNodes.forEach(o -> {
                    routeNodesDouble.add(o.lat());
                    routeNodesDouble.add(o.lon());
                }
        );

        line.getPoints().addAll(routeNodesDouble);
    }

}



