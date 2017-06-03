package com.ifragmented.apps.soldier.data;

import com.ifragmented.apps.soldier.rules.DifficultyLevel;
import com.ifragmented.apps.soldier.rules.DifficultyOffset;

import java.io.Serializable;

/**
 * Created by Ella on 2017-01-07.
 */

public class Answer implements Serializable {

    private String id;
    private String answerText;
    private Outcome outcome;
    private Dialogue followUp;
    private DifficultyOffset offset;    //some answers can make our life harder by making chance for fleeing or easier fight lower

    public Answer(String answerText, Outcome outcome) {
        this.answerText = answerText;
        this.outcome = outcome;
        this.offset = DifficultyOffset.DEFAULT;
    }

    public Answer(String answerText, Outcome outcome, DifficultyOffset offset) {
        this.answerText = answerText;
        this.outcome = outcome;
        this.offset = offset;
    }

    public Answer(String answerText, Outcome outcome, DifficultyOffset offset, Dialogue followUp) {
        this(answerText,outcome,offset);
        this.followUp = followUp;
    }

    public Answer(String answerID, String answerText, Outcome outcome, DifficultyOffset offset, Dialogue followUp) {
        this(answerText,outcome,offset,followUp);
        this.id = answerID;
    }


    public String getAnswerText() {
        return answerText;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Dialogue getFollowUp() {
        return followUp;
    }


    @Override
    public String toString() {
        return "answer:" + answerText+ "\noutcome:" + outcome.toString() + "followup?" + (followUp!=null ? "yes " + this.getFollowUp().toString():"no");
    }

    public DifficultyOffset getOffset() {
        return offset;
    }

    public String getId() {
        return id;
    }
}
