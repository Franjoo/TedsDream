package com.angrynerds.gameobjects.map;

import com.angrynerds.ai.pathfinding.AStarPathFinder;
import com.angrynerds.ai.pathfinding.ClosestHeuristic;
import com.angrynerds.game.Layer;
import com.angrynerds.game.World;
import com.angrynerds.game.collision.Detector;
import com.angrynerds.game.screens.play.PlayScreen;
import com.angrynerds.gameobjects.Enemy;
import com.angrynerds.gameobjects.Item;
import com.angrynerds.gameobjects.Player;
import com.angrynerds.gameobjects.TmxMapObject;
import com.angrynerds.util.C;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private static final int HORIZONTAL_FLIP = 0;
    private static final int VERTICAL_FLIP = 1;
    private static final int BOTH_FLIP = 2;

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
    private Texture dreamOver;

    // map relevant attributes
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthogonalTiledMapRenderer fixedRenderer;
    private int[] renderLayers;
    private OrthographicCamera camera;
    private OrthographicCamera fixedCamera;
    private Player player;

    private SpawnController spawnController;
    private Array<Enemy> enemies;

    // item list
    private Array<Item> items;

    // map properties
    private int numTilesX;
    private int numTilesY;
    private int tileWidth;
    private int tileHeight;
    private int mapWidth;
    private int mapHeight;
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

    // loading screen
    private Texture texture_loading;

    // tmx map path
    private String mapPath = "maps/map_05.tmx";

    // global helper variables
    private Array<Rectangle> qArray = new Array<Rectangle>();

    // background music
    private Sound sound_background;

    // dead enemies
    private int deadEnemies;


    /**
     * creates a new Map
     *
     * @param world
     * @param player
     */
    public Map(World world, Player player) {
        this.player = player;

        camera = world.getCamera();
        SpriteBatch batch = new SpriteBatch();
        // loading texture
        texture_loading = new Texture(Gdx.files.internal("ui/menus/main/loadingScreen.png"));

        // draw loading screen
        batch.begin();
        batch.draw(texture_loading,0,0);
        batch.end();


        init();

        instance = this;

        player.init();
        AStarPathFinder.initialize(this, 200, true, new ClosestHeuristic());
        pathFinder = AStarPathFinder.getInstance();
//        enemy = new Enemy("Spinne", "data/spine/animations/", null, player, 0.1f);
//        enemy.init();

        spawnController = new SpawnController(this);

        // creation methods
        createEnemies();


    }

    private void createEnemies() {
        enemies = new Array<Enemy>();
        MapLayers l = map.getLayers();
        for (int i = 0; i < l.getCount(); i++) {
            // contains objects
            if (l.get(i).getObjects().getCount() != 0) {
                MapObjects objects = l.get(i).getObjects();

                for (int j = 0; j < objects.getCount(); j++) {
                    MapProperties p = objects.get(j).getProperties();

                    if (p.get("type").toString().equals("spawn")) {
                        System.out.println("add enemy");
                        spawnController.add(objects.get(j));
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

        // load tmx
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, PlayScreen.getBatch());
        renderer.setView(camera);

        dreamOver = new Texture("ui/ingame/dreamover.png");

        // create texture atlas
//        atlas = new TextureAtlas("data/maps/map_02.txt");
//        regionMap = new HashMap<String, TextureRegion>();

        // set Texture Filter
//        for (Texture t : atlas.getTextures()) {
//            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//        }

        // fill region Map
//        for (int i = 0; i < atlas.getRegions().size; i++) {
//            regionMap.put(atlas.getRegions().get(i).name, atlas.getRegions().get(i));
//            // System.out.println("putted: " + atlas.getRegions().get(i).name);
//        }

        // parse map properties
        numTilesX = Integer.parseInt(map.getProperties().get("width").toString());
        numTilesY = Integer.parseInt(map.getProperties().get("height").toString());
        tileWidth = Integer.parseInt(map.getProperties().get("tilewidth").toString());
        tileHeight = Integer.parseInt(map.getProperties().get("tileheight").toString());
//        offsetX = Integer.parseInt(map.getProperties().get("OffsetX").toString());
//        offsetY = Integer.parseInt(map.getProperties().get("OffsetY").toString());
        mapWidth = numTilesX * tileWidth;
        mapHeight = numTilesY * tileHeight;

        // position and size
        x = 0;
        y = 0;
        width = mapWidth;
        height = mapHeight;

        System.out.println("w/h: " + mapWidth + " " + mapHeight);

        // fixed camera & renderer
        fixedCamera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        fixedRenderer = new OrthogonalTiledMapRenderer(map, 1, PlayScreen.getBatch());
        fixedRenderer.setView(fixedCamera);


        // fill collision relevant lists
        createMapLayers();
        collisionTileLayers = createCollisionTileLayers();
        collisionObjects = createCollisionObjects();
        mapObjects = createMapObjects();

        // item list
        items = new Array<Item>();

        // set render layers
        setRenderLayers();

        // set player relevant attributes
        spawn = findSpawn();

        // initialize Collision Detector
        Detector.initialize(map);
        detector = Detector.getInstance();

        // draw tile grid
        if (SHOW_TILE_GRID) drawTileGrid();

        // draw collision shapes
        if (SHOW_COLLISION_SHAPES) drawCollisionShapes();

        // draw collision tiles
        if (SHOW_COLLISION_TILES) drawCollisionTiles();


        // sound
        sound_background = Gdx.audio.newSound(Gdx.files.internal("sounds/ingame/game_background.mp3"));
        sound_background.loop();

    }

    private void createMapLayers() {

        layers_foreground = new Array<Layer>();
        layers_background = new Array<Layer>();

        for (int i = 0; i < map.getLayers().getCount(); i++) {

            // if is tileLayer
            if (map.getLayers().get(i).getObjects().getCount() == 0) {
                MapProperties ps = map.getLayers().get(i).getProperties();

                if (map.getLayers().get(i).getName().startsWith("bg") ||
                        map.getLayers().get(i).getName().startsWith("fg")) {

                    // parse layer properties
                    float x = Float.parseFloat(ps.get("x").toString());
                    float y = Float.parseFloat(ps.get("y").toString());
                    float vX = Float.parseFloat(ps.get("vx").toString());
                    float vY = Float.parseFloat(ps.get("vy").toString());
                    boolean moveable = ps.containsKey("mx") && ps.containsKey("my");

                    TiledMapTileLayer tl = (TiledMapTileLayer) map.getLayers().get(i);
                    System.out.println(tl.getName() + "  " + "x: " + x + "y: " + y);

                    // create layer
                    Layer layer;
                    if (!moveable) {
                        layer = new Layer(x, y, vX, vY, tl);
                    } else {
                        float mX = Float.parseFloat(ps.get("mx").toString());
                        float mY = Float.parseFloat(ps.get("my").toString());
                        layer = new Layer(x, y, vX, vY, tl, mX, mY);
                    }


                    // background layer
                    if (tl.getName().startsWith("bg")) {
                        layers_background.add(layer);
                    }

                    // foreground layer
                    else if (tl.getName().startsWith("fg")) {
                        layers_foreground.add(layer);
                    }
                }

            }
        }

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

        // set camera
        renderer.getSpriteBatch().setProjectionMatrix(camera.combined);

        // enemies in background
        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            if (player.getY() <= e.getY()) {
                e.render(batch);
            }
        }

        // render items
        for (int i = 0; i < items.size; i++) {
            Item item =items.get(i);
            if (player.getY() <= item.getY()) {
                item.render(batch);
            }
        }

        // player (middleground)
        player.render(batch);

        // enemies in foreground
        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            if (player.getY() > e.getY()) {
                e.render(batch);
            }
        }

        // render items
        for (int i = 0; i < items.size; i++) {
            Item item =items.get(i);
            if (player.getY() > item.getY()) {
                item.render(batch);
            }
        }

//        for(Item i: items) if(i != null) i.render(batch);

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

        if(player.getActualHP() <= 0){
            batch.draw(dreamOver,fixedCamera.position.x-dreamOver.getWidth()/2,fixedCamera.position.y-dreamOver.getHeight()/2);
        }

        batch.end();

    }

    private void renderForeground(SpriteBatch batch) {
        fixedRenderer.getSpriteBatch().setProjectionMatrix(fixedCamera.combined);
        fixedCamera.position.y = camera.position.y;
        for (int i = 0; i < layers_foreground.size; i++) {

            Layer l = layers_foreground.get(i);
            fixedCamera.position.x = camera.position.x * l.getvX() + l.getX() + C.VIEWPORT_WIDTH / 2;
            fixedCamera.position.y = camera.position.y * l.getvY() + l.getY();

            fixedCamera.update();

            batch.begin();
            fixedRenderer.setView(fixedCamera);
            fixedRenderer.renderTileLayer(layers_foreground.get(i).getTiledMapTileLayer());
            batch.end();
        }
    }

    private void renderBackground(SpriteBatch batch) {
        fixedRenderer.getSpriteBatch().setProjectionMatrix(fixedCamera.combined);
        fixedCamera.position.y = camera.position.y;
        for (int i = 0; i < layers_background.size; i++) {


            Layer l = layers_background.get(i);

            fixedCamera.position.x = camera.position.x * l.getvX() + l.getX() + C.VIEWPORT_WIDTH / 2;
            fixedCamera.position.y = camera.position.y * l.getvY() + l.getY();

            fixedCamera.update();

            batch.begin();
            fixedRenderer.setView(fixedCamera);
            fixedRenderer.renderTileLayer(layers_background.get(i).getTiledMapTileLayer());
            batch.end();
        }
    }

    /**
     * updates the map
     *
     * @param deltaTime time since last frame
     */
    public void update(float deltaTime) {

        // update layers
        for (Layer l : layers_background) l.update(deltaTime);
        for (Layer l : layers_foreground) l.update(deltaTime);

        // update player
        player.update(deltaTime);

        // update items
        for(Item i : items) if(i != null) i.update(deltaTime);

        // update spawnController
        spawnController.update(deltaTime);

        // update enemies
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).update(deltaTime);
        }


        renderer.setView(camera);
        fixedRenderer.setView(fixedCamera);
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
            if (collisionObjects.get(i).contains(x, y)) {
                qArray.add(collisionObjects.get(i));
            }
        }
        return qArray;
    }

    public float getXmin(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float min = Float.MAX_VALUE;
        for (Rectangle r : rectangles) {
            if (r.getX() < min) min = r.getX();
        }
        return min;
    }

    public void putItem(Item item){
        System.out.println("item puttted");
        items.add(item);
    }

    public float getYmin(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float min = Float.MAX_VALUE;
        for (Rectangle r : rectangles) {
            if (r.getY() < min) min = r.getY();
        }
        return min;
    }

    public float getXmax(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float max = Float.MIN_VALUE;
        for (Rectangle r : rectangles) {
            if (r.getX() + r.getWidth() > max) max = r.getX() + r.getWidth();
        }
        return max;
    }

    public float getYmax(Array<Rectangle> rectangles) {
        if (rectangles.size == 0) throw new IllegalArgumentException("size of rectangle array must not be 0");

        float max = Float.MIN_VALUE;
        for (Rectangle r : rectangles) {
            if (r.getY() + r.getHeight() > max) max = r.getY() + r.getHeight();
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
    private TiledMapTileLayer flipTiledMapTileLayer(final TiledMapTileLayer layer, final int flipKind) {
        TiledMapTileLayer copy = new TiledMapTileLayer(layer.getWidth(), layer.getHeight(), (int) (layer.getTileWidth()), (int) (layer.getTileHeight()));
        switch (flipKind) {
            case HORIZONTAL_FLIP:
                for (int h = 0; h < layer.getHeight(); h++) {
                    for (int w = 0; w < layer.getWidth(); w++) {
                        copy.setCell(w, layer.getHeight() - h - 1, layer.getCell(w, h));
                    }
                }
                break;
            case VERTICAL_FLIP:
                for (int h = 0; h < layer.getHeight(); h++) {
                    for (int w = 0; w < layer.getWidth(); w++) {
                        copy.setCell(layer.getWidth() - w - 1, h, layer.getCell(w, h));
                    }
                }
                break;
            case BOTH_FLIP:
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
            if (layers.get(i).getName().startsWith("$s") && layers.get(i).getObjects().getCount() == 0) {

                TiledMapTileLayer l = (TiledMapTileLayer) layers.get(i);

                for (int j = 0; j < l.getHeight(); j++) {
                    for (int k = 0; k < l.getWidth(); k++) {
                        if (l.getCell(k, j) != null && l.getCell(k, j).getTile().getProperties().containsKey("spawn")) {
                            System.out.println("SPAWN FOUND");
                            float qX = k * tileWidth;
                            float qY = j * tileHeight;
                            return new Vector2(qX, qY);
                        }
                    }
                }
            }
        }
        return null;

    }

    /**
     * returns an array of TmxMapObject which are located on a tmx object layer<br>
     * note: tmx map object layer starts with $mapObject
     */
    private Array<TmxMapObject> createMapObjects() {
        Array<TmxMapObject> mo = new Array<TmxMapObject>();

        MapLayers layers = map.getLayers();

        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i).getName().startsWith("$o") && layers.get(i).getObjects().getCount() != 0) {

                Array<HashMap<String, String>> objects = getObjectGroups(layers.get(i));
                for (int j = 0; j < objects.size; j++) {

                    int qX = Integer.parseInt(objects.get(j).get("x"));
                    int qY = Integer.parseInt(objects.get(j).get("y"));
                    int qW = Integer.parseInt(objects.get(j).get("width"));
                    int qH = Integer.parseInt(objects.get(j).get("height"));
                    String qT = objects.get(j).get("type");

                    TmxMapObject object = new TmxMapObject(atlas.findRegion(qT), qX, mapHeight - qH - qY, qW, qH);
                    mo.add(object);
                }

                mo.sort(new Comparator<TmxMapObject>() {
                    @Override
                    public int compare(TmxMapObject o1, TmxMapObject o2) {
                        if (o1.getY() < o2.getY()) return 1;
                        else if (o1.getY() > o2.getY()) return -1;
                        else return 0;
                    }
                });

            }
        }
        return mo;
    }

    /**
     * returns an array of the collision objects located on the tmx layers<br>
     * note: the tiled map layer has to be an object layer and must start with $c
     */
    private Array<Rectangle> createCollisionObjects() {
        Array<Rectangle> co = new Array<Rectangle>();

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

                        co.add(new Rectangle(qX, mapHeight - qY - qH, qW, qH));
                    }
                }
            }
        }
        return co;
    }

    /**
     * returns a collision tiled map tile layer that contains tiles which
     * represents collision objects.<br>
     * <p/>
     * note: the name of a collision tiled map tile layer starts with $c and must not contain a tmx object
     */
    private Array<TiledMapTileLayer> createCollisionTileLayers() {
        Array<TiledMapTileLayer> ctl = new Array<TiledMapTileLayer>();

        MapLayers layers = map.getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i).getName().startsWith("$c") && layers.get(i).getObjects().getCount() == 0) {
                ctl.add((TiledMapTileLayer) layers.get(i));
            }
        }
        return ctl;
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
    private void drawCollisionTiles() {
        Pixmap p = new Pixmap(mapWidth / 100, mapHeight, Pixmap.Format.RGBA8888);
        Color color_outline = new Color(0, 0, 0, 1);
        Color color_fill = new Color(1, 0, 0, 0.3f);

       /*draws the collision tiles */
        for (int j = 0; j < collisionTileLayers.size; j++) {
            for (int h = 0; h < numTilesY; h++) {
                for (int w = 0; w < numTilesX / 100; w++) {
                    TiledMapTileLayer layer = (collisionTileLayers.get(j));
                    TiledMapTileLayer.Cell cell = layer.getCell(w, h);
                    if (cell != null) {
                        Rectangle r = flipY(new Rectangle(w * tileWidth, h * tileHeight, tileWidth, tileHeight));
                        p.setColor(color_outline);
                        p.drawRectangle((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
                        p.setColor(color_fill);
                        p.fillRectangle((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
                    }
                }
            }
        }
        //System.out.println(p.toString());
        collisionTilesTexture = new Texture(p);
    }

    /**
     * draws the collision shapes of the tmx tiled map which are located on a collision object layer
     */
    private void drawCollisionShapes() {
        Pixmap p = new Pixmap(mapWidth, mapHeight, Pixmap.Format.RGBA8888);
        Color color_outline = new Color(0, 0, 0, 1);
        Color color_fill = new Color(1, 0, 0, 0.3f);

        /* draws the collision rectangles */
        for (int i = 0; i < collisionObjects.size; i++) {
            Rectangle r = flipY(new Rectangle(collisionObjects.get(i)));
            p.setColor(color_outline);
            p.drawRectangle((int) (r.getX()), (int) (r.getY()), (int) (r.getWidth()), (int) (r.getHeight()));
            p.setColor(color_fill);
            p.fillRectangle((int) (r.getX()), (int) (r.getY()), (int) (r.getWidth()), (int) (r.getHeight()));
        }
        collisionShapesTexture = new Texture(p);
    }

    //*** GETTER METHODS ****//
    //<editor-fold desc="getter methods">


    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public boolean blockedByEnemie(int x, int y) {
        for (int i = 0; i < enemies.size; i++) {
            if (x == enemies.get(i).getTilePostionX() && y == enemies.get(i).getTilePostionY())
                return true;
            else
                return false;
        }
        return false;
    }


    /**
     * returns x position
     */
    public float getX() {
        return x;
    }

    /**
     * returns y position
     */
    public float getY() {
        return y;
    }

    /**
     * returns width
     */
    public float getWidth() {
        return width;
    }

    /**
     * returns height
     */
    public float getHeight() {
        return height;
    }

    /**
     * returns the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * returns the map spawn
     */
    public Vector2 getSpawn() {
        return spawn;
    }

    /**
     * returns number of tiles in x direction
     */
    public int getNumTilesX() {
        return numTilesX;
    }

    /**
     * returns number of tiles in y direction
     */
    public int getNumTilesY() {
        return numTilesY;
    }

    /**
     * returns tile width
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * returns tile height
     */
    public int getTileHeight() {
        return tileHeight;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Array<Item> getItems() {
        return items;
    }

    public int getMaxEnemies(){
        return spawnController.getMaxEnemies();
    }

    public int getDeadEnemies() {
        return deadEnemies;
    }

    public void removeFromMap(Enemy e) {
        enemies.removeValue(e, true);
        deadEnemies++;
    }
}
