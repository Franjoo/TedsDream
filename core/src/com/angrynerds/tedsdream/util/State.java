package com.angrynerds.tedsdream.util;


import java.io.Serializable;

public final class State implements Serializable {

    public static final int JUMP = 0;
    public static final int SMASH = 1;
    public static final int RUN = 2;
    public static final int IDLE = 3;
    public static final int DASH_RIGHT = 5;
    public static final int DASH_LEFT = 6;
    public static final int ATTACK = 7;
    public static final int DEAD = 8;

}
