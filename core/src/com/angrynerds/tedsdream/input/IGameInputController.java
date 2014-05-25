package com.angrynerds.tedsdream.input;

import com.angrynerds.tedsdream.util.State;

/**
 * User: Franjo
 * Date: 25.10.13
 * Time: 23:32
 * Project: Main
 */
public interface IGameInputController {

    public float get_stickX();

    public float get_stickY();

    public int getState();

    public boolean get_isA();

    public boolean get_isB();

   public void setState(int state);
}
