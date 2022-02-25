package ch.epfl.javelo.projection;

public final class SwissBounds {
    private SwissBounds(){}
    public static final double MIN_E = 2485000,MAX_E = 2834000, MIN_N = 1075000,
            MAX_N = 1296000, WIDTH = Math.abs(MAX_E-MIN_E), HEIGHT=Math.abs(MAX_N-MIN_N);

    public static boolean containsEN(double e, double n){
        if(e<MAX_E && e>MIN_E && n<MAX_N && n>MIN_N){
            return true;
        }
        else return false;

    }
}
