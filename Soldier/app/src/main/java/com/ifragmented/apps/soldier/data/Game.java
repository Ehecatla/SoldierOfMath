package com.ifragmented.apps.soldier.data;

import android.util.Log;

import com.ifragmented.apps.soldier.player.IPlayer;
import com.ifragmented.apps.soldier.rules.DifficultyLevel;
import com.ifragmented.apps.soldier.rules.EventType;
import com.ifragmented.apps.soldier.rules.GameSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by ellaeinarsen on 2017-01-01.
 */

public class Game {

    private final IPlayer activeUser;
    private GameState gameState;
    private final GameSettings gameSettings;

    private final Story story;
    private int activeDialogueNr;
    private Dialogue activeDialogue;
    private ArrayList<Challenge> activeChallenges;
    private int activeChallengeNr;
    private List<Challenge> allChallenges = new ArrayList<>();

    public Game(IPlayer user, GameSettings gameSettings, Story story, List<Challenge>challengeList){
        this.activeUser = user;
        this.gameState = GameState.STARTED;
        this.gameSettings = gameSettings;
        this.story = story;
        activeDialogueNr=0;
        this.activeDialogue = story.getIntro();
        activeChallengeNr=-1;
        this.allChallenges = challengeList;
    }


    public void abandonGame(){
        gameState = GameState.ABANDONED;
    }

