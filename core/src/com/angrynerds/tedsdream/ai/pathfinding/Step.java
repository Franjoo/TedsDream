package com.angrynerds.tedsdream.ai.pathfinding;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian
 * Date: 07.11.13
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public class Step {



    private int x;
    private int y;

    public Step(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
