package com.example.hemanthlam.connectfour;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//The activity used for the 8 x 7 board
public class Game2Activity extends GameActivity {
    String TAG = "Game2Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);
        Log.d(TAG,"Board 8*7");
        box = (RelativeLayout) findViewById(R.id.GAME_2_INNER_RELATIVE);
        gameBoard = new Board("8x7");
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

        // Generate player disk icons
        generatePlayerNamesAndIcons(this.p1Name, this.p1Color, 1, (RelativeLayout) findViewById(R.id.GAME_2_RELATIVE_LAYOUT));
        generatePlayerNamesAndIcons(this.p2Name, this.p2Color, 2, (RelativeLayout) findViewById(R.id.GAME_2_RELATIVE_LAYOUT));

        // Required to draw the edge around player 1's icon when the game starts
        if (this.p1HighlightView != null)
            this.p1HighlightView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) { thisActivity.drawCircleEdges((ImageView)view, thisActivity.p1Color); }
            });

        // Initialize Game End Screen
        thisActivity.initializeGameEndScreen();
    }

}
