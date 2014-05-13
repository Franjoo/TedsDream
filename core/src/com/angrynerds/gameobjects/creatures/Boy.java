package com.angrynerds.gameobjects.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
public class Boy extends Creature {

    Array<Event> events;
    Animation walkAnimation;

    public Boy(String name, String path, String skin, float scale) {
        super(name, path, skin, scale);

        walkAnimation = skeletonData.findAnimation("walk");
        showBounds = false;

        x = 160;
        y = 30;


    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

           x = getX() + 320 * deltaTime;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            x = getX() - 320 * deltaTime;



        }

        walkAnimation.apply(skeleton, skeleton.getTime(), skeleton.getTime(), true, events);
       // for(int i = 0; i< getSkeletonBounds().getBoundingBoxes().size;i++)
           //System.out.println(getSkeletonBounds().getBoundingBoxes());
    }

    @Override
    public void attack() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}