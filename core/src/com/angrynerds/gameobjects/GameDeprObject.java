package com.angrynerds.gameobjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * abstract class that holds some basic attributes which all the other gameObject must have<br>
 * note: deprecated, sprite has a lot of overhead
 */
public abstract class GameDeprObject extends Sprite {
    public Vector2 position;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public float rotation;

    public GameDeprObject() {
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1, 1);
        rotation = 0;
    }

    public abstract void update(float deltaTime);

    public abstract void render(SpriteBatch batch);

}
