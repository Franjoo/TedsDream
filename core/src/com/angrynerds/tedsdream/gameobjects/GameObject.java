package com.angrynerds.tedsdream.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Author: Franz Benthin
 */
public abstract class GameObject {

    protected float x;
    protected float y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public abstract void draw(SpriteBatch batch);

    public abstract void update(float delta);

}
