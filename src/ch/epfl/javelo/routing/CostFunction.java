package ch.epfl.javelo.routing;

/**
 * CostFunction
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public interface CostFunction {

    /**
     * Return the factor, always greater or equal than 1, that multiplies the length of the edgeId going from the nodeId
     *
     * @param nodeId identity of the node
     * @param edgeId identity of the edge
     * @return factor that multiplies the length of the edgeId going from the nodeId depending on its attributes
     */
    double costFactor(int nodeId, int edgeId);
}
