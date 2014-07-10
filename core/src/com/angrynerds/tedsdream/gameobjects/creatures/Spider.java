package com.angrynerds.tedsdream.gameobjects.creatures;

import com.angrynerds.tedsdream.ai.statemachine.Activities;
import com.angrynerds.tedsdream.ai.statemachine.FSM;
import com.angrynerds.tedsdream.ai.statemachine.IActivity;
import com.angrynerds.tedsdream.gameobjects.Creature;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Author: Franz Benthin
 */
public class Spider extends Creature {

    // CREATURE TYPE CONSTANTS
    public static final float SCALE_MAX = 0.15f;
    public static final float SCALE_MIN = 0.08f;
    public static final float HP_MAX = 125;
    public static final float HP_MIN = 60;
    public static final float AP_MAX = 30;
    public static final float AP_MIN = 8;

    private FSM ai;

    private float scale; // relative to scale bounds

    public Spider(TextureAtlas atlas, float scale, float ap, float hp) {
        super(atlas, "spine/spinne/", scale, ap, hp);

        this.scale = scale / SCALE_MAX;

        createAI();

        mixAnimations();
    }

    public static Spider create(TextureAtlas atlas) {

        float size = (float) Math.random();
        float scale = (SCALE_MIN + size * (SCALE_MAX - SCALE_MIN));
        float hp = HP_MIN + (HP_MAX - HP_MIN) * size;
        float ap = AP_MIN + (AP_MAX - AP_MIN) * size;

        return new Spider(atlas, scale, hp, ap);
    }

    private void createAI() {
        ai = new FSM();

        // activity graph
        IActivity ac_runToPlayer = new Activities.RunToPlayer(this);
        IActivity ac_attackPlayer = new Activities.FearfulAttackPlayer(this, getHP() * 0.3f);
        IActivity ac_hide = new Activities.HideFromPlayer(this, getHP() * 0.3f);
        IActivity ac_waitForPlayer = new Activities.WaitForPlayer(this);

        ac_waitForPlayer.getNeighbors().add(ac_runToPlayer);
        ac_runToPlayer.getNeighbors().add(ac_attackPlayer);
        ac_runToPlayer.getNeighbors().add(ac_hide);
        ac_attackPlayer.getNeighbors().add(ac_runToPlayer);
        ac_attackPlayer.getNeighbors().add(ac_hide);

        ai.setCurrentActivity(ac_waitForPlayer);
    }

    private void mixAnimations() {
        stateData.setMix("move", "attack", 0.2f);
        stateData.setMix("attack", "move", 0.2f);
        stateData.setMix("attack", "die", 0.5f);
        stateData.setMix("move", "die", 0.2f);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        ai.update(deltaTime);
    }

    @Override
    public float getSpeed(Move speed) {
        return super.getSpeed(speed);
    }

    public float getScale() {
        return scale;
    }
}
