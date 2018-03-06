package com.example.hemanthlam.connectfour;

import android.media.Image;
import android.os.Handler;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;
import android.widget.Toast;
import com.example.hemanthlam.connectfour.db.AppDatabase;
import com.example.hemanthlam.connectfour.db.Player;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.exit;
 /**
 * Created by Sean on 1/25/2018.
 */

//Where all of the important functions for the game board will be placed this will allow Game1Activity,
//Game2Activity, and Game3Activity to share most of the same functions and variables.
public class GameActivity extends AppCompatActivity {
    protected Board gameBoard;
    protected RelativeLayout box;
    protected int turn = 1;
    protected String gameType;
    protected int p1Wins = 0;
    protected int p2Wins = 0;
    protected int Round = 1;
    protected Bundle activityData;
    protected String p1Name;
    protected String p2Name;
    protected String p1Color;
    protected String p2Color;
    protected ImageView p1HighlightView;
    protected ImageView p2HighlightView;
    protected TextView p1ScoreView;
    protected TextView p2ScoreView;
    protected GameActivity thisActivity;
    protected TextView winnerText;
    protected RelativeLayout gameEndMenu;
    protected Button roundButton;
    protected Button mainMenuButton;
    private boolean isGameOver;
    private static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call parent class
        super.onCreate(savedInstanceState);

