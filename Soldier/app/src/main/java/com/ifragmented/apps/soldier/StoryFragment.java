package com.ifragmented.apps.soldier;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ifragmented.apps.soldier.data.Answer;
import com.ifragmented.apps.soldier.data.AnswerAdapter;
import com.ifragmented.apps.soldier.data.Dialogue;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link onStoryFragInteractListener} interface
 * to handle interaction events.
 * Use the {@link StoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryFragment extends Fragment {

    public static final String STORY_FRAG_DIALOGUE="STORY_FRAG_DIALOGUE";
    public static final String STORY_FRAG_BACKGROUND="STORY_FRAG_BACKGROUND";
    private Dialogue dialogue;
    private String background;

    private ImageView storyIV;
    private ImageView npcIV;
    private TextView npcTV;
    private ListView answersLV;

    private ArrayList<Answer>answers = new ArrayList<>();
    private AnswerAdapter answerAdapter;

    private onStoryFragInteractListener mListener;

    private boolean isAnswered;

    public StoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialogue dialogue to be shown in fragment
     * @return A new instance of fragment StoryFragment.
     */
    public static StoryFragment newInstance(Dialogue dialogue, String background) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(STORY_FRAG_DIALOGUE, dialogue);
        args.putString(STORY_FRAG_BACKGROUND,background);
        fragment.setArguments(args);
        Log.d("TAG", "new story fragment was created");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialogue = (Dialogue)getArguments().getSerializable(STORY_FRAG_DIALOGUE);
            background =(String)getArguments().getString(STORY_FRAG_BACKGROUND);
        }

    }

    private void initialize(){
        Log.d("TAG","Is img res null? " + background);
        if(dialogue==null){
            //something went wrong, throw exception
            throw new RuntimeException("Failed to load story");
        }
        if(background!=null){
            try{
                storyIV.setImageResource(getResources().getIdentifier(background,
                        "drawable",getActivity().getPackageName()));
            }catch(Resources.NotFoundException e){
                storyIV.setVisibility(View.GONE);
            }
        }

        //display npc image and dialogue
        try {
            Resources resources = getActivity().getResources();
            String image = dialogue.getActor().getImage();
            if(image!=null &&!image.isEmpty()) {
                Log.d("TAG", "Actor npc name:" + image);
                final int resourceId = resources.getIdentifier(image, "drawable",
                        getActivity().getPackageName());
                Log.d("TAG", "Actor npc resource id:" + resourceId);
                Drawable id = resources.getDrawable(resourceId);
                if(id!=null){
                    npcIV.setImageDrawable(id);
                }

            } else {
                npcIV.setVisibility(View.GONE);
            }
        }catch (NullPointerException f){
            npcIV.setVisibility(View.GONE);
        }

        //npcIV.setImageResource((R.drawable.storyteller));
        npcTV.setText(dialogue.getBody());
        //create list with answers
        for(Answer a: dialogue.getAnswers()){
            answers.add(a);
        }
        answerAdapter = new AnswerAdapter(getContext(),answers);
        answersLV.setAdapter(answerAdapter);
        answersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG","Listener clicked");
                Answer clickedAnswer =(Answer)adapterView.getItemAtPosition(i);
                Log.d("TAG","Chosen answer\n" + clickedAnswer.toString());
                onStoryAnswerPressed(clickedAnswer.getAnswerText());
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_story, container, false);
        storyIV = (ImageView)v.findViewById(R.id.dial_background_iv);
        npcIV =(ImageView)v.findViewById(R.id.dial_npc_img);
        npcTV = (TextView)v.findViewById(R.id.frag_story_tv);
        answersLV = (ListView)v.findViewById(R.id.frag_story_lv);
        initialize();
        return v;
    }


    public void onStoryAnswerPressed(String answer) {

        if(!isAnswered) {
            if (mListener != null) {
                mListener.onStoryDialogueAnswer(answer);
            } else {
                mListener = (onStoryFragInteractListener) getActivity();
                mListener.onStoryDialogueAnswer(answer);
            }
            isAnswered=true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onStoryFragInteractListener) {
            mListener = (onStoryFragInteractListener) context;
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface onStoryFragInteractListener {
        void onStoryDialogueAnswer(String answer);
    }


}
