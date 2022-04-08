package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.*;

import static ch.epfl.javelo.Math2.clamp;
import static ch.epfl.javelo.projection.SwissBounds.*;

/**
 * Represents the array containing the 16384 sectors of JaVelo
 *
 * @author Gustave Charles -- Saigne (345945)
 * @author Baudoin Coispeau (339364)
 */

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_NODE = 0;
    private static final int OFFSET_LENGTH = OFFSET_NODE + Integer.BYTES;
    private static final int SECTORS_INTS = OFFSET_LENGTH + Short.BYTES;
    private final static int NUMBER_OF_SECTORS_PER_AXIS = 128;
    private final static double sectorHeight = HEIGHT / NUMBER_OF_SECTORS_PER_AXIS;
    private final static double sectorWidth = WIDTH / NUMBER_OF_SECTORS_PER_AXIS;

    /**
     * computes the list of all sectors having an intersection with the square centered at the given point and with a
     * side equal to twice the given distance.
     *
     * @param center   the given point
     * @param distance the distance between the point and the edge of the square
     * @return returns a list of all sectors contained in the given area
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        double leftBottomX = clamp(MIN_E, center.e() - distance, MAX_E);
        double leftBottomY = clamp(MIN_N, center.n() - distance, MAX_N);
        double rightTopX = clamp(MIN_E, center.e() + distance, MAX_E);
        double rightTopY = clamp(MIN_N, center.n() + distance, MAX_N);

        int minX = (int) ((leftBottomX - MIN_E) / sectorWidth);
        int maxX = (int) Math.ceil(((rightTopX - MIN_E) / sectorWidth) - 1);
        int minY = (int) ((leftBottomY - MIN_N) / sectorHeight);
        int maxY = (int) Math.ceil(((rightTopY - MIN_N) / sectorHeight) - 1);

        List<Sector> sectors = new ArrayList<>();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int sectorIndex = NUMBER_OF_SECTORS_PER_AXIS * y + x;
                int firstNodeId = buffer.getInt(sectorIndex * SECTORS_INTS + OFFSET_NODE);
                int numberOfNodes = Short.toUnsignedInt(buffer.getShort(sectorIndex * SECTORS_INTS + OFFSET_LENGTH));
                int endNodeId = firstNodeId + numberOfNodes;
                sectors.add(new Sector(firstNodeId, endNodeId));
            }
        }
        return sectors;
    }

    /**
     * Sectors are represented by two node indexes in the Sector record.
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}
