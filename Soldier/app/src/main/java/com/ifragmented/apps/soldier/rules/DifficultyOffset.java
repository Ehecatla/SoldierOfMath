package com.ifragmented.apps.soldier.rules;

/**
 * Created by Ella on 2017-01-09.
 */

public enum DifficultyOffset {

    EASIER(-1),DEFAULT(0),HARDER(1);

    private final int offset;

    DifficultyOffset(int offset) {
        this.offset = offset;
    }

    public int getOffsetValue() {
        return offset;
    }

    public static DifficultyOffset offsetForValue(String value){
        switch (value){
            case "-1":
                return EASIER;
            case "0":
                return DEFAULT;
            case "1":
                return HARDER;
            default:
                return DEFAULT;
        }
    }
}
