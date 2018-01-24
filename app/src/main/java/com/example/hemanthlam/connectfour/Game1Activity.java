package com.example.hemanthlam.connectfour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Game1Activity extends AppCompatActivity {
    private LinearLayout col0 = (LinearLayout) findViewById(R.id.GAME_ONE_COLUMN_ONE);
    private LinearLayout col1 = (LinearLayout) findViewById(R.id.GAME_ONE_COLUMN_ONE);
    private LinearLayout col2 = (LinearLayout) findViewById(R.id.GAME_ONE_COLUMN_TWO);
    private LinearLayout col3 = (LinearLayout) findViewById(R.id.GAME_ONE_COLUMN_THREE);
    private LinearLayout col4 = (LinearLayout) findViewById(R.id.GAME_ONE_COLUMN_FOUR);
    private LinearLayout col5 = (LinearLayout) findViewById(R.id.GAME_ONE_COLUMN_FIVE);
    private LinearLayout col6 = (LinearLayout) findViewById(R.id.GAME_ONE_COLUMN_SIX);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

    }

    protected void hidePieces(){

    }
}
