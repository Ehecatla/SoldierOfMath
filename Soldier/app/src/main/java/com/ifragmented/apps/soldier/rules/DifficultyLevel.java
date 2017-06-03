package com.ifragmented.apps.soldier.rules;

import java.util.Random;

/**
 * Created by Ella on 2017-01-07.
 */

public enum DifficultyLevel {

    EASY(1,0.25,0),MEDIUM(2,0.2,0.1),HARD(3,0.1,0.2),VERY_HARD(4,0.05,0.3);

    private final int diffValue;
    private final double diffChanceModifier;    //chance modifier, different difficulty is used by answer when calculating chance for fleeing, leaving in peace, getting easier fight etc
    private final double diffExtraModifier;     //for extra points calculation, the harder the more extra modifier

    //if any new levels are added, just change difficulty max to fit
    public static final int MIN_LEVEL_VAL=1;
    public static final int MAX_LEVEL_VAL=4;

    DifficultyLevel(int difficultyValue, double diffChanceModifier, double diffExtraModifier) {
        this.diffValue = difficultyValue;
        this.diffChanceModifier = diffChanceModifier;
        this.diffExtraModifier = diffExtraModifier;
    }

    public int getDiffValue() {
        return diffValue;
    }


    public static DifficultyLevel getDifficulty(int level){
        switch (level){
            case 1:
                return EASY;
            case 2:
                return MEDIUM;
            case 3:
                return HARD;
            case 4:
                return VERY_HARD;
            default:
                if(level<MIN_LEVEL_VAL){
                    return EASY;
                } else if(level>MAX_LEVEL_VAL){
                    return VERY_HARD;
                } else {
                    return MEDIUM;
                }
        }
    }

    public double getDiffChanceModifier() {
        return diffChanceModifier;
    }

    public static DifficultyLevel getRandomDifficulty() {
        Random r = new Random();
        int lvl = r.nextInt(MAX_LEVEL_VAL)+MIN_LEVEL_VAL;
        return getDifficulty(lvl);
    }

    public double getDiffExtraModifier() {
        return diffExtraModifier;
    }
}
