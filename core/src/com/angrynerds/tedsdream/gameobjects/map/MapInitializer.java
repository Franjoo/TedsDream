package com.angrynerds.tedsdream.gameobjects.map;

import com.angrynerds.tedsdream.gameobjects.Enemy;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public class MapInitializer implements ApplicationListener{

    private Array<Enemy> enemies;

    public MapInitializer(String path){

        TiledMap map = new TmxMapLoader().load(path);

        // enemies
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapObjects objects = map.getLayers().get(i).getObjects();
            for (int j = 0; j < objects.getCount(); j++) {
                MapObject o = objects.get(j);

                String type = o.getProperties().get("type").toString();

                switch (type){
                    case "spawn":
                        break;
                    default:
                }


            }
        }

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
