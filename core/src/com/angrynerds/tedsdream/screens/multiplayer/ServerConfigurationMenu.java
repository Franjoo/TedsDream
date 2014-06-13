package com.angrynerds.tedsdream.screens.multiplayer;

import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.net.GameServer;
import com.angrynerds.tedsdream.screens.game._MPGame;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 23.05.2014
 * Time: 23:58
 */
public class ServerConfigurationMenu implements Screen {

    private InputListener listener;
    private Stage stage;

    // headline
    private Label label_headline;
    private final String headline = "SETTINGS";

    // local ip adress
    private String ip;
    private TextField tf_ip;

    // server acces port
    private TextField tf_port;

    // buttons
    private TextButton btn_start;


    private Skin skin;

    final private Controller game;

    public ServerConfigurationMenu(final Controller game) {
        this.game = game;

        // back button
        Gdx.input.setCatchBackKey(true);

        // get local ip
        try {
            ip = Inet4Address.getLocalHost().getHostAddress();
            System.out.println("local ip: " + ip);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        final ServerConfigurationMenu _this = this;

        stage = new Stage();

        // skin
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // headline
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/white.fnt"), Gdx.files.internal("fonts/white_0.png"), false);
        font.setScale(1.3f);
        float fontX = (Gdx.graphics.getWidth() - font.getBounds(headline).width) / 2;
        label_headline = new Label(headline, new Label.LabelStyle(font, new Color(1, 1, 1, 1)));
        label_headline.setPosition(fontX, 380);

        // textfields
        tf_ip = new TextField(ip, skin);
        tf_port = new TextField(""+GameServer.PORT, skin);

        tf_ip.setSize(Gdx.graphics.getWidth() / 4, 40);
        tf_port.setSize(Gdx.graphics.getWidth() / 4, 40);

        tf_ip.setPosition(Gdx.graphics.getWidth() / 2 - tf_ip.getWidth() / 2, 300);
        tf_port.setPosition(Gdx.graphics.getWidth() / 2 - tf_port.getWidth() / 2, 200);


        // start buttons
        btn_start = new TextButton("START", skin);
        btn_start.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 8);
        btn_start.setPosition(Gdx.graphics.getWidth() / 2 - btn_start.getWidth() / 2, 60);

        // listener
        listener = new InputListener() {

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {

                // start game
                if (e.getTarget().getParent() == btn_start) {
                    System.out.println("start pressed");
                    _this.game.server.start();

                    if(game.MPGame == null)
                        game.MPGame = new _MPGame(game);

                    _this.game.MPGame.connect(tf_ip.getText(), Integer.parseInt(tf_port.getText()));

                    _this.game.setScreen(_this.game.multiplayer_lobby);
                    return true;
                }

                return false;

            }
        };


        // add actors
        stage.addActor(label_headline);
        stage.addActor(tf_ip);
        stage.addActor(tf_port);
        stage.addActor(btn_start);

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
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        // add listeners
        btn_start.addListener(listener);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        // remove listeners
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
