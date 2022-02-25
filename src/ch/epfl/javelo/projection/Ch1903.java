package ch.epfl.javelo.projection;

/**
 * converts WGS 84 coordinates into Swiss coordinates and vice versa
 */
public final class Ch1903 {
    private Ch1903(){}

    /**
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double e(double lon, double lat){
        double lambda1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon)-26782.5),
                phi = Math.pow(10,-4)*(3600*Math.toDegrees(lat)-169028.66);

        double E=  2600072.37 + 211455.93*lambda1 - 10938.51*lambda1*phi - 0.36*lambda1*Math.pow(phi,2)
                - 44.54*(Math.pow(lambda1,3));

        return E;
    }

    public static double n(double lon, double lat){
        double lambda1 = Math.pow(10,-4)*(3600*Math.toDegrees(lon)-26782.5),
                phi = Math.pow(10,-4)*(3600*Math.toDegrees(lat)-169028.66);

        double N = 1200147.07 + 308807.95 * phi + 3745.25 *Math.pow(lambda1,2)
                + 76.63 * Math.pow(phi,2) - 194.56 * Math.pow(lambda1,2) * phi + 119.79 * Math.pow(phi,3);

        return N;

    }

    public static double lon(double e, double n){
        double y = Math.pow(10,-6)*(e-2600000);
        double x = Math.pow(10,-6)*(n-1200000);

        double lambda0 = 2.6779094
                + 4.728982*y
                + 0.791484*y*x
                + 0.1306 *y*Math.pow(x,2)
                - 0.0436 * Math.pow(y,3);

        double lambda1= lambda0*100/36;

        return Math.toRadians(lambda1);

    }

    public static double lat(double e, double n){
        double y = Math.pow(10,-6)*(e-2600000);
        double x = Math.pow(10,-6)*(n-1200000);

        double phi0 = 16.9023892
                + 3.238272 * x
                - 0.270978 * Math.pow(y,2)
                - 0.002528 * Math.pow(x,2)
                - 0.0447 * Math.pow(y,2) * x
                - 0.0140 * Math.pow(x,3);

        double phi1= phi0*100/36;

        return Math.toRadians(phi1);
    }
}