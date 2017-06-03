package com.ifragmented.apps.soldier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ifragmented.apps.soldier.data.Game;
import com.ifragmented.apps.soldier.data.SoldierManager;

public class PausActivity extends AppCompatActivity {

    private SoldierManager s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paus);
        s = SoldierManager.getInstance(this);
    }

    public void continueGame(View view) {
        finish();
    }


    public void endGame(View view) {
        SoldierManager s = SoldierManager.getInstance(this);
        s.getGame().abandonGame();
        String message="You have abandoned game, your result will not be calculated.";
        displayEndMessage(message);

    }

    private void displayEndMessage(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        s.removeGame();
                        moveToStart();
                    }
                }).setCancelable(false);
        builder.create().show();
    }

    private void moveToStart() {
        Intent intent =new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
