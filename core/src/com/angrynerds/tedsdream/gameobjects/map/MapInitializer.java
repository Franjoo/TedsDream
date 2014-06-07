package com.angrynerds.tedsdream.gameobjects.map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public class MapInitializer implements ApplicationListener{

    Array<SpawnController.SpawnObject> spawn;

    public MapInitializer(String path){

        TiledMap map = new TmxMapLoader().load(path);





    }

    class Spawn{



    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