        // Save some of the data
        this.thisActivity = this;
        this.activityData = getIntent().getExtras();
        // Saving Player Names
        this.p1Name = this.activityData.getString("Player1", "Player 1");
        // Temporary. This will need to be changed to something better later
        if (this.activityData.getString("Game").equals("Online Multiplayer"))
            this.p2Name = "Online Player";
        else if (this.activityData.getString("Game").equals("AI Mode (Single Player)")) {
            this.gameType = "AI Mode (Single Player)";
            this.p2Name = "AI";
        }
        else {
            this.gameType = "Local Multiplayer";
            this.p2Name = this.activityData.getString("Player2", "AI");
        }
        // Saving Player Colors
        this.p1Color = this.activityData.getString("Player1Color", "blue").toLowerCase();
        if (this.activityData.getString("Player2Color") == null) {

            // Generate a random color for the AI player
            Random randNumGenerator = new Random();
            int randNum = randNumGenerator.nextInt(4);

            // RandNum
            if (randNum == 0) {
                if (this.p1Color.equalsIgnoreCase("blue"))
                    this.p2Color = "red";
                else
                    this.p2Color = "blue";
            }
            else if (randNum == 1) {
                if (this.p1Color.equalsIgnoreCase("red"))
                    this.p2Color = "green";
                else
                    this.p2Color = "red";
            }
            else if (randNum == 2) {
                if (this.p1Color.equalsIgnoreCase("green"))
                    this.p2Color = "purple";
                else
                    this.p2Color = "green";
            }
            else {
                if (this.p1Color.equalsIgnoreCase("green"))
                    this.p2Color = "blue";
                else
                    this.p2Color = "purple";
            }
        } else
            this.p2Color = this.activityData.getString("Player2Color").toLowerCase();
    }

    //Hide all UI pieces from board
    protected void hidePieces(){
        for(int i = 0; i < box.getChildCount();++i){
            LinearLayout curCol = (LinearLayout) box.getChildAt(i);
            for(int j = 0; j < curCol.getChildCount();++j){
                ImageView view = (ImageView)curCol.getChildAt(j);
                view.setImageDrawable(getResources().getDrawable(R.drawable.white));
            }
        }
    }

    //Place a disc in the correct slot on the board
    //if isGameOver dont place discs
    protected void placeDisc(int col) {
        if(!isGameOver) {
            int row = gameBoard.findPosition(col, turn);
            if (row == -1)
                return;
            LinearLayout tempCol = (LinearLayout) box.getChildAt(col);
            ImageView chip = (ImageView) tempCol.getChildAt(row);
            animate(chip);
            findWinner();
            Log.d(TAG,"Disc placed at col" + col);
            if(gameType.equals("AI Mode (Single Player)")&& !isGameOver){
                placeAIDisc();
            }
            else if (!gameType.equals("AI Mode (Single Player)"))
                changeTurn();
        }
    }

    //Have the AI place a disc
    protected void placeAIDisc(){
        LinearLayout tempCol;
        ImageView chip;
        changeTurn();
        int AIPos [] = gameBoard.AIPlaceDisc(0,2);
        tempCol = (LinearLayout) box.getChildAt(AIPos[0]);
        chip = (ImageView) tempCol.getChildAt(AIPos[1]);
        animate(chip);
        findWinner();
        changeTurn();
    }
    //Finds if the player who is playing has won. If a player has won, we send a message to the UI
    //and highlight the four winning pieces. If there is no winner, then check for a stalemate. If
    //there isn't a stalemate, then we continue
    public void findWinner() {
        List<Player> list = new ArrayList<>();
        AppDatabase appDatabase = AppDatabase.getAppDatabase(this);
        int topScore=0;
        if(appDatabase.userDao().getTop5Scores().size()>0)
            topScore = appDatabase.userDao().getTop5Scores().get(0).getScore();
        Player player;
        String color;
        final int[][] a = gameBoard.findWinner(turn);
        final String message;
        //Check if there is a winner
        if(a!=null) {
            isGameOver = true;
            if(turn==1) {
                message = p1Name + " won";
                ++p1Wins;
                color = p1Color;
                p1ScoreView.setText(Integer.toString(p1Wins));
                player  = appDatabase.userDao().getPlayer(p1Name);
                if(p1Wins > topScore){
                    Toast.makeText(getApplicationContext(),"You have reached a new high score", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"New High Score Reached");
                }
                if(player==null) {
                    player = new Player();
                    player.setScore(p1Wins);
                    player.setName(p1Name);
                    appDatabase.userDao().insertAll(player);
                }
                else{
                    if(player.getScore() < p1Wins){
                        player.setScore(p1Wins);
                        appDatabase.userDao().update(player);
                    }
                }

                list.add(player);
                Log.d(TAG,"Player 1 won");
            }
            else {
                message = p2Name + " won";
                ++p2Wins;
                color = p2Color;
                p2ScoreView.setText(Integer.toString(p2Wins));
                player  = appDatabase.userDao().getPlayer(p2Name);
                if(p2Wins > topScore){
                    Toast.makeText(getApplicationContext(),"You have reached a new high score", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"New High Score Reached");
                };
                if(player==null) {
                    player = new Player();
                    player.setScore(p2Wins);
                    player.setName(p2Name);
                    appDatabase.userDao().insertAll(player);
                }
                else{
                    if(player.getScore() < p2Wins){
                        player.setScore(p2Wins);
                        appDatabase.userDao().update(player);
                    }
                }
                player.setName(p2Name);
                player.setScore(p2Wins);
                list.add(player);
                Log.d(TAG,"Player 2 won");
            }
            for (int i = 0; i < box.getChildCount();++i){
                box.getChildAt(i).setClickable(false);
            }
            winnerText.setText(message);
            this.setGameEndWindowVisibility(true);

            LinearLayout column;
            ImageView row;
            for(int i = 0; i < 4; ++i) {
                column = (LinearLayout) box.getChildAt(a[i][0]);
                row = (ImageView) column.getChildAt(a[i][1]);
                thisActivity.drawCircleEdges(row, color);
            }
        }
        //Check if there is a stalemate
        if(gameBoard.checkIfBoardFull()) {
            isGameOver = true;
            for (int i = 0; i < box.getChildCount();++i){
                box.getChildAt(i).setClickable(false);
            }
            winnerText.setText("Stalemate");
            this.setGameEndWindowVisibility(true);
            Log.d(TAG,"Stalemate");
            winnerText.setVisibility(View.VISIBLE);
        }
    }

    // Sets the visiblity of the gamewindow. Animations and delays are used, and I didn't want this written more than once, so I put it here
    // INPUT: visible (a boolean that, if set to true, indicates that the game end menu is to be visible. If false, indicates that it is to be hidden)
    // OUTPUT: none
    public void setGameEndWindowVisibility(boolean visible) {
        if (visible) {
            // https://stackoverflow.com/questions/4817933/what-is-the-equivalent-to-a-javascript-setinterval-settimeout-in-android-java
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // https://stackoverflow.com/questions/22454839/android-adding-simple-animations-while-setvisibilityview-gone
                    thisActivity.gameEndMenu.setAlpha(0.0f);
                    thisActivity.gameEndMenu.setVisibility(View.VISIBLE);
                    thisActivity.gameEndMenu.animate().alpha(1.0f).setDuration(2000);
                }
            }, 2000);
        }
        else {
            thisActivity.gameEndMenu.setAlpha(0.0f);
            thisActivity.gameEndMenu.setVisibility(View.INVISIBLE);
        }
    }

    //Switches turns between players. This means changing the highlights player icon and the turn
    //number/
    protected void changeTurn(){
        if(turn == 1)
            turn = 2;

        else
            turn = 1;

        if (this.p1HighlightView != null && this.p2HighlightView != null) {
            // Player 1
            if (this.turn == 1) {
                this.p1HighlightView.setVisibility(View.VISIBLE);
                this.p2HighlightView.setVisibility(View.INVISIBLE);
                this.drawCircleEdges(this.p1HighlightView, this.p1Color.toLowerCase());
            } else
            // Player 2
            {
                this.p2HighlightView.setVisibility(View.VISIBLE);
                this.p1HighlightView.setVisibility(View.INVISIBLE);
                this.drawCircleEdges(this.p2HighlightView, this.p2Color.toLowerCase());
            }
        }

        // Online mode (this is where the turn handling will be done)
        //if (this.gameMode.equals("Online Multiplayer"))
        //{

        //}
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
        if(turn == 1)
            chip.setImageResource(this.colorToDiscImgId(this.p1Color));
        else chip.setImageResource(this.colorToDiscImgId(this.p2Color));
        chip.setTranslationY(-1000);
        chip.setVisibility(View.VISIBLE);
        chip.animate().translationYBy(1000).setDuration(350);
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
    protected boolean generatePlayerNamesAndIcons(String playerName, String discColor, int playerPosition, RelativeLayout parent,int discSize) {
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
        TextView playerScoreView = new TextView(imageViews.getContext());

        // Will be needed for drawing in the "highlight" view image
        Bitmap discBitmap;
        Canvas discCanvas;

        // Window width. These will be used for positioning
        int windowWidth;

        // The size of the disc image (letting a player know what color they are)
       // int discSize = 80;

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

        // Setup the score views
        playerScoreView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, discSize));
        playerScoreView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        playerScoreView.setText("0");
        playerScoreView.setTextSize(24);
        // https://stackoverflow.com/questions/14108400/how-to-align-text-vertically-center-in-android
        playerScoreView.setGravity(Gravity.CENTER_VERTICAL);

        // Set disc colors
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

        // Add individual image views to their relative layout container (the highlight view is placed on top fo the image view)
        imageViews.addView(playerImageView);
        imageViews.addView(playerHiglightView);
        imageViews.addView(playerScoreView);

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
            this.p1ScoreView = playerScoreView;
        }
        else {
            infoContainer.setX(windowWidth/2);
            infoContainer.setY(25);
            this.p2HighlightView  = playerHiglightView; // To let the user know what the highlight view is
            this.p2ScoreView = playerScoreView;
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

    // Draw the game-end menu (pops up whenever a game ends and gives the players the ability to exit to the main menu or keep playing)
    // It remains hidden by default (it is presumed to be drawn at the beginning of the match and hidden later)
    // INPUT: none
    // OUTPUT: none
    protected void initializeGameEndScreen() {
        // Create the objects needed for the window
        RelativeLayout container = new RelativeLayout(getWindow().getContext());
        LinearLayout linearLayout = new LinearLayout(container.getContext());
        TextView gameWinner = new TextView(linearLayout.getContext());
        Button menuButton = new Button(container.getContext());
        Button replayButton = new Button(container.getContext());

        // Get Display Information and save it
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int windowWidth = displayMetrics.widthPixels;
        int windowHeight = displayMetrics.heightPixels;
        int elementHeight = windowHeight / 8;

        // Style the relative layout
        container.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Style the linear layout on the screen
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((windowWidth/4)*3, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setX(windowWidth/8);
        linearLayout.setY(displayMetrics.heightPixels / 4);
        linearLayout.setDividerPadding(10);

        // Add some detail to the text view
        gameWinner.setText("Game Winner");
        gameWinner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        gameWinner.setTextSize(24);
        gameWinner.setWidth(linearLayout.getWidth());
        gameWinner.setMinHeight(elementHeight);

        // Some detail for the buttons
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, elementHeight);
        buttonLayoutParams.setMargins(0, 10, 0, 10);

        // https://android--code.blogspot.com/2015/05/android-textview-layout-margin.html
        // http://smartandroidians.blogspot.com/2009/12/setting-margin-for-widgets.html
        // https://stackoverflow.com/questions/16552811/set-a-margin-between-two-buttons-programmatically-from-a-linearlayout

        menuButton.setText("Main Menu");
        menuButton.setLayoutParams(buttonLayoutParams);
        menuButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        replayButton.setText("Next Round");
        replayButton.setLayoutParams(buttonLayoutParams);
        replayButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        // Add the text view and buttons to the linear layout
        linearLayout.addView(gameWinner);
        linearLayout.addView(menuButton);
        linearLayout.addView(replayButton);

        // Add linear layout to relative layout
        container.addView(linearLayout);

        // Add linear layout to screen
        getWindow().addContentView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Setting some variables so we can edit these values
        this.gameEndMenu = container;
        this.winnerText = gameWinner;
        this.roundButton = replayButton;
        this.mainMenuButton = menuButton;
        this.setGameEndWindowVisibility(false);

        // Set game end button listeners
        this.roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisActivity.restartGame();
            }
        });
        this.mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //Reset the game board
    protected void restartGame(){
        Log.d(TAG, "Restarting the game");
        isGameOver = false;
        for (int i = 0; i < box.getChildCount();++i){
            box.getChildAt(i).setEnabled(true);
            box.getChildAt(i).setClickable(true);
        }
        hidePieces();
        gameBoard.clearBoard();
        gameEndMenu.setVisibility(View.INVISIBLE);
        ++Round;
        ((TextView) findViewById(R.id.RoundNumber)).setText(Integer.toString(Round));
    }
}
