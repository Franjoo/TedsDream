package com.angrynerds.tedsdream.gameobjects.map;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

/**
 * Author: Franz Benthin
 */
public class EnemyInitialization {

    Array<Spawn> spawnArray;

    public EnemyInitialization(TiledMap tiledMap) {

        spawnArray = new Array<>();

        MapLayers mapLayer = tiledMap.getLayers();
        for (int i = 0; i < mapLayer.getCount(); i++) {
            // contains objects
            if (mapLayer.get(i).getObjects().getCount() != 0 && mapLayer.get(i).getName().equals("$enemySpawn")) {
                MapObjects objects = mapLayer.get(i).getObjects();

                for (int j = 0; j < objects.getCount(); j++) {
                    MapProperties p = objects.get(j).getProperties();

                    // position and dimension of spawn rectangle
                    float x = Float.parseFloat(p.get("x").toString());
                    float y = Float.parseFloat(p.get("y").toString());
                    float w = Float.parseFloat(p.get("width").toString()) * 64;
                    float h = Float.parseFloat(p.get("height").toString()) * 64;
                    Rectangle rectangle = new Rectangle(x, y, w, h);

                    // distance (spawn enemy when distance from player to rectangle <= distance)
                    float distance = Float.parseFloat(p.get("dist").toString());

                    // number of enemies in spawn area calc
                    int min = Integer.parseInt(p.get("min").toString());
                    int max = Integer.parseInt(p.get("max").toString());
                    int num = (int) (min + (Math.random() * (max - min)));

                    // name (type) of enemy
                    String name = p.get("name").toString();

                    // path (boss)
                    String path = name;
                    if (p.containsKey("path")) path = p.get("path").toString();

                    // skin of enemy
                    String skin = null;
                    if (p.containsKey("skin")) skin = p.get("skin").toString();

                    // hp & ap
                    float ap = 3;
                    float hp = 100;
                    if (p.containsKey("ap")) ap = Float.parseFloat(p.get("ap").toString());
                    if (p.containsKey("hp")) hp = Float.parseFloat(p.get("hp").toString());


                    for (int k = 0; k < num; k++) {

                        float scale = 0.2f;
                        if (p.containsKey("scale")) {
                            String[] s = p.get("scale").toString().split(" ");
                            float scaleMin = Float.parseFloat(s[0]);
                            float scaleMax = Float.parseFloat(s[1]);
                            scale = scaleMin + ((float) (Math.random() * (scaleMax - scaleMin)));
                        }

                        //Enemy enemy = new Enemy(name, "spine/" + path + "/", skin, scale, ap, hp);

                        float _x = (float) (rectangle.x + Math.random() * rectangle.getWidth());
                        float _y = (float) (rectangle.y + Math.random() * rectangle.getHeight());

                        _y = (float) (Math.random() * 384);

                        if (!(_x > 0 )){ throw new RuntimeException("spawn out of bounds");}
//                        if (!(_y > 0 && _y < 6 * 64)){ throw new RuntimeException("spawn out of bounds: " + _y);}

                        spawnArray.add(new Spawn(name, "spine/" + path + "/", skin, scale, ap, hp, _x, _y));
                    }

                }
            }
        }


    }


    public String getSerializationString() {
        Json json = new Json();
        return json.toJson(this);
    }

    public class Spawn {
        String name;
        String path;
        String skin;
        float scale;
        float ap, hp;
        float x, y;

        public Spawn(String name, String path, String skin, float scale, float ap, float hp, float _x, float _y) {
            this.name = name;
            this.path = path;
            this.skin = skin;
            this.scale = scale;
            this.ap = ap;
            this.hp = hp;
            this.x = _x;
            this.y = _y;
        }
    }


}
