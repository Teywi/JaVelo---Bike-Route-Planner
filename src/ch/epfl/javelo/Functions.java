package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Functions
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class Functions {
    private Functions() {
    }

    /**
     * Return a constant function
     *
     * @param y parameter of the function
     * @return value of the function
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    private static record Constant(double y) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double x) {
            return y;
        }
    }

    /**
     * Return a function from linear interpolation between the given samples
     *
     * @param samples values of the samples
     * @param xMax    maximum value on the x-axis
     * @throws IllegalArgumentException if there are less than 2 samples and if the maximum value on the x-axis is non-positive
     * @return function from linear interpolation between the given samples from 0 to xMax on the x-axis
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);

        return new Sampled(samples, xMax);
    }

    private static record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double x) {
            final double value;

            if (x <= 0) {
                value = samples[0];
            }
            else if (x >= xMax) {
                value = samples[samples.length - 1];
            }
            else {
                double spaceBetweenPoint = xMax / (samples.length - 1);
                int leftIndex = (int) (x / spaceBetweenPoint);
                value = Math2.interpolate(samples[leftIndex], samples[leftIndex + 1], (x - leftIndex*spaceBetweenPoint) / spaceBetweenPoint);;
            }

            return value;
        }
    }
}
