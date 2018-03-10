package com.example.hemanthlam.connectfour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * File: BroadcastReceiever.java
 * Created by jcrisan on 2/25/18.
 * Purpose: to help enable online mode connectivity
 * Class created as a part of directions noted here: https://developer.android.com/training/connect-devices-wirelessly/wifi-direct.html
 */

// Recieves and processes broadcasts from the main activity
public class BroadcastReceiverOld {
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);

                /*
                    // If an AdapterView is backed by this data, notify it
                    // of the change. For instance, if you have a ListView of
                    // available peers, trigger an update.
                    ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
                 */
            }
            //if (peers.size() == 0)
                // No peers availalble!
                //
        }
    };
    public void onReceive(Context context, Intent intent, Activity activity, WifiP2pManager p2pManager, WifiP2pManager.Channel p2pChannel) {

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
            if (p2pManager != null)
                p2pManager.requestPeers(p2pChannel, this.peerListListener);
            // Log that the peer to peer list has changed
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Connection state has changed
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }
    }
}
