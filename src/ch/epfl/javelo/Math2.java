package ch.epfl.javelo;

/**
 * Math2
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class Math2 {
    private Math2() {
    }

    /**
     * Return the result of the ceiling division of x on y.
     *
     * @param x numerator
     * @param y denominator
     * @return the result of the ceiling division
     * @throws IllegalArgumentException if the denominator is null or if the parameters are negative
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }

    /**
     * Return the linear interpolation of the line created by y0 and y1
     *
     * @param y0 the value on the y-axis at x = 0
     * @param y1 the value on the y-axis at x = 1
     * @param x  the value of the x-axis
     * @return the value of the y-axis at x
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * Clamp the given value (in int) between a minimal and a maximum
     *
     * @param min lower bound
     * @param v   value clamped
     * @param max upper bound
     * @return min if v is lower than min, max if v is greater than max, v otherwise
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(!(min > max));

        if (v < min) {
            return min;
        } else return Math.min(v, max);
    }

    /**
     * Clamp the given value (in double) between a minimal and a maximum
     *
     * @param min lower bound
     * @param v   value clamped
     * @param max upper bound
     * @throws IllegalArgumentException if the minimum value is greater than the maximum
     * @return min if v is lower than min, max if v is greater than max, v otherwise
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(!(min > max));

        if (v < min) {
            return min;
        } else return Math.min(v, max);
    }

    /**
     * Return the result of arcsinh(x)
     *
     * @param x parameter of the function
     * @return arcsinh(x) from x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }

    /**
     * Return the dot product from two vectors
     *
     * @param uX first coordinate of the first vector
     * @param uY second coordinate of the first vector
     * @param vX first coordinate of the second vector
     * @param vY second coordinate of the second vector
     * @return dot product from the two given vectors
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX * vX + uY * vY;
    }

    /**
     * Return the squared norm of a given vectors
     *
     * @param uX first coordinate of the vector
     * @param uY second coordinate of the vector
     * @return squared norm of the given vector
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Return the norm of a given vectors
     *
     * @param uX first coordinate of the vector
     * @param uY second coordinate of the vector
     * @return norm of the given vector
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Return the length of the projection of a vector P on a second vector AB
     *
     * @param aX first coordinate of A
     * @param aY second coordinate of A
     * @param bX first coordinate of B
     * @param bY second coordinate of B
     * @param pX first coordinate of P
     * @param pY second coordinate of P
     * @return length of the projection of the vector P on the second vector AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        return dotProduct(pX - aX, pY - aY, bX - aX, bY - aY) / norm(bX - aX, bY - aY);
    }
}
