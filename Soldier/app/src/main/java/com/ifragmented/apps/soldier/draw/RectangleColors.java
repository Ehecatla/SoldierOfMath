package com.ifragmented.apps.soldier.draw;

import android.graphics.Color;

/**
 * Created by Ella on 2017-01-22. Contains colors that should be used to show answer status of
 * given rectangle representing an answer to challenge question.
 */

public enum RectangleColors {
    NEUTRAL(Color.GRAY),CORRECT(Color.GREEN),FAIL(Color.RED);

    private int color;

    private RectangleColors(int color){
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
