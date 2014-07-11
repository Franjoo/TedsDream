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


    Texture tex;

    private ShapeRenderer renderer = new ShapeRenderer();


    public ShadowRenderer(Camera camera){

    }

    public void update(float deltaTime){


    }

    public void render(SpriteBatch batch){


    }


    public void renderShadow(Batch batch,GameObject object) {
        tex = new Texture(Gdx.files.internal("test/shadow.png"));
        batch.begin();
//        batch.draw(tex, object.getX() - 100, object.getY() - 50, object.getWidth(), object.getHeight());

        batch.draw(tex, object.getX()-30, object.getZ()-30, 75, 50);

        batch.end();
    }

}
