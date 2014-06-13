package com.angrynerds.tedsdream.screens.multiplayer;

import com.angrynerds.tedsdream.core.Controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * User: Franjo
 * Date: 24.05.2014
 */
public class MultiplayerChooseMenu implements Screen {

    private Stage stage;
    private Table table;
    private Skin skin;

    private Label label_headline;
    private final String headline = "MULTIPLAYER";
    private TextButton btn_client;
    private TextButton btn_host;

    private InputListener listener;


    final private Controller game;

    public MultiplayerChooseMenu(Controller game) {
        this.game = game;

        // back button
        Gdx.input.setCatchBackKey(true);

        final MultiplayerChooseMenu _this = this;

        stage = new Stage();

        // skin
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));


        // headline
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/white.fnt"), Gdx.files.internal("fonts/white_0.png"), false);
        font.setScale(1.3f);
        float fontX = (Gdx.graphics.getWidth() - font.getBounds(headline).width) / 2;
        label_headline = new Label(headline, new Label.LabelStyle(font, new Color(1, 1, 1, 1)));
        label_headline.setPosition(fontX, 380);

        // buttons
        btn_client = new TextButton("client", skin);
        btn_host = new TextButton("server", skin);

        // size
        btn_client.setSize(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 8);
        btn_host.setSize(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 8);

        // position
        btn_client.setPosition(Gdx.graphics.getWidth() / 2 - btn_client.getWidth() / 2, 260);
        btn_host.setPosition(Gdx.graphics.getWidth() / 2 - btn_host.getWidth() / 2, 120);

        // listener
        listener = new InputListener() {

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {

                // singleplayer
                if (e.getTarget().getParent() == btn_client) {
                    System.out.println("client pressed");
                    _this.game.setScreen(_this.game.multiplayer_connect);
                    return true;
                }

                // multiplayer
                else if (e.getTarget().getParent() == btn_host) {
                    System.out.println("server pressed");
                    _this.game.setScreen(_this.game.multiplayer_configuration);
                    return true;
                }

                return false;

            }
        };


        // add actors
        stage.addActor(label_headline);
        stage.addActor(btn_client);
        stage.addActor(btn_host);

    }

    @Override
    public void render(float delta) {
        // background color
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        // back to main menu
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)
                || Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)
                || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.mainMenu);
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        // add listeners
        btn_client.addListener(listener);
        btn_host.addListener(listener);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        // remove listeners
        btn_client.removeListener(listener);
        btn_host.removeListener(listener);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
