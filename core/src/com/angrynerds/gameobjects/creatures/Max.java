package com.angrynerds.gameobjects.creatures;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Event;

/**
 * User: Franjo
 * Date: 19.11.13
 * Time: 12:32
 * Project: GameDemo
 */
public class Max extends Creature {

    Array<Event> events;
//    Animation walkAnimation;
    Animation runAnimation;
    Animation dieAnimation;

    public Max(String name, String atlas, String skin, float scale) {
        super(name, atlas, skin, scale);

//        walkAnimation = skeletonData.findAnimation("walk");
//        runAnimation = skeletonData.findAnimation("run_test");
        dieAnimation = skeletonData.findAnimation("die");
        showBounds = false;

        x = 300;
        y = 30;
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

//        runAnimation.apply(skeleton, skeleton.getTime(), skeleton.getTime(), true, events);
        dieAnimation.apply(skeleton, skeleton.getTime(), skeleton.getTime()/2, true, events);
    }

    @Override
    public void attack() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
