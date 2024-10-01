package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * GraphSectors
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int NUMBER_SECTORS_SIDE = 128;
    private static final int OFFSET_LAST_NODE = Integer.BYTES;
    private static final int SECTOR_INTS = Integer.BYTES + Short.BYTES;
    private static final double SECTOR_WIDTH = (SwissBounds.MAX_E - SwissBounds.MIN_E) / 128;
    private static final double SECTOR_HEIGHT = (SwissBounds.MAX_N - SwissBounds.MIN_N) / 128;


    /**
     * Sectors in the area around the center with fixed distance
     *
     * @param center   center of the square
     * @param distance distance in which we are looking for sectors
     * @return list of sectors that intersect the square given by center and distance
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        List<Sector> sectorList = new ArrayList<>();  //2.73km x 1.73km

        double bottomX = Math2.clamp(SwissBounds.MIN_E, center.e() - distance, SwissBounds.MAX_E);
        bottomX -= SwissBounds.MIN_E;

        double bottomY = Math2.clamp(SwissBounds.MIN_N, center.n() - distance, SwissBounds.MAX_N);
        bottomY -= SwissBounds.MIN_N;

        double topX = Math2.clamp(SwissBounds.MIN_E, center.e() + distance, SwissBounds.MAX_E);
        topX -= SwissBounds.MIN_E;

        double topY = Math2.clamp(0, center.n() + distance, SwissBounds.MAX_N);
        topY -= SwissBounds.MIN_N;

        int bottomIndexX = (int) (bottomX / SECTOR_WIDTH);
        bottomIndexX = Math2.clamp(0, bottomIndexX, NUMBER_SECTORS_SIDE - 1);

        int bottomIndexY = (int) (bottomY / SECTOR_HEIGHT);
        bottomIndexY = Math2.clamp(0, bottomIndexY, NUMBER_SECTORS_SIDE - 1);

        int topIndexX = (int) (topX / SECTOR_WIDTH);
        topIndexX = Math2.clamp(0, topIndexX, NUMBER_SECTORS_SIDE - 1);

        int topIndexY = (int) (topY / SECTOR_HEIGHT);
        topIndexY = Math2.clamp(0, topIndexY, NUMBER_SECTORS_SIDE - 1);

        for (int i = bottomIndexY; i <= topIndexY; i++) {
            for (int j = bottomIndexX; j <= topIndexX; j++) {
                sectorList.add(new Sector(buffer.getInt(SECTOR_INTS * (NUMBER_SECTORS_SIDE*i + j)),
                        buffer.getInt(SECTOR_INTS * (NUMBER_SECTORS_SIDE*i + j)) +
                                Short.toUnsignedInt(buffer.getShort(SECTOR_INTS * (128 * i + j) +
                                OFFSET_LAST_NODE))));
            }
        }

        return sectorList;
    }

    /**
     * Nested class which represent a sector
     */
    public record Sector(int startNodeId, int endNodeId) {
    }

}
