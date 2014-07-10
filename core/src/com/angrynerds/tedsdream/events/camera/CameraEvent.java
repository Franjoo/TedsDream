package com.angrynerds.tedsdream.events.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Author: Franz Benthin
 */
public interface CameraEvent {

    void apply(OrthographicCamera camera, float delta);

}
