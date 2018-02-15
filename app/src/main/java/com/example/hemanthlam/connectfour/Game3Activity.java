package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//The activity used for the 10 x 8 board
public class Game3Activity extends GameActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game3);
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
        generatePlayerNamesAndIcons(this.player1Name, this.player1Color, 1, (RelativeLayout) findViewById(R.id.GAME_3_RELATIVE_LAYOUT));
        generatePlayerNamesAndIcons(this.player2Name, this.player2Color, 2, (RelativeLayout) findViewById(R.id.GAME_3_RELATIVE_LAYOUT));

        // Required to draw the edge around player 1's icon when the game starts
        if (this.p1HighlightView != null)
            this.p1HighlightView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) { thisActivity.drawCircleEdges((ImageView)view, thisActivity.player1Color); }
            });

        // Initialize Game End Screen
        thisActivity.initializeGameEndScreen();
    }
}
