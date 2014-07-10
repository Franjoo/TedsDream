package com.angrynerds.tedsdream.events.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Author: Franz Benthin
 */
public class ChatterEffect implements CameraEvent {

    private float duration;
    private float time;

    // offsets
    private float currentOffsetX;
    private float currentOffsetY;
    private float maxOffsetX;
    private float maxOffsetY;

    // offset
    private float dirX;
    private float dirY;


    public ChatterEffect(){
        maxOffsetX = 20;
        maxOffsetY = 20;
        duration = 5;
        time = 0;
    }

    @Override
    public void apply(OrthographicCamera camera,float delta) {

    }
}
