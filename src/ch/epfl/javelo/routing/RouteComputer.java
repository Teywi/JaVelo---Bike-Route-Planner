package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

/**
 * RouteComputer
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class RouteComputer {

    public static final int START_INDEX = 28;
    public static final int LENGTH = 4;
    private static final float NODE_ALREADY_VISITED = Float.NEGATIVE_INFINITY;

    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Constructor of RouteComputer
     *
     * @param graph graph of the area
     * @param costFunction function that multiplies the length of the edge depending on their OSM Attribute
     */
    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Return the best route between two nodes
     *
     * @param startNodeId identity of the starting node of the Route
     * @param endNodeId identity of the destination node of the Route
     * @return the best route between startNodeId and endNodeId based on Dijkstra algorithm, null if none was found
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){
        Preconditions.checkArgument(startNodeId != endNodeId);

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        int closedNode, toEdgeNode, fromNode, outEdgeId, A;
        int currentNode = endNodeId;
        float d, distA;
        int[] predecessors = new int[graph.nodeCount()];
        float[] distances = new float[graph.nodeCount()];

        ArrayList<Edge> edges = new ArrayList<>();
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();

        Arrays.fill(distances, Float.POSITIVE_INFINITY);

        distances[startNodeId] = 0;
        en_exploration.add(new WeightedNode(startNodeId, 0));

        while(!en_exploration.isEmpty()){
            closedNode = en_exploration.remove().nodeId;

            if (distances[closedNode] == NODE_ALREADY_VISITED){ //if the node was already visited
                continue;
            }

            if (closedNode == endNodeId){
                while(currentNode != startNodeId){ //reconstruct the path from the endNode to the startNode
                    fromNode = Bits.extractUnsigned(predecessors[currentNode], 0, START_INDEX);
                    outEdgeId = Bits.extractUnsigned(predecessors[currentNode], START_INDEX, LENGTH);

                    edges.add(Edge.of(graph, graph.nodeOutEdgeId(fromNode, outEdgeId), fromNode, currentNode));
                    currentNode = Bits.extractUnsigned(predecessors[currentNode], 0, START_INDEX);
                }
                Collections.reverse(edges);

                return new SingleRoute(edges);
            }

            //check all the edges going away from the closedNode
            for (int i = 0; i < graph.nodeOutDegree(closedNode); i++) {
                A = graph.nodeOutEdgeId(closedNode, i);
                toEdgeNode = graph.edgeTargetNodeId(A);

                d = (float)(distances[closedNode] + costFunction.costFactor(closedNode, A)*graph.edgeLength(A));

                if (d < distances[toEdgeNode]){
                    distances[toEdgeNode] = d;
                    predecessors[toEdgeNode] = (i << START_INDEX | closedNode);
                    distA = distances[toEdgeNode] + (float)graph.nodePoint(toEdgeNode).distanceTo(graph.nodePoint(endNodeId));
                    en_exploration.add(new WeightedNode(toEdgeNode, distA));
                }
            }
            distances[closedNode] = NODE_ALREADY_VISITED;
        }

        return null; //if no path were found
    }
}
