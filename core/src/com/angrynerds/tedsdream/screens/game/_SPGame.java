package com.angrynerds.tedsdream.screens.game;

import com.angrynerds.tedsdream.camera.CameraHelper;
import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.gameobjects.Player;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.angrynerds.tedsdream.gameobjects.map.EnemyInitialization;
import com.angrynerds.tedsdream.input.KeyboardInput;
import com.angrynerds.tedsdream.screens.AbstractScreen;
import com.angrynerds.tedsdream.ui.ControllerUI;
import com.angrynerds.tedsdream.ui.TimeDisplay;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;

/**
 * class that represents the screen which is used for game play
 */
public class _SPGame extends AbstractScreen {

    // TimeDisplay
    private TimeDisplay timer;

    // camera
    private OrthographicCamera camera;
    private CameraHelper cameraHelper;

    // map
    private Map map;

    // Player
    private Array<Player> players;
    private Player player;


    private final Controller game;
    private ControllerUI ui;

    public _SPGame(final Controller game) {
        this.game = game;

        // timer
        timer = new TimeDisplay();
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        ui = new ControllerUI();


        // world objects

        map = new Map(camera, game.tiledMap);
        map.initEnemies(new EnemyInitialization(game.tiledMap));

        players = new Array<>();


        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            player = new Player(ui.getListener(), map);

        } else{
            player = new Player(new KeyboardInput(), map);
        }

        map.addPlayer(player);

        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);
    }


    @Override
    public void update(float delta) {

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.update(delta);
        }

        timer.update(delta);
        map.update(delta);

        // camera
        camera.update();
        cameraHelper.update(delta);
        cameraHelper.applyTo(camera);

        player.update(delta);

        // menu on player dead
        if (player.getActualHP() <= 0) {
            game.setScreen(game.mainMenu);
        }

    }

    @Override
    public void render(float delta) {
        update(delta);


        batch.setProjectionMatrix(camera.combined);


        timer.render(batch);
        map.render(batch);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.render(delta);
        }


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

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
