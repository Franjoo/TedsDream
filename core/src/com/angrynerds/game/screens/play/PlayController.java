package com.angrynerds.game.screens.play;

import com.angrynerds.game.World;
import com.angrynerds.ui.ControllerUI;
import com.angrynerds.util.C;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * represents a class which is responsible for the camera of the gameplay and for
 * updating the world
 */
public class PlayController {
    private static final String TAG = PlayController.class.getSimpleName();

    private World world;
    private OrthographicCamera camera;

    private ControllerUI controllerUI;



    /**
     * creates an new PlayController
     */
    public PlayController() {
        System.out.println("playcontreoller craerted");
        init();
    }

    /**
     * initializes PlayController
     */
    private void init() {
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);
        camera.update();

        controllerUI = new ControllerUI();

        world = new World(this);
    }

    /**
     * updates the objects used in PlayScreen
     *
     * @param deltaTime time since last frame
     */
    public void update(float deltaTime) {
        world.update(deltaTime);
        controllerUI.getLifeBar().setLifePercent(world.getPlayer().getActualHP()/world.getPlayer().getMaxHP());
    }

    /**
     * returns the world in which the game is taking place
     */
    public World getWorld() {
        return world;
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
}
