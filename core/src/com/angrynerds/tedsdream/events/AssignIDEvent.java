package com.angrynerds.tedsdream.events;

import java.io.Serializable;

/**
 * User: Franjo
 */
public class AssignIDEvent implements Serializable{

    public final int id;

    public AssignIDEvent(int id) {
        this.id = id;
    }

//    @Override
//    public void apply(Player player) {
//        player.setID(id);
//    }
}
