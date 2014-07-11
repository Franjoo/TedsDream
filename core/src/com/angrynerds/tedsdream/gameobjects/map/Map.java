package com.angrynerds.tedsdream.gameobjects.map;

import com.angrynerds.tedsdream.ai.pathfinding.AStarPathFinder;
import com.angrynerds.tedsdream.ai.pathfinding.ClosestHeuristic;
import com.angrynerds.tedsdream.Layer;
import com.angrynerds.tedsdream.collision.Detector;
import com.angrynerds.tedsdream.gameobjects.Enemy;
import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.angrynerds.tedsdream.gameobjects.Player;
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

import java.util.HashMap;

/**
 * represents the Map in which the game is taking place.
 * the Map contains
 */
public class Map {
    // tmx map path
    public static final String mapPath = "maps/map_05.tmx";

    // map instance
    private static Map instance;
    private Properties properties;

    private static enum Flip {HORIZONTAL, VERTICAL, BOTH}

    private Array<Player> players = new Array<Player>();

    private Texture dreamOver;

    // map relevant attributes
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private OrthogonalTiledMapRenderer fixedRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera fixedCamera;

    private SpawnController spawnController;
    private Array<Enemy> enemies = new Array<Enemy>();

    private Array<Item> items = new Array<Item>();


    // player relevant subjects
    private Vector2 spawn;
    private ShadowRenderer shadowRenderer;

    private Array<Layer> layers_foreground = new Array<Layer>();
    private Array<Layer> layers_background = new Array<Layer>();

    private TextureAtlas atlas;

    private Sound sound_background;

    private int deadEnemies;

    /**
     * creates a new Map
     */
    public Map(OrthographicCamera camera, TiledMap tiledMap) {

        this.camera = camera;
        this.tiledMap = tiledMap;

        properties = new Properties();

        init();

        instance = this;

        AStarPathFinder.initialize(this, 200, true, new ClosestHeuristic());

        spawnController = new SpawnController(this);
        this.shadowRenderer = new ShadowRenderer(camera);

        createEnemies();
    }

    public void addPlayer(Player player) {
        player.init();
        players.add(player);
    }

    private void createEnemies() {
        MapLayers mapLayer = tiledMap.getLayers();
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
            throw new NullPointerException("map has not been initialized");

        return instance;
    }

    /**
     * initializes the map
     */
    private void init() {
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        renderer.setView(camera);

        dreamOver = new Texture("ui/ingame/dreamover.png");

        // fixed camera & renderer
        fixedCamera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        fixedRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        fixedRenderer.setView(fixedCamera);

        // fill collision relevant lists
        createMapLayers();

        setRenderLayers();

        // set player relevant attributes
        spawn = findSpawn();

        // sound
        sound_background = Gdx.audio.newSound(Gdx.files.internal("sounds/ingame/game_background.mp3"));
        sound_background.loop();
    }

    private void createMapLayers() {
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            MapLayer mapLayer = tiledMap.getLayers().get(i);
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

        Array<Integer> renderLayers = new Array<Integer>();

        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            MapLayer layer = tiledMap.getLayers().get(i);
            if (!layer.getName().startsWith("$c") && !layer.getName().startsWith("$s")) {
                renderLayers.add(i);
                System.out.println(layer.getName() + " : " + i);
            }
        }
    }

    /**
     * renders all objects which are located on the map
     *
     * @param batch SpriteBatch that is used for rendering
     */
    public void render(SpriteBatch batch) {
        renderLayers(layers_background);

        // set camera
        renderer.getSpriteBatch().setProjectionMatrix(camera.combined);

        renderGameObjects(batch);

        renderLayers(layers_foreground);
    }

    private void renderGameObjects(SpriteBatch batch) {
        // enemies in background
        for (Enemy enemy : enemies) {
            if (players.get(0).getY() <= enemy.getY()) {
                shadowRenderer.renderShadow(batch,enemy);
                enemy.render(batch);
            }
        }

        for (Item item : items) {
            if (players.get(0).getY() <= item.getY()) {
                shadowRenderer.renderShadow(batch,item);
                item.render(batch);
            }
        }
        for (Player player : players) {
            shadowRenderer.renderShadow(batch,player);
            player.render(batch);
        }
        // enemies in foreground
        for (Enemy enemy : enemies) {
            if (players.get(0).getZ() > enemy.getZ()) {
                shadowRenderer.renderShadow(batch,enemy);
                enemy.render(batch);
            }
        }
        for (Item item : items) {
            if (players.get(0).getZ() > item.getZ()) {
                shadowRenderer.renderShadow(batch,item);
                item.render(batch);
            }
        }
    }

    private void renderLayers(Array<Layer> layers) {
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

    /**
     * updates the map
     *
     * @param deltaTime time since last frame
     */
    public void update(float deltaTime) {
        // update layers
        for (Layer backgroundLayer : layers_background) backgroundLayer.update(deltaTime);
        for (Layer ForegroundLayer : layers_foreground) ForegroundLayer.update(deltaTime);

        for (Player player : players) {
            player.update(deltaTime);
        }

        for (Item item : items) if (item != null) item.update(deltaTime);

        spawnController.update(deltaTime);

        for (Enemy enemy : enemies) {

            enemy.update(deltaTime);
        }

        renderer.setView(camera);
        fixedRenderer.setView(fixedCamera);
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

        MapLayers layers = tiledMap.getLayers();

        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i).getName().startsWith("$s") && layers.get(i).getObjects().getCount() == 0) {

                TiledMapTileLayer layer = (TiledMapTileLayer) layers.get(i);

                for (int j = 0; j < layer.getHeight(); j++) {
                    for (int k = 0; k < layer.getWidth(); k++) {
                        if (layer.getCell(k, j) != null && layer.getCell(k, j).getTile().getProperties().containsKey("spawn")) {
                            System.out.println("SPAWN FOUND");
                            float qX = k * properties.tileWidth;
                            float qZ = j * properties.tileHeight;
                            return new Vector2(qX, qZ);
                        }
                    }
                }
            }
        }
        return null;
    }

    //*** GETTER METHODS ****//
    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public boolean blockedByEnemie(int x, int z) {
        for (int i = 0; i < enemies.size; i++) {
            if (x == enemies.get(i).getTilePostionX() && z == enemies.get(i).getTilePostionZ())
                return true;
            else
                return false;
        }
        return false;
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

    public int getMaxEnemies() {
        return spawnController.getMaxEnemies();
    }

    public int getDeadEnemies() {
        return deadEnemies;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void removeFromMap(Enemy e) {
        enemies.removeValue(e, true);
        deadEnemies++;
    }

    public Properties getProperties(){
        return properties;
    }

    public class Properties {
        // map properties
        public final int numTilesX;
        public final int numTilesZ;
        public final int tileWidth;
        public final int tileHeight;
        public final int mapWidth;
        public final int mapHeight;
        public final int borderWidth = 5;

        public Properties() {
            // parse map properties
            numTilesX = Integer.parseInt(tiledMap.getProperties().get("width").toString());
            numTilesZ = Integer.parseInt(tiledMap.getProperties().get("height").toString());
            tileWidth = Integer.parseInt(tiledMap.getProperties().get("tilewidth").toString());
            tileHeight = Integer.parseInt(tiledMap.getProperties().get("tileheight").toString());
            mapWidth = numTilesX * tileWidth;
            mapHeight = numTilesZ * tileHeight;
        }

    }

}
