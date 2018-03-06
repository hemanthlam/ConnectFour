package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineModeSetup extends Activity {

    protected OnlineModeBroadcastReceiver onlineMode;
    protected RelativeLayout getHostsWindow;
    protected OnlineModeSetup thisActivity = this;
    //protected Button selectHighlightedHostButton;
    protected Button hideGetHostsWindowButton;
    protected LinearLayout hostList;
    protected RadioButton p1blue, p1red, p1green, p1purple;
    protected Intent intent;
    protected String playerColor;
    protected String playerName;
    protected String boardSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Determines when an activity can proceed to the next game activity window
        boolean canProceed = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_mode_setup);

        // Generate the find hosts window
        this.setupGameFindWindow();

        // Generate the broadcast reciever
        this.onlineMode = new OnlineModeBroadcastReceiver();
        this.onlineMode.initConnection(this.getBaseContext(), this, hostList);

        // Show the find hosts window
        Button showFindHostsButton = (Button) findViewById(R.id.OnlineModeFindHostButton);
        showFindHostsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                // Update the visibility of the get-hosts window
                if (thisActivity.getHostsWindow != null)
                    thisActivity.getHostsWindow.setVisibility(View.VISIBLE);
            }
        });

        // Hide the find hosts window
        hideGetHostsWindowButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if (thisActivity.getHostsWindow != null)
                    thisActivity.getHostsWindow.setVisibility(View.INVISIBLE);
            }
        });

        // On joining a player game
        Button joinHostedGameButton = (Button) findViewById(R.id.OnlineModeJoinHostGameButton);
        joinHostedGameButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                // Fill in intent with data
                String address = ((EditText)findViewById(R.id.OnlineModeHostEditText)).getText().toString();
                thisActivity.onlineMode.connectToPeer(address);
            }
        });

        // Update intent data on spinner selection
        Spinner boardSizeSelectionSpinner = (Spinner)findViewById(R.id.OnlineModeBoardSizeSelectionSpinner);
        boardSizeSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boardSize = adapterView.getSelectedItem().toString();
                // Used some information from https://developer.android.com/reference/android/content/Intent.html#putExtra(java.lang.String, android.os.Parcelable[])
                Toast.makeText(getApplicationContext(), boardSize, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Update Intent data on click
        EditText nameEntryBox = (EditText)findViewById(R.id.OnlineModePlayerNameEditText);
        nameEntryBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                playerName = editable.toString();
                Toast.makeText(getApplicationContext(), playerName, Toast.LENGTH_SHORT).show();
            }
        });
        // Get radio buttons
        p1blue = (RadioButton) findViewById(R.id.OnlineModePlayer1BlueButton);
        p1red = (RadioButton) findViewById(R.id.OnlineModePlayer1RedButton);
        p1green = (RadioButton) findViewById(R.id.OnlineModePlayer1GreenButton);
        p1purple = (RadioButton) findViewById(R.id.OnlineModePlayer1PurpleButton);

        // Setup the radio button logic
        radioButtonLogic();
    }

    // Re-registers online mode broadcast receiver
    // This is recommended by the page: https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html#discover (that was also used as reference when developing the broadcast receiver)
    @Override
    public void onResume() {
        super.onResume();
        onlineMode.resetConnectionSearch();
        registerReceiver(onlineMode, onlineMode.getIntentFilter());
    }

    // Unregisters online mode broadcast receiver
    // This is recommended by the page: https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html#discover (that was also used as reference when developing the broadcast receiver)
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(onlineMode);
        //onlineMode.resetConnectionSearch();
    }

    // Setup the find hosts window (a hidden window that will appear once the user clicks the find hosts button)
    // INPUT: none
    // OUTPUT: none
    protected void setupGameFindWindow() {
        // Get Display Information and save it
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int buttonHeight = 100;
        int buttonWidth = (displayMetrics.widthPixels / 5) * 4;
        int buttonX = displayMetrics.widthPixels / 10;

        // As we add elements to the relative layout, this will allow us to keep track of what Y offset we need to give the various elements we add to the layout
        int runningYOffset = 0;

        // Setup the main window
        RelativeLayout primaryLayout = (RelativeLayout) findViewById(R.id.Online_Mode_Relative_Layout);
        RelativeLayout findHostsWindow = new RelativeLayout(getWindow().getContext());
        findHostsWindow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        findHostsWindow.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Text View
        TextView titleTextView = new TextView(findHostsWindow.getContext());
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, buttonHeight));
        titleTextView.setTextSize(24);
        titleTextView.setTextColor(Color.WHITE);
        titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        titleTextView.setText("Select Host");

        // List of hosts
        LinearLayout hostListLayout = new LinearLayout(findHostsWindow.getContext());
        hostListLayout.setLayoutParams(new LinearLayout.LayoutParams((displayMetrics.widthPixels / 4) * 3, ViewGroup.LayoutParams.WRAP_CONTENT));
        hostListLayout.setOrientation(LinearLayout.VERTICAL);
        hostListLayout.setX(displayMetrics.widthPixels / 8);
        hostListLayout.addView(titleTextView);

        // Get Available Host Names
        /*for (int i = 0; i < 5; ++i)
        {
            Button temp = new Button(hostListLayout.getContext());
            temp.setText("Host Name");
            temp.setTextSize(20);
            temp.setHeight(100);
            temp.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((EditText) findViewById(R.id.OnlineModeHostEditText)).setText(((Button)view).getText());
                    if (thisActivity.getHostsWindow != null)
                        thisActivity.getHostsWindow.setVisibility(View.INVISIBLE);
                }
            });
            hostListLayout.addView(temp);
        }*/

        // Hide Select Host Window Button
        Button hideHostsWindowButton = new Button(findHostsWindow.getContext());

        // Style buttons
        ViewGroup.LayoutParams buttonLayoutParams = new ViewGroup.LayoutParams(buttonWidth, buttonHeight);
        //selectHostButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        hideHostsWindowButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        //selectHostButton.setLayoutParams(buttonLayoutParams);
        hideHostsWindowButton.setLayoutParams(buttonLayoutParams);
        //selectHostButton.setText("Select Host");
        hideHostsWindowButton.setText("Hide Select Hosts Window");

        // Set Button Positions
        //selectHostButton.setX(buttonX);
        //selectHostButton.setY(displayMetrics.heightPixels - (2 * buttonHeight) - 100);
        hideHostsWindowButton.setX(buttonX);
        hideHostsWindowButton.setY(displayMetrics.heightPixels - buttonHeight - 200);

        // Add Buttons to window
        findHostsWindow.addView(hostListLayout);
        findHostsWindow.addView(hideHostsWindowButton);
        primaryLayout.addView(findHostsWindow);

        // Hidden by default
        findHostsWindow.setVisibility(View.INVISIBLE);

        // Assign to class for later usage
        this.getHostsWindow = findHostsWindow;
        this.hostList = hostListLayout;
        //this.selectHighlightedHostButton = selectHostButton;
        this.hideGetHostsWindowButton = hideHostsWindowButton;
    }

    protected void radioButtonLogic() {
        // Set default values
        this.p1blue.setChecked(true);
        this.p1red.setChecked(false);
        this.p1green.setChecked(false);
        this.p1purple.setChecked(false);

        // Switching Logic
        this.p1blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //player1Color = "Blue";
                p1green.setChecked(false);
                p1purple.setChecked(false);
                p1red.setChecked(false);
                playerColor = "blue";
            }
        });
        this.p1red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //player1Color = "Red";
                p1green.setChecked(false);
                p1purple.setChecked(false);
                p1blue.setChecked(false);
                playerColor = "red";
            }
        });
        this.p1green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //player1Color = "Green";
                p1blue.setChecked(false);
                p1purple.setChecked(false);
                p1red.setChecked(false);
                playerColor = "green";
            }
        });
        this.p1purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //player1Color = "Purple";
                p1green.setChecked(false);
                p1blue.setChecked(false);
                p1red.setChecked(false);
                playerColor = "purple";
            }
        });
    }
}
