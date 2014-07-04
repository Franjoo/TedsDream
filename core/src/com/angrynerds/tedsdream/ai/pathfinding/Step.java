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
    private int z;

    public Step(int x, int z) {
        this.x = x;
        this.y = 0;
        this.z = z;
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

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
