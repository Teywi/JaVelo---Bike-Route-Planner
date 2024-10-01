package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * GraphNodes
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = 3;
    private static final int FIRST_INDEX = 28;
    private static final int LENGTH_EXTRACT = 4;
    /**
     * Count of the nodes
     *
     * @return number of nodes
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * The E coordinate of the node
     *
     * @param nodeId identity of the node in the graph
     * @return E coordinate of the node
     */
    public double nodeE(int nodeId) {
        int i = nodeId * NODE_INTS;

        return Q28_4.asDouble(buffer.get(i + OFFSET_E));
    }

    /**
     * The N coordinate of the node
     *
     * @param nodeId identity of the node in the graph
     * @return N coordinate of the node
     */
    public double nodeN(int nodeId) {
        int i = nodeId * NODE_INTS;

        return Q28_4.asDouble(buffer.get(i + OFFSET_N));
    }

    /**
     * Number of edges leaving the node
     *
     * @param nodeId identity of the node in the graph
     * @return number of edges leaving the node
     */
    public int outDegree(int nodeId) {
        int i = nodeId * NODE_INTS;

        return Bits.extractUnsigned(buffer.get(i + OFFSET_OUT_EDGES), 28, 4);
    }

    /**
     * Identity of the edge
     *
     * @param nodeId    identity of the node in the graph
     * @param edgeIndex i-th edges to leave the node
     * @return identity of the edgeIndex-th leaving the node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int i = nodeId * NODE_INTS;

        return edgeIndex + Bits.extractUnsigned(buffer.get(i + OFFSET_OUT_EDGES), 0, 28);
    }
}

