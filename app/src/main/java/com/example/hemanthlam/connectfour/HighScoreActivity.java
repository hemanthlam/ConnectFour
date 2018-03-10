package com.example.hemanthlam.connectfour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hemanthlam.connectfour.db.AppDatabase;
import com.example.hemanthlam.connectfour.db.Player;
import com.example.hemanthlam.connectfour.db.PlayerDAO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {
    List<TextView> textViews = new ArrayList<>();
    String TAG = "HighScoreActivity";

    /*
    saves the textviews in the list for further processing
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        textViews.add((TextView) findViewById(R.id.highScoreOne));
        textViews.add((TextView) findViewById(R.id.highScoreTwo));
        textViews.add((TextView) findViewById(R.id.highScoreThree));
        textViews.add((TextView) findViewById(R.id.highScoreFour));
        textViews.add((TextView) findViewById(R.id.highScoreFive));

        setHighScores();
    }

    /*clear all the text views to display the empty score value*/
    public void clearTextViews(){
        for(int i=0;i<5;i++){
            textViews.get(i).setText("[Empty Score]");
        }
        Log.d(TAG,"cleared all the scores");
    }

    /*
     Gets all the db rows and deletes them
     */
    public void clearHighScores(View view){
        PlayerDAO playerDAO = AppDatabase.getAppDatabase(getApplicationContext()).userDao();
        Iterator<Player> iterator = playerDAO.getAll().iterator();
        while(iterator.hasNext()){
            Player next = iterator.next();
            playerDAO.delete(next);
        }
        Log.d(TAG,"deleted all the entries in the player table");
        clearTextViews();
    }

    /*
    gets the top five scores from db and prints them on ui
     */
    void setHighScores(){

        int i=0;
        Iterator<Player> iterator = AppDatabase.getAppDatabase(getApplicationContext()).userDao().getTop5Scores().iterator();
        while(iterator.hasNext()){
            Player next = iterator.next();
            String s = next.getScore() + "\t  " + next.getName();
            Log.d(TAG,"next high score" + s);
            textViews.get(i).setText(s);
            i++;
        }

    }
}