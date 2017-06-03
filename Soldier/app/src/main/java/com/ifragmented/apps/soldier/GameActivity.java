package com.ifragmented.apps.soldier;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.ifragmented.apps.soldier.data.Challenge;
import com.ifragmented.apps.soldier.data.Dialogue;
import com.ifragmented.apps.soldier.data.Game;
import com.ifragmented.apps.soldier.data.GameState;
import com.ifragmented.apps.soldier.data.SoldierManager;
import com.ifragmented.apps.soldier.rules.EventType;

import java.util.HashMap;

public class GameActivity extends AppCompatActivity implements StoryFragment.onStoryFragInteractListener, ChallengeFragment.OnChallengeFragInteract {

    public static final String GAME_USERNAME="GAME_USERNAME";
    private SoldierManager manager;
    private FrameLayout frameLayout;
    private StoryFragment lastStoryFrag;
    private ChallengeFragment lastChallengeFrag;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        frameLayout =(FrameLayout)findViewById(R.id.game_fragment);
        progressBar = (ContentLoadingProgressBar)findViewById(R.id.game_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        manager = SoldierManager.getInstance(this);
        initGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initGame(){
        Intent intent = getIntent();
        String username =intent.getStringExtra(GAME_USERNAME);
        if(username==null){
            throw new RuntimeException("An error occured, no username has been assigned.");
        }
        manager.initGame(username); //put on back thread, it inits user, chooses random dialogues and such
        playGame(manager.getGame());
    }

    private void playGame(Game game){
        GameState state = game.getGameState();
        switch(state){
            case STARTED:
                newGameRound(game);
                break;
            case FINISHED:
                gameFinished(game);
                break;
            case ABANDONED:
                desertGame();   //should show finish activity with abandon instead!
                break;
        }
    }

    private void newGameRound(Game game){
        progressBar.setVisibility(View.VISIBLE);
        EventType eventType = game.getEventType();
        if(eventType == EventType.DIALOGUE){
            Dialogue dialogue = game.getEventDialogue();
            if(dialogue!=null){
                setFragmentLive(createStoryFragment(dialogue, game.getStoryBackground()));
            } else {
                Log.d("TAG","ERROR, no dialogue found during newGameRound");
            }
        } else if(eventType == EventType.CHALLENGE){
            Challenge challenge = game.getEventChallenge();
            if(challenge!=null) {
                String hp = ""+game.getActiveUser().getHealth();
                String round = ""+game.getActiveRoundNr();
                setFragmentLive(createChallengeFragment(challenge.getAllAnswers(), challenge.getQuestionBody(),hp,round));
            } else {
                Log.d("TAG","ERROR, no challenge found during newGameRound");
            }
        }
        progressBar.setVisibility(View.GONE);

    }


    private void gameFinished(Game game){    //played reached end of all encounters and saw all dialogues for story, finish, display result
        String message="";
        if(game.getActiveUser().isAlive()){
            manager.saveGameResults();
            message = "Congratulations!\nYou have finished story " +game.getStoryTitle()+"\nYour result is: " + game.getActiveUser().getHealth();
        } else {
            message = "Your motivation has reached bottom, you cannot continue this adventure! Good luck next time.";
        }
        displayEndMessage(message);
    }

    private void desertGame(){
        displayEndMessage("You have left game without finishing.");
    }


    private StoryFragment createStoryFragment(Dialogue dialogueToShow, String storyBackground){
        this.lastStoryFrag = StoryFragment.newInstance(dialogueToShow, storyBackground);
        Bundle args = new Bundle();
        args.putSerializable(StoryFragment.STORY_FRAG_DIALOGUE, dialogueToShow);
        args.putString(StoryFragment.STORY_FRAG_BACKGROUND, storyBackground);
        lastStoryFrag.setArguments(args);
        return lastStoryFrag;
    }

    private ChallengeFragment createChallengeFragment(String[] challengeAnswers, String question, String hp, String round){
        this.lastChallengeFrag = ChallengeFragment.newInstance(challengeAnswers, question, hp,round);
        Bundle args = new Bundle();
        args.putString(ChallengeFragment.CHALLENGE_HP,hp);
        args.putString(ChallengeFragment.CHALLENGE_ROUND,round);
        args.putString(ChallengeFragment.CHALLENGE_QUESTION,question);
        args.putSerializable(ChallengeFragment.CHALLENGE_PARAM, challengeAnswers);
        lastChallengeFrag.setArguments(args);
        return lastChallengeFrag;
    }

    private void setFragmentLive(Fragment fragment){
        try {
            if(fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                //ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);  //fade in/out alternative
                ft.replace(R.id.game_fragment, fragment);
                ft.commit();

            }
        } catch (Exception e){
            Log.d("TAG", "cannot show the fragment:( Error");
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(this,PausActivity.class);
        startActivity(i);
    }

    private void displayEndMessage(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        manager.removeGame();
                        finish();
                    }
                }).setCancelable(false);
        builder.create().show();
    }



    @Override
    public void onStoryDialogueAnswer(String answer) {
        boolean next = manager.getGame().answerActiveDialogue(answer);
        if(next){
            playGame(manager.getGame());
        }
    }

    @Override
    public void onChallengeFragInteractAnswer(String answer) {
        HashMap<String,Boolean>result = manager.getGame().answerChallengeQuestion(answer);
        boolean next = result.get("IS_FINISHED");
        boolean isCorrect = result.get("IS_ANSWER_CORRECT");
        Log.d("TAG","Question got answered, next= "+next + " and correct=" +isCorrect);
        this.lastChallengeFrag.onAnswerCalcFinished(""+this.manager.getGame().getActiveUser().getHealth(),answer,isCorrect);
    }

    @Override
    public void onChallengeTimeOut() {
        boolean next = this.manager.getGame().answerChallengeQuestionTimeOut();
        if(next){
            this.playGame(manager.getGame());
        }
    }

    @Override
    public void onAllFinished() {
        this.progressBar.setVisibility(View.VISIBLE);
        playGame(manager.getGame());
    }


}
