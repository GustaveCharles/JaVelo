package ch.epfl.javelo.gui;


import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Represents an OSM tile manager
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public final class TileManager {

    private final LinkedHashMap<TileId, Image> cacheMemory;
    private final Path basePath;
    private final String servername;

    /**
     * Maximum capacity of the LinkedHashMap
     */
    private final static int MAX_CAPACITY = 100;
    /**
     * Ideal load Factor
     */
    private final static float LOAD_FACTOR = 0.75F;
    /**
     * Minimal value for the x and y index of the tile
     */
    private final static int MINIMAL_VALUE_FOR_TILE_INDEX = 0;

    /**
     * represents the identity of an OSM tile
     */
    public record TileId(int zoomLevel, int xIndex, int yIndex) {

        /**
         * @param zoomLevel the zoom level of the tile
         * @param xIndex    the X index of the tile
         * @param yIndex    the Y index of the tile
         * @throws IllegalArgumentException if the tile is not within the limits
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, xIndex, yIndex));
        }

        /**
         * @param zoomLevel the zoom level of the tile
         * @param xIndex    the X index of the tile
         * @param yIndex    the Y index of the tile
         * @return returns true if—and only if—the parameters constitute a valid tile identity.
         */
        public static boolean isValid(int zoomLevel, int xIndex, int yIndex) {

            int max = 1 << zoomLevel;

            return (xIndex >= MINIMAL_VALUE_FOR_TILE_INDEX && yIndex >= MINIMAL_VALUE_FOR_TILE_INDEX
                    && xIndex <= max && yIndex <= max);
        }
    }

    /**
     * Its role is to get the tiles from a tile server and store them
     * in a memory cache and in a disk cache
     *
     * @param basePath   the access path to the directory containing the disk cache, of type Path
     * @param serverName the name of the tile server
     */
    public TileManager(Path basePath, String serverName) {

        this.cacheMemory = new LinkedHashMap<>(MAX_CAPACITY, LOAD_FACTOR, true);
        this.basePath = basePath;
        this.servername = serverName;
    }

    /**
     * The image is first sought in the memory cache, and if found there,
     * is simply returned. Otherwise, it is searched in the disk cache, and if it is there,
     * it is loaded, placed in the memory cache and returned. Otherwise, it is obtained from
     * the tile server, placed in the disk cache, loaded, placed in the memory cache and finally returned.
     *
     * @param tile the identity of a tile
     * @return returns the Image of the tile
     * @throws IOException in the event of an input/output error,
     *                     *                     if one of the expected files does not exist
     */
    public Image imageForTileAt(TileId tile) throws IOException {

        Preconditions.checkArgument(TileId.isValid(tile.zoomLevel, tile.xIndex, tile.yIndex));

        Path fullPath = basePath
                .resolve("%d".formatted(tile.zoomLevel))
                .resolve("%d".formatted(tile.xIndex))
                .resolve("%d.png".formatted(tile.yIndex));

        if (cacheMemory.containsKey(tile)) {
            return cacheMemory.get(tile);
        }

        if (Files.exists(fullPath)) {
            Image imageFromDisk;
            try (FileInputStream channel = new FileInputStream(fullPath.toFile())) {
                imageFromDisk = new Image(channel);
            }
            checkCacheSize();
            cacheMemory.put(tile, imageFromDisk);
            return cacheMemory.get(tile);
        }

        if (!cacheMemory.containsKey(tile) && !Files.exists(fullPath)) {
            Files.createDirectories(fullPath.getParent());
            URL u = new URL(
                    "https://" + servername + "/%d/%d/%d.png"
                            .formatted(tile.zoomLevel, tile.xIndex, tile.yIndex));
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            try (InputStream i = c.getInputStream();
                 OutputStream o = new FileOutputStream(fullPath.toFile())) {
                i.transferTo(o);
            }
            Image imageFromDisk;
            try (FileInputStream channel = new FileInputStream(fullPath.toFile())) {
                imageFromDisk = new Image(channel);
            }
            checkCacheSize();
            cacheMemory.put(tile, imageFromDisk);
            return cacheMemory.get(tile);
        }
        return null;
    }

    private void checkCacheSize() {
        if (cacheMemory.size() == MAX_CAPACITY) {
            cacheMemory.remove(cacheMemory.keySet().iterator().next());
        }
    }
}
