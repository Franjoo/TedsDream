package com.angrynerds.tedsdream.gameobjects;

import com.angrynerds.tedsdream.input.RemoteInput;

/**
 * User: Franjo
 */
public class PlayerRemote {

    private final Player player;
    private final RemoteInput input;

    public PlayerRemote(Player player, RemoteInput input) {
        this.player = player;
        this.input = input;
    }

    public Player getPlayer() {
        return player;
    }

    public RemoteInput getInput() {
        return input;
    }
}
