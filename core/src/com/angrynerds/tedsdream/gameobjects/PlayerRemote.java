package com.angrynerds.tedsdream.gameobjects;

import com.angrynerds.tedsdream.input.RemoteInput;

/**
 * Created with IntelliJ IDEA.
 * User: Franjo
 * Date: 24.05.2014
 * Time: 14:19
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
