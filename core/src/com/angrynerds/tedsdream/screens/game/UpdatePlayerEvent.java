package com.angrynerds.tedsdream.screens.game;

import com.angrynerds.tedsdream.gameobjects.Player;

/**
 * User: Franjo
 */
public class UpdatePlayerEvent implements GameEvent {

    private int id;
    private float x, y, z;
    private boolean flip;
    private int animationState;

    public void set(int id, float x, float z, int animationState, boolean flip) {
        this.id = id;
        this.x = x;
        this.y = 0;
        this.z = z;
        this.animationState = animationState;
        this.flip = flip;
    }

    @Override
    public void apply(_MPGame game) {
        Player player = game.getPlayers().get(id).getPlayer();

        player.setPosition(x, y, z);
        player.setState(animationState);
        player.setFlip(flip);

    }
}
