package ch.epfl.javelo.projection;


import ch.epfl.javelo.projection.PointCh;


public record PointWebMercator(double x, double y) {

    /**
     * @throws IllegalArgumentException if the coordinates x and y are not in the interval [0,1]
      */

    public PointWebMercator{

        if(x<0 || x>1 || y<0 || y>1){
            throw new IllegalArgumentException();
        }
    }

    public static PointWebMercator of(int zoomLevel, double x, double y){
       double X = Math.scalb(x,8+zoomLevel);
       double Y = Math.scalb(y,8+zoomLevel);
        return new PointWebMercator(X,Y);
    }

    public static PointWebMercator ofPointCh(PointCh pointCh){
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

    public PointCh toPointCh(){
        double e= Ch1903.e(lon(), lat());
        double n = Ch1903.n(lon(),lat());
        if(!SwissBounds.containsEN(e,n)){
            return null;
        }
        return new PointCh(e,n);
    }
}