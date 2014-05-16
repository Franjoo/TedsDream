package com.angrynerds.game.core;

import com.angrynerds.game.screens.AbstractScreen;
import com.angrynerds.game.screens.mainmenu.MainMenu;
import com.angrynerds.game.screens.mainmenu.StartingScreen;
import com.angrynerds.game.screens.play.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Disposable;

/**
 * class that is reponsible for updating and rendering all the
 * screen which are in use
 */
public class GameController extends Game implements Disposable {

    // screens
    private PlayScreen playScreen;
    private MainMenu mainMenu;
    private AbstractScreen activeScreen;
    private StartingScreen startingScreen;

    // game states
    private static String STATE;
    private static final String PLAY = "play";
    private static final String MENU = "menu";


    public GameController() {
        init();
    }

    /**
     * initializes the screens which should be used
     */
    private void init() {
        mainMenu = new MainMenu(this);
        playScreen = new PlayScreen(this);
        startingScreen = new StartingScreen(this);
        activeScreen = startingScreen;
        this.setScreen(activeScreen);
    }

    @Override
    public void create() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * updates the screens which are currently in use
     *
     * @param deltaTime time since last frame
     */
    public void update(float deltaTime) {
        activeScreen.update(deltaTime);
        activeScreen.render(deltaTime);
//        mainMenu.render(deltaTime);
        //playScreen.update(deltaTime);
        //playScreen.render(deltaTime);
    }

    @Override
    public void dispose() {
        activeScreen.dispose();
    }

    /**
     * called when the application is resized
     *
     * @param width  new width in pixels
     * @param height new height in pixels
     */
    public void resize(int width, int height) {
        activeScreen.resize(width, height);
    }

    /**
     * called when the application is paused
     */
    public void pause() {
        // TODO
    }

    /**
     * called when the application is resumed from a paused state
     */
    public void resume() {
        // TODO

    }

    public PlayScreen getPlayScreen() {
        return playScreen;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public StartingScreen getStartingScreen(){
        return startingScreen;
    }

    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void setActiveScreen(AbstractScreen activeScreen) {
        this.activeScreen = activeScreen;
    }
}
