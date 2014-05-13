package com.angrynerds.game;

import com.angrynerds.gameobjects.GameObject;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * User: Franjo
 * Date: 20.11.13
 * Time: 21:12
 * Project: GameDemo
 */
public abstract class AbstractLayer extends GameObject {

    protected float vX;
    protected float vY;

    public AbstractLayer(float x, float y, float vX, float vY) {
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
    }

    public abstract void update(float deltaTime);

    public float getvX() {
        return vX;
    }

    public float getvY() {
        return vY;
    }
}
