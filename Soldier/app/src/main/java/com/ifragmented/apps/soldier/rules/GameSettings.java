package com.ifragmented.apps.soldier.rules;

/**
 * Created by Ella on 2017-01-07. Object of class GameSettings holds general game settings for a
 * specific game, different objects can be created with different sets of settings. To instantiate
 * this object, use @GameSettings.GSBuilder class, create object of it and run build method.
 */

public class GameSettings {

    public final int MAX_CHALLENGES;
    public final int MAX_ENCOUNTERS;
    public final int PLAYER_START_MOTIVATION;
    public final int QUESTION_DURATION_MILLIS;
    public final int CHALLENGE_MAX_ANSWERS;
    public final int CHALLENGE_USER_MAX_ANSWERS;

    private GameSettings(int maxEncounters, int playerHp, int questionTimeMilli, int answersNumber, int maxChallenges, int challengeUserMaxAnswers){
        this.MAX_ENCOUNTERS = maxEncounters;
        this.PLAYER_START_MOTIVATION = playerHp;
        this.QUESTION_DURATION_MILLIS = questionTimeMilli;
        this.MAX_CHALLENGES = maxChallenges;
        this.CHALLENGE_MAX_ANSWERS = answersNumber;
        this.CHALLENGE_USER_MAX_ANSWERS = challengeUserMaxAnswers;
    }


    /**
     * GSBuilder helps building GameSettings object, if any of parameters that are required by game
     * settings isn't supplied, then GSBuilder object will use default value to ensure valid game
     * settings.
     */
    public static class GSBuilder {

        //default values to be used for GameSettings object if they are not provided before build method is run
        private int maxEncounters =3;
        private int playerStartHp =100;
        private int questionDurationMillis =1000;
        private int challengeMaxAnswers =4;
        private int maxChallenges =3;
        private int challengeUMaxAnswers=3;

        /**
         * Instantiate builder to be able to create GameSettings object with chosen settings.
         */
        public GSBuilder(){}

        public GSBuilder maxEncounters(int max){
            if(max>0){
                maxEncounters = max;
            } else {//should throw exception
            }
            return this;
        }

        public GSBuilder playerHp(int hp){
            if(hp>0){
                playerStartHp = hp;
            } else {//should throw exception
            }
            return this;
        }
        public GSBuilder questionDuration(int max){
            if(max>0){
                questionDurationMillis = max;
            } else {//should throw exception

            }
            return this;
        }

        public GSBuilder maxChallenges(int max){
            if(max>0){
                maxChallenges = max;
            } else {//should throw exception

            }
            return this;
        }

        private GSBuilder setUserChallMaxAnswers(int maxUAnswers){
            if(maxUAnswers >0 && maxUAnswers< challengeMaxAnswers){
                this.challengeUMaxAnswers = maxUAnswers;
            }
            return this;
        }

        /**
         * Builds GameSettings object and returns it. All fields in game settings that haven't been
         * assigned by calling methods on GSBuilder object, will be filled with default, positive values
         * that ensure valid game settings.
         * @return configured GameSettings object
         */
        public GameSettings build(){
            return new GameSettings(maxEncounters, playerStartHp, questionDurationMillis, challengeMaxAnswers, maxChallenges, challengeUMaxAnswers);
        }
    }
}
