package com.ifragmented.apps.soldier.data;

import com.ifragmented.apps.soldier.enemy.IActor;
import com.ifragmented.apps.soldier.player.IPlayer;
import com.ifragmented.apps.soldier.rules.DifficultyLevel;

import java.io.Serializable;

/**
 * Created by Ella on 2017-01-07.
 */

public class Dialogue implements Serializable {

    private final DialogueType dialogueType;
    private final IActor actor;
    private final String body;
    private final Answer[] answers;
    private boolean isAnswered;
    private Answer chosenAnswer;
    private DifficultyLevel difficultyLevel;
    private boolean isTrigger;

    public Dialogue(String body, Answer[] answers, DialogueType dialogueType, IActor actor) {
        this.body = body;
        this.answers = answers;
        this.actor = actor;
        this.dialogueType = dialogueType;
        this.isAnswered = false;
        this.difficultyLevel = DifficultyLevel.getRandomDifficulty();
    }

    public Dialogue(String body, Answer[] answers, DialogueType dialogueType, IActor actor, DifficultyLevel difficultyLevel) {
        this.body = body;
        this.answers = answers;
        this.actor = actor;
        this.dialogueType = dialogueType;
        this.isAnswered = false;
        this.difficultyLevel = difficultyLevel;
    }

    public String getBody() {
        return body;
    }

    public Answer[] getAnswers() {
        return answers.clone();
    }

    public void answer(String userAnswer){
        for(Answer a : answers){
            if(a.getAnswerText().equalsIgnoreCase(userAnswer)){
                chosenAnswer = a;
                isAnswered = true;
                break;
            }
        }
        //should throw error if no correct answer given to avoid program hanging forever
        if(chosenAnswer==null){
            throw new RuntimeException("No matching answer has been given.");
        }
    }

    public IActor getActor() {
        return actor;
    }

    public DialogueType getDialogueType() {
        return dialogueType;
    }



    public boolean getIsAnswered() {
        return isAnswered;
    }

    public Answer getChosenAnswer() {
        return chosenAnswer;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }
}
