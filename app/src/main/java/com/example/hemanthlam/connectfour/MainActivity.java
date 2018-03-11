package com.example.hemanthlam.connectfour;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    Button highscorebutton;
    Button playbutton;
    Button onlineModebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"main activity");
        setClickListeners();
    }

    public void setClickListeners(){
        highscorebutton = findViewById(R.id.highscorebutton) ;
        playbutton = findViewById(R.id.playbutton);
        onlineModebutton = findViewById(R.id.onlinemodesetupbutton);

        playbutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                System.out.println("PLAY OFFLINE MODE");
                Intent startIntent = new Intent(getApplicationContext(), BoardTypeActivity.class);
                startActivity(startIntent);
                Log.d(TAG,"play game");
            }
        });

        highscorebutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent startIntent = new Intent(getApplicationContext(), HighScoreActivity.class);
                startActivity(startIntent);
                Log.d(TAG,"View high scores");
            }
        });

        onlineModebutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent startIntent = new Intent(getApplicationContext(), OnlineModeSetup.class);
                startActivity(startIntent);
                Log.d(TAG, "play online");
            }
        });
    }

    /**
     * created an overlay page display rules
     * which displays connect four rules
     * @param view
     */
    public void displayRules(View view) {
        setContentView(R.layout.rules);
        TextView textView = (TextView) findViewById(R.id.textView5);
        textView.setText("Players first choose a color and then take turns dropping colored discs from the top into a 7-column, 6-row or 8-column, 7-row  or 10-column, 8-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs.");
    }

    /**
     * for returning back to the main menu from rules page
     * @param view
     */
    public void mainMenuButton(View view){
        setContentView(R.layout.activity_main);
        setClickListeners();
    }
}