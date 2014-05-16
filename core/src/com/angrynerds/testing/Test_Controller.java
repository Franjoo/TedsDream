package com.angrynerds.testing;

import com.angrynerds.input.gamepads.X360Gamepad;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;

/**
 * User: Franjo
 * Date: 25.10.13
 * Time: 19:42
 * Project: Main
 */
public class Test_Controller implements ApplicationListener {

    Array<X360Gamepad> controllers;

    @Override
    public void create() {

        Array<Controller> list = Controllers.getControllers();
        controllers = new Array<X360Gamepad>();

        for (int i = 0; i < list.size; i++) {
            controllers.add(new X360Gamepad(list.get(i)));
            System.out.println(controllers.get(i).getName());
        }


    }

    @Override
    public void resize(int i, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void render() {

        for (int i = 0; i < controllers.size; i++) {
//            String s = controllers.get(i).trace();
//            if (!s.equals("")) System.out.println(s);
            controllers.get(i).test();
        }
    }

    @Override
    public void pause() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
