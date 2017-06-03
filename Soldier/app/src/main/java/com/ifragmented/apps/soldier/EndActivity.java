package com.ifragmented.apps.soldier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * EndActivity is used to display game result information to player after a game is finished, interrupter
 * and either won or lost.
 */
public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
    }
}
