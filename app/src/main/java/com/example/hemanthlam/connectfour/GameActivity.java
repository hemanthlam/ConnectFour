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

public class GameActivity extends AppCompatActivity {
    // For Logging Purposes
    private static final String TAG = "GameActivity";

    // Game Activity Data
    protected GameActivity thisActivity = this;
    protected Board gameBoard = null;
    protected RelativeLayout box = null;
    protected Bundle activityData;

    // Player Data
    protected int p1Wins = 0;
    protected int p2Wins = 0;
    protected String p1Name = null;
    protected String p2Name = null;
    protected String p1Color = null;
    protected String p2Color = null;
    protected ImageView p1HighlightView = null;
    protected ImageView p2HighlightView = null;
    protected TextView p1ScoreView = null;
    protected TextView p2ScoreView = null;

    // Game specific data
    protected int turn = 1;
    protected int Round = 1;
    protected String gameType = null;
    private boolean isGameOver = false;
    private int lastFirstTurn = 1;
    protected String gameMode = null;
    protected String board = null;
    protected int boardWidth = 7;
    protected int boardHeight = 6;

    // Online mode connectivity variables
    protected MultiplayerSession multiplayerSession = null;
    protected boolean onlineMode = false;
    protected boolean onlineModeIsServer = false;

    // UI Elements (for use with UI generation when in online mode)
    protected RelativeLayout mainLayout = null; // Reference to the android activity used in the current game session
    protected TextView winnerText = null;
    protected RelativeLayout gameEndMenu = null;
    protected Button roundButton = null;
    protected Button mainMenuButton = null;

    // For network thread/UI purposes (this is again, an online mode thing)
    protected final Object playerDiscLock = new Object();
    protected boolean playerDisc1Loaded = false;
    protected boolean playerDisc2Loaded = false;

    // For use with the network thread function (a function that will be called by a different thread
    protected final Object networkThreadLock = new Object();
    boolean continueNetworkThreadExecution = false;
    boolean placementLockActive = false;
    boolean isSendPhase = false;
    boolean isOnlineModeLoaded = false;
    boolean initiatedOnlineGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call parent class
        super.onCreate(savedInstanceState);

        // Game information from other client (if in online mode)
        String clientInformation = null;

        // Address of device to connect to if in online mode
        String address = null;

        // Used for board size processing
        int splitIndex = -1;

        // Data passed in from the previous activity
        activityData = getIntent().getExtras();

        // Getting the game mode
        this.gameMode = activityData.getString("Game");
        this.board = activityData.getString("Board").replaceAll("\\s", ""); // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java

        // Saving Player Names
        this.p1Name = this.activityData.getString("Player1", "Player 1");
        if (this.gameMode.equals("Online Multiplayer"))
            this.p2Name = "Online Player";
        else if (this.gameMode.equals("AI Mode (Single Player)")) {
            this.gameType = "AI Mode (Single Player)";
            this.p2Name = "AI";
        }
        else {
            this.gameType = "Local Multiplayer";
            this.p2Name = this.activityData.getString("Player2", "AI");
        }

