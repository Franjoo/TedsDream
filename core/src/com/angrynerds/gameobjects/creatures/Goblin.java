package com.angrynerds.gameobjects.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Event;

/**
 * User: Franjo
 * Date: 07.11.13
 * Time: 22:23
 * Project: GameDemo
 */
public class Goblin extends Creature {

    Array<Event> events;
    Animation walkAnimation;

    public Goblin(String name, String path, String skin, float scale) {
        super(name, path, skin, scale);

        walkAnimation = skeletonData.findAnimation("walk");
        showBounds = true;

        x = Gdx.graphics.getWidth() - 250;
        y = 30;
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        walkAnimation.apply(skeleton, skeleton.getTime(), skeleton.getTime(), true, events);
    }

    @Override
    public void attack() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