    public GameState getGameState(){
        return gameState;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public IPlayer getActiveUser() {
        return activeUser;
    }

    public EventType getEventType() {
        if(activeDialogue.getIsAnswered()){
           Outcome outcome=  activeDialogue.getChosenAnswer().getOutcome();
            if(outcome == Outcome.FIGHT){
                return EventType.CHALLENGE;
            } else {
                return EventType.DIALOGUE;
            }
        }else{
            return EventType.DIALOGUE;
        }
    }

    //moves between dialogues, will only move if activeDialogue has been answered and if game is still started
    public void nextEvent() {

        if(gameState == GameState.STARTED) {

            DialogueType activeDType = activeDialogue.getDialogueType();
            if (activeDType == DialogueType.INTRO) {                                                //intro is always just talk, if talk answer has no follow up, move to default dialogues
                boolean answered = activeDialogue.getIsAnswered();
                if (answered) {
                    Answer answer = activeDialogue.getChosenAnswer();
                    Outcome outcome = answer.getOutcome();
                    if (outcome == Outcome.TALK) {
                        activeDialogue = answer.getFollowUp();
                        if (activeDialogue == null) {
                            activeDialogueNr = 0;
                            activeDialogue = story.getStoryDialogues().get(0);
                        }
                    }
                }
            } else if (activeDType == DialogueType.DEFAULT) {                                       //default dialogues may lead to challenges or just move to next dialogue if user managed to avoid
                boolean answered = activeDialogue.getIsAnswered();
                if (answered) {
                    Answer answer = activeDialogue.getChosenAnswer();
                    Outcome outcome = answer.getOutcome();
                    if (outcome == Outcome.TALK) {
                        activeDialogue = answer.getFollowUp();
                        if (activeDialogue == null) {
                            activeDialogueNr++;
                            if (activeDialogueNr >= story.getStoryDialogues().size() || !activeUser.isAlive()) {  //reached end of dialogues, time to finish up
                                if (activeUser.isAlive()) {
                                    activeDialogue = story.getOutroWon();
                                } else {
                                    activeDialogue = story.getOutroLost();
                                }
                            } else {
                                if (!activeUser.isAlive()) {    //could happen if there was talk after fight and yet user lost
                                    activeDialogue = story.getOutroLost();
                                } else {
                                    activeDialogue = story.getStoryDialogues().get(activeDialogueNr);
                                }
                            }

                        }
                    } else if (outcome ==Outcome.FIGHT){
                        if(activeChallenges==null){ //havent started playing challenge yet
                            DifficultyLevel challengeDifficulty =DifficultyLevel.getDifficulty(activeDialogue.getDifficultyLevel().getDiffValue() + answer.getOffset().getOffsetValue());
                            activeChallenges  = pick(challengeDifficulty,gameSettings.MAX_CHALLENGES);
                            //activeChallenges = ChallengeGenerator.generateChallenges(challengeDifficulty,gameSettings);
                            activeChallengeNr=0;
                        } else {                        //check at which stage, if stage challenge has been answered max times and is finished then move to next, if all done move to dialogue
                            //is challenge finished? yes? move to next,
                            Challenge activeChallenge = activeChallenges.get(activeChallengeNr);
                            if(activeChallenge.isFinished()){   //either answered correctly or answered max times, move to next one
                                activeChallengeNr++;
                                if (activeChallengeNr >= (gameSettings.MAX_CHALLENGES)) { //all challenges done, time to move to next dialogue or show followup after fight if any
                                    //check if there is any dialogue after
                                    if(activeUser.isAlive()){   //first assess if user is alive after all challenges (math questions)
                                        Dialogue afterFightDialogue = activeDialogue.getChosenAnswer().getFollowUp();
                                        if(afterFightDialogue!=null){
                                            activeDialogue = afterFightDialogue;
                                        } else { //no follow up, then move onward to next dialogue
                                            activeDialogueNr++;
                                            if(activeDialogueNr >= story.getStoryDialogues().size()){   //if this was last dialogue then move to outro with user as winner as user is still alive
                                                activeDialogue = story.getOutroWon();
                                            } else{                                                     //wasnt last dialogue, move to next def dialogue
                                                activeDialogue = story.getStoryDialogues().get(activeDialogueNr);
                                            }
                                        }
                                        activeChallenges = null;

                                    } else {    //if users hp/motivation dropped below 1 after all challenges for dialogue, then cannot continue with story, just finish up
                                        activeDialogue = story.getOutroLost();
                                    }

                                }
                            }
                            //no else, if challenge is still not answered enough times or timed out, dont change to next one (for this i use feedback from fragment answer)
                        }
                    }
                }
            } else if (activeDType == DialogueType.OUTRO_WON || activeDType == DialogueType.OUTRO_LOST) {   //even finishing dialogue may have more talking options than 1 but no more fights
                boolean answered = activeDialogue.getIsAnswered();                                          //if all talks are done then set game as finished
                if (answered) {
                    Answer answer = activeDialogue.getChosenAnswer();
                    Outcome outcome = answer.getOutcome();
                    if (outcome == Outcome.TALK) {
                        activeDialogue = answer.getFollowUp();
                        if (activeDialogue == null) {
                            gameState = GameState.FINISHED;
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Challenge> pick(final DifficultyLevel challengeDifficulty, int max_challenges) {
        //pick x number challenges of given difficulty from challenge list
        Random r = new Random();
        ArrayList<Challenge> pickedChallenges = new ArrayList<>();
        ArrayList<Challenge> fittingChallenges = new ArrayList<>();
        for(Challenge c: allChallenges){
            if(c.getBaseDifficulty()==challengeDifficulty){
                fittingChallenges.add(c);
            }
        }
        if(fittingChallenges.size()<1){
            return pickedChallenges;
        }
        Challenge randomChallenge = null;
        while(pickedChallenges.size()<=max_challenges){
            int pickNr = r.nextInt(fittingChallenges.size());
            randomChallenge = fittingChallenges.get(pickNr);
            if(fittingChallenges.size()> max_challenges){   //if there are more challenges can afford to get unique ones, avoiding getting same question
                if(pickedChallenges.contains(randomChallenge)){
                    pickNr = r.nextInt(fittingChallenges.size());
                    randomChallenge = fittingChallenges.get(pickNr);
                }
            }
            pickedChallenges.add(randomChallenge);
        }
        return pickedChallenges;
    }



    public Dialogue getEventDialogue() {
        return activeDialogue;
    }

    public Challenge getEventChallenge() {
        Log.d("TAG", "returning event challenge if any remaining " + activeChallengeNr);
        if(activeChallengeNr== activeChallenges.size()){
            return null;
        }
        return activeChallenges.get(activeChallengeNr);
    }

    public String getStoryTitle() {
        return this.story.getStoryTitle();
    }


    public boolean answerActiveDialogue(String answer){
        activeDialogue.answer(answer);
        this.nextEvent();
        return true;   //true if dialogue result in change to next fragment
    }

    public HashMap<String,Boolean> answerChallengeQuestion(String answer){
        //1 answer, 2 get points for answer and modify user, 3 assess if question is done, move onward
        int answerPoints = activeChallenges.get(activeChallengeNr).answer(answer);
        activeUser.modifyHealth(answerPoints);

        HashMap<String,Boolean> result = new HashMap<>();
        result.put("IS_FINISHED", activeChallenges.get(activeChallengeNr).isFinished());
        result.put("IS_ANSWER_CORRECT", answerPoints>0);
        this.nextEvent();   //No worries, it wont move to next question if challenge is not finished
        return result;   //true if question is finished after answering
    }

    public boolean answerChallengeQuestion2(String answer){
        //1 answer, 2 get points for answer and modify user, 3 assess if question is done, move onward
        int answerPoints = activeChallenges.get(activeChallengeNr).answer(answer);
        activeUser.modifyHealth(answerPoints);
        this.nextEvent();   //No worries, it wont move to next question if challenge is not finished
        return activeChallenges.get(activeChallengeNr).isFinished();   //true if question is finished after answering
    }

    public boolean answerChallengeQuestionTimeOut(){
        if(!activeChallenges.get(activeChallengeNr).isFinished()) {
            int timeOutPoints = activeChallenges.get(activeChallengeNr).answerTimeOut();
            activeUser.modifyHealth(timeOutPoints);
        }
        this.nextEvent();
        return true;
    }

    public String getStoryBackground() {
        return this.story.getBackground();
    }


    public int getActiveRoundNr() {
        if(this.activeChallenges !=null && this.activeChallengeNr>-1){
            return this.activeChallengeNr+1;
        } else { return -1;}
    }
}
