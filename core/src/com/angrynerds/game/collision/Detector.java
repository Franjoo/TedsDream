package com.angrynerds.game.collision;

import com.angrynerds.gameobjects.creatures.Creature;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;


import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.BoundingBoxAttachment;
import com.esotericsoftware.spine.attachments.RegionAttachment;


/**
 * User: Franjo
 * Date: 11.11.13
 * Time: 15:15
 * Project: TileRunner
 */
public class Detector {
    private static String TAG = Detector.class.getSimpleName();

    private static Detector instance = null;

    // tiled map properties
    public TiledMap tiledMap;
    private int numTilesX;
    private int numTilesY;
    private int tileWidth;
    private int tileHeight;
    private int mapWidth;
    private int mapHeight;

    // map lists
    private Array<TiledMapTileLayer> collisionTileLayers;
//    private Array<Rectangle> collisionObjects;
//    private Array<TmxMapObject> mapObjects;
//    private HashMap<String, TextureRegion> regionMap;


    private Detector(TiledMap tiledMap) {
        this.tiledMap = tiledMap;

        init();

        instance = this;
    }

    private void init() {

        // parse map properties
        numTilesX = Integer.parseInt(tiledMap.getProperties().get("width").toString());
        numTilesY = Integer.parseInt(tiledMap.getProperties().get("height").toString());
        tileWidth = Integer.parseInt(tiledMap.getProperties().get("tilewidth").toString());
        tileHeight = Integer.parseInt(tiledMap.getProperties().get("tileheight").toString());
        mapWidth = numTilesX * tileWidth;
        mapHeight = numTilesY * tileHeight;

        collisionTileLayers = getCollisionTileLayers();


    }

    /**
     * checks whether there is a solid tile at specified position
     *
     * @param x position x
     * @param y position y
     * @return whether point collides with solid tile or not
     */
    public boolean isSolid(final float x, final float y) {

        for (int i = 0; i < collisionTileLayers.size; i++) {
            TiledMapTileLayer.Cell cell = collisionTileLayers.get(i).getCell((int) (x) / tileWidth, (int) (y) / tileHeight);
            if (cell != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns a collision tiled map tile layer that contains tiles which
     * represents collision objects.<br>
     * <p/>
     * note: the name of a collision tiled map tile layer starts with $c and must not contain a tmx object
     */
    private Array<TiledMapTileLayer> getCollisionTileLayers() {
        Array<TiledMapTileLayer> ctl = new Array<TiledMapTileLayer>();

        MapLayers layers = tiledMap.getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i).getName().startsWith("$c") && layers.get(i).getObjects().getCount() == 0) {
                ctl.add((TiledMapTileLayer) layers.get(i));
            }
        }
        return ctl;
    }

    public boolean polygonCollision(Creature c1, Creature c2){


        // Search BoundingBox with name "weapon"

        BoundingBoxAttachment boundingBoxWeapon = null;
        Array<BoundingBoxAttachment> boundingBoxesCreature1 = c1.getSkeletonBounds().getBoundingBoxes();

        for(int i = 0; i < boundingBoxesCreature1.size;i++){

            if(boundingBoxesCreature1.get(i).getName().equals("weapon")){
               boundingBoxWeapon = boundingBoxesCreature1.get(i);

            }
        }

        // Creates floatArray for Polygon Collision Detection


        float[] weaponFloatArrfromBB = new FloatArray(boundingBoxWeapon.getVertices()).toArray();
        float[] creatureTwoPolygon = new FloatArray(c2.getSkeletonBounds().getBoundingBoxes().get(0).getVertices()).toArray();

        Polygon weaponPolygon = new Polygon(weaponFloatArrfromBB);
        Polygon creature2Polygon = new Polygon(creatureTwoPolygon);



        if(Intersector.overlapConvexPolygons(creatureTwoPolygon, weaponFloatArrfromBB,new Intersector.MinimumTranslationVector())){
            for(int i =0; i< creatureTwoPolygon.length;i++)
            System.out.print(creatureTwoPolygon[i] + " ");
            System.out.println("");
            for(int j =0; j< weaponFloatArrfromBB.length;j++)
            System.out.print(weaponFloatArrfromBB[j] + " ");
            System.out.println(" ");

            return true;
        }
        else
           return false;


    }


    //*** SINGLETON ***//
    public static Detector getInstance() {
        if (instance == null) throw new InstantiationError(TAG + " has not been initialized");
        return instance;
    }

    public static void initialize(TiledMap tiledMap) {
        new Detector(tiledMap);
    }

}
