package com.angrynerds.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 10.02.14
 * Time: 22:37
 */
public class Item {

    public TextureRegion region;
    private float x,y;

    public Item(float x, float y, String path){
        this.x = x;
        this.y = y;

        region = new TextureRegion(new Texture(Gdx.files.internal(path)));
    }

    public void update(float delta){

    }

    public void render(SpriteBatch batch){
        batch.begin();
        batch.draw(region,x,y);
        batch.end();

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
