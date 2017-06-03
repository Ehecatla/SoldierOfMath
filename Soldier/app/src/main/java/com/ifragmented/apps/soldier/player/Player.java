package com.ifragmented.apps.soldier.player;

/**
 * Created by ellaeinarsen on 2017-01-01.
 */

public class Player implements IPlayer {

    private String name;
    private int health;

    public Player(String username, int health) {
        this.name = username;
        this.health = health;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public int getHealth() {
        return health;
    }



    @Override
    public boolean isAlive() {
        return health>0;
    }

    @Override
    public void modifyHealth(int modifier) {
        health+= modifier;
        if(health<0){
            health=0;
        }
    }

}
