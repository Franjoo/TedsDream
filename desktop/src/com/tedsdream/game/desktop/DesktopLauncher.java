package com.tedsdream.game.desktop;

import com.angrynerds.game.core.Main;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "Teds Dream";
        config.width = 800;
        config.height = 480;
        config.vSyncEnabled = false;

        new LwjglApplication(new Main(), config);
    }
}
