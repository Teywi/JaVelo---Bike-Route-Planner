package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * PointWebMercator
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record PointWebMercator(double x, double y) {

    //At zoom 0, the image is a square of side 256 (= 2^8) pixels
    private static final int PIXELS_AT_ZOOM_0 = 8;

    /**
     * Compact constructor of PointWebMercator
     *
     * @throws IllegalArgumentException if x or y are not between 0 and 1
     */
    public PointWebMercator {
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /**
     * Coordinates of the point (x,y) at level zoomLevel
     *
     * @param zoomLevel zoom level at which the points are
     * @param x         position of the point on the x-axis
     * @param y         position of the point on the y-axis
     * @return new coordinates of the point (x,y) at level zoomLevel
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -(zoomLevel + PIXELS_AT_ZOOM_0)), Math.scalb(y, -(zoomLevel + PIXELS_AT_ZOOM_0)));
    }

    /**
     * Convert Swiss coordinates to a point WebMercator
     *
     * @param pointCh Swiss coordinates
     * @return point WebMercator corresponding to the Swiss coordinates
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * Coordinate on the x-axis at zoomLevel
     *
     * @param zoomLevel zoom level
     * @return the coordinate on the x-axis at the zoom level zoomLevel
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, zoomLevel + PIXELS_AT_ZOOM_0);
    }

    /**
     * Coordinate on the y-axis at zoomLevel
     *
     * @param zoomLevel zoom level
     * @return the coordinate on the y-axis at the zoom level zoomLevel
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, zoomLevel + PIXELS_AT_ZOOM_0);
    }

    /**
     * Longitude of the point WebMercator
     *
     * @return longitude of the point WebMercator in radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Latitude of the point WebMercator
     *
     * @return latitude of the point WWebMercator in radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Swiss coordinates from the point WebMercator
     *
     * @return Swiss coordinates corresponding to the point WebMercator if the point WebMercator is in the Swiss boundaries, null otherwise
     */
    public PointCh toPointCh() {
        double lon = lon();
        double lat = lat();

        if (!SwissBounds.containsEN(Ch1903.e(lon, lat), Ch1903.n(lon, lat))) {
            return null;
        }

        return new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat));
    }
}
