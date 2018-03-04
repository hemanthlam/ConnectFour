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
        final Button highscorebutton = (Button) findViewById(R.id.highscorebutton) ;
        final Button playbutton = (Button) findViewById(R.id.playbutton);
        final Button onlineModebutton = (Button) findViewById(R.id.onlinemodesetupbutton);

        // Cheese
        // I don't know if we need a seperate thread to run this, but why not give it a shot!
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                onlineModebutton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){
                        Intent startIntent = new Intent(getApplicationContext(), OnlineModeSetup.class);
                        startActivity(startIntent);
                    }
                });
            }
        }).start();

    }
}