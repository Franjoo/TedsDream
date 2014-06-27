package com.angrynerds.tedsdream.screens;

import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.gameobjects.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
* @author Tim Kunkel
*/
public class LoadingScreen extends AbstractScreen {

    private Stage stage;

    private Image loadingFrame;
    private Image screenBg;
    private Image loadingBg;
    private Actor loadingBar;

    private float percent;

    private Controller game;

    private AssetManager manager;

    public LoadingScreen(Controller game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Tell the manager to load assets for the loading screen
        manager = new AssetManager();
        manager.load("ui/loading_screen/loadingscreen.pack", TextureAtlas.class);

        // Wait until they are finished loading
        manager.finishLoading();

        stage = new Stage();

        // Get our textureatlas from the manager
        TextureAtlas atlas = manager.get("ui/loading_screen/loadingscreen.pack", TextureAtlas.class);

        loadingFrame = new Image(atlas.findRegion("loading_frame"));
        screenBg = new Image(atlas.findRegion("screen_background"));
        loadingBg = new Image(atlas.findRegion("loading_background"));
        loadingBar = new Image(atlas.findRegion("loading_bar"));

        stage.addActor(screenBg);
        stage.addActor(loadingBg);
        stage.addActor(loadingFrame);
        stage.addActor(loadingBar);

        // Add everything to be loaded, for instance:
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load(Map.mapPath, TiledMap.class);
    }

    @Override
    public void resize(int width, int height) {
        // Set our screen to always be XXX x 480 in size
        width = 480 * width / height;
        height = 480;

        float scalingFactor = height / screenBg.getHeight();
        screenBg.setSize(screenBg.getWidth() * scalingFactor, screenBg.getHeight() * scalingFactor);
        screenBg.setX((width - screenBg.getWidth()) / 2);
        screenBg.setY((height - screenBg.getHeight()) / 2);

        scalingFactor = height / loadingBg.getHeight();
        loadingBg.setSize(loadingBg.getWidth() * scalingFactor, loadingBg.getHeight() * scalingFactor);

        scalingFactor =  (loadingBg.getWidth() / loadingFrame.getWidth()) / 1.7f;
        loadingFrame.setSize(loadingFrame.getWidth() * scalingFactor, loadingFrame.getHeight() * scalingFactor);
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        loadingBar.setSize(loadingBar.getWidth() * scalingFactor, loadingBar.getHeight() * scalingFactor);
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 35);

        loadingBg.setX(loadingFrame.getX() - (loadingBg.getWidth() - loadingFrame.getWidth()) / 2);
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (manager.update()) { // Load some, will return true if done loading
            if (Gdx.input.isTouched()) { // If the screen is touched after the game is done loading, go to the main menu screen
                game.tiledMap = manager.get(Map.mapPath);
                game.setScreen(new MainMenu(game));
            }
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, manager.getProgress(), 0.1f);

        loadingBar.setWidth((loadingFrame.getWidth() - 30) * percent);

        // Show the loading screen
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        // Dispose the loading assets as we no longer need them
        manager.unload("ui/loading.pack");
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}