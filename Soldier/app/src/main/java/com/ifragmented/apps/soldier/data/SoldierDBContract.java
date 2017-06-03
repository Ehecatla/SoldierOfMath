package com.ifragmented.apps.soldier.data;

import android.provider.BaseColumns;

/**
 * Created by Ella on 2017-02-05.
 */

public class SoldierDBContract {

    private SoldierDBContract(){}

    protected static final String SQL_CREATE_USER_ENTRIES = "CREATE TABLE " + UserTableFeed.TABLE_NAME +"("
            + UserTableFeed._ID + " INTEGER PRIMARY KEY, "
            + UserTableFeed.COLUMN_NAME_NAME + " TEXT NOT NULL, "
            + UserTableFeed.COLUMN_NAME_STORY + " TEXT, "
            + UserTableFeed.COLUMN_NAME_SCORE + " INTEGER "
            + ");"  ;

    protected static final String SQL_CREATE_ACTOR_ENTRIES = "CREATE TABLE " + ActorTableFeed.TABLE_NAME +"("
            + ActorTableFeed._ID + " INTEGER PRIMARY KEY, "
            + ActorTableFeed.COLUMN_ACTOR_ID + " TEXT NOT NULL, "
            + ActorTableFeed.COLUMN_NAME_NAME + " TEXT NOT NULL, "
            + ActorTableFeed.COLUMN_NAME_IMG + " TEXT "
            + ");"  ;

    protected static final String SQL_CREATE_DIALOGUE_ENTRIES = "CREATE TABLE " + DialogueTableFeed.TABLE_NAME +"("
            + DialogueTableFeed._ID + " INTEGER PRIMARY KEY, "
            + DialogueTableFeed.COLUMN_NAME_ACTOR_ID + " TEXT NOT NULL, "
            + DialogueTableFeed.COLUMN_NAME_DIALOGUE_BODY + " TEXT NOT NULL, "
            + DialogueTableFeed.COLUMN_NAME_ID + " TEXT NOT NULL, "
            + DialogueTableFeed.COLUMN_NAME_TYPE + " TEXT NOT NULL, "
            + DialogueTableFeed.COLUMN_DIFFICULTY_LVL + " TEXT NOT NULL, "
            + DialogueTableFeed.COLUMN_PARENT_STORY + " TEXT NOT NULL, "
            + DialogueTableFeed.COLUMN_TRIGGER + " INTEGER"
            + ");"  ;

    protected static String SQL_CREATE_STORY_ENTRIES = "CREATE TABLE " + StoryTableFeed.TABLE_NAME +"("
            + StoryTableFeed._ID + " INTEGER PRIMARY KEY, "
            + StoryTableFeed.COLUMN_NAME_U_ID + " TEXT NOT NULL UNIQUE, "
            + StoryTableFeed.COLUMN_NAME_NAME + " TEXT NOT NULL, "
            + StoryTableFeed.COLUMN_NAME_INTRO + " TEXT NOT NULL, "
            + StoryTableFeed.COLUMN_NAME_O_LOST + " TEXT NOT NULL, "
            + StoryTableFeed.COLUMN_NAME_O_WON + " TEXT NOT NULL, "
            + StoryTableFeed.COLUMN_NAME_B_IMG + " TEXT "
            + ");";

    protected static final String SQL_CREATE_ANSWER_ENTRIES = "CREATE TABLE " + AnswerTableFeed.TABLE_NAME +"("
            + AnswerTableFeed._ID + " INTEGER PRIMARY KEY, "
            + AnswerTableFeed.COLUMN_NAME_ANSWER_ID+ " TEXT NOT NULL, "
            + AnswerTableFeed.COLUMN_NAME_ANSWER + " TEXT NOT NULL, "
            + AnswerTableFeed.COLUMN_NAME_OUTCOME + " TEXT NOT NULL, "
            + AnswerTableFeed.COLUMN_DIALOGUE_FOLLOWUP + " TEXT NOT NULL, "
            + AnswerTableFeed.COLUMN_DIFFICULTY_OFFSET+ " TEXT NOT NULL, "
            + AnswerTableFeed.COLUMN_PARENT_DIALOGUE+ " TEXT NOT NULL "
            + ");"  ;

    protected static final String SQL_CREATE_CHALLENGE_ENTRIES = "CREATE TABLE " + ChallengeTableFeed.TABLE_NAME +"("
            + ChallengeTableFeed._ID + " INTEGER PRIMARY KEY, "
            + ChallengeTableFeed.COLUMN_CH_BODY+ " TEXT NOT NULL, "
            + ChallengeTableFeed.COLUMN_CH_DIFFICULTY + " TEXT NOT NULL, "
            + ChallengeTableFeed.COLUMN_CH_ALTERNATIVES + " TEXT NOT NULL, "
            + ChallengeTableFeed.COLUMN_CH_CORRECT + " TEXT NOT NULL, "
            + ChallengeTableFeed.COLUMN_CH_ID + " TEXT NOT NULL "
            + ");"  ;

