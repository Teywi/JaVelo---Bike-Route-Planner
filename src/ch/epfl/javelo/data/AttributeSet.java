package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * AttributeSet
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record AttributeSet(long bits) {

    /**
     * Compact constructor of AttributeSet
     *
     * @throws IllegalArgumentException if bits is negative or if its greater or equal to 2 to the power 63
     */
    public AttributeSet {
        Preconditions.checkArgument(bits >= 0 && bits < (1L << Attribute.COUNT));
    }

    /**
     * Create an AttributeSet with the Attribute
     *
     * @param attributes ellipse of Attribute
     * @return a new AttributeSet with attribute bits containing all the attributes
     */
    public static AttributeSet of(Attribute... attributes) {
        long bitsAttribute = 0L;

        for (Attribute attribute : attributes) {
            long mask = 1L << attribute.ordinal();
            bitsAttribute |= mask;
        }

        return new AttributeSet(bitsAttribute);
    }

    /**
     * If an Attribute is in an AttributeSet
     *
     * @param attribute attribute
     * @return true if attribute is in the AttributeSet
     */
    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();

        return (bits & mask) == mask;
    }

    /**
     * Intersection between two AttributeSet
     *
     * @param that other AttributeSet
     * @return true if both AttributeSet have a common Attribute
     */
    public boolean intersects(AttributeSet that) {
        return (bits & that.bits) != 0;
    }

    /**
     * Redefinition of method toString() for AttributeSet
     *
     * @return a new String containing all the Attribute in the AttributeSet this
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");

        for (Attribute a : Attribute.ALL) {
            if (this.contains(a)){
                j.add(a.toString());
            }
        }

        return j.toString();
    }
}
