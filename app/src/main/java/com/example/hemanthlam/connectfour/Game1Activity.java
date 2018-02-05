package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.LayoutDirection;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//The activity used for the 7 x 6 game board
public class Game1Activity extends GameActivity {
    // Player Colors
    String player1Color = "";
    String player2Color = "";
    boolean multiplayer = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);
        box = (RelativeLayout) findViewById(R.id.GAME_1_INNER_RELATIVE);
        gameBoard = new Board("7x6");
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

        // Test
        this.player1Color = "red";
        this.player2Color = "blue";
        this.multiplayer = true;
        generatePlayerNamesAndIcons("Greg", this.player1Color, 2, (RelativeLayout) findViewById(R.id.GAME_1_RELATIVE_LAYOUT));
        generatePlayerNamesAndIcons("Alexandru", this.player2Color, 1, (RelativeLayout) findViewById(R.id.GAME_1_RELATIVE_LAYOUT));
    }

    // Create Player Name
    // Generates the textview and display icon that will display the player name
    // INPUT: playerName (name of the player),
    //        pieceColor (disc color associated with the player),
    //        playerPosition (indicates position of items to be generated)
    //            1 indicates middle of left side of screen (multiplayer)
    //            2 indicates middle of right side of screen (multiplayer)
    //            3 indicates middle of screen (single player)
    // OUTPUT: true if creation was successful, false if not (if any of the input parameters that were required were left empty)
    protected boolean generatePlayerNamesAndIcons(String playerName, String discColor, int playerPosition, RelativeLayout parent) {
        // Initial Checks
        if (playerName == null || playerName.isEmpty())
            return false;
        if (discColor != null && discColor.isEmpty())
            return false;
        if (playerPosition < 1 || playerPosition > 3)
            return false;

        // Variables
        LinearLayout infoContainer = new LinearLayout(parent.getContext());
        TextView playerTextView = new TextView(infoContainer.getContext());
        RelativeLayout imageViews = new RelativeLayout(infoContainer.getContext());
        ImageView playerImageView = new ImageView(imageViews.getContext());
        ImageView playerHiglightView = new ImageView(imageViews.getContext());
        Bitmap discBitmap;
        Canvas discCanvas;
        int windowHeight;
        int windowWidth;
        int discSize = 80;

        // Get Display Information
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        windowHeight = displayMetrics.heightPixels;
        windowWidth = displayMetrics.widthPixels;

        // Get the linear layout ready
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(windowWidth/2, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoContainer.setOrientation(LinearLayout.VERTICAL);
        infoContainer.setLayoutParams(linearLayoutParams);
        infoContainer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Get the player name text view ready
        playerTextView.setText(playerName);
        playerTextView.setTextSize(24);
        playerTextView.setTextColor(Color.WHITE);
        playerTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        playerTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //ViewGroup.LayoutParams layoutParams =

        // Get the player icon view ready
        imageViews.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        playerImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, discSize));
        playerHiglightView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, discSize));
        switch (discColor) {
            case "blue":
                playerImageView.setImageDrawable(getDrawable(R.drawable.blue));
                break;
            case "red":
                playerImageView.setImageDrawable(getDrawable(R.drawable.red));
                break;
            case "green":
                playerImageView.setImageDrawable(getDrawable(R.drawable.green));
                break;
            case "purple":
                playerImageView.setImageDrawable(getDrawable(R.drawable.purple));
                break;
        }
        playerHiglightView.setId((int)(10+playerPosition));
        imageViews.addView(playerImageView);
        imageViews.addView(playerHiglightView);

        // Position the layout itself
        if (playerPosition == 3) {
            infoContainer.setX(windowWidth/4);
            infoContainer.setY(25);
        }
        else if (playerPosition == 1) {
            infoContainer.setX(0);
            infoContainer.setY(25);
        }
        else {
            infoContainer.setX(windowWidth/2);
            infoContainer.setY(25);
        }

        // Add the layers to the page
        infoContainer.addView(playerTextView);
        infoContainer.addView(imageViews);
        parent.addView(infoContainer);

        // Position the icon layout on the page
        //playerImageView.setX((windowWidth/4) - 30);

        // If we got here, all execution should be done successfully
        return true;
    }

    protected void changeTurn() {
        // Variables
        ImageView p1Highlight = (ImageView)findViewById((int)11);
        ImageView p2Highlight = (ImageView)findViewById((int)12);

        // Call parent function
        super.changeTurn();

        // Adjust player turns
        if (this.multiplayer = true)
        {
            if (this.turn == 1)
            {
                p1Highlight.setVisibility(View.VISIBLE);
                p2Highlight.setVisibility(View.INVISIBLE);
                this.drawCircleEdges(p1Highlight, this.player1Color);
            }
            else
            //Player 2
            {
                p2Highlight.setVisibility(View.VISIBLE);
                p1Highlight.setVisibility(View.INVISIBLE);
                this.drawCircleEdges(p2Highlight, this.player2Color);

            }
        }
    }

    protected void drawCircleEdges(ImageView imageView, String color) {
        // Paint style of player circle edge highlights
        Paint imageViewEdgePaint = new Paint();
        switch (color) {
            case "blue":
                imageViewEdgePaint.setColor(Color.rgb(255, 153, 153));
                break;
            case "red":
                imageViewEdgePaint.setColor(Color.rgb(153, 204, 255));
                break;
            case "green":
                imageViewEdgePaint.setColor(Color.rgb(153, 255, 153));
                break;
            case "purple":
                imageViewEdgePaint.setColor(Color.rgb(204, 153, 255));
                break;
        }
        imageViewEdgePaint.setStyle(Paint.Style.STROKE);
        imageViewEdgePaint.setStrokeWidth(10);

        // Bitmaps
        Bitmap imageViewBitmap = Bitmap.createBitmap(imageView.getHeight(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        //Bitmap P2HighlightViewBitmap = Bitmap.createBitmap(P2HighlightImgView.getHeight(), P2HighlightImgView.getHeight(), Bitmap.Config.ARGB_8888);

        // Canvas'
        Canvas imageViewCanvas = new Canvas(imageViewBitmap);
        //Canvas P2HiglightImgViewCanvas = new Canvas(P2HighlightViewBitmap);

        // Draw the circles
        imageViewCanvas.drawCircle(imageViewBitmap.getWidth() / 2, imageViewBitmap.getHeight() / 2, (imageView.getHeight() / 2) - 5, imageViewEdgePaint);
        imageView.setImageBitmap(imageViewBitmap);
    }
}