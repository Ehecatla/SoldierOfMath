package com.ifragmented.apps.soldier;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ifragmented.apps.soldier.draw.ChallengeView;
import com.ifragmented.apps.soldier.draw.GRectListener;
import com.ifragmented.apps.soldier.draw.GameRectangle;
import com.ifragmented.apps.soldier.draw.RectangleColors;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnChallengeFragInteract} interface
 * to handle interaction events.
 * Use the {@link ChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChallengeFragment extends Fragment implements GRectListener {

    private View v;

    public static final String CHALLENGE_PARAM = "challenge_param";
    public static final String CHALLENGE_HP = "challenge_hp";
    public static final String CHALLENGE_ROUND = "challenge_round";
    public static final java.lang.String CHALLENGE_QUESTION = "challenge_question";

    private String hp;
    private String round;
    private String question;
    private String[] challengeAnswers;
    private OnChallengeFragInteract mListener;

    private TextView questionRemainingTV;   //number questions remaining
    private TextView questionTV;            //body of question
    private ChallengeView challengeView;    //view that displays moving squares with possible answers
    private TextView motivationTV;          //players remaining motivation/hp
    private TextView triesTV;               //player may answer max number times per question

    private boolean answerInProgress=false;
    private ArrayList<String>queuedAnswers = new ArrayList<>(); //queued answers yet to be checked by game
    private ArrayList<String>userAnswers = new ArrayList<>();   //all (queued and already checked by game) answers by user, helps prevent user answering many times with same answer

    private boolean isActive =false;    //means active challenge

    private int timesAnswered=0;
    private boolean isTimedOut=false;

    public ChallengeFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param challengeAnswers Challenge to be displayed.
     * @return A new instance of fragment ChallengeFragment.
     */
    public static ChallengeFragment newInstance(String[] challengeAnswers,String question, String hp, String round) {
        ChallengeFragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        args.putStringArray(CHALLENGE_PARAM, challengeAnswers);
        args.putString(CHALLENGE_HP,hp);
        args.putString(CHALLENGE_QUESTION,question);
        args.putString(CHALLENGE_ROUND,round);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            challengeAnswers = (String[])getArguments().getStringArray(CHALLENGE_PARAM);
            question = getArguments().getString(CHALLENGE_QUESTION);
            hp =getArguments().getString(CHALLENGE_HP);
            round = getArguments().getString(CHALLENGE_ROUND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_challenge, container, false);
        questionRemainingTV =(TextView)v.findViewById(R.id.ch_qremaining_tv);
        questionTV =(TextView)v.findViewById(R.id.ch_question_tv);
        challengeView=(ChallengeView)v.findViewById(R.id.ch_challenge_view);
        motivationTV =(TextView)v.findViewById(R.id.ch_motivation_tv);
        triesTV =(TextView)v.findViewById(R.id.ch_tremaining_tv);
        questionRemainingTV.setText(round);
        motivationTV.setText(hp);
        questionTV.setText(question);
        String tries = " " + (challengeAnswers.length -1);
        triesTV.setText(tries);

        challengeView.post(new Runnable() {
            @Override
            public void run() {
                setUpChallenge(challengeView.getWidth(),challengeView.getHeight());
            }
        });


        this.v = v;
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void setUpChallenge(int width, int height){
        ArrayList<String> answersList = new ArrayList<>();
        for(int i=0;i< this.challengeAnswers.length;i++){
            answersList.add(challengeAnswers[i]);
        }

        challengeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(isActive) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //want event to be registered only once when user chose item
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        String answer = checkIfTouchedRectangle(x, y);
                        if (answer != null  && !userAnswers.contains(answer)) {
                            userAnswers.add(answer);
                            if (!answerInProgress) {
                                if (queuedAnswers.isEmpty()) {
                                    onAnswerBoxPressed(answer);
                                    answerInProgress = true;
                                    timesAnswered++;
                                } else {        //should never happen as if qeue is not empty then some answer should be always in progress
                                    if(timesAnswered<challengeAnswers.length -1) {
                                        queuedAnswers.add(answer);
                                        timesAnswered++;
                                    }
                                }
                            } else {
                                if (!queuedAnswers.contains(answer)) {   //doesnt allow mutltiple answering with same box
                                    if(timesAnswered<challengeAnswers.length -1) {  //can answer 1 times less than max number answers, otherwise would always get correct answer if user clicks all possible
                                        queuedAnswers.add(answer);
                                        timesAnswered++;
                                    }
                                }
                            }
                            if(timesAnswered>= challengeAnswers.length-1){  //always after answer got clicked, check if max number answers have been reached to block clicks
                                isActive=false;
                            }
                            String tries = " " + (challengeAnswers.length-1 - timesAnswered);
                            triesTV.setText(tries);
                        }
                    }
                }
                return false;
            }
        });

        //get x,y where challengeView begin in its parent view
        LinearLayout root = (LinearLayout)v.findViewById(R.id.ch_parent_ll);
        LinearLayout parent =(LinearLayout)v.findViewById(R.id.ch_v_parent_lv);
        int[] locations = new int[2];
        root.getLocationInWindow(locations);
        int x = locations[0];
        int y = locations[1];

        challengeView.createRectangles(answersList,this,x,y);
        challengeView.animateRectangles();
        isActive = true;
    }



    /**
     * This method goes through queued answers (queue is build up when user clicks on answer boxes
     * while previous answer clicked is still being calculated) chosen by user. It takes first
     * answers in queue array list if no other answer is in progress and queue is not empty. This
     * method should be called from method that runs after another answer that was in progress has
     * been calculated and is finished, to ensure that all queued answers are ran.
     */
    private void dequeueAnswers(){
        if(!answerInProgress && !queuedAnswers.isEmpty()){
            answerInProgress = true;
            String answer = queuedAnswers.get(0);
            queuedAnswers.remove(0);
            onAnswerBoxPressed(answer);
        }
    }

    private String checkIfTouchedRectangle(float x, float y){
        String intersecting = challengeView.getIntersecting(x,y);
        return intersecting;
    }

    private void onAnswerBoxPressed(String answer) {
        if (mListener != null) {
            mListener.onChallengeFragInteractAnswer(answer);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChallengeFragInteract) {
            mListener = (OnChallengeFragInteract) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChallengeFragInteract");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Update players motivation/hp to external number given in argument to this method. This is
     * necessary as ChallengeFragment displays only data but does not count any game variables on
     * its own.
     * @param newHp
     */
    public void onAnswerCalcFinished(String newHp, String answer, boolean isCorrect){

        this.hp = newHp;
        if (motivationTV != null) {
            this.motivationTV.setText(hp);
        }

        if(isCorrect){  //display correct color on answer if still relevant
            this.challengeView.colorRect(RectangleColors.CORRECT,answer);
            this.isActive = false;

        } else {
            this.challengeView.colorRect(RectangleColors.FAIL,answer);
            if(timesAnswered>= this.challengeAnswers.length){
                this.isActive = false;
            }
        }

    }


    @Override
    public void onColorAnimationDone(){
        this.answerInProgress = false;
        if(this.queuedAnswers.size()>0){    // still items in queue, time to take next answer
            this.dequeueAnswers();
        }else if(!isActive){
            this.challengeView.destroyGRects();
            if(mListener!=null){
                this.mListener.onAllFinished();
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnChallengeFragInteract {
        void onChallengeFragInteractAnswer(String answer);
        void onChallengeTimeOut();
        void onAllFinished();
    }

    //only if challenge timed out or if user answered too many times already
    @Override
    public void onGRectsOutOfView(){
        this.isActive = false;
        if(!answerInProgress && this.queuedAnswers.size()==0) {
            //this.isActive = false;
            this.mListener.onChallengeTimeOut();    //timed out
        }
    }


}
