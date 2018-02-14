package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

import java.util.Dictionary;

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
    protected int p1Wins = 0;
    protected int p2Wins = 0;
    protected int Round = 1;
    protected Bundle activityData;
    protected String player1Name;
    protected String player2Name;
    protected String player1Color;
    protected String player2Color;
    protected ImageView p1HighlightView;
    protected ImageView p2HighlightView;
    protected TextView winnerText;
    GameActivity thisActivity;
    protected Button roundButton;
    protected Button mainMenuButton;
    private boolean isGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call parent class
        super.onCreate(savedInstanceState);

        // Save some of the data
        this.thisActivity = this;
        this.activityData = getIntent().getExtras();
        // Saving Player Names
        this.player1Name = this.activityData.getString("Player1", "Player 1");
        // Temporary. This will need to be changed to something better later
        if (this.activityData.getString("Game").equals("Online Multiplayer"))
            this.player2Name = "Online Player";
        else
            this.player2Name = this.activityData.getString("Player2", "AI");

        // Saving Player Colors
        this.player1Color = this.activityData.getString("Player1Color", "blue").toLowerCase();
        if (this.activityData.getString("Player2Color") == null) {

            // Generate a random color for the AI player
            Random randNumGenerator = new Random();
            int randNum = randNumGenerator.nextInt(4);

            // RandNum
            if (randNum == 0) {
                if (this.player1Color.equalsIgnoreCase("blue"))
                    this.player2Color = "red";
                else
                    this.player2Color = "blue";
            }
            else if (randNum == 1) {
                if (this.player1Color.equalsIgnoreCase("red"))
                    this.player2Color = "green";
                else
                    this.player2Color = "red";
            }
            else if (randNum == 2) {
                if (this.player1Color.equalsIgnoreCase("green"))
                    this.player2Color = "purple";
                else
                    this.player2Color = "green";
            }
            else {
                if (this.player1Color.equalsIgnoreCase("green"))
                    this.player2Color = "blue";
                else
                    this.player2Color = "purple";
            }
        } else
            this.player2Color = this.activityData.getString("Player2Color").toLowerCase();
    }

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

    //Finds if the player who is playing has won. If a player has won, we send a message to the UI
    //and highlight the four winning pieces. If there is no winner, then check for a stalemate. If
    //there isn't a stalemate, then we continue
    public void findWinner(){
        final int[][] a = gameBoard.findWinner(turn);
        final String message;
        if(a!=null) {
            isGameOver = true;
            if(turn==1) {
                message = player1Name + " won";
                ++p1Wins;
                ((TextView) findViewById(R.id.Player1Points)).setText(Integer.toString(p1Wins));
            }
            else {
                message = player2Name + " won";
                ++p2Wins;
                ((TextView) findViewById(R.id.Player2Points)).setText(Integer.toString(p2Wins));
            }
            for (int i = 0; i < box.getChildCount();++i){
                box.getChildAt(i).setClickable(false);
            }
            winnerText.setVisibility(View.VISIBLE);
            winnerText.setText(message);
            mainMenuButton.setVisibility(View.VISIBLE);
            roundButton.setVisibility(View.VISIBLE);
            LinearLayout column;
            ImageView row1, row2, row3, row4;
            column = (LinearLayout) box.getChildAt(a[0][0]);
            row1 = (ImageView) column.getChildAt(a[0][1]);
            row1.setImageResource(R.drawable.white);
            column = (LinearLayout) box.getChildAt(a[1][0]);
            row2 = (ImageView) column.getChildAt(a[1][1]);
            row2.setImageResource(R.drawable.white);
            column = (LinearLayout) box.getChildAt(a[2][0]);
            row3 = (ImageView) column.getChildAt(a[2][1]);
            row3.setImageResource(R.drawable.white);
            column = (LinearLayout) box.getChildAt(a[3][0]);
            row4 = (ImageView) column.getChildAt(a[3][1]);
            row4.setImageResource(R.drawable.white);
        }
        if(gameBoard.checkIfBoardFull()) {
            isGameOver = true;
            for (int i = 0; i < box.getChildCount();++i){
                box.getChildAt(i).setClickable(false);
            }
            winnerText.setText("Stalemate");
            winnerText.setVisibility(View.VISIBLE);
        }
    }

    //Switches turns between players. This means changing the highlights player icon and the turn
    //number/
    protected void changeTurn(){
        // Variables
        //ImageView p1Highlight = (ImageView)findViewById((int)11);
        //ImageView p2Highlight = (ImageView)findViewById((int)12);

        if(turn == 1)
            turn = 2;
        else
            turn = 1;

        if (this.p1HighlightView != null && this.p2HighlightView != null) {
            if (this.turn == 1) {
                this.p1HighlightView.setVisibility(View.VISIBLE);
                this.p2HighlightView.setVisibility(View.INVISIBLE);
                this.drawCircleEdges(this.p1HighlightView, this.player1Color.toLowerCase());
            } else
            //Player 2
            {
                this.p2HighlightView.setVisibility(View.VISIBLE);
                this.p1HighlightView.setVisibility(View.INVISIBLE);
                this.drawCircleEdges(this.p2HighlightView, this.player2Color.toLowerCase());

            }
        }
    }

    // returns the id of the disc image corresponding to the given color
    // INPUT: color (name of color, all lowercase)
    // OUTPUT: id of the color (-1 if color was not found)
    private int colorToDiscImgId(String color) {
        // Id of color
        int colorId;

        if (color != null) {
            // Figuring out what the color id is given the name
            switch (color) {
                case "blue":
                    colorId = R.drawable.blue;
                    break;
                case "red":
                    colorId = R.drawable.red;
                    break;
                case "green":
                    colorId = R.drawable.green;
                    break;
                case "purple":
                    colorId = R.drawable.purple;
                    break;
                default:
                    colorId = -1;
                    break;
            }
        }
        else
            colorId = -1;

        return colorId;
    }

    // Animates a single chip on the board. These are the steps:
    //    1) Check turn
    //    2) Change color of chip based on player turn
    //    3) Place it above board and drop it to it's position
    protected void animate(ImageView chip){
        int player1ColorId;
        int player2ColorId;


        if(turn == 1)
            chip.setImageResource(this.colorToDiscImgId(this.player1Color));
        else chip.setImageResource(this.colorToDiscImgId(this.player2Color));
        chip.setTranslationY(-1000);
        chip.setVisibility(View.VISIBLE);
        chip.animate().translationYBy(1000).setDuration(500);
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
        if (playerPosition < 1 || playerPosition > 2)
            return false;
        if (parent == null )
            return false;

        // Variables

        // Will hold the name of the player and an image of a disc letting them know what color they are
        LinearLayout infoContainer = new LinearLayout(parent.getContext());

        // The view for the player's name
        TextView playerTextView = new TextView(infoContainer.getContext());

        // A relative layout view for the image of the disk and the "highlight" ring that will be turned on when it is their turn (to let them now that its their turn)
        // The "highlight" image is an empty circle with solid edges in a brighter shade of the (ideally) currently selected color that will be toggled visible and invisible (so the player with the highlight disk knows its their turn)
        RelativeLayout imageViews = new RelativeLayout(infoContainer.getContext());

        // The image views discussed above
        ImageView playerImageView = new ImageView(imageViews.getContext());
        ImageView playerHiglightView = new ImageView(imageViews.getContext());

        // Will be needed for drawing in the "highlight" view image
        Bitmap discBitmap;
        Canvas discCanvas;

        // Window width. These will be used for positioning
        int windowWidth;

        // The size of the disc image (letting a player know what color they are)
        int discSize = 80;

        // Get Display Information and save it
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        windowWidth = displayMetrics.widthPixels;

        // Get the main container ready
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(windowWidth/2, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoContainer.setOrientation(LinearLayout.VERTICAL);
        infoContainer.setLayoutParams(linearLayoutParams);
        infoContainer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Get the player-name text view ready
        playerTextView.setText(playerName);
        playerTextView.setTextSize(24);
        playerTextView.setTextColor(Color.WHITE);
        playerTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        playerTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Get the player icon and highlight view ready
        ViewGroup.LayoutParams imageViewsLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, discSize);
        imageViews.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        playerImageView.setLayoutParams(imageViewsLayout);
        playerHiglightView.setLayoutParams(imageViewsLayout);
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
        // We will need to reference these again, so an id will need to be set
        // I went with 10 + 1 (to indicate player 1) or 10 + 2 (to indicate player 2)
        //playerHiglightView.setId((int)(10+playerPosition));

        // Add individual image views to their relative layout container (the highlight view is placed on top fo the image view)
        imageViews.addView(playerImageView);
        imageViews.addView(playerHiglightView);

        // Add the text and image views to the main (player info) container
        infoContainer.addView(playerTextView);
        infoContainer.addView(imageViews);

        // Position the main (player info) container on the screen
        // Will be placed in the middle of the left side of the screen (if playerPosition = 1) or in the middle of the right side otherwise
        // I also save the address of the highlight image veiw because I want to use it later
        if (playerPosition == 1) {
            infoContainer.setX(0);
            infoContainer.setY(25);
            this.p1HighlightView  = playerHiglightView;
        }
        else {
            infoContainer.setX(windowWidth/2);
            infoContainer.setY(25);
            this.p2HighlightView  = playerHiglightView; // To let the user know what the highlight view is
        }

        // Add the player info to the main (relative) layout, which is passed in as a parameter
        parent.addView(infoContainer);

        // Successful execution results in a return value of true
        return true;
    }

    // Draw the highlight on the disc images displaying the color of a player
    // INPUT: imageView (the image view to draw the highlight circle on), color (the color, as a string, of the disc image, which is used to dictate the color of the highlight circle)
    // OUTPUT: none
    protected void drawCircleEdges(ImageView imageView, String color) {
        // Needed to draw the shapes (not sure if the bitmap obejct is required, but I saw it on a tutorial and I know it works, so I am rolling with it for now)
        Bitmap imageViewBitmap;
        Canvas imageViewCanvas;

        // Paint style of player circle edge highlights
        Paint imageViewEdgePaint = new Paint();
        switch (color) {
            case "blue":
                imageViewEdgePaint.setColor(Color.rgb(153, 204, 255));
                break;
            case "red":
                imageViewEdgePaint.setColor(Color.rgb(255, 153, 153));
                break;
            case "green":
                imageViewEdgePaint.setColor(Color.rgb(153, 255, 153));
                break;
            case "purple":
                imageViewEdgePaint.setColor(Color.rgb(204, 153, 255));
                break;
        }
        imageViewEdgePaint.setStyle(Paint.Style.STROKE); // Indicates that only the edge of the shape that is drawn (which will be a circle) is colored
        imageViewEdgePaint.setStrokeWidth(10); // Width of the stroke (the edge of the circle, in our case)

        // Bitmaps
        imageViewBitmap = Bitmap.createBitmap(imageView.getHeight(), imageView.getHeight(), Bitmap.Config.ARGB_8888);

        // Canvas'
        imageViewCanvas = new Canvas(imageViewBitmap);

        // Draw the circle on the bitmap and set the image view to display the bitmap
        imageViewCanvas.drawCircle(imageViewBitmap.getWidth() / 2, imageViewBitmap.getHeight() / 2, (imageView.getHeight() / 2) - 5, imageViewEdgePaint);
        imageView.setImageBitmap(imageViewBitmap);
    }

    //Reset the game board
    protected void restartGame(){
        isGameOver = false;
        for (int i = 0; i < box.getChildCount();++i){
            box.getChildAt(i).setEnabled(true);
            box.getChildAt(i).setClickable(true);
        }
        hidePieces();
        gameBoard.clearBoard();
        roundButton.setVisibility(View.INVISIBLE);
        mainMenuButton.setVisibility(View.INVISIBLE);
        winnerText.setVisibility(View.INVISIBLE);
        ++Round;
        ((TextView) findViewById(R.id.RoundNumber)).setText(Integer.toString(Round));
    }
}
