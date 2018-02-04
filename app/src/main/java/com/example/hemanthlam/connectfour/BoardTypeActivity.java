package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BoardTypeActivity extends AppCompatActivity {

    protected Spinner gridSpinner;
    protected Spinner gameSpinner;
    protected Spinner roundSpinner;
    String gameType = "Local Multiplayer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_type);
        gridSpinner = (Spinner) findViewById(R.id.gridSpinner);
        gameSpinner = (Spinner) findViewById(R.id.gameSpinner);
        roundSpinner = (Spinner) findViewById(R.id.roundSpinner);
        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (gameSpinner.getSelectedItem().toString()) {
                    case "Game Mode":
                        gameType = "Local Multiplayer";
                        break;
                    case "Local Multiplayer":
                        gameType = "Local Multiplayer";
                        break;
                    case "Online Multiplayer":
                        gameType = "Online Multiplayer";
                        break;
                    case "AI Mode (Single Player)":
                        gameType = "AI Mode (Single Player)";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    protected void clickContinue(View view){
        Intent intent = null;
        switch (gridSpinner.getSelectedItem().toString()){
            case "Board Size":
                intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("Board", "7 x 6");
                intent.putExtra("Game", gameType);
                intent.putExtra("Round",roundSpinner.getSelectedItem().toString());
                break;
            case "7 x 6":
                intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("Board", "7 x 6");
                intent.putExtra("Game", gameType);
                intent.putExtra("Round",roundSpinner.getSelectedItem().toString());
                break;
            case "8 x 7":
                intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("Board", "8 x 7");
                intent.putExtra("Game", gameType);
                intent.putExtra("Round",roundSpinner.getSelectedItem().toString());
                break;
            case "10 x 8":
                intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("Board", "10 x 8");
                intent.putExtra("Game", gameType);
                intent.putExtra("Round",roundSpinner.getSelectedItem().toString());
                break;
        }
        startActivity(intent);
    }
    /*public void boardType(View button){
        Intent intent=null;
        switch (button.getId()){
            case R.id.board_76:
                intent = new Intent(getApplicationContext(), Game1Activity.class);
                break;
            case R.id.board_87:
                intent = new Intent(getApplicationContext(), Game2Activity.class);
                break;
            case R.id.board_108:
                intent = new Intent(getApplicationContext(), Game3Activity.class);
                break;
        }
        startActivity(intent);

    }*/
}
