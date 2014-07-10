package com.angrynerds.tedsdream.input;

import com.angrynerds.tedsdream.util.States;

/**
 * User: Franjo
 */
public class RemoteInput implements IGameInputController {

    private States.Animation state;

    @Override
    public float get_stickX() {
        return 0;
    }

    @Override
    public float get_stickY() {
        return 0;
    }

    @Override
    public boolean get_isA() {
        return false;
    }

    @Override
    public boolean get_isB() {
        return false;
    }

    @Override
    public void setState(States.Animation state) {
        this.state = state;
    }

    @Override
    public States.Animation getState() {
        return state;
    }
}
