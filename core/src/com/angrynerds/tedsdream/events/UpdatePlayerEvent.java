package com.angrynerds.tedsdream.events;

import com.angrynerds.tedsdream.util.States;

import java.io.Serializable;

/**
 * Author: Franz Benthin
 */
public class UpdatePlayerEvent implements Serializable {

    private int id;
    private float x, y;
    private boolean flip;
    private States.Animation animationState;

    public void set(int id, float x, float y, States.Animation animationState, boolean flip) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.animationState = animationState;
        this.flip = flip;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isFlip() {
        return flip;
    }

    public States.Animation getAnimationState() {
        return animationState;
    }

    //    @Override
//    public void apply(_MPGame game) {
//        System.out.println("UPDATE");
//
//        Player player = game.getPlayers().get(id).getPlayer();
//
//        player.setPosition(x, y);
//        player.setState(animationState);
//        player.setFlip(flip);
//
//    }
}
