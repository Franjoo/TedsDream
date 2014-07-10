package com.angrynerds.tedsdream.events;

import java.io.Serializable;

/**
 * Author: Franz Benthin
 */
public class EnemyInitializationEvent implements Serializable {

    private String serialization;

    public EnemyInitializationEvent(String serialization) {
        this.serialization = serialization;
    }

    public String getSerialization() {
        return serialization;
    }

}
