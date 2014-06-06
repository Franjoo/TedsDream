package com.angrynerds.tedsdream.core;

import com.angrynerds.tedsdream.net.GameServer;
import com.angrynerds.tedsdream.screens.*;
import com.angrynerds.tedsdream.screens.game._MPGame;
import com.angrynerds.tedsdream.screens.game._SPGame;
import com.angrynerds.tedsdream.screens.multiplayer.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * class that is reponsible for updating and rendering all the
 * screen which are in use
 */
public class Controller extends Game {

    // screens
    public AbstractScreen playScreen;
    public AbstractScreen mainMenu;
    public AbstractScreen splashScreen;

    // multiplayer sceens
    public Screen multiplayer_choose;
    public Screen multiplayer_configuration;
    public Screen multiplayer_connect;
    public Screen multiplayer_lobby;
    public _MPGame MPGame;

    // game server
    public GameServer server;

    public Controller() {

        // create screens
        splashScreen = new SplashScreen(this);
        mainMenu = new MainMenu(this);
        playScreen = new _SPGame(this);

        // create multiplayer screens
        multiplayer_choose = new MultiplayerChooseMenu(this);
        multiplayer_configuration = new ServerConfigurationMenu(this);
        multiplayer_connect = new ServerConnectMenu(this);
        multiplayer_lobby = new ServerLobby(this);
        MPGame = new _MPGame(this);

        // create server
        server = new GameServer();

        // set splash screen on game start
        this.setScreen(splashScreen);
    }

    @Override
    public void create() {

    }

    @Override
    public void dispose() {
        server.dispose();
    }
}
