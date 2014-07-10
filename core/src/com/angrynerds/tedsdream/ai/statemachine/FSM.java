package com.angrynerds.tedsdream.ai.statemachine;

import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public class FSM {

    public IActivity currentActivity;
    public Array<IActivity> activities;

    public FSM() {
        activities = new Array<>();
    }

    public FSM(Array<IActivity> activities) {
        this.activities = activities;
        if (activities.size != 0) {
            currentActivity = activities.first();
        }
    }

    public void setCurrentActivity(IActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public IActivity getCurrentActivity() {
        return currentActivity;
    }

    public void update(float delta) {
        boolean completed = currentActivity.update(delta);
        if (completed) {
            setCurrentActivity(Activity.FindBestNextActivity(currentActivity.getNeighbors()));
        }
    }

}
