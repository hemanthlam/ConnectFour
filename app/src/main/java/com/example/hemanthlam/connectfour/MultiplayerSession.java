package com.example.hemanthlam.connectfour;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * File" MultiplayerSession.java
 * Created by jcrisan on 2/28/18.
 * Purpose: a class to handle online mode interactions between two players (given an ip address to a connected peer)
 */

public class MultiplayerSession {
    // Server Socket and client
    private ServerSocket serverSocket = null;
    private Socket client = null;

    // Connection Things (for date exchange with client)
    // Will be used to send and receive data
    private OutputStream connectionOutputStream = null;
    private InputStream connectioninputStream = null;

    // Atomic Ints: http://winterbe.com/posts/2015/05/22/java8-concurrency-tutorial-atomic-concurrent-map-examples/
    //private AtomicBoolean sendSignal = new AtomicBoolean(false);
    //private AtomicBoolean receiveSignal = new AtomicBoolean(false);
    //private AtomicBoolean continueThreadExecution = new AtomicBoolean(true);
    public String connectedDeviceAddress = "";
    public boolean connected = false;
    boolean connectedDeviceIsGroupOwner = false;

    // For receiving data
    //private byte buffer[] = new byte[1024];

    // Initiate socket connection
    // INPUT: none
    // OUTPUT: true if the connection succeeded, false otherwise
    public boolean initiateConnectionWithConnectedDevice(String connectedDeviceAddress, boolean connectedDeviceIsGroupOwner) {
        // Variables
        boolean connectionSucessful = true;
        String deviceAddress = connectedDeviceAddress.substring(1);

        // Only proceed if we have valid input data
        if (connectedDeviceAddress == null || connectedDeviceAddress != "") {
            // Assign data
            this.connectedDeviceAddress = deviceAddress;
            this.connectedDeviceIsGroupOwner = connectedDeviceIsGroupOwner;

            try {
                // The group owner is the server, therefore if the connected device is not the group owner, this is the server.
                // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
                if (!connectedDeviceIsGroupOwner) {
                    // 8080 will be the send channel
                    this.serverSocket = new ServerSocket(8080);
                    this.client = serverSocket.accept();

                    // Setup the input streams
                    this.connectioninputStream = client.getInputStream();
                    this.connectionOutputStream = client.getOutputStream();

                    if (this.client == null || this.serverSocket == null || this.connectioninputStream == null || this.connectionOutputStream == null)
                        connectionSucessful = false;
                }
                // Otherwise, this is the client
                // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
                else {
                    // Initiate connection to server
                    this.client = new Socket();
                    this.client.bind(null);
                    this.client.connect(new InetSocketAddress(this.connectedDeviceAddress, 8080), 1000);

                    // Setup the input and output streams (I think we can use one port for this?)
                    this.connectioninputStream = client.getInputStream();
                    this.connectionOutputStream = client.getOutputStream();

                    // Verify
                    if (this.client == null || this.connectioninputStream == null || this.connectionOutputStream == null)
                        connectionSucessful = false;
                }
            } catch (IOException InteruptedEx) {
                System.out.println("Error in connectionSucessful: " + InteruptedEx.getMessage() + ". Connection Initialization Failed.");
                connectionSucessful = false;
            }
        }

        // Return success status
        this.connected = connectionSucessful;
        return connectionSucessful;
    }

    // End Socket Connection
    // INPUT: none
    // OUTPUT: none
    public void endConnectionWithConnectedDevice() {
        // Close sockets and I/O streams
        // I thought closing the individual sockets and streams would be better than putting all of them in one try catch (so if close() attempt fails, the rest don't have to)
        if (serverSocket != null && !serverSocket.isClosed())
            try {serverSocket.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when tryin to close ServerSocket: " + ex.getMessage());};
        if (client != null && !client.isClosed())
            try {client.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when trying to close client socket: " + ex.getMessage());};
        if (connectioninputStream != null)
            try {connectioninputStream.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when trying to close input stream: " + ex.getMessage());};
        if (connectionOutputStream != null)
            try {connectionOutputStream.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when trying to close output stream: " + ex.getMessage());};
    }

    // Attempts to get a move from another player (connected via a socket)
    // INPUT: none
    // OUTPUT: the column number from the other player's turn (-1 if things didn't work)
    public int getMoveFromOtherPlayer() {
        // Temporary Variable
        int playerTurn = -1;

        // This function can only execute if our thread function (controlling socket functionality) is running
        if (this.connected) {
            // Read data off of the input buffer (a.k.a: get it from the client socket)
            // The example: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
            // simply shows you waiting until you get data... A timer was put in an an attempt to mitigate that
            try {
                while ((playerTurn = this.connectioninputStream.read()) != -1) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println("Error in MultiplayerSession.getMoveFromOtherPlayer: " + ex.getMessage());
                    }
                }
            } catch(IOException ex) {
                System.out.println("Error in MultiplayerSession.getMoveFromOtherPlayer: " + ex.getMessage());
            }

            // Copy data to the move data string
            // https://stackoverflow.com/questions/17354891/java-bytebuffer-to-string
            //temp = buffer[0];

            // Update the appropriate signal variables
            //this.receiveSignal.set(false);
            //this.receiveSignal.notifyAll();
        } else {
            System.out.println("Error in MultiplayerSession.getMoveFromOtherPlayer: No connected devices. Can't send data!");
        }

        // Return result
        return playerTurn;
    }

    public void networkThread() {

    }

    // Sends move data (contained in string) to other player
    // INPUT: data (string to send)
    // OUTPUT: none
    public boolean sendMoveToOtherPlayer(int data) {
        boolean sentSuccessfully = false;

        // This function can only execute if our thread function (controlling socket functionality) is running
        if (this.connected) {
            //this.sendData = data;
            //this.sendSignal.set(true);
            //this.continueThreadExecution.notifyAll();

            // Send string
            try {
                //this.connectionOutputStream.write(data.getBytes());
                this.connectionOutputStream.write(data);
                sentSuccessfully = true;
            } catch (IOException ex) {
                System.out.println("Error in MultiplayerSession.sendMoveToOtherPlayer: " + ex.getMessage());
            }

            //this.sendSignal.set(false);
            //this.sendSignal.notifyAll(); // Notifies all threads waiting for the notification on this variable

            //try {sendSignal.wait();} catch (InterruptedException ex) {}
        } else {
            System.out.println("Error in MultiplayerSession.sendMoveToOtherPlayer: No connected devices. Can't send data!");
        }

        // Return result
        return sentSuccessfully;
    }
}
