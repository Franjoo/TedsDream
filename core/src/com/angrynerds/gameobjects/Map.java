package com.angrynerds.gameobjects;

import com.angrynerds.ai.pathfinding.AStarPathFinder;
import com.angrynerds.game.Layer;
import com.angrynerds.game.World;
import com.angrynerds.game.collision.Detector;
import com.angrynerds.game.screens.play.PlayScreen;
import com.angrynerds.util.C;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;

/**
 * represents the Map in which the game is taking place.
 * the Map contains
 */
public class Map {

    public static final String TAG = Map.class.getSimpleName();

    // constants
    private static enum Flip {HORIZONTAL, VERTICAL, BOTH} ;

    // map instance
    private static Map instance;

    // debug controlls
    private static final boolean SHOW_TILE_GRID = false;
    private static final boolean SHOW_COLLISION_SHAPES = false;

    private AStarPathFinder pathFinder;

    private static final boolean SHOW_COLLISION_TILES = false;

    private Texture gridTexture;
    private Texture collisionShapesTexture;
    private Texture collisionTilesTexture;

    // map relevant attributes
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthogonalTiledMapRenderer fixedRenderer;
    private int[] renderLayers;
    private OrthographicCamera camera;
    private OrthographicCamera fixedCamera;
    private Player player;
    private Enemy enemy;

    private Array<Enemy> enemies;

    private World world;

    // map properties
    private int numTilesX;
    private int numTilesY;
    private int tileWidth;
    private int tileHeight;
    private int mapWidth;
    private int mapHeight;
    private int offsetX;
    private int offsetY;
    public final int borderWidth = 5;

    private float x;
    private float y;
    private float width;
    private float height;

    // collision detector
    private Detector detector;

    // player relevant subjects
    private Vector2 spawn;

    // map lists
    private Array<TiledMapTileLayer> collisionTileLayers;
    private Array<Layer> mapLayers;
    private Array<Layer> layers_background;
    private Array<Layer> layers_foreground;
    private Array<Rectangle> collisionObjects;
    private Array<TmxMapObject> mapObjects;
    private HashMap<String, TextureRegion> regionMap;

    // texture atlas
    private TextureAtlas atlas;

    // tmx map path
    private String mapPath = "data/maps/map_04.tmx";

    // global helper variables
    private Array<Rectangle> qArray = new Array<Rectangle>();

    /**
     * creates a new Map
     *
     * @param world
     * @param player
     */
    public Map(World world, Player player) {
        this.player = player;
        this.world = world;

        camera = world.getCamera();

        init();

        instance = this;

        player.init();
       // AStarPathFinder.initialize(map, 200, true, new ClosestHeuristic());
        pathFinder = AStarPathFinder.getInstance();
        enemy = new Enemy("goblins", "data/spine/goblins/", "goblingirl", player, 1);
       // enemy.init();

        // creation methods
        createEnemies();
    }

