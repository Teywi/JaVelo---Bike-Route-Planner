package ch.epfl.javelo;

/**
 * Bits
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class Bits {
    private Bits() {
    }

    /**
     * Extract from a 32 bits value a number of length bits starting from the start-th index
     *
     * @param value  the initial 32 bits
     * @param start  the first bit to extract
     * @param length the length in bits of the part to extract
     * @return the bits extracted in the binary signed representation
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start + length <= Integer.SIZE && start >= 0 && length > 0);

        value = value << (Integer.SIZE - start - length);
        value = value >> (Integer.SIZE - length);

        return value;
    }

    /**
     * Extract from a 32 bits value a number of length bits starting from the start-th index
     *
     * @param value  the initial 32 bits
     * @param start  the first bit to extract
     * @param length the length in bits of the part to extract
     * @return the bits extracted in the binary unsigned representation
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start + length <= Integer.SIZE && start >= 0 && length > 0 && length < Integer.SIZE);

        value = value << (Integer.SIZE - start - length);
        value = value >>> (Integer.SIZE - length);

        return value;
    }
}
