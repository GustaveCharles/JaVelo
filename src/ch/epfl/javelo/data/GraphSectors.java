package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

public record GraphSectors(ByteBuffer buffer) {

    //Cas limite :
    // que doit on renvoyer lorsque qu'on a le dernier secteur et que l'on doit renvoyer l'id du premier noeud du secteur d'après
    // => apparement c'est pas important on renvoie quand même le dernier noeud + 1

    private static final int OFFSET_SHORT = Short.BYTES; //=2
    private static final int OFFSET_INT = Integer.BYTES; //=4

    public List<Sector> sectorsInArea(PointCh center, double distance){
        double xCenter = center.e();
        double yCenter = center.n();

        double sectorLength = SwissBounds.HEIGHT;
        double sectorWidth = SwissBounds.WIDTH;

        int leftBottomX = Math2.clamp(0, (int) ((xCenter - distance)/sectorLength), 127);
        int leftBottomY = Math2.clamp(0, (int) ((yCenter - distance)/sectorWidth),127);

        int rightTopX = Math2.clamp(0, (int) ((xCenter + distance)/sectorLength), 127);
        int rightTopY = Math2.clamp(0, (int) ((yCenter + distance)/sectorWidth), 127);

        ArrayList<Sector> sectors = new ArrayList<>();

        for (int i=leftBottomX; i<=rightTopX; i++){
            for (int j=leftBottomY; j<=rightTopY; j++){
                int sectorIndex = (int) sectorLength * j + i;

                int firstNodeId = buffer().getInt(sectorIndex);
                int numberOfNodes = Short.toUnsignedInt(buffer().getShort(sectorIndex+OFFSET_INT));
                int endNodeId = buffer().getInt(sectorIndex+numberOfNodes);

                Sector s = new Sector(firstNodeId, endNodeId);
                sectors.add(s);
            }
        }
        return sectors;
    }

    public record Sector(int startNodeId, int endNodeId){}

}
