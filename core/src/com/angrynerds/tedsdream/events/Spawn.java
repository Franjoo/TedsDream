package com.angrynerds.tedsdream.events;

import java.io.Serializable;

/**
 * Author: Franz Benthin
 */
public class Spawn implements Serializable {
    public String name;
    public String path;
    public String skin;
    public float scale;
    public float ap, hp;
    public float x, y;

    // standard constructor needed for serialization
    public Spawn(){}

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