package com.example.hemanthlam.connectfour;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Sean on 1/25/2018.
 */

//Where all of the important functions for the game board will be placed this will allow Game1Activity,
//Game2Activity, and Game3Activity to share most of the same functions and variables.
public class GameActivity extends AppCompatActivity {
    protected Board gameBoard;
    protected RelativeLayout box;
    protected int turn = 1;
    protected int gameType;

    //Hide all UI pieces from board
    protected void hidePieces(){
        for(int i = 0; i < box.getChildCount();++i){
            LinearLayout curCol = (LinearLayout) box.getChildAt(i);
            for(int j = 0; j < curCol.getChildCount();++j){
                View view = curCol.getChildAt(j);
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    //Place a disc in the correct slot on the board
    protected void placeDisc(int col){
        int row = gameBoard.findPosition(col,turn);
        if(row == -1)
            return;
        LinearLayout temp = (LinearLayout) box.getChildAt(col);
        ImageView chip = (ImageView) temp.getChildAt(row);
        animate(chip);
        changeTurn();
    }
    //Switch turns
    protected void changeTurn(){
        if(turn == 1)
            turn = 2;
        else turn = 1;
    }

    protected void animate(ImageView chip){
        if(turn == 1)
            chip.setImageResource(R.drawable.blue);
        else chip.setImageResource(R.drawable.red);
        chip.setTranslationY(-1000);
        chip.setVisibility(View.VISIBLE);
        chip.animate().translationYBy(1000).setDuration(500);
    }
}
