package com.ifragmented.apps.soldier.player;

/**
 * Created by Ella on 2017-01-07.
 */

public interface IPlayer {

    String getName();
    int getHealth();
    boolean isAlive();
    void modifyHealth(int modifier);

}
