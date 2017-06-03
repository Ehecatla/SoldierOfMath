package com.ifragmented.apps.soldier;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UserInfoActivity extends AppCompatActivity {

    private EditText mETUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mETUserName = (EditText)findViewById(R.id.et_user_name);
    }

    public void startGameWithUser(View view) {
        String username = mETUserName.getText().toString();
        if(username.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Start game with default username?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startGame("Player");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        } else if(username.trim().length()<1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your username is invalid, start game as default user instead?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startGame("Player");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        } else{
          startGame(username);
        }
    }

    private void startGame(String username){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.GAME_USERNAME, username);
        startActivity(i);
        finish();   //don't want user being able to back to this activity
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Leave game to main menu?")
                .setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        builder.create().show();
        // super.onBackPressed();
    }


}
