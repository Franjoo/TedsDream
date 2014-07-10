package com.angrynerds.tedsdream.ai.statemachine;

import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public interface IActivity {

    boolean update(float delta);

    float getScore();

    Array<IActivity> getNeighbors();

    String getName();

}
