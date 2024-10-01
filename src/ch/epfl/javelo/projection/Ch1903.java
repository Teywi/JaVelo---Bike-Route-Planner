package ch.epfl.javelo.projection;

/**
 * Ch1903
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class Ch1903 {
    private Ch1903(){}

    private static double lambda(double lon){
        return 1e-4 * (3600*Math.toDegrees(lon) - 26782.5);
    }

    private static double phi(double lat){
        return 1e-4 * (3600*Math.toDegrees(lat) - 169028.66);
    }

    private static double x(double E){
        return 1e-6 * (E - 2600000);
    }

    private static double y(double N){
        return 1e-6 * (N - 1200000);
    }

    /**
     * Convert coordinates in WGS84 to Swiss coordinates
     *
     * @param lon longitude of the point in WGS84
     * @param lat latitude of the point in WGS84
     * @return position of the point in Swiss coordinates on the E-axis
     */
    public static double e(double lon, double lat){
        double lambda = lambda(lon);
        double phi = phi(lat);

        return 2600072.37 + 211455.93*lambda - 10938.51*lambda*phi - 0.36*lambda*Math.pow(phi,2) - 44.54*Math.pow(lambda,3);
    }

    /**
     * Convert coordinates in WGS84 to Swiss coordinates
     *
     * @param lon longitude of the point in WGS84
     * @param lat latitude of the point in WGS84
     * @return position of the point in Swiss coordinates on the N-axis
     */
    public static double n(double lon, double lat){
        double lambda = lambda(lon);
        double phi = phi(lat);

        return 1200147.07 + 308807.95*phi + 3745.25*Math.pow(lambda, 2) + 76.63*Math.pow(phi, 2) - 194.56*Math.pow(lambda, 2)*phi + 119.79*Math.pow(phi, 3);
    }

    /**
     * Convert Swiss coordinates to WGS84
     *
     * @param e position of the point on the E-axis
     * @param n position of the point on the N-axis
     * @return the longitude of the point in WGS84
     */
    public static double lon(double e, double n){
        double x = x(e);
        double y = y(n);

        double lon0 = 2.6779094 + 4.728982*x + 0.791484*x*y + 0.1306*x*Math.pow(y, 2) - 0.0436*Math.pow(x, 3);

        return Math.toRadians(lon0 * (100.0 / 36.0));
    }

    /**
     * Convert Swiss coordinates to WGS84
     *
     * @param e position of the point on the E-axis
     * @param n position of the point on the N-axis
     * @return the latitude of the point in WGS84
     */
    public static double lat(double e, double n){
        double x = x(e);
        double y = y(n);

        double lat0 = 16.9023892 + 3.238272*y - 0.270978*Math.pow(x, 2) - 0.002528*Math.pow(y, 2) - 0.0447*Math.pow(x, 2)*y - 0.014*Math.pow(y, 3);

        return Math.toRadians(lat0 * (100.0 / 36.0));
    }
}
