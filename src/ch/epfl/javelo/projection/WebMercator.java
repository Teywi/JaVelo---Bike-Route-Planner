package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * WebMercator
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class WebMercator {
    private WebMercator(){}

    /**
     * Convert WGS84 coordinates to WebMercator coordinates
     *
     * @param lon longitude of the point in WGS84 coordinates
     * @return position of the point on the x-axis in WebMercator coordinates
     */
    public static double x(double lon){
        return (1 / (2*Math.PI)) * (lon + Math.PI);
    }

    /**
     * Convert WGS84 coordinates to WebMercator coordinates
     *
     * @param lat longitude of the point in WGS84 coordinates
     * @return position of the point on the y-axis in WebMercator coordinates
     */
    public static double y(double lat){
        return (1 / (2*Math.PI)) * (Math.PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     * Convert WebMercator coordinates to WGS84 coordinates
     *
     * @param x position of the point on the x-axis in WebMercator coordinates
     * @return  longitude of the point in WGS84 coordinates
     */
    public static double lon(double x){
        return 2*Math.PI*x - Math.PI;
    }

    /**
     * Convert WebMercator coordinates to WGS84 coordinates
     *
     * @param y position of the point on the y-axis in WebMercator coordinates
     * @return latitude of the point in WGS84 coordinates
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI - 2*Math.PI*y));
    }
}
