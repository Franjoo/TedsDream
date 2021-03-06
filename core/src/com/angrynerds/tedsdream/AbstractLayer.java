package com.angrynerds.tedsdream;

import com.angrynerds.tedsdream.gameobjects.GameObject;

/**
 * User: Franjo
 * Date: 20.11.13
 * Time: 21:12
 * Project: GameDemo
 */
public abstract class AbstractLayer extends GameObject {

    protected float velocityX;
    protected float velocityY;

    public AbstractLayer(float x, float y, float velocityX, float velocityY) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public abstract void update(float deltaTime);

    public float getVelocityX() {
        return velocityX;
    }
    public float getVelocityY() {
        return velocityY;
    }
}