    protected static final String SQL_DELETE_USER_ENTRIES =
            "DROP TABLE IF EXISTS " + UserTableFeed.TABLE_NAME;

    protected static final String SQL_DELETE_STORY_ENTRIES =
            "DROP TABLE IF EXISTS " + StoryTableFeed.TABLE_NAME;

    protected static final String SQL_DELETE_NPC_ENTRIES =
            "DROP TABLE IF EXISTS " + ActorTableFeed.TABLE_NAME;

    protected static final String SQL_DELETE_DIALOGUE_ENTRIES =
            "DROP TABLE IF EXISTS " + DialogueTableFeed.TABLE_NAME;

    protected static final String SQL_DELETE_ANSWER_ENTRIES =
            "DROP TABLE IF EXISTS " + AnswerTableFeed.TABLE_NAME;

    protected static final String SQL_DELETE_CHALLENGE_ENTRIES =
            "DROP TABLE IF EXISTS " + ChallengeTableFeed.TABLE_NAME;


    protected static class UserTableFeed implements BaseColumns {
        protected static final String TABLE_NAME = "USERS";
        protected static final String COLUMN_NAME_NAME = "NAME";
        protected static final String COLUMN_NAME_SCORE = "SCORE";
        protected static final String COLUMN_NAME_STORY = "STORY"; //story title, could be key to story in future instead
    }

    //intro,outroL,outroW,dialogues, background);
    protected static class StoryTableFeed implements BaseColumns {
        protected static final String TABLE_NAME = "STORY";
        protected static final String COLUMN_NAME_U_ID = "STORY_ID";
        protected static final String COLUMN_NAME_NAME = "STORY_TITLE";
        protected static final String COLUMN_NAME_INTRO = "STORY_INTRO";
        protected static final String COLUMN_NAME_O_LOST = "STORY_O_LOST";
        protected static final String COLUMN_NAME_O_WON = "STORY_O_WON";
        protected static final String COLUMN_NAME_B_IMG= "STORY_BACKGROUND_IMG";
        //protected static final String COLUMN_NAME_DIALOGUES = "STORY_DIALOGUES";
    }

    protected static class ChallengeTableFeed implements BaseColumns {
        protected static final String TABLE_NAME = "CHALLENGES";
        protected static final String COLUMN_CH_BODY = "CHALLENGE_BODY";
        protected static final String COLUMN_CH_DIFFICULTY= "CHALLENGE_DIFF";
        protected static final String COLUMN_CH_CORRECT = "CHALLENGE_CORRECT_A";
        protected static final String COLUMN_CH_ALTERNATIVES = "CHALLENGE_ALTERNATIVES";
        protected static final String COLUMN_CH_ID = "CHALLENGE_ID";
    }

    //DIALOGUE_TYPE, DIALOGUE_BODY, ANSWER_TABLE_ID(can be many answers), NPC_TABLE_ID (foreign key)
    protected static class DialogueTableFeed implements BaseColumns {
        protected static final String TABLE_NAME = "DIALOGUES";
        protected static final String COLUMN_NAME_TYPE= "TYPE"; //should make it one of few choices only in table creation
        protected static final String COLUMN_DIFFICULTY_LVL= "DIALOGUE_DIFFICULTY"; //base difficulty of dialogue, used to count difficulty for challenge, offset by answer diff offset
        protected static final String  COLUMN_NAME_ID = "DIALOGUE_ID";
        protected static final String COLUMN_NAME_ACTOR_ID = "ACTOR_ID";
        protected static final String COLUMN_NAME_DIALOGUE_BODY = "DIALOGUE_BODY";
        public static final String COLUMN_PARENT_STORY = "PARENT_STORY";
        protected static final String COLUMN_TRIGGER ="IS_TRIGGER";
    }

    protected static class ActorTableFeed implements BaseColumns {
        protected static final String TABLE_NAME = "NPCS";
        protected static final String COLUMN_ACTOR_ID = "NPCS_ID";
        protected static final String COLUMN_NAME_NAME = "NAME";
        protected static final String COLUMN_NAME_IMG = "IMAGE_ID";
    }

    protected static class AnswerTableFeed implements BaseColumns {
        protected static final String TABLE_NAME = "ANSWERS";
        protected static final String COLUMN_NAME_ANSWER_ID = "ANSWER_ID";
        protected static final String COLUMN_NAME_ANSWER = "ANSWER";
        protected static final String COLUMN_NAME_OUTCOME = "OUTCOME";
        protected static final String COLUMN_DIALOGUE_FOLLOWUP ="DIALOGUE_FOLLOWUP";
        protected static final String COLUMN_DIFFICULTY_OFFSET ="DIFFICULTY_OFFSET";
        protected static final String COLUMN_PARENT_DIALOGUE = "PARENT_DIALOGUE";
    }
}
