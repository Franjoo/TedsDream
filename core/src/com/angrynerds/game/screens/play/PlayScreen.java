package com.angrynerds.game.screens.play;

import com.angrynerds.game.core.Controller;
import com.angrynerds.game.screens.AbstractScreen;
import com.angrynerds.ui.TimeDisplay;
import com.angrynerds.util.C;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * class that represents the screen which is used for game play
 */
public class PlayScreen extends AbstractScreen {
    private static final String TAG = PlayScreen.class.getSimpleName();

    private PlayController playController;
    private PlayRenderer playRenderer;

    // TimeDisplay
    private TimeDisplay timer;
    private OrthographicCamera camera;

    private static SpriteBatch batch;

    private final Controller game;

    /**
     * creates a new PlayScreen.
     * PlayScreen provides static access to the SpriteBatch which
     * is used for rendering
     * @param game
     */
    public PlayScreen(Controller game) {
        this.game = game;

        // allow static access, note: ugly code practice
        batch = super.getSpriteBatch();

        playController = new PlayController();
        playRenderer = new PlayRenderer(playController, batch);

        // timer
        timer = new TimeDisplay(playController);
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        camera.setToOrtho(true);
    }


    /**
     * returns the static SpriteBatch which is used for rendering the PlayScreen
     */
    public static SpriteBatch getBatch() {
        return batch;
    }


    @Override
    public void update(float deltaTime) {
        playController.update(deltaTime);
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);

        playRenderer.render(deltaTime);

        // timer
        batch.setProjectionMatrix(camera.combined);
        timer.update(deltaTime);
        timer.render(batch);
        camera.update();

        if(playController.getWorld().getPlayer().getActualHP() <= 0){
            if (Gdx.input.isTouched()) { // If the screen is touched after the game is done loading, go to the main menu screen
                game.setScreen(game.mainMenu);
            }
        }


    }

    @Override
    public void resize(int width, int height) {
        playRenderer.resize(width, height);
    }

    @Override
    public void show() {
        playController.getControllerUI().show();
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
