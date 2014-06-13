package com.angrynerds.tedsdream.input;

/**
 * User: Franjo
 */
public class RemoteInput implements IGameInputController {

    private int state;

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
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int getState() {
        return state;
    }
}
