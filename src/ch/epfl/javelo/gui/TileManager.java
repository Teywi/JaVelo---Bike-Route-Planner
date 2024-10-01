package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TileManager is an OSM tile manager.
 * It obtains the tiles from a server and stores them in a cache memory or in a disk memory.
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class TileManager {

    private static final int MAX_CAPACITY = 100;
    private static final float LOAD_FACTOR = 0.75f;
    private final LinkedHashMap<TileId, Image> cacheMemory;
    private final Path diskPath;
    private final String server;

    /**
     * Constructor of the Tile Manager
     *
     * @param diskPath path of the disk memory where the tiles are stored
     * @param server name of the tile server where the tiles are obtained
     */
    public TileManager(Path diskPath, String server){
        this.diskPath = diskPath;
        this.server = server;
        cacheMemory = new LinkedHashMap<>(MAX_CAPACITY, LOAD_FACTOR, true);
    }

    /**
     * Image of a given tile
     *
     * @param tileId identity of the tile
     * @return image of the tile given
     * @throws IOException if the identity of the tile is not valid
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        Preconditions.checkArgument(TileId.isValid(tileId.zoom(), tileId.x(), tileId.y()));

        Path p = diskPath.resolve(String.valueOf(tileId.zoom))
                .resolve(String.valueOf(tileId.y))
                .resolve(tileId.x + ".png");

        if (cacheMemory.containsKey(tileId)){ //case tile in the cache memory
            return cacheMemory.get(tileId);

        }else if (Files.exists(p)){ //case tile in the disk memory
            removeEldestImage();

            cacheMemory.put(tileId, new Image(String.valueOf(p)));

            return cacheMemory.get(tileId);
        }else { //case new tile, so need to download it

            URL u = new URL("https://" + server + "/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            Image png;

            String pathToDirectories = diskPath + "/" + tileId.zoom + "/" + tileId.x;
            Files.createDirectories(Path.of(pathToDirectories));

            try (InputStream i = c.getInputStream();
                OutputStream o = new FileOutputStream(pathToDirectories + "/" + tileId.y + ".png");
                InputStream i2 = new FileInputStream(pathToDirectories + "/" + tileId.y + ".png")) { //need to create a 2nd stream because transferTo() uses the stream

                i.transferTo(o);

                png = new Image(i2);
            }

            removeEldestImage();
            cacheMemory.put(tileId, png);

            return cacheMemory.get(tileId);
        }

    }

    private void removeEldestImage(){
        if (cacheMemory.size() < MAX_CAPACITY){ //if the cache memory is not full, we do not remove the eldest tile
            return;
        }

        Iterator<Map.Entry<TileId, Image>> it = cacheMemory.entrySet().iterator();

        it.next();
        it.remove();
    }

    /**
     * TileId represents each tile
     *
     * @param zoom zoom level of the tile
     * @param x coordinate on the x-axis of the tile
     * @param y coordinate on the y-axis of the tile
     */
    public record TileId(int zoom, int x, int y){

        /**
         * Checks if the given tile is valid or not
         *
         * @param zoom zoom level of the tile
         * @param x coordinate on the x-axis of the tile
         * @param y coordinate on the y-axis of the tile
         * @return true if the tile is valid, false otherwise
         */
        public static boolean isValid(int zoom, int x, int y){
            return zoom >= 0 && x >= 0 && y >= 0;
        }
    }

}