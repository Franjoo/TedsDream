package com.angrynerds.game.screens.play;

import com.angrynerds.game.core.GameController;
import com.angrynerds.game.screens.AbstractScreen;
import com.angrynerds.game.screens.mainmenu.MainMenu;
import com.angrynerds.ui.ControllerUI;
import com.angrynerds.ui.TimeDisplay;
import com.angrynerds.util.C;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    private GameController game;

    /**
     * creates a new PlayScreen.
     * PlayScreen provides static access to the SpriteBatch which
     * is used for rendering
     * @param gameController
     */
    public PlayScreen(GameController gameController) {
        super();
        game = gameController;
        // allow static access, note: ugly code practice
        batch = super.getSpriteBatch();

    }

    /**
     * initializes PlayScreen
     */
    private void init() {
        playController = new PlayController();
        playRenderer = new PlayRenderer(playController, batch);

        // timer
        timer = new TimeDisplay(playController);
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        camera.setToOrtho(true);

    }

    /**
     * returns the PlayController which is used in the PlayScreen
     */
    public PlayController getPlayController() {
        return playController;
    }

    /**
     * returns the PlayRenderer which is used in the PlayScreen
     */
    public PlayRenderer getPlayRenderer() {
        return playRenderer;
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
        playRenderer.render(deltaTime);

        // timer
        batch.setProjectionMatrix(camera.combined);
        timer.update(deltaTime);
        timer.render(batch);
        camera.update();

        if(playController.getWorld().getPlayer().getActualHP() <= 0){
            if (Gdx.input.isTouched()) { // If the screen is touched after the game is done loading, go to the main menu screen
                game.setMainMenu(new MainMenu(game));
                game.setActiveScreen(game.getMainMenu());
                game.setScreen(game.getMainMenu());
            }
        }


    }

    @Override
    public void resize(int width, int height) {
        playRenderer.resize(width, height);
    }

    @Override
    public void show() {
        init();
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
