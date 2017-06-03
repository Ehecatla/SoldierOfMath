package com.ifragmented.apps.soldier.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ifragmented.apps.soldier.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ella on 2017-03-05. Class that reads existing JSON files containing base data for all
 * stories and challenges.
 */

public class JSONSoldierDecoder {

    private static final int SOLDIER_STORIES_JSON_SOURCE = R.raw.soldier_stories;
    private static final int SOLDIER_ACTORS_JSON_SOURCE = R.raw.actors;
    private static final int SOLDIER_DIALOGUES_JSON_SOURCE = R.raw.dialogues;
    private static final int SOLDIER_ANSWERS_JSON_SOURCE =R.raw.answers ;
    private static final int SOLDIER_CHALLENGE_JSON_SOURCE = R.raw.challenges;

    public static final String ANSWER_BODY = "answer_body";
    public static final String ANSWER_OUTCOME = "answer_outcome";
    public static final String ANSWER_FOLLOWUP_ID = "answer_followup_id";
    public static final String ANSWER_DIFFICULTY_OFFSET = "answer_difficulty_offset";
    public static final String ANSWER_ID = "answer_id";
    public static final String PARENT_DIALOGUE = "parent_dialogue";

    public static final String DIALOGUE_ID = "dialogue_id";
    public static final String DIALOGUE_BODY = "dialogue_body";
    public static final String DIALOGUE_TYPE = "dialogue_type";
    public static final String DIALOGUE_ACTOR = "dialogue_actor";
    public static final String DIALOGUE_DIFFICULTY = "dialogue_difficulty";

    public static final String STORY_PARENT = "story_parent";
    public static final String STORY_ID = "story_id";
    public static final String STORY_TITLE = "story_title";
    public static final String STORY_INTRO = "story_intro";
    public static final String STORY_O_W = "story_outro_won";
    public static final String STORY_O_L = "story_outro_lost";
    public static final String STORY_BACKGROUND_IMG = "story_background_img";

    private static final String ACTOR_ID = "actor_id";
    private static final String ACTOR_NAME = "actor_name";
    private static final String ACTOR_IMG = "actor_img";
    private static final String ACTOR_IMG_DEFAULT = "default_actor";
    public static final String CHALLENGE_ID = "challenge_id";
    public static final String CH_DIFFICULTY = "difficulty";
    public static final String CH_BODY = "body";
    public static final String CH_ALTERNATIVES = "alternatives";
    public static final String CH_CORRECT_ALT = "correct_alt";
    private static final String DIALOGUE_TRIGGER = "is_trigger";

    /**
     * Reads files with JSON data for database and adds them to database.
     * @param writableDB readable SQLiteDatabase with tables fullfilling SoldierDBContract
     * @param context application context
     */
    protected static void readJSONSourceToDatabase(SQLiteDatabase writableDB, Context context){
        SQLiteDatabase db = writableDB;
        if(db ==null ||!db.isOpen() || db.isReadOnly()){
            Log.d("JSONDECODER","Database provided is incompatible.");
            return;
        }
        //add actors to database
        String actors = loadJSONFileToString(context, SOLDIER_ACTORS_JSON_SOURCE);
        readActors(db,actors);

        //add answers to database
        String answers = loadJSONFileToString(context, SOLDIER_ANSWERS_JSON_SOURCE);
        readAnswers(db,answers);

        //add dialogues to database
        String dialogues = loadJSONFileToString(context, SOLDIER_DIALOGUES_JSON_SOURCE);
        readDialogues(db,dialogues);

        //read every story
        String stories = loadJSONFileToString(context, SOLDIER_STORIES_JSON_SOURCE);
        readStories(db,stories);

        //read challenges
        String challenges = loadJSONFileToString(context,SOLDIER_CHALLENGE_JSON_SOURCE);
        readChallenges(db,challenges);

        db.close();
        Log.d("JSONDECODER","JSON data decoded and added to database");
    }


