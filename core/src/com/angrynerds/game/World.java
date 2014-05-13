package com.angrynerds.game;

import com.angrynerds.game.camera.CameraHelper;
import com.angrynerds.game.screens.play.PlayController;
import com.angrynerds.gameobjects.map.Map;
import com.angrynerds.gameobjects.Player;
import com.angrynerds.input.IGameInputController;
import com.angrynerds.input.gamepads.X360Gamepad;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * represents the World in which the game is taking place
 */
public class World {
    public static final String TAG = World.class.getSimpleName();

    private Map map;
    private Player player;

    private OrthographicCamera camera;
    private PlayController playController;
    private CameraHelper cameraHelper;

    /**
     * creates a new World
     */
    public World(PlayController playController) {
        Gdx.app.log(TAG, " created");

        this.playController = playController;
        camera = playController.getCamera();

        init();
    }

    /**
     * initializes the World
     */
    private void init() {
        // input
        IGameInputController input = playController.getControllerUI().getListener();
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {

            // xbox 360 controller
            Array<Controller> controllers = Controllers.getControllers();
            try {
                if (controllers != null) {
                    input = new X360Gamepad(controllers.get(0));
                    playController.getControllerUI().hideInputUI();
                }
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("No Controller detected");
            }

        }

        // world objects
        player = new Player(input);
        map = new Map(this, player);

        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);
    }

    /**
     * updates the world
     *
     * @param deltaTime time since last frame
     */
    public void update(float deltaTime) {
        map.update(deltaTime);

        playController.getControllerUI().update(deltaTime);
    }

    /**
     * renders all world objects
     *
     * @param batch SpriteBatch that is used for rendering
     */
    public void render(SpriteBatch batch) {
        cameraHelper.update(Gdx.graphics.getDeltaTime());

        map.render(batch);

        playController.getControllerUI().render(Gdx.graphics.getDeltaTime());

        cameraHelper.applyTo(camera);

    }

    /**
     * returns the map
     */
    public Map getMap() {
        return map;
    }

    /**
     * returns the player
     */
    public Player getPlayer() {
        return player;
    }


    /**
     * returns the camera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * returns the camera helper
     *
     * @return
     */
    public CameraHelper getCameraHelper() {
        return cameraHelper;
    }

    // TODO es bestehen bisher noch abhaengigkeiten zwischen player und map die nicht sein duerften. player wird in world erzeugt aber in map geupdated, das sollte überdacht werden.


}
