package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Graph
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Constructor of the class Graph, associates the parameters to the attributes of the class
     *
     * @param nodes         instance of GraphNodes containing all the nodes of the Graph
     * @param sectors       instance of GraphSectors containing all the sectors of the Graph
     * @param edges         instance of GraphEdges containing all the edges of the Graph
     * @param attributeSets List of AttributeSet containing all the attributes of the Graph
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * Loads a Graph from a directory
     *
     * @param basePath path to the package in which the files are
     * @return a new instance of Graph with the information in the provided files
     * @throws IOException if usage of the flow after closure
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        Path nodesPath = basePath.resolve("nodes.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path attributeSetsPath = basePath.resolve("attributes.bin");
        Path profileIdsPath = basePath.resolve("profile_ids.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");

        IntBuffer nodesBuffer;
        ByteBuffer sectorsBuffer;
        ByteBuffer edgesBuffer;
        IntBuffer profileIdsBuffer;
        ShortBuffer elevationsBuffer;
        LongBuffer attributeSetsBuffer;
        List<AttributeSet> attributeSetsList = new ArrayList<>();

        try (FileChannel nodesChannel = FileChannel.open(nodesPath); FileChannel sectorsChannel = FileChannel.open(sectorsPath);
             FileChannel edgesChannel = FileChannel.open(edgesPath); FileChannel attributeSetsChannel = FileChannel.open(attributeSetsPath);
             FileChannel profileIdsChannel = FileChannel.open(profileIdsPath); FileChannel elevationsChannel = FileChannel.open(elevationsPath)) {
            nodesBuffer = nodesChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, nodesChannel.size())
                    .asIntBuffer();
            sectorsBuffer = sectorsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, sectorsChannel.size());
            edgesBuffer = edgesChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, edgesChannel.size());
            profileIdsBuffer = profileIdsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, profileIdsChannel.size())
                    .asIntBuffer();
            elevationsBuffer = elevationsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, elevationsChannel.size())
                    .asShortBuffer();
            attributeSetsBuffer = attributeSetsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, attributeSetsChannel.size())
                    .asLongBuffer();
        }

        for (int i = 0; i < attributeSetsBuffer.capacity(); i++) {
            attributeSetsList.add(new AttributeSet(attributeSetsBuffer.get(i)));
        }

        return new Graph(new GraphNodes(nodesBuffer),
                new GraphSectors(sectorsBuffer),
                new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer), attributeSetsList);
    }

    /**
     * Number of nodes
     *
     * @return number of nodes in the Graph
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Coordinates of the node
     *
     * @param nodeId identity of the node in the Graph
     * @return a point of coordinates of the node
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Number of edges from a node
     *
     * @param nodeId identity of the node in the Graph
     * @return number of edges going out of a node
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Return the identity of the edge given by the identity of the node and the number of the edge
     *
     * @param nodeId     identity of the node in the Graph
     * @param edgeIndex  the number of the edge going out of the node
     * @return identity of the edge given by the identity of the node and the number of the edge
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Closest node to a point at a minimum distance
     *
     * @param point           point from which we are looking for the closest node
     * @param searchDistance  distance in which we are looking for a node
     * @return identity the closest node on the Graph from the point, with maximum distance of searchDistance, -1 if none found
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        List<GraphSectors.Sector> sectorsInArea = sectors.sectorsInArea(point, searchDistance);
        int nodeId = -1;
        double distance;
        double searchingDistance = searchDistance*searchDistance;

        for (GraphSectors.Sector sector : sectorsInArea) {
            for (int j = sector.startNodeId(); j < sector.endNodeId(); j++) {

                distance = point.squaredDistanceTo(nodePoint(j));

                if (distance <= searchingDistance) {
                    nodeId = j;
                    searchingDistance = distance;
                }
            }
        }
        return nodeId;
    }

    /**
     * Destination node
     *
     * @param edgeId identity of the node in the Graph
     * @return identity of the destination node on the edge
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Inverted statue of the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return true if the edge is inverted from the OSM edge, false otherwise
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * AttributeSet of Attribute of the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return the set of Attribute linked to the edge given by its identity edgeId
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * Length of the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return the length of the edge given by its identity edgeId
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Elevation gain of the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return the gain of elevation on the edge given by its identity edgeId
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * Profile along the edge
     *
     * @param edgeId identity of the node in the Graph
     * @return function sampled given by its profileSample and length, obtained by the identity of the edge edgeId
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (!edges.hasProfile(edgeId)) {
            return Functions.constant(Double.NaN);
        }

        return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
    }
}

