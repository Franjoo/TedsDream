package com.angrynerds.tedsdream.gameobjects.map;

import com.angrynerds.tedsdream.ai.pathfinding.AStarPathFinder;
import com.angrynerds.tedsdream.ai.pathfinding.ClosestHeuristic;
import com.angrynerds.tedsdream.Layer;
import com.angrynerds.tedsdream.collision.Detector;
import com.angrynerds.tedsdream.gameobjects.Enemy;
import com.angrynerds.tedsdream.gameobjects.Player;
import com.angrynerds.tedsdream.gameobjects.TmxMapObject;
import com.angrynerds.tedsdream.gameobjects.items.Item;
import com.angrynerds.tedsdream.renderer.ShadowRenderer;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
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

    // DEBUG
    private static final boolean SHOW_TILE_GRID = false;
    private static final boolean SHOW_COLLISION_SHAPES = false;
    private static final boolean SHOW_COLLISION_TILES = false;

    private static enum Flip {HORIZONTAL, VERTICAL, BOTH}

    private Array<Player> players = new Array<Player>();

    // map instance
    private static Map instance;

    private Texture gridTexture;
    private Texture collisionShapesTexture;
    private Texture collisionTilesTexture;
    private Texture dreamOver;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    // map relevant attributes
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthogonalTiledMapRenderer fixedRenderer;
    private int[] renderLayers;
    private OrthographicCamera camera;
    private OrthographicCamera fixedCamera;

    private SpawnController spawnController;
    private Array<Enemy> enemies = new Array<Enemy>();

    private Array<Item> items = new Array<Item>();

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

    // player relevant subjects
    private Vector2 spawn;
    private ShadowRenderer shadowRenderer = new ShadowRenderer();

    // map lists
    private Array<TiledMapTileLayer> collisionTileLayers;
    private Array<Layer> layers_background = new Array<Layer>();
    private Array<Layer> layers_foreground = new Array<Layer>();
    private Array<Rectangle> collisionObjects = new Array<Rectangle>();

    private TextureAtlas atlas;

    // loading screen
    private Texture texture_loading;

    // tmx map path
    private String mapPath = "maps/map_05.tmx";

    // global helper variables
    private Array<Rectangle> qArray = new Array<Rectangle>();

    private Sound sound_background;

    private int deadEnemies;


    /**
     * creates a new Map
     *
     */
    public Map(OrthographicCamera camera) {

        this.camera = camera;
        SpriteBatch batch = new SpriteBatch();
        // loading texture
        texture_loading = new Texture(Gdx.files.internal("ui/menus/main/loadingScreen.png"));

        // draw loading screen
        batch.begin();
        batch.draw(texture_loading,0,0);
        batch.end();

        init();

        instance = this;

        AStarPathFinder.initialize(this, 200, true, new ClosestHeuristic());

        spawnController = new SpawnController(this);

        createEnemies();
    }

    public void addPlayer(Player player){
        player.init();
        players.add(player);
    }

    private void createEnemies() {
        MapLayers mapLayer = map.getLayers();
        for (int i = 0; i < mapLayer.getCount(); i++) {
            // contains objects
            if (mapLayer.get(i).getObjects().getCount() != 0) {
                MapObjects objects = mapLayer.get(i).getObjects();

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
            throw new NullPointerException( "map has not been initialized");

        return instance;
    }

    /**
     * initializes the map
     */
    private void init() {
        // load tmx
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map);
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
        fixedRenderer = new OrthogonalTiledMapRenderer(map);
        fixedRenderer.setView(fixedCamera);

        // fill collision relevant lists
        createMapLayers();
        collisionTileLayers = createCollisionTileLayers();
        collisionObjects = createCollisionObjects();

        setRenderLayers();

        // set player relevant attributes
        spawn = findSpawn();

        // initialize Collision Detector
        Detector.initialize(map);
        Detector detector = Detector.getInstance();

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
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer mapLayer = map.getLayers().get(i);
            // if is tileLayer
            if (mapLayer.getObjects().getCount() == 0) {
                MapProperties mapProperties = mapLayer.getProperties();

                if (mapLayer.getName().startsWith("bg") || mapLayer.getName().startsWith("fg")) {

                    // parse layer properties
                    float x = Float.parseFloat(mapProperties.get("x").toString());
                    float y = Float.parseFloat(mapProperties.get("y").toString());
                    float vX = Float.parseFloat(mapProperties.get("vx").toString());
                    float vY = Float.parseFloat(mapProperties.get("vy").toString());
                    boolean moveable = mapProperties.containsKey("mx") && mapProperties.containsKey("my");

                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapLayer;
                    System.out.println(tileLayer.getName() + "  " + "x: " + x + "y: " + y);

                    // create layer
                    Layer layer;
                    if (!moveable) {
                        layer = new Layer(x, y, vX, vY, tileLayer);
                    } else {
                        float mX = Float.parseFloat(mapProperties.get("mx").toString());
                        float mY = Float.parseFloat(mapProperties.get("my").toString());
                        layer = new Layer(x, y, vX, vY, tileLayer, mX, mY);
                    }


                    // background layer
                    if (tileLayer.getName().startsWith("bg")) {
                        layers_background.add(layer);
                    }

                    // foreground layer
                    else if (tileLayer.getName().startsWith("fg")) {
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

        Array<Integer> renderLayers = new Array<>();

        // get number of render layers
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);
            if (!layer.getName().startsWith("$c") && !layer.getName().startsWith("$s")) {
                renderLayers.add(i);
                System.out.println(layer.getName() + " : " + i);
            }
        }

        // set render layer integers
        this.renderLayers = new int[renderLayers.size];
        for (int i = 0; i < renderLayers.size; i++) {
            this.renderLayers[i] = renderLayers.get(i).intValue();
//            System.out.println("layer: " + renderLayers.get(i).intValue());
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
                parseObjectGroups(layername, objects, reader, line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
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
     * renders all objects which are located on the map
     *
     * @param batch SpriteBatch that is used for rendering
     */

    public void render(SpriteBatch batch) {

        // background
        renderLayers(layers_background);

        // set camera
        renderer.getSpriteBatch().setProjectionMatrix(camera.combined);

        renderShadowsForGameObjects();
        renderGameObjects(batch);

        renderLayers(layers_foreground);

//        renderParticles(batch);

        drawDebugTextures(batch);


//        if(player.getActualHP() <= 0){
//            batch.draw(dreamOver,fixedCamera.position.x-dreamOver.getWidth()/2,fixedCamera.position.y-dreamOver.getHeight()/2);
//        }

    }

    private void drawDebugTextures(SpriteBatch batch) {
        batch.begin();
        if (SHOW_TILE_GRID) batch.draw(gridTexture, 0, 0);

        if (SHOW_COLLISION_SHAPES) batch.draw(collisionShapesTexture, 0, 0);

        if (SHOW_COLLISION_TILES) batch.draw(collisionTilesTexture, 0, 0);
        batch.end();
    }

    private void renderGameObjects(SpriteBatch batch) {
        //        // enemies in background
        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            if (players.get(0).getY() <= e.getY()) {
                e.render(batch);
            }
        }
//
//        // render items
        for (Item item : items) {
            if (players.get(0).getY() <= item.getY()) {
                item.render(batch);
            }
        }

        // player (middleground)
        for(Player p : players){
            p.render(batch);
        }

        // enemies in foreground
        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            if (players.get(0).getY() > e.getY()) {
                e.render(batch);
            }
        }
//
//        // render items
        for (Item item : items) {
            if (players.get(0).getY() > item.getY()) {
                item.render(batch);
            }
        }
    }

    private void renderShadowsForGameObjects() {
        for(Enemy e : enemies) {
            shadowRenderer.renderShadow(shapeRenderer,camera,e);
        }

        for(Item item : items){
            shadowRenderer.renderShadow(shapeRenderer,camera,item);
        }

        for (Player player : players) {
            shadowRenderer.renderShadow(shapeRenderer,camera,player);
        }
    }

    private void renderLayers(Array<Layer> layers){
        fixedRenderer.getSpriteBatch().setProjectionMatrix(fixedCamera.combined);
        fixedCamera.position.y = camera.position.y;
        for (int i = 0; i < layers.size; i++) {
            Layer layer = layers.get(i);
            updateFixedCameraPosition(layer);
            RenderTileLayerByFixedRenderer(layer);
        }
    }

    private void RenderTileLayerByFixedRenderer(Layer layer) {
        fixedRenderer.setView(fixedCamera);
        fixedRenderer.getSpriteBatch().begin();
        fixedRenderer.renderTileLayer(layer.getTiledMapTileLayer());
        fixedRenderer.getSpriteBatch().end();
    }

    private void updateFixedCameraPosition(Layer layer) {
        fixedCamera.position.x = camera.position.x * layer.getVelocityX() + layer.getX() + C.VIEWPORT_WIDTH / 2;
        fixedCamera.position.y = camera.position.y * layer.getVelocityY() + layer.getY();
        fixedCamera.update();
    }

    private void renderShadows(){
        camera.update();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for(Enemy enemy : enemies){
            enemy.renderShadow(shapeRenderer);
        }
           shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * updates the map
     *
     * @param deltaTime time since last frame
     */
    public void update(float deltaTime) {

        // update layers
        for (Layer backgroundLayer : layers_background) backgroundLayer.update(deltaTime);
        for (Layer ForegroundLayer : layers_foreground) ForegroundLayer.update(deltaTime);

        for(Player player :players){
            player.update(deltaTime);
        }

        for(Item item : items) if(item != null) item.update(deltaTime);

        spawnController.update(deltaTime);

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
        throwIllegalArgumentExceptionIfRectanglesEmpty(rectangles);

        float min = Float.MAX_VALUE;
        for (Rectangle r : rectangles) {
            if (r.getX() < min) min = r.getX();
        }
        return min;
    }

    public float getYmin(Array<Rectangle> rectangles) {
        throwIllegalArgumentExceptionIfRectanglesEmpty(rectangles);

        float min = Float.MAX_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getY() < min) min = rectangle.getY();
        }
        return min;
    }

    public float getXmax(Array<Rectangle> rectangles) {
        throwIllegalArgumentExceptionIfRectanglesEmpty(rectangles);

        float max = Float.MIN_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getX() + rectangle.getWidth() > max)
                max = rectangle.getX() + rectangle.getWidth();
        }
        return max;
    }

    public float getYmax(Array<Rectangle> rectangles) {
        throwIllegalArgumentExceptionIfRectanglesEmpty(rectangles);

        float max = Float.MIN_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getY() + rectangle.getHeight() > max)
                max = rectangle.getY() + rectangle.getHeight();
        }
        return max;
    }

    private void throwIllegalArgumentExceptionIfRectanglesEmpty(Array<Rectangle> rectangles) {
        if (rectangles.size == 0)
            throw new IllegalArgumentException("size of rectangle array must not be 0");
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
            if (layers.get(i).getName().startsWith("$s") && layers.get(i).getObjects().getCount() == 0) {

                TiledMapTileLayer layer = (TiledMapTileLayer) layers.get(i);

                for (int j = 0; j < layer.getHeight(); j++) {
                    for (int k = 0; k < layer.getWidth(); k++) {
                        if (layer.getCell(k, j) != null && layer.getCell(k, j).getTile().getProperties().containsKey("spawn")) {
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
        Array<TmxMapObject> tmxMapObjects = new Array<TmxMapObject>();

        MapLayers layers = map.getLayers();

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

                    TmxMapObject tmxMapObject = new TmxMapObject(atlas.findRegion(qT), qX, mapHeight - qH - qY, qW, qH);
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
     * returns an array of the collision objects located on the tmx layers<br>
     * note: the tiled map layer has to be an object layer and must start with $c
     */
    private Array<Rectangle> createCollisionObjects() {
        Array<Rectangle> co = new Array<Rectangle>();

        MapLayers layers = map.getLayers();
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
            MapLayer mapLayer = layers.get(i);
            if (mapLayer.getName().startsWith("$c") && mapLayer.getObjects().getCount() == 0) {
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
        Pixmap pixmap = new Pixmap(mapWidth / 100, mapHeight, Pixmap.Format.RGBA8888);
        Color color_outline = new Color(0, 0, 0, 1);
        Color color_fill = new Color(1, 0, 0, 0.3f);
        drawCollisionTiles(pixmap, color_outline, color_fill);
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
    private void drawCollisionShapes() {
        Pixmap pixmap = new Pixmap(mapWidth, mapHeight, Pixmap.Format.RGBA8888);
        Color color_outline = new Color(0, 0, 0, 1);
        Color color_fill = new Color(1, 0, 0, 0.3f);

        /* draws the collision rectangles */
        for (int i = 0; i < collisionObjects.size; i++) {
            Rectangle rectangle = flipY(new Rectangle(collisionObjects.get(i)));
            drawRectangleOnPixmap(pixmap, color_outline, color_fill, rectangle);
        }
        collisionShapesTexture = new Texture(pixmap);
    }

    private void drawRectangleOnPixmap(Pixmap pixmap, Color color_outline, Color color_fill, Rectangle rectangle) {
        pixmap.setColor(color_outline);
        pixmap.drawRectangle((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
        pixmap.setColor(color_fill);
        pixmap.fillRectangle((int) rectangle.getX(), (int) rectangle.getY(), (int) rectangle.getWidth(), (int) rectangle.getHeight());
    }

    //*** GETTER METHODS ****//
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

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

    public void addItem(Item item) {
        items.add(item);
    }

    public Array<Item> getItems() {
        return items;
    }

    public Array<Player> getPlayers() {
        return players;
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
