package com.angrynerds.gameobjects.creatures;

import com.angrynerds.gameobjects.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;

/**
 * User: Franjo
 * Date: 07.11.13
 * Time: 14:38
 * Project: GameDemo
 */
public abstract class Creature extends GameObject {
    // debug attributes
    public boolean showBounds;

    // skeleton
    protected SkeletonData skeletonData;
    public Skeleton skeleton;
    private SkeletonJson skeletonJson;
    private SkeletonBounds skeletonBounds;

    // renderer
    protected SkeletonRenderer skeletonRenderer;
    protected SkeletonRendererDebug skeletonDebugRenderer;

    // atlas
    private TextureAtlas atlas;

    // constructor params
    private String name;
    private String path;
    private float scale;
    private String skin;

    public Creature(String name, String path, String skin, float scale) {
        this.name = name;
        this.path = path;
        this.scale = scale;
        this.skin = skin;

        create();
    }

    public Creature(String name, String path, String skin) {
        this(name, path, skin, 1);
    }

    private void create() {
        // atlas
        atlas = new TextureAtlas(Gdx.files.internal(path + name + ".atlas"));
        showBounds = true;

        // skeleton json
        skeletonJson = new SkeletonJson(atlas);

        // set scale
        skeletonJson.setScale(scale);

        // get skeletonData
        skeletonData = skeletonJson.readSkeletonData(Gdx.files.internal(path + name + ".json"));

        // create skeleton
        skeleton = new Skeleton(skeletonData);

        // create skeleton Bounds
        skeletonBounds = new SkeletonBounds();

        if (skin != null) {
            // set skin
            skeleton.setSkin(skin);
            skeleton.setToSetupPose();

            // recreate skeleton with skin
            skeleton = new Skeleton(skeleton);
            skeleton.updateWorldTransform();
        }

        // create renderer
        skeletonRenderer = new SkeletonRenderer();
        skeletonDebugRenderer = new SkeletonRendererDebug();
    }

    public void update(float deltaTime) {
        // set skeleton position
        skeleton.setX(x);
        skeleton.setY(y);

        // update state
        skeleton.updateWorldTransform();
        skeleton.update(deltaTime);
        skeletonBounds.update(skeleton,true);
    }

    public void render(SpriteBatch batch) {
        // draw skeleton
        //batch.draw(skeletonDebugRenderer.draw(skeleton);
        //skeletonDebugRenderer.draw(skeleton);

        batch.begin();
        skeletonRenderer.draw(batch, skeleton);
        batch.end();
    }

    public SkeletonBounds getSkeletonBounds(){
        return skeletonBounds;
    }

    public abstract void attack();
}
