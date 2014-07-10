package com.angrynerds.tedsdream.events.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Author: Franz Benthin
 */
public class ZoomEffect implements CameraEvent {

    private float from;
    private float to;
    private float current;

    public ZoomEffect(){
        from = 3;
        to = 1;
        current = from;
    }

    @Override
    public void apply(OrthographicCamera camera,float delta) {
         if(current != to){
             current += (to - from) * delta;
             if(current < to) current = to;
         }

        camera.zoom = current;
        camera.update();
    }
}
