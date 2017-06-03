package com.ifragmented.apps.soldier.draw;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Ella on 2017-01-22.
 */

public class GRectManager {

    private ArrayList<GameRectangle> rectangleArrayList;

    public GRectManager(ArrayList<GameRectangle> rectangleArrayList) {
        this.rectangleArrayList = rectangleArrayList;
    }

    public void showRectanglesOnCanvas(){}
    public void destroyRectangles(){
        this.rectangleArrayList = new ArrayList<>();
    }


    public void moveRectangles(){
        for(GameRectangle gameRectangle: this.rectangleArrayList){
            gameRectangle.moveDown();
        }
    }

    /**
     * Returns title text to first rectangle that contains touch x,y given in parameter to this
     * method. Null if none are found.
     * @param x touch points x
     * @param y touch points y
     * @return if any rectangle contains point x,y then its title, otherwise null
     */
    public String getAnswerFor(float x, float y) {
        for(GameRectangle r: rectangleArrayList){
            boolean contains =r.getRect().contains((int)x,(int)y);
            if(contains){
                return r.getTitle();
            }
        }
        return null;
    }

    /**
     * Finds and returns GameRectangle with given answer on it.
     * @param answer answer to search rectangle with
     * @return GameRectangle object or null
     */
    public GameRectangle getRectFor(String answer) {
        for(GameRectangle r: rectangleArrayList){
            boolean contains =r.getTitle().equals(answer);
            if(contains){
                return r;
            }
        }
        return null;
    }

    /**
     * Finds rectangle with given answer text on it and changes its color to given one.
     * @param answer answer on rectangle
     * @param color new color for rectangle
     */
    public void changeColorFor(String answer, RectangleColors color){
        GameRectangle gameRectangle =getRectFor(answer);
        if(gameRectangle!=null){
            gameRectangle.setRectangleColor(color);
        }
    }

    public ArrayList<GameRectangle> getGameRectangles() {
        return rectangleArrayList;
    }
}
