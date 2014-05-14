package com.angrynerds.input;

import com.angrynerds.util.State;

/**
 * User: Franjo
 * Date: 07.11.13
 * Time: 16:01
 * Project: GameDemo
 */
public class TouchInput implements IGameInputController {




    @Override
    public float get_stickX() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float get_stickY() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public State getState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean get_isA() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean get_isB() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setState(State state) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
