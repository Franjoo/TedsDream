package com.angrynerds.gameobjects.items;

import com.angrynerds.gameobjects.GameObject;
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
public class Item extends GameObject {

    public TextureRegion region;

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

    public float getWidth() { return region.getRegionWidth();}

    public float getHeight() { return region.getRegionHeight();}
}
