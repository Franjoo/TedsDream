package com.angrynerds.tedsdream.core;

import com.angrynerds.tedsdream.screens.AbstractScreen;
import com.angrynerds.tedsdream.screens.MainMenu;
import com.angrynerds.tedsdream.screens.SplashScreen;
import com.angrynerds.tedsdream.screens.PlayScreen;
import com.badlogic.gdx.Game;

/**
 * class that is reponsible for updating and rendering all the
 * screen which are in use
 */
public class Controller extends Game {

    // screens
    public AbstractScreen playScreen;
    public AbstractScreen mainMenu;
    public AbstractScreen splashScreen;

    public Controller() {

        // create screens
        mainMenu = new MainMenu(this);
        playScreen = new PlayScreen(this);
        splashScreen = new SplashScreen(this);

        // set splash screen on game start
        this.setScreen(splashScreen);
    }

    @Override
    public void create() {

    }
}
