package com.angrynerds.game.screens.mainmenu;

import com.angrynerds.game.core.GameController;
import com.angrynerds.game.screens.AbstractScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 12.02.14
 * Time: 22:50
 * To change this template use File | Settings | File Templates.
 */
public class StartingScreen extends AbstractScreen {

    private GameController game;
    private Texture logo;
    private SpriteBatch batch;
    private Stage stage;

    public StartingScreen(GameController game){
        this.game = game;
    }

    @Override
    public void update(float deltaTime) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isTouched()) { // If the screen is touched after the game is done loading, go to the main menu screen
            game.setActiveScreen(game.getMainMenu());
            game.setScreen(game.getMainMenu());
        }

        batch.begin();
        batch.draw(logo,stage.getWidth()/2 - logo.getWidth()/2,stage.getHeight()/2 - logo.getHeight()/2,logo.getWidth(),logo.getHeight());
        batch.end();


    }

    @Override
    public void resize(int width, int height) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void show() {
        stage  = new Stage();
        logo = new Texture("ui/menus/main/logo.png");
        batch = new SpriteBatch();
    }

    @Override
    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pause() {
        batch.dispose();
        stage.dispose();
    }

    @Override
    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
