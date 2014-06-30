package com.angrynerds.tedsdream.screens.game;

/**
 * Author: Franz Benthin
 */
public class EnemyInitializationEvent implements Event{

    private String serialization;

    public EnemyInitializationEvent(String serialization) {
        this.serialization = serialization;
    }

    public String getSerialization() {
        return serialization;
    }

}
