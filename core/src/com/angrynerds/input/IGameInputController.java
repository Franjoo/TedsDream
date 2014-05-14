package com.angrynerds.input;

import com.angrynerds.util.State;

/**
 * User: Franjo
 * Date: 25.10.13
 * Time: 23:32
 * Project: Main
 */
public interface IGameInputController {


    public float get_stickX();

    public float get_stickY();

    public State getState();

    public boolean get_isA();

    public boolean get_isB();


   public void setState(State state);
}
