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

<<<<<<< HEAD:core/src/com/angrynerds/renderer/ShadowRenderer.java
    Texture tex;

=======
    private ShapeRenderer renderer = new ShapeRenderer();
    private Camera camera;
>>>>>>> fa2d67ded173ea0131e54ff25ef7125c44aa6624:core/src/com/angrynerds/tedsdream/renderer/ShadowRenderer.java

    public ShadowRenderer(Camera camera){
        this.camera = camera;
    }

    public void update(float deltaTime){


    }

    public void render(SpriteBatch batch){


    }

<<<<<<< HEAD:core/src/com/angrynerds/renderer/ShadowRenderer.java
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

=======
    public void renderShadow(GameObject object){
        camera.update();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0,0,0,0.5f);
        renderer.ellipse(object.getX()-object.getWidth()/2,object.getY(),object.getWidth()*0.7f,object.getHeight()/3);
        renderer.end();
>>>>>>> fa2d67ded173ea0131e54ff25ef7125c44aa6624:core/src/com/angrynerds/tedsdream/renderer/ShadowRenderer.java
    }
}
