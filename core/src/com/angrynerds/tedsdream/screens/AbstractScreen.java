package com.angrynerds.tedsdream.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents one of many application screens, such as a main menu, a settings menu, the game screen and so on.
 * Provides a SpriteBatch which is used for rendering
 */
public abstract class AbstractScreen implements Screen {

    protected SpriteBatch batch;

    /**
     * creates a new Screen, which uses his own SpriteBatch
     */
    public AbstractScreen() {
        batch = new SpriteBatch();
    }

    /**
     * creates a new Screen, which uses the assigned SpriteBatch
     * @param batch SpriteBatch that is used for rendering
     */
    public AbstractScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void dispose() {
        batch.dispose();
    }


    /**
     * updates the screen
     * @param deltaTime time since last frame
     */
    public abstract void update(float deltaTime);

    public abstract void render(float deltaTime);

    /**
     * Resizes the Screen
     * @param width new width in pixels
     * @param height new height in pixels
     */
    public abstract void resize(int width, int height);

    public abstract void show();

    public abstract void hide();

    /**
     * Pauses the Screen
     */
    public abstract void pause();

    /**
     * Resumes the Screen
     */
    public abstract void resume();
}
