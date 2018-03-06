package com.example.hemanthlam.connectfour;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//The activity used for the 7 x 6 game board
public class Game1Activity extends GameActivity {

    // Tag for logging purposes
    String TAG = "Game1Activity";

    // onCreate function
    // Executes when the activity is created (but before its UI elements are loaded)
    // Info can be found here: https://developer.android.com/reference/android/app/Activity.html (though my understanding is also built on information from the various sources cited in the code and from some stackoverflow posts that I might not have mentioned here)
    // INPUT: savedInstanceState (not sure_
    // OUTPUT: none
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (RelativeLayout) findViewById(R.id.GAME_2_RELATIVE_LAYOUT);
        setContentView(R.layout.activity_game1);
        Log.d(TAG,"Board 7*6");
        box = (RelativeLayout) findViewById(R.id.GAME_1_INNER_RELATIVE);
        gameBoard = new Board("7x6");
        hidePieces();
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
                // Load the game discs (they are hidden initially, and colored, moved up, and animated back down as they are placed by the user, if my understanding is correct)
                for(int i = 0; i < box.getChildCount();++i){
                    final int I = i;
                    box.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            synchronized (networkThreadLock) {
                                // If we are waiting on a move form an online player, we don't want to let the local player place any more discs
                                if (!placementLockActive)
                                    placeDisc(I);
                            }
                        }
                    });
                }
                /*synchronized (interfaceLoadLock) {
                    interfaceElementsLoaded += 1;
                    if (interfaceElementsLoaded >= 3)
                        interfaceLoadLock.notify();
                }
            }
        }).start();*/


        // Generate player disk icons
        // Multi-threading Yo-s
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/

        // Generate the player names and icons associated wtih player 1
        // These elements are programatically generated
        generatePlayerNamesAndIcons(p1Name, p1Color, 1, (RelativeLayout) findViewById(R.id.GAME_1_RELATIVE_LAYOUT));

                /*synchronized (interfaceLoadLock) {
                    interfaceElementsLoaded += 1;
                    if (interfaceElementsLoaded >= 3)
                        interfaceLoadLock.notify();
                }
            }
        }).start();*/
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/

        // Generate the player names and icons associated with player 2
        generatePlayerNamesAndIcons(p2Name, p2Color, 2, (RelativeLayout) findViewById(R.id.GAME_1_RELATIVE_LAYOUT));

                /*synchronized (interfaceLoadLock) {
                    interfaceElementsLoaded += 1;
                    if (interfaceElementsLoaded >= 3)
                        interfaceLoadLock.notify();
                }
            }
        }).start();*/

        // Initialize Game End Screen
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
                initializeGameEndScreen();
                /*synchronized (interfaceLoadLock) {
                    interfaceElementsLoaded += 1;
                    if (interfaceElementsLoaded >= 4)
                        interfaceLoadLock.notify();
                }
            }
        }).start();*/

        // Color the player discs (indicating which player is taking their turn first)
        // If this is an online game, the server user goes first (so if your device is the server, the player 1 disc will get highlighted. If your device is the client, the player 2 disc is highlighted initially)
        drawInitialHighlights();
    }

    // Some code to run when the game starts
    /*public void startGame() {
        //if (!(activityData.getBoolean("OnlineModeIsGroupHost")))
        //    Log.d(TAG,"This device is the online group host!");
        //else{
        //    Log.d(TAG,"This device is not the online group host!");
        //    changeTurn();
        //}
    }*/


    /*@Override
    public void onStart() {
        super.onStart();
        if (gameMode.equals("Online Multiplayer"))
            loadOnlineModeStartButton((RelativeLayout) findViewById(R.id.GAME_1_RELATIVE_LAYOUT));
    }*/

    /*@Override
    public  void onResume() {
        super.onResume();
        //placeDisc(multiplayerSession.getMoveFromOtherPlayer());
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (interfaceLoadLock) {
                    try {
                        // Wait for all of the interface elements load up before we begin execution of player turns
                        interfaceLoadLock.wait();
                        System.out.println("Interface Elements Loaded: " + interfaceElementsLoaded);
                        // Change the user turn
                        // The group host goes first, so if this isn't the group host, the turn needs to be changed
                        if (!(activityData.getBoolean("OnlineModeIsGroupHost")))
                            Log.d(TAG,"This device is the online group host!");
                        else{
                            Log.d(TAG,"This device is not the online group host!");
                            changeTurn();
                        }
                    } catch (InterruptedException ex) {
                        Log.d(TAG, "Thread exception in the GameActivity1 Constructor while waiting for UI to finish loading everything. Error Msg: " + ex.getMessage());
                    }
                }
            }
        }).start();
    }*/
}