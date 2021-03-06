package com.angrynerds.tedsdream.gameobjects.items;

import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 10.02.14
 * Time: 22:37
 */
public class Item extends GameObject {

    public TextureRegion region;

    public Item(float x, float y, float z, String path){
        this.x = x;
        this.y = y;
        this.z = z;

        region = new TextureRegion(new Texture(Gdx.files.internal(path)));
    }

    public void update(float delta){
    }

    public void render(SpriteBatch batch){
        batch.begin();
        batch.draw(region,x,z);
        batch.end();
    }

    public float getWidth() { return region.getRegionWidth();}

    public float getHeight() { return region.getRegionHeight();}
}
