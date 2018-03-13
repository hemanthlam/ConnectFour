package com.example.hemanthlam.connectfour;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * File: OnlineModeBroadcastReceiver.java
 * Created by jcrisan on 2/25/18
 * Purpose: a class to handle mutltiplayer connections
 * Used a lot of info from:
 *    -https://developer.android.com/guide/topics/connectivity/wifip2p.html
 *    -https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html
 */

// For use in online mode selection activity
// This class handles online mode setup, and is used to initialize an online game once a connection has been established between two devices
public class OnlineModeBroadcastReceiver extends BroadcastReceiver {

    // Used for logging
    private String TAG = "OnlineModeBroadcastReciever";

    // Will be used to help reference this class in listeners (which can be called upon the success or failure of WifiP2PManager method calls)
    // WifiP2PManger calls can be (or are) asynchronous, so listeners are executed once the method ends (you can't simply execute the code in the listeners after the initial WifiP2PManager calls returns, since it doesn't execute in serial)
    // See this link for more information: https://developer.android.com/guide/topics/connectivity/wifip2p.html (this explanation was derived from some details in the "Wifi Peer-to-Peer" section of the document, and some knowledge gained from these links and potentially stack overflow posts (which I hopefully also cited in the code) as the code was put together
    private OnlineModeBroadcastReceiver thisClass = this;

    // Will be needed for handling broadcast intents (see links in file header)
    // Intents notify you "of specific events detected by the Wi-Fi P2P framework, such as a dropped connection or a newly discovered peer" -https://developer.android.com/guide/topics/connectivity/wifip2p.html
    private final IntentFilter intentFilter = new IntentFilter();

    // Variables needed for Wifi Peer to Peer Setup
    private WifiP2pManager.Channel wifiP2pChannel;
    private WifiP2pManager wifiP2pManager;

    // Some code will need to be executed in the activity associated with this broadcast receiver (which instantiates a copy of this online mode broadcast receiver)
    // We therefore need references to that object (and its context)
    private Context parentContext;
    private OnlineModeSetup associatedActivity;

    // Connection information (which is used after a connection to a device has been successfully setup, and is used by the GameActivity class to connect to a peer device when establishing an online game)
    private String connectedDeviceName = "";
    private String connectedDeviceAddress = "";
    boolean connectedDeviceIsGroupOwner = false;

    // This boolean is used to indicates if wifi discovery was successful (after a call to the appropriate WifiP2PManager object was made)
    private boolean discoverySuccessful = false;

    // Used to inciate if WifiP2P is enabled on the current device
    private boolean WifiP2PEnabled = false;

    // This object is used for when connecting and disconnecting from previously connected devices (since the calls are asynchronous, and hence occur in differen threads, this will be needed if the threads want to touch the same data)
    //private final Object connectionLock = new Object();

    // A list of available wifi peers
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    // Return a copy of the intentFilter object
    // Will be needed for certain functions in the parent activity
    // INPUT: none
    // OUTPUT: a reference to the intentFiler object (I presume that a reference is passed, and not a deep copy)
    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    // A LinearLayout list to update with information
    // In the parent class, there will be a need to update a certain list with Wifi-Direct capable devices (so the user can select from this list when attempting to initiate a game with another player)
    // But because the requestPeers call (which is used to get the list of peers) is asynchronous, we don't know when this call will finish (and it is on a separate thread, so a list of peers can't simply return a list with the results)
    // The list still needs to be updated somehow though, and one way to do this is to pass a reference to the list here and have the asychronous call update it once it is done
    // Another way to do this would be to send some sort of signal and pass the object to the parent class (and have it update the list), but might be more efficient (though it may be worse practice). Time is a bit short now though, so this remains the working solution.
    private LinearLayout hostList;

    // A wifi P2P manager peer list listener
    // Updates list of peers if we find that the peers list is outdated
    // INPUT: peerList (a list of peers returned by the WifiP2PManger
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            // A list that will contain the list of peers
            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();

            // Used for information when updating the peer list
            String hostInformation = "";

