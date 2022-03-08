package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

import static ch.epfl.javelo.projection.SwissBounds.*;

public record GraphSectors(ByteBuffer buffer) {

    //Cas limite :
    // que doit on renvoyer lorsque qu'on a le dernier secteur et que l'on doit renvoyer l'id du premier noeud du secteur d'après
    // => apparement c'est pas important on renvoie quand même le dernier noeud + 1

    private static final int OFFSET_SHORT = Short.BYTES; //=2
    private static final int OFFSET_INT = Integer.BYTES; //=4
    public final static int OFFSET_ID = 0;
    public final static int NUMBER_OF_SECTORS_PER_AXIS = 128;
    public final static int sectorHeight = 1730;
    public final static int sectorWidth = 2730;

    public List<Sector> sectorsInArea(PointCh center, double distance){
        double xCenter = center.e();
        double yCenter = center.n();

        /*int leftBottomX = Math2.clamp(0, (int) ((xCenter - distance)/sectorWidth), 127);
        int leftBottomY = Math2.clamp(0, (int) ((yCenter - distance)/sectorHeight),127);

        int rightTopX = Math2.clamp(0, (int) ((xCenter + distance)/sectorWidth), 127);
        int rightTopY = Math2.clamp(0, (int) ((yCenter + distance)/sectorHeight), 127);
         */

        int leftBottomX = (int) Math2.clamp(MIN_E, (int) (xCenter - distance), MAX_E);
        int leftBottomY = (int) Math2.clamp(MIN_N, (int) (yCenter - distance), MAX_N);

        int rightTopX = (int) Math2.clamp(MIN_E, (int) (xCenter + distance), MAX_E);
        int rightTopY = (int) Math2.clamp(MIN_E, (int) (yCenter + distance), MAX_E);

        int minX = (int) ((leftBottomX - MIN_E)/sectorWidth);
        int minY = (int) ((leftBottomY - MIN_N)/sectorHeight);
        int maxX = (int) ((rightTopX - MAX_E)/sectorWidth);
        int maxY = (int) ((rightTopY - MAX_N)/sectorHeight);

        ArrayList<Sector> sectors = new ArrayList<>();

        for (int i=minX; i<=maxX; i++){
            for (int j=minY; j<=maxY; j++){
                int sectorIndex = NUMBER_OF_SECTORS_PER_AXIS * j + i; //donner un nom

                int firstNodeId = buffer.getInt(sectorIndex*(OFFSET_INT+OFFSET_SHORT) + OFFSET_ID);
                int numberOfNodes = Short.toUnsignedInt(buffer.getShort(sectorIndex*(OFFSET_INT+OFFSET_SHORT) +
                        OFFSET_INT + OFFSET_ID));
                int endNodeId = buffer.getInt(sectorIndex+numberOfNodes);

                Sector s = new Sector(firstNodeId, endNodeId);
                sectors.add(s);
            }
        }
        return sectors;
    }

    public record Sector(int startNodeId, int endNodeId){}

}


