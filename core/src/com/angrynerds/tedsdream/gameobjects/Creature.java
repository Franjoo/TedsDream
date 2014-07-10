package com.angrynerds.tedsdream.gameobjects;

import com.angrynerds.tedsdream.gameobjects.creatures.Skeletal;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;

/**
 * Author: Franz Benthin
 */
public class Creature extends Skeletal {

    // Movement Constants
    public static enum Move {
        WALK, RUN, HIDE
    }

    // creature stats
    protected float ap,hp;
    protected float apMax, hpMax;

    // movement
    protected Vector2 direction = new Vector2();

    // animation
    protected AnimationStateData stateData;
    protected AnimationState state;

    public Creature(TextureAtlas atlas, String path, float scale, String skin, float ap, float hp) {
        super(atlas, path, scale, skin);

        this.ap = ap;
        this.hp = hp;

        // set to init values
        apMax = this.ap;
        hpMax = this.hp;

        // animation relevant
        stateData = new AnimationStateData(skeletonData);
        state = new AnimationState(stateData);
        state.setAnimation(0,skeletonData.getAnimations().first().getName(),true);
    }

    public Creature(TextureAtlas atlas, String path, float scale, float ap, float hp) {
        this(atlas, path, scale, null, ap, hp);
    }

    public Creature(TextureAtlas atlas, String path, float ap, float hp) {
        this(atlas, path, 1, null, ap, hp);
    }

    public void init(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setAnimation(String name, boolean loop) {
        state.setAnimation(0, name, loop);
    }

    public String getCurrentAnimationName() {
        return state.getCurrent(0).getAnimation().getName();
    }

    public void updateAnimations(float delta) {
        super.update(delta);

        // set flip
        skeleton.setFlipX(direction.x > 0);

        // update animation
        state.apply(skeleton);
        state.update(delta);
    }

    public void updatePosition(float delta) {
        x += direction.x * delta;
        y += direction.y * delta;
    }

    @Override
    public void update(float delta) {
        updateAnimations(delta);
        updatePosition(delta);
    }

    public void moveInDirection(float dx, float dy, float speed) {
        direction.set(dx, dy).scl(speed);
    }

    public void moveInDirection(Vector2 direction, float speed) {
        this.direction.set(direction.x, direction.y).scl(speed);
    }

    public void setDamage(float dmg) {
        hp -= dmg;
    }

    public float getHP() {
        return hp;
    }

    public float getAP() {
        return ap;
    }

    public float getApMax() {
        return apMax;
    }

    public float getHpMax() {
        return hpMax;
    }

    public float getSpeed(Move speed) {
        switch (speed) {
            case WALK:
                return 100;
            case RUN:
                return 160;
            case HIDE:
                return 140;
        }

        return 0;
    }

    public float getSize(){
        return scale;
    }

//    protected SkeletonData getSkeletonData(){
//        return skeletonData;
//    }


}
