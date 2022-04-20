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
 * represents an OSM tile manager *
 *
 * @author Baudoin Coispeau (339364)
 * @author Gustave Charles-Saigne (345945)
 */
public final class TileManager {

    private final LinkedHashMap<TileId, Image> cacheMemory;
    private final Path basePath;
    private final String servername;

    private static final int MAX_CAPACITY = 100;

    public record TileId(int zoomLevel, int xIndex, int yIndex) {

        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, xIndex, yIndex));
        }

        public static boolean isValid(int zoomLevel, int xIndex, int yIndex) {

            int max = 1 << zoomLevel;

            return (xIndex >= 0 && yIndex >= 0 && xIndex <= max && yIndex <= max);
        }
    }

    public TileManager(Path basePath, String serverName) {

        this.cacheMemory = new LinkedHashMap<>(MAX_CAPACITY, 0.75F, true);
        this.basePath = basePath;
        this.servername = serverName;
    }

    public Image imageForTileAt(TileId tile) throws IOException {

        Preconditions.checkArgument(TileId.isValid(tile.zoomLevel, tile.xIndex, tile.yIndex));

        Path fullPath = basePath.resolve("%d".formatted(tile.zoomLevel)).
                resolve("%d".formatted(tile.xIndex))
                .resolve("%d.png".formatted(tile.yIndex));

        Path toxPath = basePath.resolve("%d".formatted(tile.zoomLevel)).
                resolve("%d".formatted(tile.xIndex));

        if (cacheMemory.containsKey(tile)) {
            return cacheMemory.get(tile);
        }

        if (Files.exists(fullPath)) {
            Image imageFromDisk;
            try (FileInputStream channel = new FileInputStream(fullPath.toFile())) { //toString ou toFile?
                imageFromDisk = new Image(channel);
            }
            checkCacheSize(cacheMemory);
            cacheMemory.put(tile, imageFromDisk);
            return cacheMemory.get(tile);
        }

        if (!cacheMemory.containsKey(tile) && !Files.exists(fullPath)) {
            Files.createDirectories(Path.of(String.valueOf(toxPath)));
            URL u = new URL(
                    "https://" + servername + "/%d/%d/%d.png"
                            .formatted(tile.zoomLevel, tile.xIndex, tile.yIndex));
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            try (InputStream i = c.getInputStream(); OutputStream o = new FileOutputStream(fullPath.toFile())) {
                i.transferTo(o);
            }
            Image imageFromDisk;
            try (FileInputStream channel = new FileInputStream(fullPath.toFile())) {
                imageFromDisk = new Image(channel);
            }
            checkCacheSize(cacheMemory);
            cacheMemory.put(tile, imageFromDisk);
            return cacheMemory.get(tile);
        }
        return null;
    }

    private void checkCacheSize(LinkedHashMap<TileId,Image> cacheMemory) {
        if (cacheMemory.size() == MAX_CAPACITY) {
            cacheMemory.remove(cacheMemory.keySet().iterator().next());
        }
    }
}
