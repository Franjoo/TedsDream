package com.angrynerds.game.screens.mainmenu;

import com.angrynerds.game.screens.AbstractScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 11.02.14
 * Time: 16:14
 */
public class LoadingScreen extends AbstractScreen {

    Texture t = new Texture(Gdx.files.internal("ui/menus/main/loadingScreen.png"));
    SpriteBatch batch;

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(t, 0, 0);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        render(0);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
