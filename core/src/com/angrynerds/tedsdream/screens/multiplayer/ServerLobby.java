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
import com.badlogic.gdx.scenes.scene2d.ui.*;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 15.05.2014
 * Time: 22:06
 */
public class ServerLobby implements Screen {

    private InputListener listener;
    private Stage stage;

    // headline
    private Label label_headline;
    private final String headline = "LOBBY";

    // start
    private TextButton btn_start;


    // client text area
    private TextArea ta_clients;

    // skin
    private Skin skin;

    final private Controller game;

    public ServerLobby(Controller game) {
        this.game = game;

        // back button
        Gdx.input.setCatchBackKey(true);

        final ServerLobby _this = this;

        stage = new Stage();

        // skin
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // headline
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/white.fnt"), Gdx.files.internal("fonts/white_0.png"), false);
        font.setScale(1.3f);
        float fontX = (Gdx.graphics.getWidth() - font.getBounds(headline).width) / 2;
        label_headline = new Label(headline, new Label.LabelStyle(font, new Color(1, 1, 1, 1)));
        label_headline.setPosition(fontX, 380);


        // text area clients
        ta_clients = new TextArea("",skin);
        ta_clients.setSize(Gdx.graphics.getWidth() / 2, 200);
        ta_clients.setPosition(Gdx.graphics.getWidth() / 2 - ta_clients.getWidth() / 2, 150);
        
        
        // button
        btn_start = new TextButton("START", skin);
        btn_start.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 8);
        btn_start.setPosition(Gdx.graphics.getWidth() / 2 - btn_start.getWidth() / 2, 60);

        // listener
        listener = new InputListener() {

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {

                // singleplayer
                if (e.getTarget().getParent() == btn_start) {
                    System.out.println("start pressed");
                    _this.game.server.startGame();
                    _this.game.setScreen(_this.game.multiplayerScreen);
                    return true;
                }

                return false;

            }
        };

        // add actors
        stage.addActor(label_headline);
        stage.addActor(btn_start);
        stage.addActor(ta_clients);



    }

    @Override
    public void render(float delta) {
        // background color
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        // back to multiplayer choose
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)
                || Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)
                || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.multiplayer_choose);
        }

        // update client text area
        ta_clients.setText(game.multiplayerScreen.getClientNames());
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

        btn_start.addListener(listener);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        btn_start.removeListener(listener);

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
