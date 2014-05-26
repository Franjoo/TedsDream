package com.angrynerds.renderer;

import com.angrynerds.gameobjects.GameObject;
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


    void ShadowRenderer(){


    }

    public void update(float deltaTime){


    }

    public void render(SpriteBatch batch){


    }

    public void renderShadow(Batch batch,Camera camera,GameObject object){
         tex = new Texture(Gdx.files.internal("test/shadow.png"));
         batch.begin();



        batch.draw(tex,object.getX()-100,object.getY()-50,200,100);
        batch.end();
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        renderer.setProjectionMatrix(camera.combined);
//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        renderer.setColor(0,0,0,0.5f);
//        renderer.ellipse(object.getX()-object.getWidth()/2,object.getY(),object.getWidth()*0.7f,object.getHeight()/3);
//        renderer.end();

    }
}
