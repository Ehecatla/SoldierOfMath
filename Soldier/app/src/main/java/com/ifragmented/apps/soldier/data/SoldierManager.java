package com.ifragmented.apps.soldier.data;

import android.content.Context;

import com.ifragmented.apps.soldier.player.Player;
import com.ifragmented.apps.soldier.rules.GameSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ellaeinarsen on 2017-01-01.
 */

public class SoldierManager {

    private static SoldierManager instance = new SoldierManager();
    private Game activeGame;
    private DBHelper dbHelper;

    public static SoldierManager getInstance(Context context) {
        if(instance.dbHelper ==null){
            instance.dbHelper = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public Game getGame(){
        return activeGame;
    }

    public void initGame(String username){
        GameSettings gameSettings = dbHelper.getGameSettings();
        Player player = new Player(username,gameSettings.PLAYER_START_MOTIVATION);
        activeGame = new Game(player,gameSettings,dbHelper.getRandomStory(gameSettings.MAX_ENCOUNTERS),getNumberChallenges(gameSettings));
    }

    private List<Challenge> getNumberChallenges(GameSettings gameSettings) {
        return dbHelper.getChallenges(gameSettings);
    }


    /**
     * Saves actual game if game is won.
     */
    public void saveGameResults(){
        if(activeGame.getGameState() == GameState.FINISHED && activeGame.getActiveUser().isAlive()){
            Score userScore = new Score(activeGame.getActiveUser().getName(),activeGame.getStoryTitle(),activeGame.getActiveUser().getHealth());
            dbHelper.saveGameResults(userScore);
            activeGame.abandonGame(); //ensure game is stopped for good and wont be able to be saved again
            //removeGame();
        }
    }


    /**
     * Method removeGame sets active game instance to null thus making it impossible to continue with
     * that game instance, to be used only when game is finished or played decided to abandon it.
     */
    public void removeGame() {
        activeGame = null;
    }

    public ArrayList<Score> getScores() {
        return this.dbHelper.getScoreList();
    }
}
