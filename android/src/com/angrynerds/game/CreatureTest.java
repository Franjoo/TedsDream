package com.angrynerds.game;

import android.os.Bundle;
import com.angrynerds.gameobjects.creatures.Boy;
import com.angrynerds.gameobjects.creatures.Creature;
import com.angrynerds.gameobjects.creatures.Goblin;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * User: Franjo
 * Date: 07.11.13
 * Time: 22:19
 * Project: GameDemo
 */
public class CreatureTest extends AndroidApplication implements ApplicationListener {
    // creatures
    Creature goblin;
    Creature boy;

    // batch
    SpriteBatch batch;

    public void create() {

        // batch
        batch = new SpriteBatch();

        // params
        String name;
        TextureAtlas atlas;
        float scale;
        String skin;

//        // set up goblin
//        name = "goblins";
//        atlas = new TextureAtlas(Gdx.files.internal("goblins" + ".atlas"));
//        scale = 1;
//        skin = "goblin";
//        goblin = new Goblin(name, atlas, skin, scale);

//        // set up boy
//        name = "spineboy";
//        atlas = new TextureAtlas(Gdx.files.internal(name + ".atlas"));
//        scale = 1;
//        skin = null;
//        boy = new Boy(name, atlas, skin, scale);
//
//        // set up ted

    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // render goblin
        goblin.update(Gdx.graphics.getDeltaTime());
        goblin.render(batch);

        // render boy
        boy.update(Gdx.graphics.getDeltaTime());
        boy.render(batch);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;

        initialize(new CreatureTest(), cfg);
    }
}