    private static void readStories(SQLiteDatabase writableDB, String sourceJSON){
        ContentValues cv;
        try {
            JSONArray jsonStoryArray = new JSONArray(sourceJSON);
            if(jsonStoryArray.length()<1){
                return;
            }
            for(int i=0; i < jsonStoryArray.length(); i++){
                cv = new ContentValues();
                JSONObject story = jsonStoryArray.getJSONObject(i);
                String storyId = story.getString(STORY_ID);
                String storyTitle = story.getString(STORY_TITLE);
                String storyIntro = story.getString(STORY_INTRO);
                String storyOutroW = story.getString(STORY_O_W);
                String storyOutroL = story.getString(STORY_O_L);
                String storyImg = story.getString(STORY_BACKGROUND_IMG);

                cv.put(SoldierDBContract.StoryTableFeed.COLUMN_NAME_U_ID,storyId);
                cv.put(SoldierDBContract.StoryTableFeed.COLUMN_NAME_NAME,storyTitle);
                cv.put(SoldierDBContract.StoryTableFeed.COLUMN_NAME_INTRO,storyIntro);
                cv.put(SoldierDBContract.StoryTableFeed.COLUMN_NAME_O_WON,storyOutroW);
                cv.put(SoldierDBContract.StoryTableFeed.COLUMN_NAME_O_LOST,storyOutroL);
                cv.put(SoldierDBContract.StoryTableFeed.COLUMN_NAME_B_IMG,storyImg);
                writableDB.insert(SoldierDBContract.StoryTableFeed.TABLE_NAME,null,cv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static void readActors(SQLiteDatabase writableDB, String sourceJSON){
        ContentValues cv;
        try {
            JSONArray jsonActorArray = new JSONArray(sourceJSON);
            for(int i=0; i < jsonActorArray.length(); i++){
                JSONObject actorJO = jsonActorArray.getJSONObject(i);
                Log.d("DECODING JSON NPCS:", actorJO.toString());
                cv = new ContentValues();
                String id=actorJO.getString(ACTOR_ID) ;
                String name=actorJO.getString(ACTOR_NAME);
                String srcImg =actorJO.getString(ACTOR_IMG);
                if(srcImg==null || srcImg.isEmpty()){srcImg=ACTOR_IMG_DEFAULT;}
                if(id!=null && name!=null){
                    cv.put(SoldierDBContract.ActorTableFeed.COLUMN_ACTOR_ID,id);
                    cv.put(SoldierDBContract.ActorTableFeed.COLUMN_NAME_NAME,name);
                    cv.put(SoldierDBContract.ActorTableFeed.COLUMN_NAME_IMG,srcImg);
                    writableDB.insert(SoldierDBContract.ActorTableFeed.TABLE_NAME,null,cv);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void readChallenges(SQLiteDatabase writableDB, String sourceJSON){
        ContentValues cv;
        try {
            JSONArray jsonStoryArray = new JSONArray(sourceJSON);
            if(jsonStoryArray.length()<1){
                return;
            }
            for(int k=0; k < jsonStoryArray.length(); k++){
                cv = new ContentValues();
                JSONObject challengeJO = jsonStoryArray.getJSONObject(k);
                String chID = challengeJO.getString(CHALLENGE_ID);
                String chDiff = challengeJO.getString(CH_DIFFICULTY);
                String chBody = challengeJO.getString(CH_BODY);
                String chAlternatives = challengeJO.getString(CH_ALTERNATIVES);
                String chCAnswer = challengeJO.getString(CH_CORRECT_ALT);

                cv.put(SoldierDBContract.ChallengeTableFeed.COLUMN_CH_ID,chID);
                cv.put(SoldierDBContract.ChallengeTableFeed.COLUMN_CH_DIFFICULTY,chDiff);
                cv.put(SoldierDBContract.ChallengeTableFeed.COLUMN_CH_BODY,chBody);
                cv.put(SoldierDBContract.ChallengeTableFeed.COLUMN_CH_ALTERNATIVES,chAlternatives);
                cv.put(SoldierDBContract.ChallengeTableFeed.COLUMN_CH_CORRECT,chCAnswer);
                writableDB.insert(SoldierDBContract.ChallengeTableFeed.TABLE_NAME,null,cv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void readAnswers(SQLiteDatabase writableDB, String sourceJSON){
        ContentValues cv;
        JSONArray answersJOA = null;
        try {
            answersJOA = new JSONArray(sourceJSON);
            for (int j = 0; j < answersJOA.length(); j++) {
                cv = new ContentValues();
                JSONObject jsonobject = answersJOA.getJSONObject(j);
                String answerBody = jsonobject.getString(ANSWER_BODY);
                String answerOutcome = jsonobject.getString(ANSWER_OUTCOME);
                String answerFollowup = jsonobject.getString(ANSWER_FOLLOWUP_ID);
                String answerDiff = jsonobject.getString(ANSWER_DIFFICULTY_OFFSET);
                String answerId = jsonobject.getString(ANSWER_ID);
                String parentDialogue = jsonobject.getString(PARENT_DIALOGUE);

                cv.put(SoldierDBContract.AnswerTableFeed.COLUMN_NAME_ANSWER_ID,answerId);
                cv.put(SoldierDBContract.AnswerTableFeed.COLUMN_NAME_ANSWER,answerBody);
                cv.put(SoldierDBContract.AnswerTableFeed.COLUMN_DIALOGUE_FOLLOWUP,answerFollowup);
                cv.put(SoldierDBContract.AnswerTableFeed.COLUMN_NAME_OUTCOME,answerOutcome);
                cv.put(SoldierDBContract.AnswerTableFeed.COLUMN_DIFFICULTY_OFFSET,answerDiff);
                cv.put(SoldierDBContract.AnswerTableFeed.COLUMN_PARENT_DIALOGUE,parentDialogue);
                writableDB.insert(SoldierDBContract.AnswerTableFeed.TABLE_NAME,null,cv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static void readDialogues(SQLiteDatabase writableDB, String sourceJSON){
        ContentValues cv;
        try {
            JSONArray jsonActorArray = new JSONArray(sourceJSON);
            for(int i=0; i < jsonActorArray.length(); i++){
                JSONObject dialogueJO = jsonActorArray.getJSONObject(i);
                Log.d("JSON_DIALOGUE", dialogueJO.toString());
                cv = new ContentValues();

                String dialogueId =dialogueJO.getString(DIALOGUE_ID);
                String dialogueBody =dialogueJO.getString(DIALOGUE_BODY);
                String type = dialogueJO.getString(DIALOGUE_TYPE);
                String actor = dialogueJO.getString(DIALOGUE_ACTOR);
                String storyParent = dialogueJO.getString(STORY_PARENT);
                String difficulty = dialogueJO.getString(DIALOGUE_DIFFICULTY);
                int triggerValue = Integer.parseInt(dialogueJO.getString(DIALOGUE_TRIGGER));
                cv.put(SoldierDBContract.DialogueTableFeed.COLUMN_NAME_ID,dialogueId);
                cv.put(SoldierDBContract.DialogueTableFeed.COLUMN_NAME_ACTOR_ID,actor);
                cv.put(SoldierDBContract.DialogueTableFeed.COLUMN_NAME_TYPE,type);
                cv.put(SoldierDBContract.DialogueTableFeed.COLUMN_DIFFICULTY_LVL,difficulty);
                cv.put(SoldierDBContract.DialogueTableFeed.COLUMN_NAME_DIALOGUE_BODY,dialogueBody);
                cv.put(SoldierDBContract.DialogueTableFeed.COLUMN_PARENT_STORY,storyParent);
                cv.put(SoldierDBContract.DialogueTableFeed.COLUMN_TRIGGER,triggerValue);
                writableDB.insert(SoldierDBContract.DialogueTableFeed.TABLE_NAME,null,cv);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns string being read from json file.
     * @param context app context
     * @param filename id to resource json file
     * @return String with file content being json
     */
    public static String loadJSONFileToString(Context context, int filename) {
        Log.d("DBHELPER","Json decode");
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d("DBHELPER",json);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
