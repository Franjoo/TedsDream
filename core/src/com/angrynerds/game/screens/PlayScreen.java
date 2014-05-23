package com.angrynerds.game.screens;

import com.angrynerds.game.camera.CameraHelper;
import com.angrynerds.game.core.Controller;
import com.angrynerds.game.screens.AbstractScreen;
import com.angrynerds.gameobjects.Player;
import com.angrynerds.gameobjects.map.Map;
import com.angrynerds.input.KeyboardInput;
import com.angrynerds.ui.ControllerUI;
import com.angrynerds.ui.TimeDisplay;
import com.angrynerds.util.C;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * class that represents the screen which is used for game play
 */
public class PlayScreen extends AbstractScreen {

    // TimeDisplay
    private TimeDisplay timer;

    // camera
    private OrthographicCamera camera;
    private CameraHelper cameraHelper;

    // map
    private Map map;

    // Player
    private Player player;

    private ControllerUI controllerUI;


    private final Controller game;

    public PlayScreen(final Controller game) {
        this.game = game;

        // timer
        timer = new TimeDisplay();
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);

        // world objects
        player = new Player(new KeyboardInput());
        map = new Map(player,camera);

        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);
    }


    @Override
    public void update(float delta) {

        timer.update(delta);
        map.update(delta);

        // camera
        camera.update();
        cameraHelper.update(delta);
        cameraHelper.applyTo(camera);

        // menu on player dead
        if(player.getActualHP() <= 0){
            game.setScreen(game.mainMenu);
        }

    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.setProjectionMatrix(camera.combined);

        timer.render(batch);
        map.render(batch);

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
