package com.angrynerds.tedsdream.input;

import com.angrynerds.tedsdream.util.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * User: Franjo
 * Date: 26.10.13
 * Time: 10:47
 * Project: Main
 */
public class KeyboardInput extends InputAdapter implements IGameInputController {

    public KeyboardInput() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public float get_stickX() {
        if (Gdx.input.isKeyPressed(Keys.LEFT) && Gdx.input.isKeyPressed(Keys.RIGHT)) return 0;
        else if (Gdx.input.isKeyPressed(Keys.LEFT)) return -1;
        else if (Gdx.input.isKeyPressed(Keys.RIGHT)) return 1;
        return 0;
    }

    @Override
    public float get_stickY() {
        if (Gdx.input.isKeyPressed(Keys.UP) && Gdx.input.isKeyPressed(Keys.DOWN)) return 0;
        else if (Gdx.input.isKeyPressed(Keys.UP)) return 1;
        else if (Gdx.input.isKeyPressed(Keys.DOWN)) return -1;
        return 0;
    }

    @Override
    public State getState() {
        // jump
        if (get_isA()) return State.JUMPING;
        // dash
        else if (get_isB() && get_stickX() >= 0) return State.DASHINGRIGHT;
        else if (get_isB() && get_stickX() < 0) return State.DASHINGLEFT;
        // attack
        else if (get_isB()) return State.ATTACKING;
        // run
        else if (get_stickX() != 0 || get_stickY() != 0) return State.RUNNING;

        return State.IDLE;

    }

    @Override
    public boolean get_isA() {
        return Gdx.input.isKeyPressed(Keys.A);
    }

    @Override
    public boolean get_isB() {
        return Gdx.input.isKeyPressed(Keys.S);
    }

    @Override
    public void setState(State state) {
//        this.state = state;
    }
}
