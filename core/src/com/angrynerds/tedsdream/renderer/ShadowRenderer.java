package com.angrynerds.tedsdream.renderer;

import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Sebastian on 20.05.2014.
 */
public class ShadowRenderer {


    Texture tex_shadow;

    public ShadowRenderer(){
        tex_shadow = new Texture(Gdx.files.internal("test/shadow.png"));
    }


    public void renderShadow(Batch batch,GameObject object) {
//        tex_shadow = new Texture(Gdx.files.internal("test/shadow.png"));
//        batch.begin();
//        batch.draw(tex_shadow, object.getX() - 100, object.getY() - 50, 200, 100);
//        batch.end();
    }

    public void drawShadows(SpriteBatch batch, GameObject go){
        batch.draw(tex_shadow,go.getX() - 100, go.getY() - 50, 200, 100);
    }

}
