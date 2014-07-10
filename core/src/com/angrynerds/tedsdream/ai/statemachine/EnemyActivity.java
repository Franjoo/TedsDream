package com.angrynerds.tedsdream.ai.statemachine;

import com.angrynerds.tedsdream.gameobjects.Creature;
import com.angrynerds.tedsdream.screens.GameController;
import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public abstract class EnemyActivity implements IActivity {

    protected Array<IActivity> neighbors;
    protected final Creature actor;
    protected final GameController game;

    public EnemyActivity(Creature actor, GameController game) {
        this.actor = actor;
        this.game = game;

        neighbors = new Array<>();
    }

    @Override
    public float getScore() {
        return 0;
    }

    @Override
    public Array<IActivity> getNeighbors() {
        return neighbors;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
