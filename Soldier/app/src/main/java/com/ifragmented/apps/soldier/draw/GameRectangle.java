package com.ifragmented.apps.soldier.draw;

import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by Ella on 2017-01-10.
 */

public class GameRectangle {

    private final double rectWidth;
    private final double rectHeight;
    private final String title;
    private RectangleColors rectangleColor;   //only color of rectangle can change

    private Rect rect;

    public GameRectangle(double rectWidth, double rectHeight, String title) {
        this.rectWidth = rectWidth;
        this.rectHeight = rectHeight;
        this.title = title;
    }

    public GameRectangle(double rectWidth, double rectHeight, String title, Rect rect) {
        this(rectWidth,rectHeight,title);
        this.rect = rect;
    }

    public GameRectangle(double rectWidth, double rectHeight, String title, Rect rect, RectangleColors color) {
        this(rectWidth,rectHeight,title,rect);
        this.rectangleColor = color;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getTitle() {
        return title;
    }

    public double getRectWidth() {
        return rectWidth;
    }

    public double getRectHeight() {
        return rectHeight;
    }

    public void moveDown(){
        this.rect.offset(0,5);
    }

    public RectangleColors getRectangleColor() {
        return rectangleColor;
    }

    public void setRectangleColor(RectangleColors rectangleColor) {
        this.rectangleColor = rectangleColor;
    }
}