    private void createEnemies() {
        enemies = new Array<Enemy>();
        MapLayers mapLayers = map.getLayers();
        for (int i = 0; i < mapLayers.getCount(); i++) {
            // contains objects
            if (mapLayers.get(i).getObjects().getCount() != 0) {
                MapObjects objects = mapLayers.get(i).getObjects();
                for (int j = 0; j < objects.getCount(); j++) {
                    MapProperties properties = objects.get(j).getProperties();

                    // goblin spawn
                    if (properties.containsKey("spawn") && properties.get("spawn").equals("goblin")) {

                        System.out.println("goblin spawned");

                        int min = Integer.parseInt((String) properties.get("min"));
                        int max = Integer.parseInt((String) properties.get("max"));

                        int num = (int) (min + Math.random() * (max - min));
                        for (int k = 0; k < num; k++) {
                            float x = Float.parseFloat(properties.get("x").toString());
                            float y = Float.parseFloat(properties.get("y").toString());
                            float w = Float.parseFloat(properties.get("width").toString());
                            float h = Float.parseFloat(properties.get("height").toString());

                          //  Enemy e = new Enemy((float)(x + Math.random() * 180),(float)(y - Math.random() * 192),"goblins", "data/spine/goblins/", "goblingirl", player, 0.4f);
                         //   enemies.add(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns the map instance
     */
    public static Map getInstance() {
        if (instance == null)
            throw new NullPointerException(TAG + " has not been initialized");

        return instance;
    }

    /**
     * initializes the map
     */
    private void init() {
        loadTmx();

        createTextureAtlas();

        setTextureFilter();

        fillRegionMap();

        parseMapProperties();

        setPositionAndSize();

        System.out.println("w/h: " + mapWidth + " " + mapHeight);

        initFixedCameraAndRenderer();

        initCollisionRelevantLists();

        setRenderLayers();

        spawn = findSpawn();

        initCollisionDetector();

        drawDebugStuff();
    }

    private void drawDebugStuff() {
        if (SHOW_TILE_GRID)
            drawTileGrid();

        if (SHOW_COLLISION_SHAPES)
            createCollisionShapes();

        if (SHOW_COLLISION_TILES)
            createCollisionTileShapes();
    }

    private void initCollisionDetector() {
        Detector.initialize(map);
        detector = Detector.getInstance();
    }

    private void initCollisionRelevantLists() {
        createMapLayers();
        collisionTileLayers = createCollisionTileLayers();
        collisionObjects = createCollisionObjects();
        mapObjects = createMapObjects();
    }

    private void initFixedCameraAndRenderer() {
        fixedCamera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        fixedRenderer = new OrthogonalTiledMapRenderer(map, 1, PlayScreen.getBatch());
        fixedRenderer.setView(fixedCamera);
    }

    private void setPositionAndSize() {
        x = 0;
        y = 0;
        width = mapWidth;
        height = mapHeight;
    }

    private void parseMapProperties() {
        numTilesX = Integer.parseInt(map.getProperties().get("width").toString());
        numTilesY = Integer.parseInt(map.getProperties().get("height").toString());
        tileWidth = Integer.parseInt(map.getProperties().get("tilewidth").toString());
        tileHeight = Integer.parseInt(map.getProperties().get("tileheight").toString());
//        offsetX = Integer.parseInt(map.getProperties().get("OffsetX").toString());
//        offsetY = Integer.parseInt(map.getProperties().get("OffsetY").toString());
        offsetX = 0;
        offsetY = 0;
        mapWidth = numTilesX * tileWidth;
        mapHeight = numTilesY * tileHeight;
    }

    private void fillRegionMap() {
        for (int i = 0; i < atlas.getRegions().size; i++) {
            regionMap.put(atlas.getRegions().get(i).name, atlas.getRegions().get(i));
            // System.out.println("putted: " + atlas.getRegions().get(i).name);
        }
    }

    private void setTextureFilter() {
        for (Texture t : atlas.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    private void createTextureAtlas() {
        atlas = new TextureAtlas("data/maps/map_02.txt");
        regionMap = new HashMap<String, TextureRegion>();
    }

    private void loadTmx() {
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, PlayScreen.getBatch());
        renderer.setView(camera);
    }

    private void createMapLayers() {
        layers_foreground = new Array<Layer>();
        layers_background = new Array<Layer>();
        MapLayer mapLayer;

        for (int i = 0; i < map.getLayers().getCount(); i++) {
            mapLayer = map.getLayers().get(i);
            // if is tileLayer
            if (mapLayer.getObjects().getCount() == 0) {
                fillBackgroundOrForegroundLayer(mapLayer);
            }
        }

    }

    private void fillBackgroundOrForegroundLayer(MapLayer mapLayer) {
        String layerName = mapLayer.getName();
        if (layerName.startsWith("bg") || layerName.startsWith("fg")) {
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapLayer;
            System.out.println(tileLayer.getName() + "  " + "x: " + x + "y: " + y);
            MapProperties mapProperties = mapLayer.getProperties();
            Layer layer = createLayer(mapProperties, tileLayer);

            if (tileLayer.getName().startsWith("bg")) {
                layers_background.add(layer);
            }
            else if (tileLayer.getName().startsWith("fg")) {
                layers_foreground.add(layer);
            }
        }
    }

    private Layer createLayer(MapProperties mapProperties, TiledMapTileLayer tileLayer) {
        // parse layer properties
        float x = Float.parseFloat(mapProperties.get("x").toString());
        float y = Float.parseFloat(mapProperties.get("y").toString());
        float velocityX = Float.parseFloat(mapProperties.get("vx").toString());
        float velocityY = Float.parseFloat(mapProperties.get("vy").toString());

        return new Layer(x, y, velocityX, velocityY, tileLayer);
    }

    /**
     * creates an int array which contains the indexes of layers that can be seen
     */
    private void setRenderLayers() {

        Array<Integer> rl = new Array<Integer>();

        // get number of render layers
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);
            if (!layer.getName().startsWith("$c") && !layer.getName().startsWith("$s")) {
                rl.add(i);
                System.out.println(layer.getName() + " : " + i);
            }
        }

        // set render layer integers
        renderLayers = new int[rl.size];
        for (int i = 0; i < rl.size; i++) {
            renderLayers[i] = rl.get(i).intValue();
//            System.out.println("layer: " + rl.get(i).intValue());
        }
    }

    /**
     * flips the y postition of the assigned rectangles
     *
     * @param rects
     * @return
     */
    private Array<Rectangle> flipY(Array<Rectangle> rects) {
        for (int i = 0; i < rects.size; i++) {
            flipY(rects.get(i));
        }
        return rects;
    }

    private Rectangle flipY(Rectangle rectangle) {
        rectangle.setY(mapHeight - rectangle.getHeight() - rectangle.getY());
        return rectangle;
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
            BufferedReader reader = new BufferedReader(new FileReader(mapPath));
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().startsWith("<objectgroup") && line.contains("name=\"" + layername + "\"")) {
                    line = reader.readLine();
                    while (line.trim().startsWith("<object")) {
                        HashMap<String, String> hm = new HashMap<String, String>();
                        objects.add(hm);
                        String[] properties = line.trim().split(" ");
                        for (int i = 1; i < properties.length; i++) {
                            String k = properties[i].split("=")[0];
                            String v = properties[i].split("=")[1].replace("\"", "").trim().replace("/>", "");

                            hm.put(k, v);
                        }
                        line = reader.readLine();
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }

    /**
     * renders all objects which are located on the map
     *
     * @param batch SpriteBatch that is used for rendering
     */
    public void render(SpriteBatch batch) {
        // background
        renderBackground(batch);

        // render player

        // set camera
        renderer.getSpriteBatch().setProjectionMatrix(camera.combined);

        // render map object in which are in foreground
        for (int i = 0; i < mapObjects.size; i++) {
            if (player.getY() > mapObjects.get(i).getY()) {
                mapObjects.get(i).render(batch);
            }
        }
        player.render(batch);

        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).render(batch);
        }

//        enemy.render(batch);
        // render foreground
        renderForeground(batch);

        //** draw debug textures **//
        batch.begin();

        // shows the grid
        if (SHOW_TILE_GRID) batch.draw(gridTexture, 0, 0);

        // shows the collision shapes
        if (SHOW_COLLISION_SHAPES) batch.draw(collisionShapesTexture, 0, 0);

        // shows the collision tiles
        if (SHOW_COLLISION_TILES) batch.draw(collisionTilesTexture, 0, 0);

        batch.end();
    }

    private void renderForeground(SpriteBatch batch) {
        fixedRenderer.getSpriteBatch().setProjectionMatrix(fixedCamera.combined);
        fixedCamera.position.y = camera.position.y;
        for (int i = 0; i < layers_foreground.size; i++) {
            Layer layer = layers_foreground.get(i);
            setFixedCamerePositionAndRenderLayer(batch, layer);
        }
    }

    private void renderBackground(SpriteBatch batch) {
        fixedRenderer.getSpriteBatch().setProjectionMatrix(fixedCamera.combined);
        fixedCamera.position.y = camera.position.y;
        for (int i = 0; i < layers_background.size; i++) {
            Layer layer = layers_background.get(i);
            setFixedCamerePositionAndRenderLayer(batch, layer);
        }
    }

    private void setFixedCamerePositionAndRenderLayer(SpriteBatch batch, Layer layer) {
        setFixedCameraPositionAndUpdate(layer);

        batch.begin();
        fixedRenderer.setView(fixedCamera);
        fixedRenderer.renderTileLayer(layer.getTiledMapTileLayer());
        batch.end();
    }

    private void setFixedCameraPositionAndUpdate(Layer layer) {
        fixedCamera.position.x = camera.position.x * layer.getVelocityX() + layer.getX() + C.VIEWPORT_WIDTH / 2;
        fixedCamera.position.y = camera.position.y * layer.getVelocityY() + layer.getY();
        fixedCamera.update();
    }

    /**
     * updates the map
     *
     * @param deltaTime time since last frame
     */
    public void update(float deltaTime) {
        player.update(deltaTime);

        updateEnemies(deltaTime);

        setRenderViews();
    }

    private void setRenderViews() {
        renderer.setView(camera);
        fixedRenderer.setView(fixedCamera);
    }

    private void updateEnemies(float deltaTime) {
        for (int i = 0; i < enemies.size; i++) {
           enemies.get(i).update(deltaTime);
        }
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
     * returns an array of rectangles which contains the assigned point
     *
     * @param x position x of the point
     * @param y position y of the point
     */
    public Array<Rectangle> getCollisionObjects(final float x, final float y) {
        if (qArray.size != 0) qArray.clear();
        for (int i = 0; i < collisionObjects.size; i++) {
            Rectangle collisionObject = collisionObjects.get(i);
            if (collisionObject.contains(x, y)) {
                qArray.add(collisionObject);
            }
        }
        return qArray;
    }

    public float getXmin(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float min = Float.MAX_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getX() < min) min = rectangle.getX();
        }
        return min;
    }

    public float getYmin(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float min = Float.MAX_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getY() < min) min = rectangle.getY();
        }
        return min;
    }

    public float getXmax(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float max = Float.MIN_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getX() + rectangle.getWidth() > max) max = rectangle.getX() + rectangle.getWidth();
        }
        return max;
    }

    public float getYmax(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float max = Float.MIN_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getY() + rectangle.getHeight() > max) max = rectangle.getY() + rectangle.getHeight();
        }
        return max;
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
     * flips a TiledMapTileLayer depending on the flipKind
     *
     * @param layer    TileMapTileLayer that should be flipped
     * @param flipKind the way the layer should be flipped
     * @return a flipped copy of the layer
     */
    private TiledMapTileLayer flipTiledMapTileLayer(final TiledMapTileLayer layer, Flip flipKind) {
        TiledMapTileLayer copy = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), (int) (layer.getTileWidth()), (int) (layer.getTileHeight()));
        switch (flipKind) {
            case HORIZONTAL:
                for (int h = 0; h < layer.getHeight(); h++) {
                    for (int w = 0; w < layer.getWidth(); w++) {
                        copy.setCell(w, layer.getHeight() - h - 1, layer.getCell(w, h));
                    }
                }
                break;
            case VERTICAL:
                for (int h = 0; h < layer.getHeight(); h++) {
                    for (int w = 0; w < layer.getWidth(); w++) {
                        copy.setCell(layer.getWidth() - w - 1, h, layer.getCell(w, h));
                    }
                }
                break;
            case BOTH:
                for (int h = 0; h < layer.getHeight(); h++) {
                    for (int w = 0; w < layer.getWidth(); w++) {
                        copy.setCell(layer.getWidth() - w - 1, layer.getHeight() - h - 1, layer.getCell(w, h));
                    }
                }
                break;
            default:
                break;
        }
        return copy;
    }

    /**
     * returns the position on the map where the player is spawn
     */
    private Vector2 findSpawn() {
        MapLayers layers = map.getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            MapLayer mapLayer = layers.get(i);
            if (mapLayer.getName().startsWith("$s") && mapLayer.getObjects().getCount() == 0) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapLayer;
                for (int j = 0; j < tileLayer.getHeight(); j++) {
                    for (int k = 0; k < tileLayer.getWidth(); k++) {
                        TiledMapTileLayer.Cell tileCell = tileLayer.getCell(k, j);
                        if (tileCell != null && tileCell.getTile().getProperties().containsKey("spawn")) {
                            Vector2 spawnVector = getSpawnVector(j,k);
                            return spawnVector;
                        }
                    }
                }
            }
        }
        return null;

    }

    private Vector2 getSpawnVector(int tileInY, int tileInX) {
        System.out.println("SPAWN FOUND");
        float qX = tileInX * tileWidth;
        float qY = tileInY * tileHeight;
        Vector2 spawnVector = new Vector2(qX, qY);
        return spawnVector;
    }

    /**
     * returns an array of TmxMapObject which are located on a tmx object layer<br>
     * note: tmx map object layer starts with $o
     */
    private Array<TmxMapObject> createMapObjects() {
        Array<TmxMapObject> mapObjects = new Array<TmxMapObject>();
        createMapObjectsOnMapLayers(mapObjects);
        return mapObjects;
    }

    private void createMapObjectsOnMapLayers(Array<TmxMapObject> mapObjects) {
        MapLayers layers = map.getLayers();

        for (int i = 0; i < layers.getCount(); i++) {
            MapLayer mapLayer = layers.get(i);
            if (mapLayer.getName().startsWith("$o") && mapLayer.getObjects().getCount() != 0) {
                createAndFillMapObjects(mapObjects, mapLayer);
                sortMapObjects(mapObjects);
            }
        }
    }

    private void sortMapObjects(Array<TmxMapObject> mapObjects) {
        mapObjects.sort(new Comparator<TmxMapObject>() {
            @Override
            public int compare(TmxMapObject o1, TmxMapObject o2) {
                if (o1.getY() < o2.getY()) return 1;
                else if (o1.getY() > o2.getY()) return -1;
                else return 0;
            }
        });
    }

    private void createAndFillMapObjects(Array<TmxMapObject> mapObjects, MapLayer mapLayer) {
        Array<HashMap<String, String>> objects = getObjectGroups(mapLayer);
        for (int j = 0; j < objects.size; j++) {

            int qX = Integer.parseInt(objects.get(j).get("x"));
            int qY = Integer.parseInt(objects.get(j).get("y"));
            int qW = Integer.parseInt(objects.get(j).get("width"));
            int qH = Integer.parseInt(objects.get(j).get("height"));
            String qT = objects.get(j).get("type");

            TmxMapObject mapObject = new TmxMapObject(atlas.findRegion(qT), qX, mapHeight - qH - qY, qW, qH);
            mapObjects.add(mapObject);
        }
    }

    /**
     * returns an array of the collision objects located on the tmx layers<br>
     * note: the tiled map layer has to be an object layer and must start with $c
     */
    private Array<Rectangle> createCollisionObjects() {
        Array<Rectangle> collisionObjects = new Array<Rectangle>();
        MapLayers layers = map.getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i).getName().startsWith("$c") && layers.get(i).getObjects().getCount() != 0) {
                Array<HashMap<String, String>> objects = getObjectGroups(layers.get(i));
                for (int j = 0; j < objects.size; j++) {
                    if (objects.get(j).containsKey("width") &&
                            objects.get(j).containsKey("height") &&
                            objects.get(j).containsKey("x") &&
                            objects.get(j).containsKey("y")) {

                        float qX = Float.parseFloat(objects.get(j).get("x").toString());
                        float qY = Float.parseFloat(objects.get(j).get("y").toString());
                        float qW = Float.parseFloat(objects.get(j).get("width").toString());
                        float qH = Float.parseFloat(objects.get(j).get("height").toString());

                        collisionObjects.add(new Rectangle(qX, mapHeight - qY - qH, qW, qH));
                    }
                }
            }
        }
        return collisionObjects;
    }

    /**
     * returns a collision tiled map tile layer that contains tiles which
     * represents collision objects.<br>
     * <p/>
     * note: the name of a collision tiled map tile layer starts with $c and must not contain a tmx object
     */
    private Array<TiledMapTileLayer> createCollisionTileLayers() {
        Array<TiledMapTileLayer> collisionTileLayers = new Array<TiledMapTileLayer>();
        fillCollisionTileLayers(collisionTileLayers);
        return collisionTileLayers;
    }

    private void fillCollisionTileLayers(Array<TiledMapTileLayer> collisionTileLayers) {
        MapLayers layers = map.getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            MapLayer mapLayer = layers.get(i);
            if (mapLayer.getName().startsWith("$c") && mapLayer.getObjects().getCount() == 0) {
                collisionTileLayers.add((TiledMapTileLayer) mapLayer);
            }
        }
    }


    //***** DRAW METHODS *****//

    /**
     * draws the tile grid of the tmx tile map
     */
    private void drawTileGrid() {
        Pixmap pixmap = new Pixmap(mapWidth, mapHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        for (int h = 0; h < mapHeight; h++) {
            for (int w = 0; w < mapWidth; w++) {
                pixmap.drawRectangle(w * tileWidth, h * tileHeight, tileWidth, tileHeight);
            }
        }
        gridTexture = new Texture(pixmap);
    }

    /**
     * draws the collision tiles of the map which are located on a collision tile layer
     */
    private void createCollisionTileShapes() {
        Pixmap pixmap = new Pixmap(mapWidth / 100, mapHeight, Pixmap.Format.RGBA8888);
        Color color_outline = new Color(0, 0, 0, 1);
        Color color_fill = new Color(1, 0, 0, 0.3f);

        drawCollisionTiles(pixmap, color_outline, color_fill);
        System.out.println(pixmap.toString());
        collisionTilesTexture = new Texture(pixmap);
    }

    private void drawCollisionTiles(Pixmap pixmap, Color color_outline, Color color_fill) {
        for (int j = 0; j < collisionTileLayers.size; j++) {
            for (int h = 0; h < numTilesY; h++) {
                for (int w = 0; w < numTilesX / 100; w++) {
                    TiledMapTileLayer layer = (collisionTileLayers.get(j));
                    TiledMapTileLayer.Cell cell = layer.getCell(w, h);
                    if (cell != null) {
                        Rectangle rectangle = flipY(new Rectangle(w * tileWidth, h * tileHeight, tileWidth, tileHeight));
                        drawRectangleOnPixmap(pixmap, color_outline, color_fill, rectangle);
                    }
                }
            }
        }
    }

    /**
     * draws the collision shapes of the tmx tiled map which are located on a collision object layer
     */
    private void createCollisionShapes() {
        Pixmap pixmap = new Pixmap(mapWidth, mapHeight, Pixmap.Format.RGBA8888);
        Color color_outline = new Color(0, 0, 0, 1);
        Color color_fill = new Color(1, 0, 0, 0.3f);

        drawCollisionRectangles(pixmap, color_outline, color_fill);
        collisionShapesTexture = new Texture(pixmap);
    }

    private void drawCollisionRectangles(Pixmap pixmap, Color color_outline, Color color_fill) {
        for (int i = 0; i < collisionObjects.size; i++) {
            Rectangle rectangle = flipY(new Rectangle(collisionObjects.get(i)));
            drawRectangleOnPixmap(pixmap, color_outline, color_fill, rectangle);
        }
    }

    private void drawRectangleOnPixmap(Pixmap pixmap, Color color_outline, Color color_fill, Rectangle rectangle) {
        pixmap.setColor(color_outline);
        pixmap.drawRectangle((int) (rectangle.getX()), (int) (rectangle.getY()), (int) (rectangle.getWidth()), (int) (rectangle.getHeight()));
        pixmap.setColor(color_fill);
        pixmap.fillRectangle((int) (rectangle.getX()), (int) (rectangle.getY()), (int) (rectangle.getWidth()), (int) (rectangle.getHeight()));
    }

    //*** GETTER METHODS ****//
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * returns the map spawn
     */
    public Vector2 getSpawn() {
        return spawn;
    }

    public int getNumTilesX() {
        return numTilesX;
    }

    public int getNumTilesY() {
        return numTilesY;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    //</editor-fold>
}
