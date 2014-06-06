package com.angrynerds.tedsdream.net;

import com.angrynerds.tedsdream.gameobjects.Player;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 25.05.2014
 * Time: 18:45
 */
public class Update implements Serializable {

    private float x,y;

    public void set(Player player){
        x = player.getX();
        y = player.getY();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
