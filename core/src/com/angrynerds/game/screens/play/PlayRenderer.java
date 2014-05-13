package com.angrynerds.game.screens.play;

import com.angrynerds.util.C;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * class which is responsible for rendering the PlayScreen
 */
public class PlayRenderer {
    private static final String TAG = PlayRenderer.class.getSimpleName();

    private PlayController playController;
    private SpriteBatch batch;

    /**
     * creates an new PlayRenderer
     *
     * @param playController PlayController that is used
     * @param batch          SpriteBatch that is used for rendering
     */
    public PlayRenderer(PlayController playController, SpriteBatch batch) {
        this.playController = playController;
        this.batch = batch;

    }

    /**
     * renders the objects of the playScreen
     *
     * @param deltaTime
     */
    public void render(float deltaTime) {
        batch.setProjectionMatrix(playController.getCamera().combined);

        playController.getWorld().render(batch);
    }

    /**
     * resizes the viewport of the camera which is used.
     *
     * @param width  new width of the viewport in pixels
     * @param height new height of the viewport in pixels
     */
    public void resize(int width, int height) {
        playController.getCamera().viewportWidth = (C.VIEWPORT_HEIGHT / height) * width;
        playController.getCamera().update();
    }
}
