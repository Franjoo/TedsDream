package com.angrynerds.tedsdream.screens.game;

import com.angrynerds.tedsdream.gameobjects.Player;

/**
 * User: Franjo
 */
interface PlayerEvent extends Event{

    void apply(Player player);

}
