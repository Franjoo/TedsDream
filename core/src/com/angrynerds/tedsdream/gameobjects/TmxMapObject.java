package com.angrynerds.tedsdream.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * represents a map object that holds a textureRegion which is used in rendering process
 */
public class TmxMapObject {
    private TextureRegion region;
    private int x;
    private int y;
    private int width;
    private int height;

    /**
     * creates a new TmxMapObject with assigned region, position and dimension
     */
    public TmxMapObject(TextureRegion region, int x, int y, int width, int height) {
        this.region = region;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * creates a new TmxMapObject with assigned region, position (0,0) and dimension (0,0)
     */
    public TmxMapObject(TextureRegion region) {
        this(region, 0, 0, 0, 0);
    }

    /**
     * renders the texture region that is used
     */
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(region, x, y, width, height);
        batch.end();
    }

    //*** GETTERS & SETTERS ***//
    public TextureRegion getRegion() {
        return region;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
