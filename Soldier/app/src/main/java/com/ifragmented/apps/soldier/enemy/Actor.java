package com.ifragmented.apps.soldier.enemy;

import com.ifragmented.apps.soldier.data.Dialogue;

import java.io.Serializable;

/**
 * Created by Ella on 2017-01-07.
 */

public class Actor implements IActor, Serializable {

    private String id;
    private final String enemyName;
    private final String image;

    public Actor(String id, String enemyName, String image) {
        this.id = id;
        this.enemyName = enemyName;
        this.image = image;
    }

    @Override
    public String getName() {
        return enemyName;
    }

    @Override
    public String getImage() {
        return image;
    }


    public String getId() {
        return id;
    }
}