            // Updating the internal peer list if necessary
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);
            }

            // Update the linear layout of peers
            // That is, for every peer, add a button with peer information to the linear layout passed into the broadcast receiver class
            if (associatedActivity != null && associatedActivity.hostList != null) {

                // Clear out old values form the list
                for (int i = 2; i < associatedActivity.hostList.getChildCount(); ++i) {
                    System.out.println("Removed Child: " + i);
                    associatedActivity.hostList.removeViewAt(i);
                }

                for (WifiP2pDevice device : refreshedPeers) {
                    // Get host informwifiP2pManageration
                    hostInformation = device.deviceName + ": " + device.deviceAddress;
                    System.out.println(device.toString());

                    // Create new button using that host information
                    Button temp = new Button(associatedActivity.hostList.getContext());
                    temp.setText(hostInformation);
                    temp.setTextSize(20);
                    temp.setHeight(100);
                    temp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Get host name and address from button
                            String hostText = ((Button) view).getText().toString();

                            // Cutting out the name portion (leaving only the address)
                            int startOfAddress = hostText.indexOf(":");
                            hostText = hostText.substring(startOfAddress + 2);

                            // Fill in the address in the host window
                            ((EditText) associatedActivity.findViewById(R.id.OnlineModeHostEditText)).setText(hostText);

                            // Hide the game window code associated with it
                            if (associatedActivity != null && associatedActivity.getHostsWindow != null)
                                associatedActivity.getHostsWindow.setVisibility(View.INVISIBLE);
                        }
                    });

                    // https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html
                    wifiP2pManager.requestGroupInfo(wifiP2pChannel, new WifiP2pManager.GroupInfoListener() {
                        @Override
                        public void onGroupInfoAvailable(WifiP2pGroup group) {
                            String groupPassword;
                            if (group != null)
                                groupPassword = group.getPassphrase();
                        }
                    });

                    // Add the button to the list
                    //associatedActivity.hostList.addView(temp);
                    associatedActivity.hostList.addView(temp);
                }

            }
            //if (peers.size() == 0)
            // No peers availalble!
            //
        }
    };

    // The Connection Information Listener
    // Executes whenever a connection is successfully created
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            if (wifiP2pInfo.groupOwnerAddress != null && WifiP2PEnabled) {
                connectedDeviceName = wifiP2pInfo.toString();/*wifiP2pInfo.groupOwnerAddress()*/
                connectedDeviceAddress = wifiP2pInfo.groupOwnerAddress.toString(); /*temp.getHostAddress()*/
                connectedDeviceIsGroupOwner = wifiP2pInfo.isGroupOwner;

                // Create the intent for the new activity
                Intent gameScreen = new Intent(associatedActivity.getApplicationContext(), AutoGeneratedBoard.class);

                // Default (may be ignored when using online mode if this device is not the game host)
                gameScreen.putExtra("Board", associatedActivity.boardSize);
                if (associatedActivity.boardSize.equals("7 x 6")) {
                    gameScreen.putExtra("Board", "7x6");
                    gameScreen.putExtra("width", 7);
                    gameScreen.putExtra("height", 6);
                }
                else if (associatedActivity.boardSize.equals("8 x 7")) {
                    gameScreen.putExtra("Board", "8x7");
                    gameScreen.putExtra("width", 8);
                    gameScreen.putExtra("height", 7);
                }
                else {
                    gameScreen.putExtra("Board", "10x8");
                    gameScreen.putExtra("width", 10);
                    gameScreen.putExtra("height", 8);
                }

                // Fill in the rest of the information
                gameScreen.putExtra("Game", "Online Multiplayer");
                gameScreen.putExtra("Player1", associatedActivity.playerName);
                gameScreen.putExtra("Player1Color", associatedActivity.playerColor);
                gameScreen.putExtra("OnlineModeGroupHostAddress", connectedDeviceAddress);
                gameScreen.putExtra("OnlineModeIsServer", !connectedDeviceIsGroupOwner);
                gameScreen.putExtra("OnlineModeInitiatedGame", associatedActivity.initiatedOnlineGame);

                // Start Activity
                associatedActivity.startActivity(gameScreen);
            } else
                Toast.makeText(associatedActivity.getApplicationContext(), "Couldn't connect to device", Toast.LENGTH_SHORT);
        }
    };



    // Exchange early game information (board size and player name)
    //     If the current device is the group host, their board size information is sent over and used by the client.
    //     If the current device is not the group host, it receives information from the group host and uses that to initialize the game.
    // INPUT: none
    // OUTPUT: true if the connection to the client was established and the information received. False otherwise.
    /*private boolean exchangeGameInfo(Intent intent, boolean isGroupOwner) {
        // Socket Info
        ServerSocket server;
        Socket client;

        // Will be used to send and receive data
        OutputStream connectionOutputStream = null;
        InputStream connectionInputStream = null;

        // Attempts to connect to online player
        int connectionAttemptLimit = 16;

        // Buffer for data
        byte buffer[] = new byte[1024];
        String gameData = null;

        // Try to connect to client
        try {
            // The group owner is the server, therefore if the connected device is not the group owner, this is the server.
            // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
            if (!isGroupOwner) {
                // Create the Server socket and wait on connections
                server = new ServerSocket(7432);
                client = server.accept();

                // Setup the input streams
                connectionInputStream = client.getInputStream();
                connectionOutputStream = client.getOutputStream();

                // Get Board and player information size string
                /*connectionInputStream.read(buffer);
                gameData = new String(buffer);

                // Process String
                // Convention: AxB-PlayerName (BoardSize-PlayerName)
                // https://stackoverflow.com/questions/4570037/java-substring-index-range
                String boardSize = gameData.substring(0, 3);
                String playerName = gameData.substring(5);

                intent.putExtra("Board", boardSize);
                intent.putExtra("width", Integer.parseInt(boardSize.substring(0, 1)));
                intent.putExtra("height", Integer.parseInt(boardSize.substring(2)));
                intent.putExtra("Player2", playerName);

                // Send player name to other device
                connectionOutputStream.write(associatedActivity.playerName.getBytes());

                connectionInputStream.close();
                connectionOutputStream.close();
                server.close();
                client.close();
            }
            // Otherwise, this is the client
            // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
            else {
                // Create Client
                client = new Socket();
                client.bind(null);

                // Variables for use when attempting to connect to the server
                boolean stopTryingToConnect = false;
                int attempts = 0;

                // Initiate connection to server (other device)
                // Keep trying to connect until it suceeds or until you hit the connection attempt theshold
                // This might be needed for hosts with slow load times
                while (!stopTryingToConnect) {
                    try {
                        // Connect to the client
                        client.connect(new InetSocketAddress(connectedDeviceAddress.toString().substring(1), 7432), 1500);

                        // If this succeeds, we are done and set this variable to true to indicate that the while loop need not execute anymore
                        stopTryingToConnect = true;

                    } catch (IOException ex) {

                        // Increment connection attempt count (indicating that we have just tried again)
                        ++attempts;

                        // Wait a bit before trying to connect again
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException intEx) {}

                        // Clear out the socket of old data
                        client.close();
                        client = null;

                        // Exit if we hit our connection attempt limit. Try again with a new socket variable if we need it
                        // I recreate the socket to avoid a bad file descriptor error
                        if (attempts >= connectionAttemptLimit)
                            stopTryingToConnect = true;
                        else {
                            client = new Socket();
                            client.bind(null);
                        }
                    }
                }

                // We couldn't connect to the other player. Return
                if (client == null)
                    return false;

                // Setup the input streams
                connectionInputStream = client.getInputStream();
                connectionOutputStream = client.getOutputStream();

                // Send Information To Server
                // Convention: AxB-PlayerName (BoardSize-PlayerName)
                String dataToSend = "";
                // .replaceAll("\\s", ""); // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
                dataToSend += associatedActivity.boardSize;
                dataToSend += "-";
                dataToSend += associatedActivity.playerName;
                System.out.println(dataToSend);

                // Send game data
                connectionOutputStream.write(dataToSend.getBytes());

                // Get name of other player
                connectionInputStream.read(buffer);
                gameData = new String(buffer);
                intent.putExtra("Player2", gameData);

                connectionOutputStream.close();
                connectionInputStream.close();
                client.close();
            }
        } catch (IOException ioException) {
            Toast.makeText(associatedActivity.getApplicationContext(), ioException.getMessage(), Toast.LENGTH_LONG);
            return false;
        }

        // Return true to indicate that data was sent
        return true;
    }*/

    // Initializes WifiP2P connection and connects parent activity to this class
    // INPUT:
    //     parentContext (context of current activity. It will need to be passed in to the online mode class),
    //     activity (the activity associated with this online connection mode)
    // OUTPUT: none
    // https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
    // https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html
    // https://stackoverflow.com/questions/4870667/how-can-i-use-getsystemservice-in-a-non-activity-class-locationmanager (this is where I got the information telling me how I needed a parent context to use the getSystemService function)
    public void initConnection(Context parent, OnlineModeSetup activity, LinearLayout givenHostList) {
        // Connecting to parent
        parentContext = parent;
        associatedActivity = activity;
        hostList = givenHostList;

        // Handles changes in the Wi-Fi P2P status (comment copied from wifi-direct.html link)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates change in list of available peers (comment copied from wifi-direct.html link)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wifi P2P connectivity has changed (comment copied from wifi-direct.html link)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed (comment copied from wifi-direct.html link)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Wifi things
        wifiP2pManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        if (wifiP2pManager != null)
        {
            // Initialize Channel
            wifiP2pChannel = wifiP2pManager.initialize(parentContext, Looper.getMainLooper(), null);

            // Disconnect fom any previously connected peers
            resetConnectionSearch();
        }
        else
            System.out.println("Failed generating broadcast receiver!");
    }

    // Schedules a peer list update
    // INPUT: a linear layout to put updated peer information in. Sadly, the requestPeers function is asynchronous, so we have to do something like this
    // OUTPUT: a list of the updated peers
    public void updatePeerList() {
        if (discoverySuccessful)
            wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
    }


    // This function is executed when connection information is received
    // INPUT: context (I am not sure what this is for, but I don't use it), intent (the intent containing the information we will need to process)
    // OUTPUT: none
    // Used information from sources mentioned in file header (and various other points in the class code)
    @Override
    public void onReceive(Context context, Intent intent) {
        // The action that just occured (which led to the trigger of the onReceive function)
        String action = intent.getAction();

        // Determine if wifi P2P mode is enabled or not. if not, alert something
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                // Alert wifi is disabled
                Toast.makeText(associatedActivity.getApplicationContext(), "Wifi peer to peer has been disabled! You will not be able to play online games, and current online games will cease functioning.", Toast.LENGTH_LONG);
                WifiP2PEnabled = false;
            }
            else {
                // Wifi is enabled
                Toast.makeText(associatedActivity.getApplicationContext(), "Wifi peer to peer has been enabled. Presuming you can find another player, you should be able to play online.", Toast.LENGTH_LONG);
                WifiP2PEnabled = true;
            }

        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed
            if (wifiP2pManager != null && discoverySuccessful)
                wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
        }
        //
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html
            if (wifiP2pManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // We are connected to the other device
                wifiP2pManager.requestConnectionInfo(wifiP2pChannel, connectionInfoListener);
            }
        }
        // Not sure if anything shoudl really be done here
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            //DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
            //WifiP2pDevice wifiP2pDevice = (WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_STATE);
            // https://android.googlesource.com/platform/development/+/master/samples/WiFiDirectDemo/src/com/example/android/wifidirect/DeviceListFragment.java
            return;
        }
    }

    // Initiate peer discovery
    // INPUT: none
    // OUTPUT: none, though it updates the class-wide discoverySuccessful class if discovery was successful
    public void initiatePeerDiscovery() {
        if (wifiP2pManager != null && wifiP2pChannel != null) {
            wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    discoverySuccessful = true;
                    System.out.println("Discovery of peers search succeeded (in theory)");
                    updatePeerList();
                }

                @Override
                public void onFailure(int reasonCode) {
                    discoverySuccessful = false;
                    System.out.println("Discovery of Peers search failed. Reason code: " + reasonCode);
                }
            });
        }
    }

    // For disconnecting from previously discovered hosts before looking for new hosts to connect to
    // INPUT: none
    // OUTPUT: none
    public void resetConnectionSearch() {
        if (wifiP2pManager != null && wifiP2pChannel != null) {
            // Referenced: https://groups.google.com/forum/#!topic/android-developers/6lwXJCnv5zU
            wifiP2pManager.removeGroup(wifiP2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                        initiatePeerDiscovery();
                    }

                @Override
                public void onFailure(int i) { initiatePeerDiscovery(); }
            });
        }
    }

    // Connect to a peer using the address passed into the function
    // INPUT: deviceAddress (the address of the device you are connecting to)
    // OUTPUT: none
    // https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html#discover
    public void connectToPeer(String deviceAddress) {
        if (wifiP2pManager != null && wifiP2pChannel != null) {
            // https://stackoverflow.com/questions/23713176/what-can-fail-wifip2pmanager-connect (an old, unused link, but I read it so I left this in here). The information in it may not actually be used

            // Configuration setup
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            // Actual connection attempt
            wifiP2pManager.connect(this.wifiP2pChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    if (!WifiP2PEnabled)
                        ((EditText) associatedActivity.findViewById(R.id.OnlineModeHostEditText)).setText("Wifi Peer to Peer is disabled. We can't conenct to any peers!");
                }

                public void onFailure(int reason) {
                    ((EditText) associatedActivity.findViewById(R.id.OnlineModeHostEditText)).setText("Connection failed! Reason: " + reason);
                }
            });
        }
        else
            ((EditText) associatedActivity.findViewById(R.id.OnlineModeHostEditText)).setText("Connection failed! Not able to use WiFi service.");
    }
}
