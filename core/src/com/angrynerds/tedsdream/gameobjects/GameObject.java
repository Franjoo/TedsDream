package com.angrynerds.tedsdream.gameobjects;

/**
 * User: Franjo
 */
public abstract class GameObject {

    protected float x;
    protected float y;
    protected float z;
    protected float width;
    protected float height;

    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getZ() { return z; }


    public float getWidth() { return width;}

    public float getHeight() { return  height;}

}
