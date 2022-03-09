package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import java.nio.ByteBuffer;
import java.util.*;
import static ch.epfl.javelo.Math2.clamp;
import static ch.epfl.javelo.projection.SwissBounds.*;

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_NODE = 0;
    private static final int OFFSET_LENGTH = OFFSET_NODE + Integer.BYTES;
    private static final int SECTORS_INTS = OFFSET_LENGTH + Short.BYTES;
    private final static int NUMBER_OF_SECTORS_PER_AXIS = 128;
    private final static int sectorHeight = (int) HEIGHT/NUMBER_OF_SECTORS_PER_AXIS;
    private final static int sectorWidth = (int) WIDTH/NUMBER_OF_SECTORS_PER_AXIS;


    public List<Sector> sectorsInArea(PointCh center, double distance){

        double leftBottomX = clamp(MIN_E, center.e() - distance, MAX_E);
        double leftBottomY = clamp(MIN_N, center.n() - distance, MAX_N);
        double rightTopX = clamp(MIN_E, center.e() + distance, MAX_E);
        double rightTopY = clamp(MIN_N, center.n() + distance, MAX_N);

        int minX = (int) Math.floor((leftBottomX - MIN_E)/sectorWidth);
        int maxX = (int) Math.ceil(((rightTopX - MIN_E)/sectorWidth) - 1);
        int minY = (int) Math.floor((leftBottomY - MIN_N)/sectorHeight);
        int maxY = (int) Math.ceil(((rightTopY - MIN_N)/sectorHeight) - 1);

        List<Sector> sectors = new ArrayList<>();

        for (int y = minY; y <= maxY; y++){
            for (int x = minX; x <= maxX; x++){
                int sectorIndex = NUMBER_OF_SECTORS_PER_AXIS * y + x;
                int firstNodeId = buffer.getInt(sectorIndex * SECTORS_INTS + OFFSET_NODE);
                int numberOfNodes = Short.toUnsignedInt(buffer.getShort(sectorIndex*SECTORS_INTS+OFFSET_LENGTH));
                int endNodeId = sectorIndex+numberOfNodes;
                sectors.add(new Sector(firstNodeId, endNodeId));
            }
        }
        return sectors;
    }

    public record Sector(int startNodeId, int endNodeId){}
}


