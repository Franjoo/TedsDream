package com.angrynerds.tedsdream;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Author: Franz Benthin
 */
public class Assets { // singleton

    private static Assets instance;

    private AssetManager manager;
    private HashMap<String, TextureAtlas> atlases;

    private Assets() {
        manager = new AssetManager();
        manager.load("ui/loading.pack", TextureAtlas.class);
        manager.load("maps/map_05.tmx", TiledMap.class);
        manager.finishLoading();

        // fill hashmaps
        Array<TextureAtlas> a = new Array<>();
        manager.getAll(TextureAtlas.class, a);


    }

    public static Assets instance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    public TextureAtlas getAtlas(String name) {
        return manager.get(name + ".atlas");
    }

}
