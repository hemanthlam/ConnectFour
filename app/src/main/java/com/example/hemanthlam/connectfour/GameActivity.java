package com.example.hemanthlam.connectfour;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    private boolean isGameOver;

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
    //if isGameOver dont place discs
    protected void placeDisc(int col){
        if(!isGameOver) {
            int row = gameBoard.findPosition(col, turn);
            if (row == -1)
                return;
            LinearLayout temp = (LinearLayout) box.getChildAt(col);
            ImageView chip = (ImageView) temp.getChildAt(row);
            animate(chip);
            findWinner();
            changeTurn();
        }
    }

    //finds if the player who is playing has won
    public void findWinner(){

        int[][] a = gameBoard.findWinner(turn);
        final String message = "player" + turn + "won";
        if(a!=null){
            isGameOver = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }, 100);

        }
        else {
            if(gameBoard.checkIfBoardFull()) {
                isGameOver = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Stalemate" , Toast.LENGTH_SHORT).show();
                    }
                }, 100);

            }
        }
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
