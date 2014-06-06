package com.angrynerds.tedsdream.screens.game;

import com.badlogic.gdx.Gdx;

/**
 * User: Franjo
 */
public class AddPlayerEvent implements GameEvent {

    private int id;

    public AddPlayerEvent(int id) {
        this.id = id;
    }

    @Override
    public void apply(final _MPGame game) {

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.addPlayer(id);
            }
        });
    }
}
