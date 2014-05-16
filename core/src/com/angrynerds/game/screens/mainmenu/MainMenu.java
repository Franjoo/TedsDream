package com.angrynerds.game.screens.mainmenu;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.*;
import com.angrynerds.game.core.GameController;
import com.angrynerds.game.screens.AbstractScreen;
import com.angrynerds.tweens.LifebarAccessor;
import com.angrynerds.tweens.MenuAccessor;
import com.angrynerds.ui.Lifebar;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 12.01.14
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class MainMenu extends AbstractScreen implements TweenAccessor<Sound> {

    private GameController game;

    private Stage stage;
    private SpriteBatch batch;
    private Texture bg;
    private Skin skin;
    private Table table;
    private Button buttonPlay, buttonSettings;
    private BitmapFont white;
    private TextureAtlas atlas;
    private Label heading;
    private MenuButtonListener bListener;
    private TweenManager manager;

    // sounds
    private static final int SOUND_TITLE = 0;
    private float volume;
    private Sound sound_title;


    public MainMenu(GameController gameController) {
        game = gameController;
        init();
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Table.drawDebug(stage);
        manager.update(v);

        batch.begin();
        batch.draw(bg, 0, 0, stage.getWidth(), stage.getHeight());
        batch.end();

        stage.act(v);
        stage.draw();
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void resize(int i, int i2) {

    }

    private void init() {
        bListener = new MenuButtonListener();
        stage = new Stage();
        atlas = new TextureAtlas("ui/menus/main/mainMenuButton.pack");
        skin = new Skin(atlas);
        white = new BitmapFont(Gdx.files.internal("fonts/bmtFont.fnt"), false);
        batch = new SpriteBatch();
        bg = new Texture("ui/menus/main/titel_moon_02.jpg");

        table = new Table(skin);
        table.setBounds(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2.0f, Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() / 6);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        //textButtonStyle.up = skin.getDrawable("button.up");
        //textButtonStyle.down = skin.getDrawable("button.down");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = white;

        buttonPlay = new Button(skin.getDrawable("button_play"));
        buttonPlay.addListener(bListener);
        buttonPlay.padLeft(100);

        buttonSettings = new Button((skin.getDrawable("button_settings")));

        table.add(buttonPlay);
        table.getCell(buttonPlay).size(buttonPlay.getWidth() / 2.5f, buttonPlay.getHeight() / 2.5f);
        table.add(buttonSettings);
        table.getCell(buttonSettings).size(buttonSettings.getWidth() / 2.5f, buttonSettings.getHeight() / 2.5f);
        stage.addActor(table);
        table.debug();

        Tween.registerAccessor(buttonPlay.getClass(), new MenuAccessor());

        manager = new TweenManager();
        Tween.to(buttonPlay, MenuAccessor.POSITION_Y, 2.0f)
                .targetRelative(15)
                .ease(Sine.IN)
                .repeatYoyo(-1, 0)
                .start(manager);
        Tween.to(buttonSettings, MenuAccessor.POSITION_Y, 1.5f)
                .targetRelative(15)
                .ease(Sine.IN)
                .repeatYoyo(-1, 0)
                .start(manager);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // start tile music
        sound_title = Gdx.audio.newSound(Gdx.files.internal("sounds/menus/titelmusik.wav"));
        sound_title.setLooping(0, true);
        volume = 1;
        sound_title.play(volume);

        //todo sound ausfaden, problem: kein volume getter um TweenAccessor zu benutzen

    }

    @Override
    public void hide() {
        sound_title.dispose();
        bg.dispose();
        stage.dispose();
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

    @Override
    public int getValues(Sound target, int tweenType, float[] returnValues) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValues(Sound target, int tweenType, float[] newValues) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private class MenuButtonListener extends ClickListener {
        TextButton target = null;

        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (event.getTarget() instanceof TextButton)
                target = (TextButton) event.getTarget();
            System.out.println(target);
            //super.clicked(event, x, y);
            if (true) {
                game.setActiveScreen(game.getPlayScreen());
                game.setScreen(game.getPlayScreen());

                LoadingScreen s = new LoadingScreen();
                game.setScreen(s);
            }
        }
    }

}
