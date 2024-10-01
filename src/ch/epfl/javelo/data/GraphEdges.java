package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;


/**
 * GraphEdges
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int OFFSET_LENGTH = 4;
    private static final int OFFSET_SLOPE = OFFSET_LENGTH + 2;
    private static final int OFFSET_ATTRIBUTES = OFFSET_SLOPE + 2;
    private static final int EDGES_INTS = 10;


    /**
     * Inverted statue
     *
     * @param edgeId identity of the node in the Graph
     * @return true if inverted, false otherwise
     */
    public boolean isInverted(int edgeId) {
        int data = edgesBuffer.get(edgeId * EDGES_INTS); //sens + ID of the node destination

        return data < 0;
    }

    /**
     * Destination node
     *
     * @param edgeId identity of the node in the Graph
     * @return identity of the destination node
     */
    public int targetNodeId(int edgeId) {
        int data = edgesBuffer.getInt(edgeId * EDGES_INTS); //sens + ID of the node destination

        return data < 0 ? ~data : data;
    }

    /**
     * Length of the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return length of the edge
     */
    public double length(int edgeId) {
        short s = edgesBuffer.getShort(edgeId*EDGES_INTS + OFFSET_LENGTH);

        return Q28_4.asDouble(Short.toUnsignedInt(s));
    }

    /**
     * Elevation gain
     *
     * @param edgeId identity of the node in the Graph
     * @return total gain of elevation along the edge
     */
    public double elevationGain(int edgeId) {
        short s = edgesBuffer.getShort(edgeId*EDGES_INTS + OFFSET_SLOPE);

        return Q28_4.asDouble(Short.toUnsignedInt(s));
    }

    /**
     * Profile status
     *
     * @param edgeId identity of the node in the Graph
     * @return true if the edge has a profile, false otherwise
     */
    public boolean hasProfile(int edgeId) {
        int s = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);

        return s > 0;
    }

    /**
     * Elevation along the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return list containing the elevation of each profile on the edge
     */
    public float[] profileSamples(int edgeId) {
        int typeProfile = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);

        if (!hasProfile(edgeId)) {
            return new float[0];
        } else {
            int firstProfile = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);

            short edgeLength = edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_LENGTH);
            int length = Short.toUnsignedInt(edgeLength);
            int sampleNb = 1 + Math2.ceilDiv(length, Q28_4.ofInt(2));

            float[] samples = new float[sampleNb];
            samples[0] = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(firstProfile), 0, 16));  //remplit la premiere valeur.

            int compression = (int) Math.pow(2, typeProfile - 1);   //longueur des donnee.
            int size = (int) Math.pow(2, 5 - typeProfile);  //nombre de donnee dans 1 int.
            int count = 0;
            int pos = 1;

            for (int i = 1; i < sampleNb; i++) {

                if (typeProfile != 1) {
                    int s23 = Bits.extractSigned(elevations.get(firstProfile + i - count), 16 - (pos * 16 / compression), size);
                    samples[i] = samples[i - 1] + Q28_4.asFloat(s23);
                } else {
                    samples[i] = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(firstProfile + i), 0, size));
                }

                if (pos != compression) {
                    ++count;
                    ++pos;
                } else {
                    pos = 1;
                }
            }

            return (isInverted(edgeId)) ? reverse(samples) : samples;
        }
    }

    private float[] reverse(float[] f){
        float temp;
        for (int i = 0; i < f.length/2; i++) {
            temp = f[i];
            f[i] = f[f.length - 1 - i];
            f[f.length - 1 -i] = temp;
        }

        return f;
    }

    /**
     * AttributeSet of the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return identity of the AttributeSet linked to the edge
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId*EDGES_INTS + OFFSET_ATTRIBUTES));
    }
}