        // Saving online mode data
        if (this.gameMode.equals("Online Multiplayer")) {
            this.onlineMode = true;
            this.onlineModeIsServer = activityData.getBoolean("OnlineModeIsServer");
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
            this.p2Color = activityData.getString("Player2Color").toLowerCase();



        // Attempt to load online mode
        if (onlineMode) {
            // Get address of device to connect to (a web socket is created)
            address = activityData.getString("OnlineModeGroupHostAddress");

            // Keeps track of whether the current device (the one running this code) initiated the online game. If so, it gets to decide the board size of the game.
            initiatedOnlineGame = activityData.getBoolean("OnlineModeInitiatedGame");

            // Create a new multiplayer session variables
            this.multiplayerSession = new MultiplayerSession();

            // Attempt to initialize a multiplayer session. If it doesn't, return to the main menu
            // Removing all whitespace from a string: // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
            if ((clientInformation = multiplayerSession.initiateConnectionWithConnectedDevice(address, onlineModeIsServer, activityData.getString("Board").replaceAll("\\s", "") + "-" + p1Name, initiatedOnlineGame)) == null) {
                Toast.makeText(getApplicationContext(), "Failed to initiate connection with client device. Returning to main menu", Toast.LENGTH_LONG).show();
                returnToMain();
            }
            else {
                // If the multiplayer session was created successfully, and the device running this instance of the connect four app is not the group host
                // set the turn to 2 (turn #2 is reserved for the online player, while turn #1 corresponds to the local player)
                // Setting the turn to 2 indicates we want to wait for input from the online player
                if (!onlineModeIsServer) {
                    // Initial Game Information
                    turn = 2;
                    isSendPhase = false;
                    placementLockActive = true;
                } else {
                    turn = 1;
                    isSendPhase = true;
                    placementLockActive = false;
                }

                // Game start information
                splitIndex = clientInformation.indexOf('-');
                p2Name = clientInformation.substring(splitIndex+1);

                if (initiatedOnlineGame) {
                    // Get board information from other device
                    board = clientInformation.substring(0, splitIndex);
                }
            }

            // Start the network thread (which will handle communications with the other device)
            // It seemed wise to have this running in a different thread so that it didn't interfere with the UI thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    networkThread();
                }
            }).start();
        }
        // If we are in offline mode, we don't need a multiplayer session
        else {
            this.multiplayerSession = null;
        }

        // Parse Board Information
        splitIndex = board.indexOf('x');
        boardWidth = Integer.parseInt(board.substring(0, splitIndex));
        boardHeight = Integer.parseInt(board.substring(splitIndex+1));
    }

    // Draws initial score circle highlights, depending on the game mode (a user interface generation function)
    // INPUT: none
    // OUTPUT: none
    protected void drawInitialHighlights() {
        if (this.p1HighlightView != null && this.p2HighlightView != null) {
            this.p1HighlightView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    if (!onlineMode || onlineModeIsServer) {
                        drawCircleEdges((ImageView) view, p1Color);

                        // Will be used to help us guage when we can start online mode
                        // This is a bit of an estimation
                        synchronized (playerDiscLock) {
                            playerDisc1Loaded = true;
                            if (playerDisc2Loaded) {
                                playerDiscLock.notify();
                                System.out.println("playerDiscLock notified");
                            }
                        }
                    }
                }
            });
            p2HighlightView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    if (onlineMode && !onlineModeIsServer) {
                        drawCircleEdges((ImageView) view, p2Color);

                        // Will be used to help us guage when we can start online mode
                        // This is a bit of an estimation
                        synchronized (playerDiscLock) {
                            playerDisc2Loaded = true;
                            if (playerDisc1Loaded) {
                                playerDiscLock.notify();
                                System.out.println("playerDiscLock notified");
                            }

                        }
                    }
                }
            });
        }
    }

    // Returns to the main menu (loads that activity)
    // INPUT: none
    // OUTPUT: none
    protected void returnToMain() {
        if (multiplayerSession != null) {
            // End session with connected player
            multiplayerSession.endConnectionWithConnectedDevice();

            // Notify the network (remote disc placement) thread that we need to stop executing
            synchronized (networkThreadLock) {
                if (continueNetworkThreadExecution)
                {
                    continueNetworkThreadExecution = false;
                    networkThreadLock.notify();
                }
            }
            Toast.makeText(getApplicationContext(), "I hope you enjoyed your online game!", Toast.LENGTH_LONG);
        }
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }

    // Executes on activity start
    // https://stackoverflow.com/questions/18703841/call-method-on-activity-load-android
    // INPUT: none
    // OUTPUT: none
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Hide all UI pieces from board
    // INPUT: none
    // OUTPUT: none
    protected void hidePieces() {
        for(int i = 0; i < this.box.getChildCount();++i){
            LinearLayout curCol = (LinearLayout) this.box.getChildAt(i);
            for(int j = 0; j < curCol.getChildCount();++j){
                ImageView view = (ImageView)curCol.getChildAt(j);
                view.setImageDrawable(getResources().getDrawable(R.drawable.white));
            }
        }
    }

    // Place a disc in the correct slot on the board
    // if isGameOver dont place discs
    // INPUT: col (column from which to place disc)
    // OUTPUT: none
    // Evidently you can label a parameter variable as final
    protected void placeDisc(final int col) {
        System.out.println("Disc Just Placed...");
        if(!isGameOver) {
            int row = gameBoard.findPosition(col, turn);
            if (row == -1)
                return;
            LinearLayout tempCol = (LinearLayout) box.getChildAt(col);
            ImageView chip = (ImageView) tempCol.getChildAt(row);
            animate(chip);

            // Send turn to other player (if in multi-player mode)
            if (onlineMode && isSendPhase) {
                // If our multiplayer session wasn't set up successfully
                if (this.multiplayerSession == null) {
                    Toast.makeText(getApplicationContext(), "The multiplayer session didn't set up successfully, so we couldn't send move to online player... exiting back to main", Toast.LENGTH_LONG).show();
                    //try {Thread.sleep(2000);} catch (InterruptedException ex) {}
                    returnToMain();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // If things didn't work...
                            if (!multiplayerSession.sendMoveToOtherPlayer(col)) {
                                Toast.makeText(getApplicationContext(), "Couldn't send move to online player... exiting back to main", Toast.LENGTH_LONG).show();
                                //try {Thread.sleep(2000);} catch (InterruptedException ex) {}
                                returnToMain();
                            }
                        }
                    }).start();
                }
                //System.out.println("Sent move to other player");
            }

            // Log our progress
            Log.d(TAG,"Disc placed at col" + col);

            // Change turns (and handle AI moves)
            if(gameMode.equals("AI Mode (Single Player)") && !isGameOver){
                placeAIDisc();
            }
            else if (!gameMode.equals("AI Mode (Single Player)"))
                changeTurn();
        }
    }

    // Network thread function (waits for remote player input and attempt to change turns so thread so it doesn't interfere with animations)
    // This function presumes online mode has been setup (otherwise it won't be called)
    // INPUT: none
    // OUTPUT: noen
    protected void networkThread() {
        // Wait for setup
        synchronized (playerDiscLock)
        {
            try {
                if (!playerDisc1Loaded || !playerDisc2Loaded) {
                    // Waits for the signal from the main thread indicating that some UI setup has completed
                    // Stop waiting after 8 seconds (UI setup shouldn't take that long) in case things don't work out
                    playerDiscLock.wait(6000);

                    // Might consider failing at this part of the code
                    if (!playerDisc1Loaded || !playerDisc2Loaded)
                        Log.d(TAG, "The Network Thread (which takes care of online mode interactions) ran into problems when executing the playerDiscLock.wait() operation (the playerDiscLock is supposed to be triggered when certain UI elements are finished loading). The operation seems to have timed out. Execution will still continue though....");
                }
            } catch (InterruptedException exception) {
                Log.d(TAG, "The Network Thread (which takes care of online mode interactions) ran into problems when waiting on the palyerDiscLock (which is supposed to be triggered when certain UI elements are loaded). Error code: " + exception.getMessage());
            }
        }

        // Will be used to help us determine if we need to continue thread execution
        boolean continueExecuting = true;

        // Indicate to the main thread that the network thread has been setup
        isOnlineModeLoaded = true;

        // For checking first turn (if this device is a connected client, it has to wait on a connection from the server before it can begin)
        if (multiplayerSession != null && !isSendPhase) {
            // Get move from other player
            final int onlinePlayerMove = multiplayerSession.getMoveFromOtherPlayer();

            // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onlinePlayerMove != -1)
                        placeDisc(onlinePlayerMove);
                }
            });

            System.out.println("Signal Just Received");
        }

        // Start waiting for turns
        while (continueExecuting) {
            synchronized (networkThreadLock) {
                try {
                    // Wait on signal (for 40 seconds max)
                    networkThreadLock.wait(40000);

                    // In the case that the connection timed out and we haven't recieved a response from the online player
                    /*if (isSendPhase)
                    {
                        continueNetworkThreadExecution = false;
                        continueExecuting = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Response from client took to long. Exiting...", Toast.LENGTH_SHORT);
                                returnToMain();
                            }
                        });
                    }*/
                } catch (InterruptedException exception) {
                    Log.d(TAG, "The network thread ran into problems waiting on a signal from the main thread (to either receive a move from the connected device or indicate that is has sent off a move to the other player)");
                }
            }

            // If execution is to stop (which may be the case), the loop needs to stop executing
            if (!continueNetworkThreadExecution) {
                continueExecuting = false;
                continue;
            }

            // https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
            final int onlinePlayerMove = multiplayerSession.getMoveFromOtherPlayer();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onlinePlayerMove != -1)
                        placeDisc(onlinePlayerMove);
                }
            });
        }
    }

    //Have the AI place a disc
    protected void placeAIDisc() {
        LinearLayout tempCol;
        ImageView chip;
        changeTurn();
        if (!isGameOver) {
            int AIPos[] = gameBoard.AIPlaceDisc(2);
            tempCol = (LinearLayout) box.getChildAt(AIPos[0]);
            chip = (ImageView) tempCol.getChildAt(AIPos[1]);
            animate(chip);
            //findWinner();
        }
        changeTurn();
    }

    //Finds if the player who is playing has won. If a player has won, we send a message to the UI
    //and highlight the four winning pieces. If there is no winner, then check for a stalemate. If
    //there isn't a stalemate, then we continue
    // INPUT: none
    // OUTPUT: none
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
            setGameEndWindowVisibility(true);

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
            setGameEndWindowVisibility(true);
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
                    gameEndMenu.setAlpha(0.0f);
                    gameEndMenu.setVisibility(View.VISIBLE);
                    gameEndMenu.animate().alpha(1.0f).setDuration(2000);
                }
            }, 2000);
        }
        else {
            gameEndMenu.setAlpha(0.0f);
            gameEndMenu.setVisibility(View.INVISIBLE);
        }
    }

    // Notifies the network thread to wait on a new move from an online player
    // (designed for use with online mode only)
    // INPUT: none
    // OUTPUT: none
    protected void notifyNetworkThreadOfMove() {
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
                // Notify network thread
                // Check if online mode was created successfully
                if (multiplayerSession == null) {
                    Log.d(TAG, "Error: Can't get move from online player. The multiplayer session doesn't seem to be active. Also, we shouldn't have run into this error (the program should have returned to the main screen by now)");
                    Toast.makeText(getApplicationContext(), "There was an error trying to receive a move from the connected device. The multiplayer session didn't seem to be setup correctly. ", Toast.LENGTH_LONG);
                    returnToMain();
                }
                // Indicate that we want to wait for a turn from the remote player
                else if (!isSendPhase) {
                    synchronized (networkThreadLock) {
                        // Indicate to the network thread that we want to continue execution (this is not necessary, but it is nice just to be safe)
                        continueNetworkThreadExecution = true;

                        // Debug
                        Log.d(TAG,"Info: Notifying the network thread of move");

                        // Notify the network thread (which should be waiting on this signal)
                        networkThreadLock.notify();
                    }
                }
        //    }
        //}).start();
    }

    // Switches turns between players. This means changing the highlights player icon and the turn
    // number
    // INPUT: none
    // OUTPUT: none
    protected void changeTurn() {
        // Check for winner (if not in AI mode, since it does this already
        findWinner();

        // Switch turn
        if (turn == 1)
            turn = 2;
        else
            turn = 1;

        // Take the appropriate action (i.e update the highlight views)
        if (p1HighlightView != null && p2HighlightView != null) {
            // Player 1
            if (turn == 1) {
                p1HighlightView.setVisibility(View.VISIBLE);
                p2HighlightView.setVisibility(View.INVISIBLE);
                drawCircleEdges(this.p1HighlightView, this.p1Color.toLowerCase());
            } else
            // Player 2
            {
                p2HighlightView.setVisibility(View.VISIBLE);
                p1HighlightView.setVisibility(View.INVISIBLE);
                drawCircleEdges(this.p2HighlightView, this.p2Color.toLowerCase());
            }
        }

        // Update the boolean that indicates whether it is the send or receive phase
        if (onlineMode) {
            if (isSendPhase) {
                isSendPhase = false;
                placementLockActive = true;
            } else {
                isSendPhase = true;
                placementLockActive = false;
            }

            // Only start looking for a new move
            if (!isGameOver) {
                notifyNetworkThreadOfMove();
            }
        }

        // Log
        Log.d(TAG, "Info: Change Turn Completed");
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
    // INPUT: chip (a reference to the chip to animate)
    protected void animate(ImageView chip){
        if (chip != null) {
            if (turn == 1)
                chip.setImageResource(colorToDiscImgId(p1Color));
            else chip.setImageResource(colorToDiscImgId(p2Color));
            chip.setTranslationY(-1000);
            chip.setVisibility(View.VISIBLE);
            chip.animate().translationYBy(1000).setDuration(350);
        }
    }

    // Create Player Name and Color Image views
    // (Generates the text view and display icon that will display the player name)
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
        Bitmap discBitmap = null;
        Canvas discCanvas = null;

        // Window width. These will be used for positioning
        int windowWidth = 0;

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
            p1HighlightView  = playerHiglightView;
            p1ScoreView = playerScoreView;
        }
        else {
            infoContainer.setX(windowWidth/2);
            infoContainer.setY(25);
            p2HighlightView  = playerHiglightView; // To let the user know what the highlight view is
            p2ScoreView = playerScoreView;
        }

        // Add the player info to the main (relative) layout, which is passed in as a parameter
        parent.addView(infoContainer);

        // Successful execution results in a return value of true
        return true;
    }

    // Draw the highlight on the disc images displaying the color of a player
    // INPUT:
    //     -imageView (the image view to draw the highlight circle on),
    //     -color (the color, as a string, of the disc image, which is used to dictate the color of the highlight circle)
    // OUTPUT: none
    protected void drawCircleEdges(ImageView imageView, String color) {
        // Initial Checks (exit if either our color or image view is null)
        if (imageView == null || color == null)
        {
            Log.d(TAG, "DrawCircleEdges failed (null disc image view or color). Image View is non-null: " + (imageView != null) + " Color is non-null: " + (color != null));
            return;
        }

        // Needed to draw the shapes (not sure if the bitmap obejct is required, but I saw it on a tutorial and I know it works, so I am rolling with it for now)
        Bitmap imageViewBitmap = null;
        Canvas imageViewCanvas = null;

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
        gameEndMenu = container;
        winnerText = gameWinner;
        roundButton = replayButton;
        mainMenuButton = menuButton;
        setGameEndWindowVisibility(false);

        // Set game end button listeners
        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGame();
            }
        });
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToMain();
            }
        });
    }

    // Reset the game board
    // INPUT: none
    // OUTPUT: none
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

        // Only start looking for a new move
        if (!isGameOver && onlineMode && multiplayerSession != null) {
            notifyNetworkThreadOfMove();
        }
    }
}
