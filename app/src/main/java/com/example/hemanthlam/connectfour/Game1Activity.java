package com.example.hemanthlam.connectfour;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//The activity used for the 7 x 6 game board
public class Game1Activity extends GameActivity {
    String TAG = "Game1Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);
        Log.d(TAG,"Board 7*6");
        box = (RelativeLayout) findViewById(R.id.GAME_1_INNER_RELATIVE);
        gameBoard = new Board("7x6");
        hidePieces();
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
                for(int i = 0; i < box.getChildCount();++i){
                    final int I = i;
                    box.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            placeDisc(I);
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
                generatePlayerNamesAndIcons(p2Name, p2Color, 2, (RelativeLayout) findViewById(R.id.GAME_1_RELATIVE_LAYOUT));
                /*synchronized (interfaceLoadLock) {
                    interfaceElementsLoaded += 1;
                    if (interfaceElementsLoaded >= 3)
                        interfaceLoadLock.notify();
                }
            }
        }).start();*/

        // Required to draw the edge around player 1's icon when the game starts
        if (activityData.get("Game").equals("Online Multiplayer") && !activityData.getBoolean("OnlineModeIsGroupHost"))
        {
            if (this.p2HighlightView != null)
                this.p2HighlightView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) { thisActivity.drawCircleEdges((ImageView)view, thisActivity.p1Color); }
                });
        }
        else
        {
            if (this.p1HighlightView != null)
                this.p1HighlightView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) { thisActivity.drawCircleEdges((ImageView)view, thisActivity.p1Color); }
                });

        }
        // Initialize Game End Screen
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
                thisActivity.initializeGameEndScreen();
                /*synchronized (interfaceLoadLock) {
                    interfaceElementsLoaded += 1;
                    if (interfaceElementsLoaded >= 4)
                        interfaceLoadLock.notify();
                }
            }
        }).start();*/

    }

    // Some code to run when the game starts
    public void startGame() {
        if (!(activityData.getBoolean("OnlineModeIsGroupHost")))
            Log.d(TAG,"This device is the online group host!");
        else{
            Log.d(TAG,"This device is not the online group host!");
            changeTurn();
        }
    }

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