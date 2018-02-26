package com.example.hemanthlam.connectfour;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * File: OnlineModeBroadcastReceiver.java
 * Created by jcrisan on 2/25/18
 * Purpose: a class to handle mutltiplayer connections
 * Will use a lot of info from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 */

// For use in online mode selection activity
public class OnlineModeBroadcastReceiver extends BroadcastReceiver {

    // Will be used to help reference this class in listeners
    private OnlineModeBroadcastReceiver thisClass = this;

    // Will be needed for handline broadcast intents (see link below file purpose)
    private final IntentFilter intentFilter = new IntentFilter();

    // Wifi things
    WifiP2pManager.Channel wifiP2pChannel;
    WifiP2pManager wifiP2pManager;
    WifiP2pDeviceList deviceList;
    Context parentContext;
    OnlineModeSetup associatedActivity;

    // Connection Info
    String connectedDeviceName = "";
    String connectedDeviceAddress = "";
    boolean connectedDeviceIsGroupOwner = false;

    // Indicates if wifi discovery was successful
    boolean discoverySuccessful = false;

    // A list of available wifi peers
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    // Will be needed for initalization
    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    // A LinearLayout list to update with information
    // Because the requestPeers call (which will get our list of peers), is asynchronous, we have to update the list in the peer listener
    // (We can't just return a list of peers once it is done, if we want to be able to request updates of the list manually)
    LinearLayout hostList;

    // A wifi P2P manager peer list listener
    // Updates list of peers if we find that the peers list is outdated
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

            // Clear out old values form the list
            for (int i = 1; i < associatedActivity.hostList.getChildCount(); ++i)
                associatedActivity.hostList.removeViewAt(i);

            // Update the linear layout of peers
            // That is, for every peer, add a button with peer information to the linear layout passed into the broadcast receiver class
            if (associatedActivity != null && associatedActivity.hostList != null) {
                for (WifiP2pDevice device : refreshedPeers) {
                    // Get host information
                    hostInformation = device.deviceName + ": " + device.deviceAddress;

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
                            hostText = hostText.substring(startOfAddress + 1);

                            // Fill in the address in the host window
                            ((EditText) associatedActivity.findViewById(R.id.OnlineModeHostEditText)).setText(hostText);

                            // Hide the game window code associated with it
                            if (associatedActivity != null && associatedActivity.getHostsWindow != null)
                                associatedActivity.getHostsWindow.setVisibility(View.INVISIBLE);
                        }
                    });

                    // Add the button to the list
                    associatedActivity.hostList.addView(temp);
                }
            }
            //if (peers.size() == 0)
            // No peers availalble!
            //
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            java.net.InetAddress temp = wifiP2pInfo.groupOwnerAddress;
            thisClass.connectedDeviceName = temp.getHostName();
            thisClass.connectedDeviceAddress = temp.getHostAddress();
            thisClass.connectedDeviceIsGroupOwner = wifiP2pInfo.isGroupOwner;
        }
    };

    // Verifies if WifiP2P is supported
    // INPUT: none
    // OUTPUT: true if wifiP2P is supported. False otherwise
    //public boolean
    // Initlaize connection
    // INPUT: parentContext (context of current activity. It will need to be passed in to the online mode class), activity (the activity associated with this online connection mode)
    // OUTPUT: none
    // https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
    // https://stackoverflow.com/questions/4870667/how-can-i-use-getsystemservice-in-a-non-activity-class-locationmanager (this is where I got the information telling me how I needed a parent context to use the getSystemService function)
    public void initConnection(Context parent, OnlineModeSetup activity, LinearLayout givenHostList) {

        // Variable Association
        this.parentContext = parent;
        this.associatedActivity = activity;
        this.hostList = givenHostList;

        // Handles changes in the Wi-Fi P2P status
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates change in list of available peers
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wifi P2P connectivity has changed
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Wifi things
        this.wifiP2pManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        if (wifiP2pManager != null)
        {
            // Initialize Channel
            this.wifiP2pChannel = this.wifiP2pManager.initialize(parentContext, Looper.getMainLooper(), null);

            // Initialize Peer Discovery
            this.initiatePeerDiscovery();
        }
        else
            System.out.println("Failed generating broadcast receiver!");
    }

    // Get list of updated peers
    // INPUT: a linear layout to put updated peer information in. Sadly, the requestPeers function is asynchronous, so we have to do something like this
    // OUTPUT: a list of the updated peers
    public void updatePeerList() {
        if (this.discoverySuccessful)
            this.wifiP2pManager.requestPeers(this.wifiP2pChannel, this.peerListListener);
    }


    // Receives connection information
    // INPUT:
    // OUTPUT: none
    @Override
    public void onReceive(Context context, Intent intent) {

        // Will hold actions from given intent
        String action = intent.getAction();

        // Determine if wifi P2P mode is enabled or not. if not, alert something
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                // Alert wifi is disabled
            }
            else {
                // Alert wifi is enabled
            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // THe peer list has changed
            if (this.wifiP2pManager != null && this.discoverySuccessful)
                this.wifiP2pManager.requestPeers(this.wifiP2pChannel, this.peerListListener);
            // Log that the peer to peer list has changed
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (this.wifiP2pManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // We are connected to the other decivice
                wifiP2pManager.requestConnectionInfo(this.wifiP2pChannel, connectionInfoListener);
            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            //DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
            //WifiP2pDevice wifiP2pDevice = (WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_STATE);
            // https://android.googlesource.com/platform/development/+/master/samples/WiFiDirectDemo/src/com/example/android/wifidirect/DeviceListFragment.java
        }
    }

    // Initiate peer discovery
    // INPUT: none
    // OUTPUT: none, though it updates the class-wide discoverySuccessful class if discovery was successful
    public void initiatePeerDiscovery() {
        wifiP2pManager.discoverPeers(this.wifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                thisClass.discoverySuccessful = true;
                System.out.println("Discovery of peers search succeeded (in theory)");
            }

            @Override
            public void onFailure(int reasonCode) {
                thisClass.discoverySuccessful = false;
                System.out.println("Discovery of Peers search failed. Reason code: " + reasonCode);
            }
        });
    }

    // Returns a copy of the latest list of peers
    // INPUT: none
    // OUTPUT: a list of peers
    public List<WifiP2pDevice> getListOfPeers() {
        return this.peers;
    }

    // https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html#discover
    public void connectToPeer(String deviceAddress) {
        // Configuration setup
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        wifiP2pManager.connect(this.wifiP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ((EditText) associatedActivity.findViewById(R.id.OnlineModeHostEditText)).setText("Now Connected!");
            }

            public void onFailure(int reason) {
                ((EditText) associatedActivity.findViewById(R.id.OnlineModeHostEditText)).setText("Connection failed! Reason: " + reason);

            }
        });
    }
}
