package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/*
author sonam
 */
//This class displays the various grid boards available in the game
public class BoardTypeActivity extends AppCompatActivity {

    protected Spinner gridSpinner;
    protected Spinner gameSpinner;
    String gameType = "Local Multiplayer";
    String TAG = "BoardTypeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_type);
        gridSpinner = findViewById(R.id.gridSpinner);
        gameSpinner = findViewById(R.id.gameSpinner);
        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Saves the gridspinner information
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (gameSpinner.getSelectedItem().toString()) {
                    case "Local Multiplayer":
                        System.out.println("SELECT LOCAL MULTI-PLAYER");
                        gameType = "Local Multiplayer";
                        break;
                    case "AI Mode (Single Player)":
                        gameType = "AI Mode (Single Player)";
                        break;
                }
                Log.d(TAG, "Game type selected:" + gameType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    // Sets up our transition to the NameActivity. This will send the board, gameType, and round
    // information to the next page.
    public void clickContinue(View view){
        Intent intent = null;
        switch (gridSpinner.getSelectedItem().toString()){
            case "7 x 6":
                System.out.println("SELECT 7*6");
                intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("Board", "7 x 6");
                intent.putExtra("Game", gameType);
                break;
            case "8 x 7":
                intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("Board", "8 x 7");
                intent.putExtra("Game", gameType);
                break;
            case "10 x 8":
                intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("Board", "10 x 8");
                intent.putExtra("Game", gameType);
                break;
        }
        startActivity(intent);
    }
}
