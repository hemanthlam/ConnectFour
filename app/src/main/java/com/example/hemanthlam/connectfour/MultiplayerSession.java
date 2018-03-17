package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * File" MultiplayerSession.java
 * Created by jcrisan on 2/28/18.
 * Purpose: a class to handle online mode interactions between two players (given an ip address to a connected peer)
 */

public class MultiplayerSession {
    // Logging purposes
    private String TAG = "MultiplayerSession";

    // Needed to setup sockets
    private String connectedDeviceAddress = "";
    private boolean isGroupOwner = false;

    // Atomic Ints: http://winterbe.com/posts/2015/05/22/java8-concurrency-tutorial-atomic-concurrent-map-examples/
    private AtomicBoolean sendSignal = new AtomicBoolean(false);
    private AtomicBoolean receiveSignal = new AtomicBoolean(false);
    private AtomicBoolean connected = new AtomicBoolean(false);
    private AtomicBoolean continueThreadExecution = new AtomicBoolean(false);

    private final Object threadLock = new Object();
    private boolean setupThreadCompleted = false;
    private boolean setupThreadSignalRecieved = false;

    // Will be used for transferring data
    private int sendData = -1;
    private int receiveData = -1;

    // Server Socket and client
    private ServerSocket serverSocket = null;
    private Socket client = null; // https://stackoverflow.com/questions/14425826/variable-is-accessed-within-inner-class-needs-to-be-declared-final

    // This variable will be bullet proof!
    // https://stackoverflow.com/questions/40413717/cant-connect-to-android-devices-when-using-network-service-discovery-through-wi (I thought that perhaps using a local integer might have been causing problems?)
    //private static final int port = 8916;

    // Will be used to send and receive data once devices are connected
    private OutputStream connectionOutputStream = null;
    private InputStream connectionInputStream = null;

    // Initial Game Information
    private boolean initiatedGame;
    private String initialGameInformation = null;
    private String gameInfoFromClient = null;

