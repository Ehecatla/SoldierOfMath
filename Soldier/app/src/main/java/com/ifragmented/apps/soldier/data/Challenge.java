package com.ifragmented.apps.soldier.data;

import com.ifragmented.apps.soldier.rules.DifficultyLevel;

import java.io.Serializable;

/**
 * Created by Ella on 2017-01-07.
 */

public class Challenge implements Serializable {

    private String questionBody;
    private String[] allAnswers;
    private String correctAnswer;
    private int timesAnswered;
    private boolean isFinished;
    private ChallengePoints challengePoints;
    private DifficultyLevel baseDifficulty;

    public Challenge(String questionBody, String[] allAnswers, String correctAnswer, DifficultyLevel baseDifficulty) {
        this.questionBody = questionBody;
        this.allAnswers = allAnswers;
        this.correctAnswer = correctAnswer;
        this.timesAnswered =0;
        this.baseDifficulty = baseDifficulty;
    }

    public String getQuestionBody() {
        return questionBody;
    }

    public String[] getAllAnswers() {
        return allAnswers;
    }

    public int answer(String answer){
        timesAnswered++;
        boolean correct = answer.equalsIgnoreCase(correctAnswer);
        if(correct){
            isFinished=true;
        } else {
            isFinished = timesAnswered>= allAnswers.length-1;//user should not be ever able to answer as many times as number answers, max is one less than all answers
        }
        return challengePoints.calculatePoints(timesAnswered,correct);
    }

    public int answerTimeOut(){
        isFinished=true;
        //timesAnswered = allAnswers.length; //should do it after points counted if at all
        if(timesAnswered==0){
            return challengePoints.calculatePoints(timesAnswered,false);    //only if user did not answer any time at all punish with minuspoints
        } else {
            return 0;   //if user answered wrong some times and then question timed out anyway, do not take any hp from user
        }

    }

    public boolean isFinished() {
        return isFinished;
    }

    public DifficultyLevel getBaseDifficulty() {
        return baseDifficulty;
    }

    public void setBaseDifficulty(DifficultyLevel baseDifficulty) {
        this.baseDifficulty = baseDifficulty;
    }

    public void setChallengePoints(ChallengePoints challengePoints) {
        this.challengePoints = challengePoints;
    }
}
