package com.angrynerds.tedsdream.gameobjects.creatures;

import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;

/**
 * Author: Franz Benthin
 */
public class Skeletal extends GameObject {

    // debug attributes
    protected boolean showBounds;

    // skeleton
    protected SkeletonData skeletonData;
    protected SkeletonBounds skeletonBounds;
    protected Skeleton skeleton;

    // renderer
    protected SkeletonRenderer skeletonRenderer;
    protected SkeletonRendererDebug skeletonDebugRenderer;

    // constructor params
    private String path;
    protected float scale;
    private String skin;
    private TextureAtlas atlas;

    public Skeletal(TextureAtlas atlas, String path, float scale, String skin) {
        this.path = path;
        this.scale = scale;
        this.skin = skin;
        this.atlas = atlas;

        setup();
    }

    public Skeletal(TextureAtlas atlas, String path, float scale) {
        this(atlas, path, scale, null);
    }

    public Skeletal(TextureAtlas atlas, String path) {
        this(atlas, path, 1, null);
    }

    private void setup() {
        // atlas
        // atlas = new TextureAtlas(Gdx.files.internal(path + name + ".atlas"));
        showBounds = true;

        // skeleton json
        SkeletonJson skeletonJson = new SkeletonJson(atlas);

        // set scale
        skeletonJson.setScale(scale);

        // get skeletonData
        skeletonData = skeletonJson.readSkeletonData(Gdx.files.internal(path + ".json"));

        // setup skeleton
        skeleton = new Skeleton(skeletonData);

        // setup skeleton Bounds
        skeletonBounds = new SkeletonBounds();

        if (skin != null) {
            // set skin
            skeleton.setSkin(skin);
            skeleton.setToSetupPose();

            // recreate skeleton with skin
            skeleton = new Skeleton(skeleton);
            skeleton.updateWorldTransform();
        }

        // setup renderer
        skeletonRenderer = new SkeletonRenderer();
        skeletonDebugRenderer = new SkeletonRendererDebug();
    }


    @Override
    public void draw(SpriteBatch batch) {
        if(showBounds){
            skeletonDebugRenderer.draw(skeleton);
        }

        skeletonRenderer.draw(batch, skeleton);
    }

    @Override
    public void update(float delta) {
        // set skeleton position
        skeleton.setX(x);
        skeleton.setY(y);

        // update state
        skeleton.updateWorldTransform();
        skeleton.update(delta);
        skeletonBounds.update(skeleton, true);
    }

    public SkeletonBounds getSkeletonBounds() {
        return skeletonBounds;
    }

    public SkeletonData getSkeletonData() {
        return skeletonData;
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }
}
