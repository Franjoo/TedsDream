package com.angrynerds.tedsdream.gameobjects.map;

import com.angrynerds.tedsdream.Layer;
import com.angrynerds.tedsdream.gameobjects.Player;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

/**
 * represents the Map in which the game is taking place.
 * the Map contains
 */
public class Map {

    private static Properties properties;

    // map relevant attributes
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private OrthogonalTiledMapRenderer fixedRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera fixedCamera;

    private Array<Layer> layers_foreground = new Array<>();
    private Array<Layer> layers_background = new Array<>();

    /**
     * creates a new Map
     */
    public Map(OrthographicCamera camera, TiledMap tiledMap) {

        this.camera = camera;
        this.tiledMap = tiledMap;

        tiledMap.getLayers().get(0).getObjects().getByType()

        properties = new Properties(tiledMap);


        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        renderer.setView(camera);

        // fixed camera & renderer
        fixedCamera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        fixedRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        fixedRenderer.setView(fixedCamera);

        // fill collision relevant lists
        createMapLayers();

        setRenderLayers();

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
//                        spawnController.add(objects.get(j));
                    }

                }

            }
        }

    }

    public void renderForeground(){
        renderLayers(layers_foreground);
    }

    public void renderBackground(){
        renderLayers(layers_background);
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

        // get number of render layers
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            MapLayer layer = tiledMap.getLayers().get(i);
            if (!layer.getName().startsWith("$c") && !layer.getName().startsWith("$s")) {
                renderLayers.add(i);
                System.out.println(layer.getName() + " : " + i);
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


        renderer.setView(camera);
        fixedRenderer.setView(fixedCamera);
    }


    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static class Properties {

        // map properties
        public final int numTilesX;
        public final int numTilesY;
        public final int tileWidth;
        public final int tileHeight;
        public final int mapWidth;
        public final int mapHeight;

        public Properties(TiledMap tiledMap) {
            // parse map properties
            numTilesX = Integer.parseInt(tiledMap.getProperties().get("width").toString());
            numTilesY = Integer.parseInt(tiledMap.getProperties().get("height").toString());
            tileWidth = Integer.parseInt(tiledMap.getProperties().get("tilewidth").toString());
            tileHeight = Integer.parseInt(tiledMap.getProperties().get("tileheight").toString());
            mapWidth = numTilesX * tileWidth;
            mapHeight = numTilesY * tileHeight;
        }

    }

}
