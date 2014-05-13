package com.angrynerds.game;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * User: Franjo
 * Date: 31.10.13
 * Time: 11:28
 * Project: GameDemo
 */
public class Layer extends AbstractLayer {

    private TiledMapTileLayer tl;
    private float scale;
    private boolean useTS;
    private boolean movable;

    private float mX;
    private float mY;


    public Layer(float x, float y, float vX, float vY, TiledMapTileLayer tl) {
        super(x, y, vX, vY);
        this.tl = tl;

        movable = false;
    }

    public Layer(float x, float y, float vX, float vY, TiledMapTileLayer tl, float mX,float mY) {
        super(x, y, vX, vY);
        this.tl = tl;

        this.mX = mX;
        this.mY = mY;

        movable = true;
    }

    @Override
    public void update(float deltaTime) {
        if (movable) {
            x += mX * deltaTime;
            y += mY * deltaTime;
        }
    }

    public TiledMapTileLayer getTiledMapTileLayer() {
        return tl;
    }

}