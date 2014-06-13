package com.angrynerds.tedsdream.screens;

import com.angrynerds.tedsdream.core.Controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 12.02.14
 * Time: 22:50
 * To change this template use File | Settings | File Templates.
 */
public class SplashScreen extends AbstractScreen {

    private Controller game;
    private Texture logo;
    private SpriteBatch batch;
    private Stage stage;

    public SplashScreen(Controller game){
        this.game = game;
    }

    @Override
    public void update(float deltaTime) {
            }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isTouched()) { // If the screen is touched after the game is done loading, go to the main menu screen
            game.setScreen(game.mainMenu);
        }

        batch.begin();
        batch.draw(logo,stage.getWidth()/2 - logo.getWidth()/2,stage.getHeight()/2 - logo.getHeight()/2,logo.getWidth(),logo.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        stage  = new Stage();
        logo = new Texture("ui/menus/main/logo.png");
        batch = new SpriteBatch();
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
