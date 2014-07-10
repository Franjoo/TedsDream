package com.angrynerds.tedsdream.camera;

import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraHelper {

    private static final float MAX_ZOOM_IN = 0.25f;
    private static final float MAX_ZOOM_OUT = 4.0f;

    private float aX = 0.07f;
    private float aY = 0.1f;

    private Vector2 position;
    private float zoom;
    private GameObject target;

    public float deltaX;
    public float deltaY;

    public CameraHelper() {
        position = new Vector2();
        zoom = 1;

        position.x = C.VIEWPORT_WIDTH / 2;
        position.y = C.VIEWPORT_HEIGHT / 2;
    }

    public void update(float deltaTime) {

        handleDebugControlls();

        if (!hasTarget()) {
            return;
        }


        float focusY = 150;

        float qX = target.getX();
        float qY = target.getY() + focusY;

        deltaX = qX - position.x;
        deltaY = qY - position.y;

        position.x += deltaX * aX;
        position.y += deltaY * aY;

        if (position.y <= C.VIEWPORT_HEIGHT / 2) position.y = C.VIEWPORT_HEIGHT / 2;
        if (position.y >= C.VIEWPORT_HEIGHT / 2 + focusY) position.y = C.VIEWPORT_HEIGHT / 2 + focusY;

//        position.x = Math.round(position.x);
//        position.y = Math.round(position.y);

    }

    private void handleDebugControlls() {
        // Zoom in
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            addZoom(-0.006f);
            if (getZoom() < MAX_ZOOM_IN) setZoom(MAX_ZOOM_IN);
            System.out.println("Zoom: " + getZoom());
        }

        // Zoom out
        if (Gdx.input.isKeyPressed(Input.Keys.Y)) {
            addZoom(0.006f);
            if (getZoom() > MAX_ZOOM_OUT) setZoom(MAX_ZOOM_OUT);
            System.out.println("Zoom: " + getZoom());
        }

        // set Zoom 1
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_3)) {
            setZoom(1);
            System.out.println("Zoom: " + getZoom());
        }
    }


    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void addZoom(float amount) {
        setZoom(zoom + amount);
    }

    public void setZoom(float zoom) {
        this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
    }

    public float getZoom() {
        return zoom;
    }

    public void setTarget(GameObject target) {
        this.target = target;
        position.x = target.getX();
        position.y = target.getY() + C.VIEWPORT_HEIGHT / 2;

//        position.x = C.VIEWPORT_WIDTH / 2;
//        position.y = C.VIEWPORT_HEIGHT / 2;
    }

    public GameObject getTarget() {
        return target;
    }

    public boolean hasTarget(Sprite target) {
        return hasTarget() && this.target.equals(target);
    }

    public boolean hasTarget() {
        return target != null;
    }

    public void applyTo(OrthographicCamera camera) {
        camera.position.x = Math.round(position.x);
        camera.position.y = Math.round(position.y);

//        camera.position.x = (position.x);
//        camera.position.y = (position.y);

        camera.zoom = zoom;
        camera.update();
    }

}
