package com.ifragmented.apps.soldier.data;

import android.util.Log;

import com.ifragmented.apps.soldier.rules.DifficultyLevel;
import com.ifragmented.apps.soldier.rules.GameSettings;

import java.io.Serializable;

/**
 * Created by Ella on 2017-01-15.
 */

public class ChallengePoints implements Serializable {

    private final int nrDialogues;
    private final int nrChallengeInDial;
    private final int maxPlayerHp;
    private final DifficultyLevel difficultyLevel;
    private final int maxAnswers;

    private int maxLostHp;

    public ChallengePoints(int nrDialogues, int nrChallengeInDialogue, int maxPlayerHp,int maxAnswers, DifficultyLevel difficultyLevel){
        this.difficultyLevel=difficultyLevel;
        this.maxPlayerHp = maxPlayerHp;
        this.nrChallengeInDial = nrChallengeInDialogue;
        this.nrDialogues = nrDialogues;
        this.maxAnswers = maxAnswers;
        countValues();
    }

    public ChallengePoints(GameSettings gameSettings, DifficultyLevel difficultyLevel) {
        this.difficultyLevel=difficultyLevel;
        this.maxPlayerHp = gameSettings.PLAYER_START_MOTIVATION;
        this.nrChallengeInDial = gameSettings.MAX_CHALLENGES;
        this.nrDialogues = gameSettings.MAX_ENCOUNTERS;
        this.maxAnswers = gameSettings.CHALLENGE_MAX_ANSWERS;
        countValues();
    }

    private void countValues(){
        maxLostHp = (maxPlayerHp/(nrDialogues/2))/nrChallengeInDial;
        if(maxLostHp<1){
            maxLostHp=1;
        }
    }

    public int calculatePoints(int timesAnswered, boolean isCorrectAnswer){
        if(isCorrectAnswer){
            int hp = (int) (maxLostHp /timesAnswered);
            hp =(int) (hp +  difficultyLevel.getDiffExtraModifier()*hp);    //the harder the more extra points if answered correctly, no minus extras tho if answered wrongly
            Log.d("CALCULATE","hp diff " + hp);
            return hp;
        } else {
            if(timesAnswered==0){   //if user didnt answer at all
                Log.d("CALCULATE","hp diff " + (-maxLostHp));
                return -maxLostHp;
            }
            Log.d("CALCULATE","hp diff " + (maxLostHp/(maxAnswers) * (-timesAnswered)));
            return maxLostHp/(maxAnswers) * (-timesAnswered);
        }
    }
}
