package com.ifragmented.apps.soldier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ifragmented.apps.soldier.data.DBHelper;
import com.ifragmented.apps.soldier.data.Score;
import com.ifragmented.apps.soldier.data.ScoreboardAdapter;
import com.ifragmented.apps.soldier.data.SoldierManager;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private ListView mListView;
    private LinearLayout scoreboardLL;
    private SoldierManager soldierManager;

    private ScoreboardAdapter scoreboardAdapter;
    private ArrayList<Score> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mListView = (ListView)findViewById(R.id.lv_highscore);
        scoreboardLL =(LinearLayout)findViewById(R.id.scoreboard_ll);
        soldierManager = SoldierManager.getInstance(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        showScoreboard();


    }

    /**
     * When clicked ask user about name and start new GameActivity.
     * @param view button that starts new game
     */
    public void startGame(View view) {
        Intent i = new Intent(this, UserInfoActivity.class);
        startActivity(i);
    }

    /**
     * When view clicked start AboutActivity with information about application and gameplay.
     * @param view button that leads to about activity
     */
    public void showAbout(View view) {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    /**
     * showScoreboard displays list of user scores from database. Users are sorted by highest points
     * to lowest.
     */
    private void showScoreboard() {
        scores = soldierManager.getScores();
        if(scores!=null && scores.size()>0){
            scoreboardLL.setVisibility(View.VISIBLE);
            scoreboardAdapter = new ScoreboardAdapter(this,scores);
            mListView.setAdapter(scoreboardAdapter);
        }
    }

}
