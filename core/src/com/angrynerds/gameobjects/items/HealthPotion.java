package com.angrynerds.gameobjects.items;

import com.angrynerds.gameobjects.Item;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 11.02.14
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class HealthPotion extends Item {

    public HealthPotion(float x, float y) {
        super(x, y, "items/potion_01.png");
    }

}
