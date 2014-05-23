package com.angrynerds.game.screens.play;

import com.angrynerds.game.camera.CameraHelper;
import com.angrynerds.gameobjects.Player;
import com.angrynerds.gameobjects.map.Map;
import com.angrynerds.input.IGameInputController;
import com.angrynerds.ui.ControllerUI;
import com.angrynerds.util.C;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * represents a class which is responsible for the camera of the gameplay and for
 * updating the world
 */
public class PlayController {

    private Map map;
    private Player player;

    private OrthographicCamera camera;
    private PlayController playController;
    private CameraHelper cameraHelper;

    private ControllerUI controllerUI;

    /**
     * creates an new PlayController
     */
    public PlayController() {
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        camera.update();

        controllerUI = new ControllerUI();


        // world objects
        player = new Player(controllerUI.getListener());
        map = new Map(player, camera);

        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);

    }


    /**
     * updates the objects used in PlayScreen
     *
     * @param delta time since last frame
     */
    public void update(float delta) {
        controllerUI.getLifeBar().setLifePercent(player.getActualHP()/player.getMaxHP());
        controllerUI.update(delta);

        map.update(delta);

        cameraHelper.update(delta);
        cameraHelper.applyTo(camera);

    }

    /**
     * returns the camera which is used in the game play
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    public ControllerUI getControllerUI() {
        return controllerUI;
    }

    public Map getMap(){
        return map;
    }

    public Player getPlayer() {
        return player;
    }
}
