package com.angrynerds.tedsdream.events;

import com.angrynerds.tedsdream.screens.GameController;

import java.io.Serializable;

/**
 * User: Franjo
 */
public class AddPlayerEvent implements Serializable {

    private int id;

    public AddPlayerEvent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    //    @Override
    public void apply(final GameController game) {

//        Gdx.app.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                game.addPlayer(id);
//            }
//        });
    }
}
