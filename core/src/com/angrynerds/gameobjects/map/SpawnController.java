package com.angrynerds.gameobjects.map;

import com.angrynerds.gameobjects.Enemy;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class SpawnController {

    private Array<SpawnObject> objects;
    private Map map;

    // num max enemies
    private int maxEnemies;

    public SpawnController(Map map) {
        this.map = map;
        objects = new Array();

        maxEnemies = 0;
    }

    public void add(MapObject mapObject) {
        objects.add(new SpawnObject(mapObject));
    }

    public int getMaxEnemies(){
        return maxEnemies;
    }

    public void update(float delta) {
        for (int i = 0; i < objects.size; i++) {
            SpawnObject o = objects.get(i);
            if (o.rectangle.x - o.distance <= map.getPlayer().getX()) {
                o.spawn();
                objects.removeIndex(i);
            }
        }

    }

    class SpawnObject extends MapObject {

        public Rectangle rectangle;
        public float distance;
        private Array<Enemy> freeEnemies;

        public SpawnObject(MapObject mapObject) {

            // parse mapObject
            MapProperties p = mapObject.getProperties();

            // position and dimension of spawn rectangle
            float x = Float.parseFloat(p.get("x").toString());
            float y = Float.parseFloat(p.get("y").toString());
            float w = Float.parseFloat(p.get("width").toString()) * map.getTileWidth();
            float h = Float.parseFloat(p.get("height").toString()) * map.getTileHeight();
            rectangle = new Rectangle(x, y, w, h);

            // distance (spawn enemy when distance from player to rectangle <= distance)
            distance = Float.parseFloat(p.get("dist").toString());

            // number of enemies in spawn area calc
            int min = Integer.parseInt(p.get("min").toString());
            int max = Integer.parseInt(p.get("max").toString());
            int num = (int) (min + (Math.random() * (max - min)));

            // name (type) of enemy
            String name = p.get("name").toString();

            // path (boss)
            String path = name;
            if(p.containsKey("path")) path = p.get("path").toString();

            // skin of enemy
            String skin = null;
            if (p.containsKey("skin")) skin = p.get("skin").toString();

            // hp & ap
            float ap = 3;
            float hp = 100;
            if(p.containsKey("ap")) ap =Float.parseFloat(p.get("ap").toString());
            if(p.containsKey("hp")) hp =Float.parseFloat(p.get("hp").toString());

            freeEnemies = new Array();
            for (int i = 0; i < num; i++) {

                float scale = 0.2f;
                if (p.containsKey("scale")) {
                    String[] s = p.get("scale").toString().split(" ");
                    float scaleMin = Float.parseFloat(s[0]);
                    float scaleMax = Float.parseFloat(s[1]);
                    scale = scaleMin + ((float) (Math.random() * (scaleMax - scaleMin)));
                }

                Enemy enemy = new Enemy(name, "spine/" + path + "/", skin, map.getPlayer(), scale,ap,hp);

                float _x = (float) (rectangle.x + Math.random() * rectangle.getWidth());
                float _y = (float) (rectangle.y + Math.random() * rectangle.getHeight());

                // initial position
                enemy.init(_x, _y);

                freeEnemies.add(enemy);
                maxEnemies++;
            }

        }

        private void spawn() {
            map.getEnemies().addAll(freeEnemies);
        }


    }

}

