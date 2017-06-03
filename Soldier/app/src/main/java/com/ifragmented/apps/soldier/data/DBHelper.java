package com.ifragmented.apps.soldier.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ifragmented.apps.soldier.R;
import com.ifragmented.apps.soldier.enemy.Actor;
import com.ifragmented.apps.soldier.rules.DifficultyLevel;
import com.ifragmented.apps.soldier.rules.DifficultyOffset;
import com.ifragmented.apps.soldier.rules.GameSettings;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.ifragmented.apps.soldier.data.SoldierDBContract.*;


/**
 * Created by ellaeinarsen on 2017-01-01. Take care of SQLite database used by application.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Soldier.db";

    private static int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        generateStoryData(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER_ENTRIES);
        db.execSQL(SQL_CREATE_STORY_ENTRIES);
        db.execSQL(SQL_CREATE_ACTOR_ENTRIES);
        db.execSQL(SQL_CREATE_ANSWER_ENTRIES);
        db.execSQL(SQL_CREATE_DIALOGUE_ENTRIES);
        db.execSQL(SQL_CREATE_CHALLENGE_ENTRIES);
    }

    /**
     * Checks if there are any story entries, if none are present then this method will read json
     * files and fetch the pre-saved stories to be used in game.
     */
    private void generateStoryData(Context context) {
        //check if data present, return if true
        SQLiteDatabase db= this.getReadableDatabase();
        Log.d("DBHelper","Checking if first time start db");
        String[]scoreProjection ={StoryTableFeed._ID};
        Cursor cursor = db.query(
                ActorTableFeed.TABLE_NAME,                                      // The table to query
                scoreProjection,                                                // The columns to return
                null,                                                           // The columns for the WHERE clause
                null,                                                           // The values for the WHERE clause
                null,                                                           // don't group the rows
                null,                                                           // don't filter by row groups
                null                                                            // The sort order
        );

        if(cursor.moveToNext()){
            cursor.close();
            db.close();
            return;
        }
        cursor.close();
        db.close();

        db = this.getWritableDatabase();

        JSONSoldierDecoder.readJSONSourceToDatabase(db,context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_USER_ENTRIES);
        db.execSQL(SQL_DELETE_STORY_ENTRIES);
        db.execSQL(SQL_DELETE_ANSWER_ENTRIES);
        db.execSQL(SQL_DELETE_CHALLENGE_ENTRIES);
        db.execSQL(SQL_DELETE_DIALOGUE_ENTRIES);
        db.execSQL(SQL_DELETE_NPC_ENTRIES);
        onCreate(db);
    }

    /**
     * Returns exactly 1 story with all its dialogues and actors. Story is randomly chosen from
     * database.
     * @return a random Story object with data from database
     * @param MAX_ENCOUNTERS
     */
    public Story getRandomStory(int MAX_ENCOUNTERS) {
        SQLiteDatabase db= this.getReadableDatabase();
        Log.d("DBHelper","Getting random story");

        String randomQuery = "SELECT * FROM "+ StoryTableFeed.TABLE_NAME+" ORDER BY RANDOM() LIMIT 1;";

        String storyId;
        String storyTitle;
        String storyIntroId;
        String storyOutroL;
        String storyOutroW;
        String storyBkgrnd;

        Cursor randomRowCursor = db.rawQuery(randomQuery,null);
        randomRowCursor.moveToFirst();
        storyId = randomRowCursor.getString(1);
        storyTitle = randomRowCursor.getString(2);
        storyIntroId = randomRowCursor.getString(3);
        storyOutroL = randomRowCursor.getString(4);
        storyOutroW = randomRowCursor.getString(5);
        storyBkgrnd = randomRowCursor.getString(6);

        Log.d("DBHelper","Random story lotted, info: " + storyId +" " + storyTitle + " intro:"
                + storyIntroId + " outroL" + storyOutroL + " outroW:" + storyOutroW
                + " and background image: " + storyBkgrnd);

        randomRowCursor.close();

        Dialogue intro = fetchDialogueFromDB(storyIntroId);
        Dialogue outroL = fetchDialogueFromDB(storyOutroL);
        Dialogue outroW = fetchDialogueFromDB(storyOutroW);
        ArrayList<Dialogue>storyDialogues= getDefaultDialoguesForStory(storyId,MAX_ENCOUNTERS);

        Story story = new Story(storyTitle,intro,outroL,outroW,storyDialogues,storyBkgrnd);
        Log.d("DBHelper","\nDone creating random story for game\n");
        db.close();
        return story;
    }

    private void testTables(){
        Log.d("TESTAB",""+"\nTEST A");
        SQLiteDatabase db= this.getReadableDatabase();
        String randomQuery = "SELECT * FROM "+ AnswerTableFeed.TABLE_NAME+";";
        Cursor dialogueCursor = db.rawQuery(randomQuery,null);
        while(dialogueCursor.moveToNext()){
            Log.d("TESTA",""+dialogueCursor.getString(0));
            Log.d("TESTA",""+dialogueCursor.getString(1));
            Log.d("TESTA",""+dialogueCursor.getString(2));
            Log.d("TESTA",""+dialogueCursor.getString(3));
            Log.d("TESTA",""+dialogueCursor.getString(4));
            Log.d("TESTA",""+dialogueCursor.getString(5));
            Log.d("TESTA",""+dialogueCursor.getString(6));
            Log.d("TESTA",""+"///////");
        }
        Log.d("TESTAB",""+"\nTEST B");
        randomQuery = "SELECT * FROM "+ ActorTableFeed.TABLE_NAME+";";
        Cursor dialogueCursor2 = db.rawQuery(randomQuery,null);
        while(dialogueCursor2.moveToNext()){
            Log.d("TESTAB",""+dialogueCursor2.getString(0));
            Log.d("TESTAB",""+dialogueCursor2.getString(1));
            Log.d("TESTAB",""+dialogueCursor2.getString(2));
            Log.d("TESTAB",""+"///////");
        }
    }

    private ArrayList<Dialogue> getDefaultDialoguesForStory(String storyID,int numberDialogues){
        ArrayList<Dialogue>storyDialogues = new ArrayList<>();
        SQLiteDatabase db= this.getReadableDatabase();
//        String query = "SELECT "+ DialogueTableFeed.COLUMN_NAME_ID + " FROM "+ DialogueTableFeed.TABLE_NAME
//                +" WHERE "+DialogueTableFeed.COLUMN_PARENT_STORY + " = '" + storyID
//                + "' AND "+DialogueTableFeed.COLUMN_NAME_TYPE +" = 'DEFAULT' "
//                + " AND "+DialogueTableFeed.COLUMN_TRIGGER +" = 1 " + "ORDER BY RANDOM() LIMIT " +numberDialogues+" ;";
        String query = "SELECT "+ DialogueTableFeed.COLUMN_NAME_ID + " FROM "+ DialogueTableFeed.TABLE_NAME
                +" WHERE "+DialogueTableFeed.COLUMN_PARENT_STORY + " = '" + storyID
                + "' AND "+DialogueTableFeed.COLUMN_NAME_TYPE +" = 'DEFAULT' "
                + " AND "+DialogueTableFeed.COLUMN_TRIGGER +" = 1 " + "ORDER BY RANDOM() LIMIT " +numberDialogues+1+" ;";
/*
        String query = "SELECT "+ DialogueTableFeed.COLUMN_NAME_ID + " FROM "+ DialogueTableFeed.TABLE_NAME
                +" WHERE "+DialogueTableFeed.COLUMN_PARENT_STORY + " = '" + storyID
                + "' AND "+DialogueTableFeed.COLUMN_NAME_TYPE +" = 'DEFAULT' "
                + "' AND "+DialogueTableFeed.COLUMN_TRIGGER +" = 1 " + "ORDER BY RANDOM() LIMIT " +numberDialogues+" ;";
 */
        Cursor dialogueCursor = db.rawQuery(query,null);
        if(dialogueCursor==null || !dialogueCursor.moveToNext()){return null;}

        while(dialogueCursor.moveToNext()){
            Dialogue dialogue = fetchDialogueFromDB(dialogueCursor.getString(0));
            storyDialogues.add(dialogue);
        }
        dialogueCursor.close();
        return storyDialogues;
    }

    private Dialogue fetchDialogueFromDB(String dialogueId){
        if(dialogueId==null){return null;}
        Dialogue dialogue = null;
        SQLiteDatabase db= this.getReadableDatabase();
        String randomQuery = "SELECT * FROM "+ DialogueTableFeed.TABLE_NAME+" WHERE "+DialogueTableFeed.COLUMN_NAME_ID + " = '" + dialogueId+"' ;";
        Log.d("DBHELPER","Searching for dialogue with id: " + dialogueId + " and by sql: \n" + randomQuery);
        Cursor dialogueCursor = db.rawQuery(randomQuery,null);
        if(dialogueCursor==null || !dialogueCursor.moveToNext()){return null;}
        String actor = dialogueCursor.getString(1);
        String body = dialogueCursor.getString(2);
        String id = dialogueCursor.getString(3);
        String type = dialogueCursor.getString(4);
        String diffLvl = dialogueCursor.getString(5);
        String parentStory = dialogueCursor.getString(6);
        int triggerValue = dialogueCursor.getInt(7);
        Log.d("DBHELPER","Fetched dialogue with data:" +actor+" " + body+" " + id+" " + type+" "+ "and parent story:" + parentStory);
        dialogueCursor.close();
        Answer [] answers = fetchAnswersFromDB(id);
        Actor dialActor = fetchActorFromDB(actor);
        DialogueType dt = DialogueType.valueOf(type);
        DifficultyLevel df = DifficultyLevel.getDifficulty(Integer.parseInt(diffLvl));
        dialogue = new Dialogue(body,answers,dt,dialActor,df);
        dialogue.setTrigger(triggerValue == 1);
        dialogueCursor.close();
        db.close();
        return dialogue;
    }

    private Answer[] fetchAnswersFromDB(String id) {
        if(id==null){return null;}
        Answer[]answers=null;
        SQLiteDatabase db= this.getReadableDatabase();
        String randomQuery = "SELECT * FROM "+ AnswerTableFeed.TABLE_NAME+" WHERE "+AnswerTableFeed.COLUMN_PARENT_DIALOGUE + " = '" + id+"' ;";
        Log.d("DBHELPER","Searching for answers for dialogue with id: " + id + " and by sql: \n" + randomQuery);
        Cursor dialogueCursor = db.rawQuery(randomQuery,null);
        if(dialogueCursor==null || dialogueCursor.getCount()<1){
            Log.d("DBHelper","Error, row count 0 for " + id);
            return null;
        }
        ArrayList<Answer>answersList = new ArrayList<>();
        while (dialogueCursor.moveToNext()){
            String answerID = dialogueCursor.getString(1);
            String answeBody=dialogueCursor.getString(2);
            String outcome=dialogueCursor.getString(3);
            String followup=dialogueCursor.getString(4);
            String difficultyOffset =dialogueCursor.getString(5);
            String answerParent = dialogueCursor.getString(6);
            Dialogue followupD = null;
            if(followup!=null && !followup.isEmpty()){
                followupD = fetchDialogueFromDB(followup);

            }
            Answer answer = new Answer(answerID,answeBody,Outcome.valueOf(outcome), DifficultyOffset.offsetForValue(difficultyOffset),followupD);
            answersList.add(answer);
        }
        answers= new Answer[answersList.size()];
        for(int i=0;i<answers.length;i++){
            answers[i]=answersList.get(i);
        }
        dialogueCursor.close();
        db.close();
        return answers;
    }

    private Actor fetchActorFromDB(String actor) {
        Actor a = null;
        SQLiteDatabase db= this.getReadableDatabase();
        String randomQuery = "SELECT * FROM "+ ActorTableFeed.TABLE_NAME+" WHERE "+ActorTableFeed.COLUMN_ACTOR_ID + " = '" + actor+"' ;";
        Log.d("DBHELPER","Searching for actor with id: " + actor + " and by sql: \n" + randomQuery);
        Cursor dialogueCursor = db.rawQuery(randomQuery,null);
        if(dialogueCursor==null || dialogueCursor.getCount()<1){
            Log.d("DBHelper","Error, row count 0 for " + actor);
            return null;
        }
        if (dialogueCursor.moveToNext()) {  //should be only 1 result
            String id =dialogueCursor.getString(1);
            String name = dialogueCursor.getString(2);
            String img = dialogueCursor.getString(3);
            a = new Actor(id,name,img);

        }
        dialogueCursor.close();
        db.close();
        return a;
    }

    /**
     * Returns list of all possible Challenges that is used to get random challenges for every
     * encounter in game.
     * @param gameSettings required GameSettings to set challenge points correctly
     * @return List of Challenge objects ready to be used in game
     */
    public List<Challenge> getChallenges(GameSettings gameSettings){
        SQLiteDatabase db= this.getReadableDatabase();
        Log.d("DBHelper","Getting challenges.");
        String[]scoreProjection ={ChallengeTableFeed._ID,
                ChallengeTableFeed.COLUMN_CH_BODY,
                ChallengeTableFeed.COLUMN_CH_ID,
                ChallengeTableFeed.COLUMN_CH_DIFFICULTY,
                ChallengeTableFeed.COLUMN_CH_ALTERNATIVES,
                ChallengeTableFeed.COLUMN_CH_CORRECT};

        Cursor cursor = db.query(
                ChallengeTableFeed.TABLE_NAME,                // The table to query
                scoreProjection,                                                // The columns to return
                null,                                                           // The columns for the WHERE clause
                null,                                                           // The values for the WHERE clause
                null,                                                           // don't group the rows
                null,                                                           // don't filter by row groups
                null                                                            // The sort order
        );

        ArrayList<Challenge>challenges = new ArrayList<>();
        while(cursor.moveToNext()){
            //Log.d("PrintOfChallenge",cursor.get);
            String body = cursor.getString(1);
            String correct = cursor.getString(5);
            String alt = cursor.getString(4);
            String diff = cursor.getString(3);
            String id = cursor.getString(2);
            String [] answers= (String[]) divideChallengeAnswers(alt).toArray();
            DifficultyLevel difficultyLevel = DifficultyLevel.valueOf(diff);
            Challenge challenge = new Challenge(body,answers,correct,difficultyLevel);
            challenge.setChallengePoints(new ChallengePoints(gameSettings,difficultyLevel));
            challenges.add(challenge);
        }
        cursor.close();

        return challenges;
    }

    /**
     * Challenge answers are one String divided by comma signs and this method separates answers
     * and returns these as a list of answers.
     * @param answers list of answers in one string separated by comma
     * @return list of answers as list of String
     */
    private List<String> divideChallengeAnswers(String answers){
        List<String> dividedList;
        dividedList = Arrays.asList(answers.split(","));
        return  dividedList;
    }

    /**
     * This method creates and returns default game settings using GSBuilder inner class.
     *
     * INFO It could be possible to save specific, different settings in future in sql database
     * and load them from there, although this is not necessary with low lvl of complexity and
     * not game setting change availabe for user.
     * @return GameSettings object containing important, base settings used by Soldier game
     */
    public GameSettings getGameSettings() {
        GameSettings.GSBuilder builder = new GameSettings.GSBuilder();
        return builder.build();
    }

    /**
     * Saves scores to database. Method should be called only if players hp is > 0 after finishing all
     * rounds.
     * @param score is players score after finished game
     */
    public void saveGameResults(Score score) {

        String name = score.getUserName();
        int hp = score.getPoints();
        String story = score.getStoryTitle();

        SQLiteDatabase db;
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTableFeed.COLUMN_NAME_NAME, name);
        values.put(UserTableFeed.COLUMN_NAME_SCORE, hp);
        values.put(UserTableFeed.COLUMN_NAME_STORY, story);
        long newRowId = db.insert(UserTableFeed.TABLE_NAME,null,values);

    }

    /**
     * Retrieves and returns score list containing list of scores with username, story title and
     * points (hp/motivation) earned sorted from highest score to lowest.
     * @return
     */
    public ArrayList<Score> getScoreList(){
        ArrayList<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[]scoreProjection ={UserTableFeed.COLUMN_NAME_NAME,
                                                UserTableFeed.COLUMN_NAME_STORY,
                                                    UserTableFeed.COLUMN_NAME_SCORE};

        String sortOrder = UserTableFeed.COLUMN_NAME_SCORE + " DESC";

        Cursor cursor = db.query(
                UserTableFeed.TABLE_NAME,                     // The table to query
                scoreProjection,                                                // The columns to return
                null,                                                           // The columns for the WHERE clause
                null,                                                           // The values for the WHERE clause
                null,                                                           // don't group the rows
                null,                                                           // don't filter by row groups
                sortOrder                                                       // The sort order
        );

       while(cursor.moveToNext()){
           //long itemId = cursor.getLong(
                   //cursor.getColumnIndexOrThrow(SoldierDBContract.UserTableFeed._ID));
           String user =cursor.getString(0);
           String story = cursor.getString(1);
           int points = cursor.getInt(2);
           Log.d("DBTAG","Retrieved user: " +user + " and story: " + story + " and points: " + points);
           if(user!=null && story!=null && points>0){
               Score sc = new Score(user,story,points);
               scores.add(sc);
           }
       }
        cursor.close();
        return scores;
    }







}
