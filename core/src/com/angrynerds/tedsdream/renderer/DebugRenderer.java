package com.angrynerds.tedsdream.renderer;

import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by tim on 09.06.2014.
 */
public class DebugRenderer {

    private static final boolean SHOW_TILE_GRID = false;
    private static final boolean SHOW_COLLISION_SHAPES = false;
    private static final boolean SHOW_COLLISION_TILES = false;
    private final CollisionHandler collisionHandler;

    private final int mapWidth;
    private final int mapHeight;

    private Texture gridTexture;
    private Texture collisionShapesTexture;
    private Texture collisionTilesTexture;

    Map map;

    public DebugRenderer(Map map, CollisionHandler collisionHandler){
        this.map = map;
        this.mapWidth = (int) map.getProperties().mapWidth;
        this.mapHeight = (int) map.getProperties().mapHeight;
        this.collisionHandler = collisionHandler;
        init();
    }

    private void init(){
        if (SHOW_TILE_GRID) drawTileGrid();

        if (SHOW_COLLISION_SHAPES) drawCollisionShapes();

        if (SHOW_COLLISION_TILES) drawCollisionTiles();
    }

    /**
     * draws the tile grid of the tmx tile map
     */
    private void drawTileGrid() {
        Pixmap pixmap = new Pixmap(mapWidth, mapHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        for (int h = 0; h < mapHeight; h++) {
            for (int w = 0; w < mapWidth; w++) {
                pixmap.drawRectangle(w * map.getProperties().tileHeight, h * map.getProperties().tileHeight, map.getProperties().tileWidth, map.getProperties().tileHeight);
            }
        }
        gridTexture = new Texture(pixmap);
    }
    /**
     * draws the collision tiles of the map which are located on a collision tile layer
     */
    private void drawCollisionTiles() {
        Pixmap pixmap = new Pixmap(mapWidth/ 100, mapHeight, Pixmap.Format.RGBA8888);
        Color color_outline = new Color(0, 0, 0, 1);
        Color color_fill = new Color(1, 0, 0, 0.3f);
        drawCollisionTiles(pixmap, color_outline, color_fill);
        collisionTilesTexture = new Texture(pixmap);
    }

    private void drawCollisionTiles(Pixmap pixmap, Color color_outline, Color color_fill) {
        for (int j = 0; j < collisionHandler.getCollisionTileLayers().size; j++) {
            for (int h = 0; h < map.getProperties().numTilesZ; h++) {
                for (int w = 0; w < map.getProperties().numTilesX / 100; w++) {
                    TiledMapTileLayer layer = (collisionHandler.getCollisionTileLayers().get(j));
                    TiledMapTileLayer.Cell cell = layer.getCell(w, h);
                    if (cell != null) {
                        Rectangle rectangle = flipY(new Rectangle(w * map.getProperties().tileWidth, h * map.getProperties().tileHeight, map.getProperties().tileWidth, map.getProperties().tileHeight));
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
        for (int i = 0; i < collisionHandler.getCollisionObjects().size; i++) {
            Rectangle rectangle = flipY(new Rectangle(collisionHandler.getCollisionObjects().get(i)));
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

    private void drawDebugTextures(SpriteBatch batch) {
        batch.begin();
        if (SHOW_TILE_GRID) batch.draw(gridTexture, 0, 0);

        if (SHOW_COLLISION_SHAPES) batch.draw(collisionShapesTexture, 0, 0);

        if (SHOW_COLLISION_TILES) batch.draw(collisionTilesTexture, 0, 0);
        batch.end();
    }

    public void render(SpriteBatch batch){
        drawDebugTextures(batch);
    }

}
