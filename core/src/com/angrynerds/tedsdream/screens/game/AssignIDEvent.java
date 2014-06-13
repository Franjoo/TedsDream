package com.angrynerds.tedsdream.screens.game;

import com.angrynerds.tedsdream.gameobjects.Player;

/**
 * User: Franjo
 */
public class AssignIDEvent implements PlayerEvent {

    public final int id;

    public AssignIDEvent(int id) {
        this.id = id;
    }

    @Override
    public void apply(Player player) {
        player.setID(id);
    }
}
