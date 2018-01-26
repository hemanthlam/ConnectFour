package com.example.hemanthlam.connectfour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

//The activity used for the 10 x 8 board
public class Game3Activity extends GameActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3);
        box = (RelativeLayout) findViewById(R.id.GAME_1_INNER_RELATIVE);
        gameBoard = new Board("10x8");
        hidePieces();
        for(int i = 0; i < box.getChildCount();++i){
            final int I = i;
            box.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    placeDisc(I);
                }
            });
        }
    }
}
