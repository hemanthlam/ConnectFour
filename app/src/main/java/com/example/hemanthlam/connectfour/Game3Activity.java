package com.example.hemanthlam.connectfour;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//The activity used for the 10 x 8 board
public class Game3Activity extends GameActivity {
    String TAG = "Game3Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (RelativeLayout) findViewById(R.id.GAME_3_RELATIVE_LAYOUT);
        setContentView(R.layout.activity_game3);
        Log.d(TAG,"Board 10*8");
        box = (RelativeLayout) findViewById(R.id.GAME_3_INNER_RELATIVE);
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

        // Generate player disk icons
        generatePlayerNamesAndIcons(this.p1Name, this.p1Color, 1, (RelativeLayout) findViewById(R.id.GAME_3_RELATIVE_LAYOUT));
        generatePlayerNamesAndIcons(this.p2Name, this.p2Color, 2, (RelativeLayout) findViewById(R.id.GAME_3_RELATIVE_LAYOUT));
        drawInitialHighlights();

        // Required to draw the edge around player 1's icon when the game starts
        if (this.p1HighlightView != null)
            this.p1HighlightView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) { thisActivity.drawCircleEdges((ImageView)view, thisActivity.p1Color); }
            });

        // Initialize Game End Screen
        thisActivity.initializeGameEndScreen();

        // Potential problem: what if it isn't set...
        // The group host goes first, so if this isn't the group host, the turn needs to be changed
        if (!(activityData.getBoolean("OnlineModeIsGroupHost")))
            System.out.println("This device is the online group host!");
        else{
            System.out.println("This device is not the online group host!");
            changeTurn();
        }
    }
}
