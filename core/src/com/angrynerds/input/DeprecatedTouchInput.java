package com.angrynerds.input;

import com.angrynerds.game.screens.play.PlayScreen;
import com.angrynerds.ui.DeprecatedControllUI;
import com.angrynerds.util.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

/**
 * User: Franjo
 * Date: 26.10.13
 * Time: 12:27
 * Project: Main
 */
public class DeprecatedTouchInput implements IGameInputController {

    public DeprecatedControllUI ui;

    private PlayScreen playScreen;
    private Camera camera;


//    public DeprecatedTouchInput(float centerX, float centerY) {
//        this(new Vector2(centerX, centerY));
//    }

    public DeprecatedTouchInput(PlayScreen playScreen) {
        this.playScreen = playScreen;

        init();
    }
    public DeprecatedTouchInput(Camera camera) {
//        this.playScreen = playScreen;

        this.camera = camera;
        init();
    }

    private void init() {
        ui = new DeprecatedControllUI(camera);
        Gdx.app.log("!!!!!!!!!! UI","" + ui);
    }

    @Override
    public float get_stickX() {
        if (Gdx.input.isTouched()) {
            return ui.get_stickX();
        }
        return 0;
    }

    @Override
    public float get_stickY() {
        if (Gdx.input.isTouched()) {
            return ui.get_stickY();
        }
        return 0;
    }

    @Override
    public State getState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean get_isA() {
        return ui.get_isA();
    }

    @Override
    public boolean get_isB() {
        return ui.get_isB();
    }

    @Override
    public void setState(State state) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
