package com.angrynerds.tedsdream.renderer;

import com.angrynerds.tedsdream.gameobjects.TmxMapObject;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by tim on 09.06.2014.
 */
public class CollisionHandler {

    private final TextureAtlas atlas;
    private Array<TiledMapTileLayer> collisionTileLayers;
    private TiledMap tiledMap;
    private Map map;
    private Array<Rectangle> qArray = new Array<Rectangle>();
    private Array<Rectangle> collisionObjects;

    public CollisionHandler(Map map){
        this.map = map;
        this.atlas = map.getAtlas();
        this.tiledMap = map.getTiledMap();
        init();
    }

    private void init(){
        collisionTileLayers = createCollisionTileLayers();
        collisionObjects = createCollisionObjects();
    }

    /**
     * returns a collision tiled map tile layer that contains tiles which
     * represents collision objects.<br>
     * <p/>
     * note: the name of a collision tiled map tile layer starts with $c and must not contain a tmx object
     */
    private Array<TiledMapTileLayer> createCollisionTileLayers() {
        Array<TiledMapTileLayer> tiledMapTileLayers = new Array<TiledMapTileLayer>();

        MapLayers layers = tiledMap.getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            MapLayer mapLayer = layers.get(i);
            if (mapLayer.getName().startsWith("$c") && mapLayer.getObjects().getCount() == 0) {
                tiledMapTileLayers.add((TiledMapTileLayer) layers.get(i));
            }
        }
        return tiledMapTileLayers;
    }
    /**
     * returns an array of the collision objects located on the tmx layers<br>
     * note: the tiled map layer has to be an object layer and must start with $c
     */
    private Array<Rectangle> createCollisionObjects() {
        Array<Rectangle> co = new Array<Rectangle>();

        MapLayers layers = tiledMap.getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            MapLayer mapLayer = layers.get(i);
            if (mapLayer.getName().startsWith("$c") && mapLayer.getObjects().getCount() != 0) {

                Array<HashMap<String, String>> objects = getObjectGroups(mapLayer);

                for (int j = 0; j < objects.size; j++) {
                    HashMap<String, String> object = objects.get(j);
                    if (object.containsKey("width") &&
                            object.containsKey("height") &&
                            object.containsKey("x") &&
                            object.containsKey("y")) {

                        float qX = Float.parseFloat(object.get("x"));
                        float qY = Float.parseFloat(object.get("y"));
                        float qW = Float.parseFloat(object.get("width"));
                        float qH = Float.parseFloat(object.get("height"));

                        co.add(new Rectangle(qX, map.getProperties().mapHeight - qY - qH, qW, qH));
                    }
                }
            }
        }
        return co;
    }

    private void parseObjectGroups(String layername, Array<HashMap<String, String>> objects, BufferedReader reader, String line) throws IOException {
        if (line.trim().startsWith("<objectgroup") && line.contains("name=\"" + layername + "\"")) {
            line = reader.readLine();
            while (line.trim().startsWith("<object")) {
                CreateObjectGroupAndAddIt(objects, line);
                line = reader.readLine();
            }
        }
    }

    private void CreateObjectGroupAndAddIt(Array<HashMap<String, String>> objects, String line) {
        HashMap<String, String> hm = new HashMap<String, String>();
        objects.add(hm);
        String[] properties = line.trim().split(" ");
        for (int i = 1; i < properties.length; i++) {
            String key = properties[i].split("=")[0];
            String value = properties[i].split("=")[1].replace("\"", "").trim().replace("/>", "");

            hm.put(key, value);
        }
    }
    /**
     * returns an array of hashmaps which contains the object properties of tmx map object<br>
     * note: MapLayer m -> m.getObjects.get(n).getProperties() is incorrect
     *
     * @param layer MapLayer which should be parsed
     */
    private Array<HashMap<String, String>> getObjectGroups(final MapLayer layer) {

        final String layername = layer.getName();
        Array<HashMap<String, String>> objects = new Array<HashMap<String, String>>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(map.mapPath));
            String line = reader.readLine();
            while (line != null) {
                parseObjectGroups(layername, objects, reader, line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }

    /**
     * returns an array of TmxMapObject which are located on a tmx object layer<br>
     * note: tmx map object layer starts with $mapObject
     */
    private Array<TmxMapObject> createMapObjects() {
        Array<TmxMapObject> tmxMapObjects = new Array<TmxMapObject>();

        MapLayers layers = tiledMap.getLayers();

        for (int i = 0; i < layers.getCount(); i++) {
            MapLayer layer = layers.get(i);
            if (layer.getName().startsWith("$o") && layer.getObjects().getCount() != 0) {

                Array<HashMap<String, String>> objects = getObjectGroups(layer);
                for (int j = 0; j < objects.size; j++) {
                    HashMap<String, String> object = objects.get(j);
                    int qX = Integer.parseInt(object.get("x"));
                    int qY = Integer.parseInt(object.get("y"));
                    int qW = Integer.parseInt(object.get("width"));
                    int qH = Integer.parseInt(object.get("height"));
                    String qT = object.get("type");

                    TmxMapObject tmxMapObject = new TmxMapObject(atlas.findRegion(qT), qX, (int) map.getProperties().mapHeight - qH - qY, qW, qH);
                    tmxMapObjects.add(tmxMapObject);
                }

                tmxMapObjects.sort(new Comparator<TmxMapObject>() {
                    @Override
                    public int compare(TmxMapObject o1, TmxMapObject o2) {
                        if (o1.getY() < o2.getY()) return 1;
                        else if (o1.getY() > o2.getY()) return -1;
                        else return 0;
                    }
                });

            }
        }
        return tmxMapObjects;
    }


    /**
     * returns an array of rectangles which contains the assigned point
     *
     * @param x position x of the point
     * @param y position y of the point
     */
    public Array<Rectangle> getCollisionObjects(final float x, final float y) {
        if (qArray.size != 0) qArray.clear();
        for (int i = 0; i < collisionObjects.size; i++) {
            if (collisionObjects.get(i).contains(x, y)) {
                qArray.add(collisionObjects.get(i));
            }
        }
        return qArray;
    }
    public Array<Rectangle> getCollisionObjects(final float x1, final float y1, final float x2, final float y2) {
        if (qArray.size != 0) qArray.clear();
        for (int i = 0; i < collisionObjects.size; i++) {
            if (collisionObjects.get(i).contains(x1, y1) || collisionObjects.get(i).contains(x2, y2)) {
                qArray.add(collisionObjects.get(i));
            }
        }
        return qArray;
    }

    public Array<Rectangle> getCollisionObjects(Vector2 p1, Vector2 p2) {
        return getCollisionObjects(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * returns an array of rectangles which contains the assigned point
     *
     * @param position point that should be tested
     */
    public Array<Rectangle> getCollisionObjects(final Vector2 position) {
        return this.getCollisionObjects(position.x, position.y);
    }


    /**
     * checks whether there is a solid tile at specified position
     *
     * @param x position x
     * @param y position y
     * @return whether point collides with solid tile or not
     */
    public boolean isSolid(final float x, final float z) {

        for (int i = 0; i < collisionTileLayers.size; i++) {
            TiledMapTileLayer.Cell cell = collisionTileLayers.get(i).getCell((int) (x) / map.getProperties().tileWidth, (int) (z) / map.getProperties().tileHeight);
            if (cell != null) {
                System.out.println("Colliding");
                return true;
            }
        }
        return false;
    }

    public Array<TiledMapTileLayer> getCollisionTileLayers() {
        return collisionTileLayers;
    }

    public Array<Rectangle> getCollisionObjects() {
        return collisionObjects;
    }

}
