package com.angrynerds.tedsdream.core;

import com.angrynerds.tedsdream.net.GameServer;
import com.angrynerds.tedsdream.screens.*;
import com.angrynerds.tedsdream.screens.GameController;
import com.angrynerds.tedsdream.screens.multiplayer.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Author: Franz Benthin
 */
public class Controller extends Game {

    // settings (should be in settings menu)
    public float difficulty = 1.2f;

    public TiledMap tiledMap;

    // screens
    public AbstractScreen loadingScreen;
    public GameController playScreen;
    public MainMenu mainMenu;
    public Screen introScreen;
    public AbstractScreen splashScreen;

    // multiplayer sceens
    public Screen multiplayer_choose;
    public Screen multiplayer_configuration;
    public Screen multiplayer_connect;
    public Screen multiplayer_lobby;
    public GameController MPGame;

    // game server
    public GameServer server;
    public Screen nextScreen;

    public Controller() {

        // create screens
        loadingScreen = new LoadingScreen(this);
        splashScreen = new SplashScreen(this);
        mainMenu = new MainMenu(this);
        introScreen = new IntroScreen(this,);

        // create multiplayer screens
        multiplayer_choose = new MultiplayerChooseMenu(this);
        multiplayer_configuration = new ServerConfigurationMenu(this);
        multiplayer_connect = new ServerConnectMenu(this);
        multiplayer_lobby = new ServerLobby(this);

        // create server
        server = new GameServer();

        // set splash screen on game start
        this.setScreen(loadingScreen);


    }

    @Override
    public void create() {

    }

    @Override
    public void dispose() {
//        server.dispose();
    }


}
