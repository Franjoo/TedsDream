package com.angrynerds.game.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.GL10;

/**
 * Main class which provides the render() method to realize the game loop.
 * Through the implementation of the ApplicationListener Interface, this class
 * follows the standard Android activity life-cycle.
 */
public class Main implements ApplicationListener {
    private static final String TAG = Main.class.getSimpleName();

    // debug bools
    private static final boolean FPS_LOGGING = false;

    private GameController gameController;
    private FPSLogger fpsLogger;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        gameController = new GameController();
        fpsLogger = new FPSLogger();
    }

    @Override
    public void dispose() {
        gameController.dispose();
    }

    @Override
    public void render() {

        // background color
        Color c = Color.valueOf("BAB6BF");
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update
        gameController.update(Gdx.graphics.getDeltaTime());

        // log fps
        if (FPS_LOGGING) fpsLogger.log();
    }

    @Override
    public void resize(int width, int height) {
        gameController.resize(width, height);
    }

    @Override
    public void pause() {
        gameController.pause();
    }

    @Override
    public void resume() {
        gameController.resume();
    }

}