    // Initiate socket connection
    // INPUT:
    //     connectedDeviceAddress (address of device to connect to),
    //     isGroupOwner (a boolean that indicates if this device is to be the group owner, a.k.a: the device hosting the socket connection)
    //     boardInformation (a string containg player information.
    //         If the device is the group owner, the board size and the player name in the form AxB-PlayerName,
    //         or just the player name if it isn't the group owner)
    //     currentDeviceIsInitiatingDevice (a boolean indicating if the device that called this function is the one that initiated the connection-if so, we are going to end up sending game information over to the client)
    // OUTPUT: game information from the other client (board size and/or player name), null if not
    public String initiateConnectionWithConnectedDevice(String connectedDeviceAddress, boolean isGroupOwner, String playerInformation, boolean currentDeviceIsInitiatingDevice) {
        // Variables
        boolean connectionSucessful = true;

        // Checking the address
        if (connectedDeviceAddress == null || connectedDeviceAddress == "" || playerInformation == null || playerInformation == null)
            return null;

        // Save this information
        initialGameInformation = playerInformation;
        initiatedGame = currentDeviceIsInitiatingDevice;

        // Only proceed if we have valid input data
        if (connectedDeviceAddress != null && connectedDeviceAddress != "") {
            // Assign data
            this.connectedDeviceAddress = connectedDeviceAddress.substring(1);
            System.out.println("Connected Device Address: " + connectedDeviceAddress.substring(1));
            this.isGroupOwner = isGroupOwner;

            // https://developer.android.com/guide/components/processes-and-threads.html (used a bit of information, mostly the new Thread(Runnable) syntax and noted an example)
            // https://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html (a little dated?)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadFunction();
                }
            }).start();

            // --We can't exit this loop until the network thread finishes setting itself up
            // --This probably isn't the best way to do this, but unpredictable scheduling makes this more difficult then it should be
            // Check if the thread has been setup by checking the setupThreadComplete variable (which is set to true when the network thread function finishes running)
            // There is a chance that it was already set (so we don't need to wait for it to complete), so we check if the the setupThreadComplete variable has already been set to true
            // If it hasn't, we wait for the network thread to finish setup
            synchronized (threadLock) {
                if (!setupThreadCompleted) {
                    // Wait for the network thread to finish connection setup
                    try {
                        // Wait for the network thread to finish initial connection setup
                        threadLock.wait();
                    } catch (InterruptedException intEx) {
                        Log.d(TAG, "Error in waiting for the network thread to finish initial connection setup: " + intEx.getMessage());
                    }
                }
            }
        }

        // Return success status
        return gameInfoFromClient;
    }

    // End Socket Connection
    // INPUT: none
    // OUTPUT: none
    public void endConnectionWithConnectedDevice() {
        // Close sockets and I/O streams
        // I thought closing the individual sockets and streams would be better than putting all of them in one try catch (so if close() attempt fails, the rest don't have to)
        /*if (serverSocket != null && !serverSocket.isClosed())
            try {serverSocket.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when tryin to close ServerSocket: " + ex.getMessage());};
        if (client != null && !client.isClosed())
            try {client.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when trying to close client socket: " + ex.getMessage());};
        if (connectioninputStream != null)
            try {connectioninputStream.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when trying to close input stream: " + ex.getMessage());};
        if (connectionOutputStream != null)
            try {connectionOutputStream.close();} catch (IOException ex) {System.out.println("Error in endConnectionWithConnectedDevice when trying to close output stream: " + ex.getMessage());};*/
        synchronized (threadLock) {
            continueThreadExecution.set(false);
            threadLock.notify();
        }
    }


    // A function (that will be run in a seperate thread) that will handle network communications
    // INPUT: none
    // OUTPUT: none
    /*public void networkThread() {
        // Variables
        //boolean connectionSucessful = true;


        // Only proceed if we have valid input data
        if (connectedDeviceAddress == null || connectedDeviceAddress != "") {
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
                        this.connected.set(false);
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
                        this.connected.set(false);
                }
            } catch (IOException InteruptedEx) {
                System.out.println("Error in connectionSucessful: " + InteruptedEx.getMessage() + ". Connection Initialization Failed.");
                connected.set(false);
            }
        }
        // Will alert all threads waiting on this variable
        // INfor from here: https://stackoverflow.com/questions/37026/java-notify-vs-notifyall-all-over-again
        this.connected.notifyAll();

        while(this.connected.get() == true) {
            // Get from cleint
            // Send to client
        }
    }*/

    // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
    // https://stackoverflow.com/questions/15056978/tcp-server-on-android-phone-crashes-at-accept (referenced?)
    // A thread function that will handle all of the actual network communications
    // INPUT: none
    // OUTPUT: none
    void threadFunction() {
        boolean continueExec = false;
        int connectionAttemptLimit = 24;
        byte buffer[] = new byte[1024];

        try {
            // The group owner is the server, therefore if the connected device is not the group owner, this is the server.
            // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
            // Read some info from https://stackoverflow.com/questions/2983835/how-can-i-interrupt-a-serversocket-accept-method, but ended up removing the code associated with it. I decided to keep this here just in case.
            if (!isGroupOwner) {
                // Create the Server socket and wait on connections
                serverSocket = new ServerSocket(7432);

                // Set timeout
                // https://stackoverflow.com/questions/21934839/how-to-set-a-time-limit-for-serversocket-to-wait-for-the-client
                serverSocket.setSoTimeout(12000);

                // Accept connection from client
                client = serverSocket.accept();

                // Setup the input streams
                connectionInputStream = client.getInputStream();
                connectionOutputStream = client.getOutputStream();
                connected.set(true);
                continueThreadExecution.set(true);
                continueExec = true;
                setupThreadCompleted = true;
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
                //System.out.println("Connected Device Address: " + connectedDeviceAddress.toString());
                while (!stopTryingToConnect) {
                    try {
                        // Connect to the client
                        client.connect(new InetSocketAddress(connectedDeviceAddress.toString(), 7432), 1500);

                        // If this succeeds, we are done and set this variable to true to indicate that the while loop need not execute anymore
                        stopTryingToConnect = true;

                    } catch (IOException ex) {

                        // Increment our attempts (indicating that we have just tried again)
                        ++attempts;

                        // Print an error message
                        //System.out.println("Client attempted to connect to server... but it failed. Error: " + ex.getMessage());

                        // Wait a bit before trying to connect again
                        try {Thread.sleep(2000);} catch (InterruptedException intEx) {}

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

                // We weren't able to establish a connection...
                if (client == null)
                    return;

                // Setup the input and output streams (I think we can use one port for this?)
                connectionInputStream = client.getInputStream();
                connectionOutputStream = client.getOutputStream();
                connected.set(true);
                setupThreadCompleted = true;
                continueThreadExecution.set(true);
                continueExec = true;
            }

            // Exchange game setup information with the client
            if (!initiatedGame) {
                // Send player information (board size and player name)
                connectionOutputStream.write(initialGameInformation.getBytes());

                // Get name of player from other device
                connectionInputStream.read(buffer);

                // https://stackoverflow.com/questions/17354891/java-bytebuffer-to-string
                gameInfoFromClient = new String(buffer);
            }
            else {
                // Get player name and board size from other device
                connectionInputStream.read(buffer);
                gameInfoFromClient = new String(buffer);

                // Send player name to other device
                connectionOutputStream.write(initialGameInformation.getBytes());
            }

            // Lets the initialization thread know that thread execution has been completed (by sending a signal)
            synchronized (threadLock) {
                // Got some information from here: https://stackoverflow.com/questions/37026/java-notify-vs-notifyall-all-over-again
                threadLock.notify();
            }

            // Thread waiting: https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#wait()
            while(continueExec) {
                synchronized (threadLock) {
                    // Check if we want to continue thread execution
                    if (!(continueThreadExecution.get()))
                    {
                        continueExec = false;
                        continue;
                    }
                    // Wait for a signal (to let this thread know that something has been done)
                    else
                        try {threadLock.wait();} catch (InterruptedException ex) {}

                    // Check to see if data structure (indicating that we need to send a move to the client) is updated to indicate that we do
                    if (sendSignal.get()) {
                        // Notify waiting thread
                        //System.out.println("Send Signal Sent");

                        // Send data
                        connectionOutputStream.write(sendData);
                        //System.out.println("Send Signal successfully wrote data to stream");

                        // Reset variables
                        sendData = -1;
                        sendSignal.set(false);

                        // Debug
                        //System.out.println("Send Signal Set sendSignal to false");

                        // Notify waiting thread
                        threadLock.notifyAll();
                    }

                    // Receive signal
                    //https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
                    else if (receiveSignal.get()) {
                        // Read data off of the input buffer (a.k.a: get it from the client socket)
                        // The example simply shows you waiting until you get data... A timer was put in an an attempt to mitigate that
                        // https://stackoverflow.com/questions/38163938/return-value-of-assignment-operation-in-java
                        while ((receiveData = connectionInputStream.read()) == -1) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException intEx) {
                                Log.d(TAG, "Error in waiting for recieve signal: " + intEx.getMessage());
                            }
                        }
                        receiveSignal.set(false);
                        threadLock.notifyAll();
                    }
                }
            }

        } catch (IOException ioEx) {
            // Lets the initialization thread know we got here
            sendSignal.set(false);
            receiveSignal.set(false);
            connected.set(false);
            sendSignal.set(false);
            receiveSignal.set(false);
            gameInfoFromClient = null;

            synchronized (threadLock) {
                setupThreadCompleted = true;
                threadLock.notify();
            }

            // Log the error
            Log.d(TAG, "Error in network thread: " + ioEx.getMessage());
        }

        // Close sockets and I/O streams
        // I thought closing the individual sockets and streams would be better than putting all of them in one try catch (so if close() attempt fails, the rest don't have to)
        if (serverSocket != null && !serverSocket.isClosed())
            try {serverSocket.close();} catch (IOException ex) {};
        if (client != null && !client.isClosed())
            try {
                // https://stackoverflow.com/questions/43226607/application-crashes-after-i-closed-the-socket-waiting-for-reading-in-a-thread
                client.shutdownInput();
                client.shutdownOutput();
                client.close();
        } catch (IOException ex) {};
        if (connectionInputStream != null)
            try {connectionInputStream.close();} catch (IOException ex) {};
        if (connectionOutputStream != null)
            try {connectionOutputStream.close();} catch (IOException ex) {};

        // Update the connected value
        connected.set(false);
    }

    // Sends move data (contained in string) to other player
    // INPUT: data (string to send)
    // OUTPUT: none
    public boolean sendMoveToOtherPlayer(int data) {
        boolean sentSuccessfully = false;

        // This function can only execute if our thread function (controlling socket functionality) is running
        if (connected.get()) {
            // Prepare some variables and wait for the network thread to automatically update the variables
            sendData = data;
            sendSignal.set(true);
            synchronized (threadLock) {
                threadLock.notifyAll();
                System.out.println("Waiting on sendSignal...");
                try {threadLock.wait();} catch (InterruptedException ex) {}
                //try {continueThreadExecution.wait();} catch (InterruptedException ex) {};
            }

            // Note that the operation status
            if (sendData == -1)
                sentSuccessfully = true;
        } else {
            System.out.println("Error in MultiplayerSession.sendMoveToOtherPlayer: No connected devices. Can't send data!");
        }

        // Return result
        return sentSuccessfully;
    }

    // Attempts to get a move from another player (connected via a socket)
    // Referenced the example: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
    // INPUT: none
    // OUTPUT: the column number from the other player's turn (-1 if things didn't work)
    public int getMoveFromOtherPlayer() {
        // Temporary Variable
        int playerTurn = -1;

        // This function can only execute if our thread function (controlling socket functionality) is running
        if (connected.get()) {
            // Prep some variables and wait for the network thread to automatically update the variables
            receiveData = -1;
            receiveSignal.set(true);
            synchronized (threadLock) {
                threadLock.notifyAll();
                try {threadLock.wait();} catch (InterruptedException ex) {}
                //try {continueThreadExecution.wait();} catch (InterruptedException ex) {};
            }

            // Save the result once we get it
            playerTurn = receiveData;
        } else {
            Log.d(TAG, "Error in MultiplayerSession.getMoveFromOtherPlayer: No connected devices. Can't send data!");
        }

        // Return result
        return playerTurn;
    }
}
