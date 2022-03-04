package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * represents a point in the Web Mercator system
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Jean Dupond (339364)
 */

public record PointWebMercator(double x, double y) {

    /**
     * @throws IllegalArgumentException if the coordinates x and y are not in the interval [0,1]
      */

    public PointWebMercator{

        Preconditions.checkArgument(x>=0 && x<=1 && y>=0 && y<=1);
    }

    /**
     *
     * @param zoomLevel
     * @param x
     * @param y
     * @return
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){ //probleme de test ici
       double X = Math.scalb(x,-8-zoomLevel);
       double Y = Math.scalb(y,-8-zoomLevel);
        return new PointWebMercator(X,Y);
    }

    /**
     *
     * @param pointCh
     * @return
     */

    public static PointWebMercator ofPointCh(PointCh pointCh){ //pas arrivé a bien faire le test pour cette methode
       double X = WebMercator.x(pointCh.lon());
        double Y = WebMercator.y(pointCh.lat());

        return new PointWebMercator(X,Y);
    }

    public double xAtZoomLevel(int zoomLevel){
        return Math.round(Math.scalb(this.x,8+zoomLevel));
    }

    public double yAtZoomLevel(int zoomLevel){
        return Math.round(Math.scalb(this.y,8+zoomLevel));
    }

    public double lon(){
       return WebMercator.lon(this.x);
    }

    public double lat(){
       return WebMercator.lat(this.y);
    }

    /**
     * from Web Mercator coordinates to SwissBounds coordinates
     * @return SwissBound coordinates if they are in the Swiss limits, returns null otherwise
     */
    public PointCh toPointCh(){
        double e= Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(),lat());
        if(!SwissBounds.containsEN(e,n)){
            return null;
        }
        return new PointCh(e,n);
    }
}