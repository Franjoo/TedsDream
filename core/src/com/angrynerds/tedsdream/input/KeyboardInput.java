package com.angrynerds.tedsdream.input;

import com.angrynerds.tedsdream.util.States;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * Author: Franz Benthin
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
    public States.Animation getState() {
        // jump
        if (get_isA()) return States.Animation.JUMP;
        // dash
        else if (get_isB() && get_stickX() >= 0) return States.Animation. DASH_RIGHT;
        else if (get_isB() && get_stickX() < 0) return States.Animation. DASH_LEFT;
        // attack
        else if (get_isD()) return States.Animation. ATTACK;
        // run
        else if (get_stickX() != 0 || get_stickY() != 0) return States.Animation. RUN;

        return States.Animation. IDLE;

    }

    @Override
    public boolean get_isA() {
        return Gdx.input.isKeyPressed(Keys.A);
    }

    @Override
    public boolean get_isB() {
        return Gdx.input.isKeyPressed(Keys.S);
    }

    public boolean get_isD() {
        return Gdx.input.isKeyPressed(Keys.D);
    }

    @Override
    public void setState(States.Animation state) {
//        this.state = state;
    }
}
