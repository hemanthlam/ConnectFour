package com.example.hemanthlam.connectfour;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"main activity");
        Button highscorebutton = (Button) findViewById(R.id.highscorebutton) ;
        Button playbutton = (Button) findViewById(R.id.playbutton);
        playbutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
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
    }
}