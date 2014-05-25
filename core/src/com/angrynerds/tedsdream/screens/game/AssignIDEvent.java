package com.angrynerds.tedsdream.screens.game;

import com.angrynerds.tedsdream.gameobjects.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 25.05.2014
 * Time: 19:09
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
