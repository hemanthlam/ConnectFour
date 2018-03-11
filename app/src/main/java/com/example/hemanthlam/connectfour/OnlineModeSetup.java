package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
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

public class OnlineModeSetup extends AppCompatActivity {

    protected OnlineModeBroadcastReceiver onlineMode = null;
    protected RelativeLayout getHostsWindow = null;
    protected OnlineModeSetup thisActivity = this;
    protected Button hideGetHostsWindowButton = null;
    protected LinearLayout hostList = null;
    protected RadioButton p1blue = null, p1red = null, p1green = null, p1purple = null;
    protected String playerColor = null;
    protected String playerName = null;
    protected String boardSize = null;
    protected boolean initiatedOnlineGame = false;

    // Called when the OnlineModeSetup class is being created
    // INPUT: savedInstanceState (comes with the call, though we don't use it)
    // OUTPUT: none
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup things
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_mode_setup);

        // Generate the find hosts window (a separate window that will contain a list of WifiP2P hosts the current player can connect to)
        setupGameFindWindow();

        // Generate the broadcast reciever
        this.onlineMode = new OnlineModeBroadcastReceiver();
        this.onlineMode.initConnection(this.getBaseContext(), this, hostList);

        // A button that shows the find hosts window when clicked
        Button showFindHostsButton = (Button) findViewById(R.id.OnlineModeFindHostButton);
        showFindHostsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if (getHostsWindow != null)
                    getHostsWindow.setVisibility(View.VISIBLE);
            }
        });

        // A button that hides the connect hosts button when it is clicked
        this.hideGetHostsWindowButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if (getHostsWindow != null)
                    getHostsWindow.setVisibility(View.INVISIBLE);
            }
        });

        // A button that initiates the process of connecting to a peer once the peer's information is loaded into the appropriate EditView (it doesn't end up doing anything if you can't connect)
        Button joinHostedGameButton = (Button) findViewById(R.id.OnlineModeJoinHostGameButton);
        joinHostedGameButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                // Fill in intent with data
                String address = ((EditText)findViewById(R.id.OnlineModeHostEditText)).getText().toString();
                initiatedOnlineGame = true;
                onlineMode.connectToPeer(address);
            }
        });

        // Update intent data on spinner selection
        Spinner boardSizeSelectionSpinner = (Spinner)findViewById(R.id.OnlineModeBoardSizeSelectionSpinner);
        boardSizeSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boardSize = adapterView.getSelectedItem().toString();
                // Used some information from https://developer.android.com/reference/android/content/Intent.html#putExtra(java.lang.String, android.os.Parcelable[])
                //Toast.makeText(getApplicationContext(), boardSize, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
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
                //Toast.makeText(getApplicationContext(), playerName, Toast.LENGTH_SHORT).show();
            }
        });
        // Get radio buttons
        this.p1blue = (RadioButton) findViewById(R.id.OnlineModePlayer1BlueButton);
        this.p1red = (RadioButton) findViewById(R.id.OnlineModePlayer1RedButton);
        this.p1green = (RadioButton) findViewById(R.id.OnlineModePlayer1GreenButton);
        this.p1purple = (RadioButton) findViewById(R.id.OnlineModePlayer1PurpleButton);

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

    // Setup the find hosts window (a hidden window that will appear once the user clicks the find hosts button, which presents the user with a list of WifiP2P hosts they can connect to)
    // INPUT: none
    // OUTPUT: none
    protected void setupGameFindWindow() {
        // Get Display Information and save it (would be nice in a utility class)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int buttonHeight = 100;
        int buttonWidth = (displayMetrics.widthPixels / 5) * 4;
        int buttonX = displayMetrics.widthPixels / 10;

        // As we add elements to the relative layout, this will allow us to keep track of what Y offset we need to give the various elements we add to the layout
        int runningYOffset = 0;

        // Object neede for setting up the main window
        RelativeLayout primaryLayout = (RelativeLayout) findViewById(R.id.Online_Mode_Relative_Layout);
        RelativeLayout findHostsWindow = new RelativeLayout(getWindow().getContext());
        findHostsWindow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        findHostsWindow.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Window Title Text View
        TextView titleTextView = new TextView(findHostsWindow.getContext());
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, buttonHeight));
        titleTextView.setTextSize(24);
        titleTextView.setTextColor(Color.WHITE);
        titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        titleTextView.setText("Select Host");

        // List that will hold lis to of potential WifiP2P hosts
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

        // A button that will be used to hid the Select Host window
        Button hideHostsWindowButton = new Button(hostListLayout.getContext());

        // Styling the button
        ViewGroup.LayoutParams buttonLayoutParams = new ViewGroup.LayoutParams(buttonWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        hideHostsWindowButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        hideHostsWindowButton.setLayoutParams(buttonLayoutParams);
        hideHostsWindowButton.setText("Hide Select Hosts Window");

        // Positioning the button
        //hideHostsWindowButton.setX(buttonX);
        //hideHostsWindowButton.setY(displayMetrics.heightPixels - buttonHeight - 300);
        hostListLayout.addView(hideHostsWindowButton);

        // Adding the button and list to the Select Host window
        findHostsWindow.addView(hostListLayout);
        //findHostsWindow.addView(hideHostsWindowButton);

        // Adding the Select Hosts window to the main window (the activity window)
        primaryLayout.addView(findHostsWindow);

        // The select host window is set to invisible by default (but is made visible when a certain button is clicked)
        findHostsWindow.setVisibility(View.INVISIBLE);

        // These are assinged to the OnlineModeSetup class for later usage
        getHostsWindow = findHostsWindow;
        hostList = hostListLayout;
        hideGetHostsWindowButton = hideHostsWindowButton;
    }

    // Handles radio button logic (logic that is executed when a radio button is selected in a name activity)
    // INPUT: none
    // OUTPUT: none
    protected void radioButtonLogic() {
        // Set default values
        p1blue.setChecked(true);
        p1red.setChecked(false);
        p1green.setChecked(false);
        p1purple.setChecked(false);

        // Switching Logic
        p1blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //player1Color = "Blue";
                p1green.setChecked(false);
                p1purple.setChecked(false);
                p1red.setChecked(false);
                playerColor = "blue";
            }
        });
        p1red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //player1Color = "Red";
                p1green.setChecked(false);
                p1purple.setChecked(false);
                p1blue.setChecked(false);
                playerColor = "red";
            }
        });
        p1green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //player1Color = "Green";
                p1blue.setChecked(false);
                p1purple.setChecked(false);
                p1red.setChecked(false);
                playerColor = "green";
            }
        });
        p1purple.setOnClickListener(new View.OnClickListener() {
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
