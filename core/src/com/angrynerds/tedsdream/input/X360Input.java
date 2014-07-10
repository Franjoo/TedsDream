package com.angrynerds.tedsdream.input;

import com.angrynerds.tedsdream.util.States;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;

/**
 * User: Franjo
 * Date: 25.10.13
 * Time: 19:40
 * Project: Main
 */
public class X360Input implements IGameInputController {

    public static int NUM_CONTROLLERS = 0;
    public final int id;

    // buttons
    public static final int BTN_A = 0;
    public static final int BTN_B = 1;
    public static final int BTN_X = 2;
    public static final int BTN_Y = 3;
    public static final int L1 = 4;
    public static final int R1 = 5;
    public static final int BTN_BACK = 6;
    public static final int BTN_START = 7;
    public static final int L3 = 8;
    public static final int R3 = 9;

    // sticks
    private static final float STICK_DEAD = 0.3f;
    private static final int STICK_LEFT_X = 1;
    private static final int STICK_LEFT_Y = 0;
    private static final int STICK_RIGHT_X = 3;
    private static final int STICK_RIGHT_Y = 2;

    private Vector2 vec2 = new Vector2();

    // trigger
    public static final int TRIGGER = 4;
    public static final float TRGGER_MIN_VALUE = 0.0005f;

//    public static final int R2 = ;
//    public static final int L2 = ;

    private Controller c;

    public X360Input(Controller controller) {
        c = controller;

        id = NUM_CONTROLLERS;

        System.out.println("Controller created");
    }

    public String getName() {
        return c.getName();
    }

    //<editor-fold desc="button pressed getters">
    public boolean is_A_pressed() {
        return c.getButton(BTN_A);
    }

    public boolean is_B_pressed() {
        return c.getButton(BTN_B);
    }

    public boolean is_X_pressed() {
        return c.getButton(BTN_X);
    }

    public boolean is_Y_pressed() {
        return c.getButton(BTN_Y);
    }

    public boolean is_L1_pressed() {
        return c.getButton(L1);
    }

    public boolean is_R1_pressed() {
        return c.getButton(R1);
    }

    public boolean is_BACK_pressed() {
        return c.getButton(BTN_BACK);
    }

    public boolean is_START_pressed() {
        return c.getButton(BTN_START);
    }

    public boolean is_L3_pressed() {
        return c.getButton(L3);
    }

    public boolean is_R3_pressed() {
        return c.getButton(R3);
    }


    //</editor-fold>

    //<editor-fold desc="sticks">
    public float stick_left_X() {
        if (Math.abs(c.getAxis(STICK_LEFT_X)) > STICK_DEAD) return c.getAxis(STICK_LEFT_X);
        return 0;
    }

    public float stick_left_Y() {
        if (Math.abs(c.getAxis(STICK_LEFT_Y)) > STICK_DEAD) return -c.getAxis(STICK_LEFT_Y);
        return 0;
    }

    public float stick_left_intensity() {
//        vec2 = new Vector2(stick_left_X(),stick_left_Y());
//        System.out.println("vec 2 length :" + vec2.len2());
        float magnitude = (float) Math.sqrt(stick_left_X() * stick_left_X() + stick_left_Y() * stick_left_Y());
//        System.out.println(magnitude);
        return magnitude;
    }

    public float stick_right_X() {
        return c.getAxis(STICK_RIGHT_X);
    }

    public float stick_right_Y() {
        return c.getAxis(STICK_RIGHT_Y);
    }
    //</editor-fold>

    public float trigger_left() {
        float t = c.getAxis(TRIGGER);
        if (t > TRGGER_MIN_VALUE) {
            return t;
        }
        return 0;
    }

    public float trigger_right() {
        float t = c.getAxis(TRIGGER);
        if (t < 0) {
            return -t;
        }
        return 0;
    }

    public void test() {
//        for (int i = 0; i < 10; i++) {
//            if (c.getButton(i)) System.out.println("-> " + i);
//        }

//        System.out.println("L:" + trigger_left() + " | R:" + trigger_right());
//        System.out.println(c.;
//        System.out.println(c.getAxis(4));
    }


    public void trace() {
        StringBuilder s = new StringBuilder();
        // button pressed getters
        if (is_A_pressed()) s.append("Button [ A ] pressed");
        if (is_B_pressed()) s.append("Button [ B ] pressed");
        if (is_X_pressed()) s.append("Button [ X ] pressed");
        if (is_Y_pressed()) s.append("Button [ Y ] pressed");
        if (is_L1_pressed()) s.append("Button [ L1 ] pressed");
        if (is_R1_pressed()) s.append("Button [ R1 ] pressed");
        if (is_BACK_pressed()) s.append("Button [ BACK ] pressed");
        if (is_START_pressed()) s.append("Button [ START ] pressed");
        if (is_L3_pressed()) s.append("Button [ L3 ] pressed");
        if (is_R3_pressed()) s.append("Button [ R3 ] pressed");

        if (!s.equals("")) System.out.println(s.toString());
    }

    @Override
    public float get_stickX() {
        return stick_left_X();
    }

    @Override
    public float get_stickY() {
        return stick_left_Y();
    }

    @Override
    public States.Animation getState() {
        // jump
        if(is_A_pressed()) return States.Animation.JUMP;
        // attack
        if(is_X_pressed()) return States.Animation.ATTACK;
        // dash
        if(is_B_pressed() && stick_left_X() >= 0) return States.Animation.DASH_RIGHT;
        if(is_B_pressed() && stick_left_X() < 0) return States.Animation.DASH_LEFT;

        // run
        if(stick_left_X() != 0 || stick_left_Y() != 0) return States.Animation.RUN;

        return States.Animation.IDLE;

    }

    @Override
    public boolean get_isA() {
        return is_A_pressed();
    }

    @Override
    public boolean get_isB() {
        return is_B_pressed();
    }

    @Override
    public void setState(States.Animation state) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
