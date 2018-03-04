package com.example.hemanthlam.connectfour;

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
    // Server Socket and client
    //private ServerSocket serverSocket = null;
    //private Socket client = null;

    // Connection Things (for date exchange with client)
    // Will be used to send and receive data
    //private OutputStream connectionOutputStream = null;
    //private InputStream connectioninputStream = null;

    // Needed to setup sockets
    private String connectedDeviceAddress = "";
    private boolean connectedDeviceIsGroupOwner = false;

    // Atomic Ints: http://winterbe.com/posts/2015/05/22/java8-concurrency-tutorial-atomic-concurrent-map-examples/
    private AtomicBoolean sendSignal = new AtomicBoolean(false);
    private AtomicBoolean receiveSignal = new AtomicBoolean(false);
    private AtomicBoolean connected = new AtomicBoolean(false);
    private AtomicBoolean continueThreadExecution = new AtomicBoolean(false);

    //private final Object continueThreadExecutinoLock = new Object();
    //private final Object sendSignalLock = new Object();
    //private final Object getSendSignalLock = new Object();
    private AtomicBoolean setupThreadCompleted = new AtomicBoolean(false);
    private AtomicBoolean setupThreadSignalRecieved = new AtomicBoolean(false);

    // Will be used for transferring data
    private int sendData = -1;
    private int receiveData = -1;

    // COnnection Attempt Limit
    private final int connectionAttemptLimit = 8;

    // For receiving data
    //private byte buffer[] = new byte[1024];

    // Initiate socket connection
    // INPUT: none
    // OUTPUT: true if the connection succeeded, false otherwise
    public boolean initiateConnectionWithConnectedDevice(String connectedDeviceAddress, boolean connectedDeviceIsGroupOwner) {
        // Variables
        boolean connectionSucessful = true;
        String deviceAddress = null;

        // Checking the address
        if (connectedDeviceAddress != null && connectedDeviceAddress != "")
            deviceAddress = connectedDeviceAddress.substring(1);

        // Printout out the address
        System.out.println("Connected Device Address: " + deviceAddress);

        // Only proceed if we have valid input data
        if (deviceAddress == null || connectedDeviceAddress != "") {
            // Assign data
            this.connectedDeviceAddress = deviceAddress;
            this.connectedDeviceIsGroupOwner = connectedDeviceIsGroupOwner;

            // https://developer.android.com/guide/components/processes-and-threads.html (used a bit of information, mostly the new Thread(Runnable) syntax and noted an example)
            // https://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html (a little dated?)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadFunction();
                }
            }).start();

            // We can't exit this loop until the network thread finishes setting itself up
            // This probably isn't the best way to do this, but unpredictable scheduling makes this more difficult then it should be
            while (!(setupThreadCompleted.get()))
                try { Thread.sleep(500); System.out.println("Waiting for setup to complete...");} catch (InterruptedException ex) {}

            //synchronized (setupThreadCompleted) {
            //    try {setupThreadCompleted.wait();} catch (InterruptedException ex) {}
            //    setupThreadSignalRecieved.set(true);
            //}

            //synchronized (continueThreadExecution) {
            //    try {continueThreadExecution.wait();} catch (InterruptedException ex) {}
            //}
            /*try {
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
            }*/
        }

        System.out.println("Initial Thread COnnection Value: " + connected.get());

        // Return success status
        //this.connected = connectionSucessful;
        return connected.get();
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
        synchronized (continueThreadExecution) {
            continueThreadExecution.set(false);
            continueThreadExecution.notifyAll();
        }
    }

    // https://stackoverflow.com/questions/15056978/tcp-server-on-android-phone-crashes-at-accept
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

    //https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
    // A thread function that will handle all of the actual network communications
    // INPUT: none
    // OUTPUT: none
    void threadFunction() {
        // Will be used to send and receive data
        OutputStream connectionOutputStream = null;
        InputStream connectioninputStream = null;

        // For receiving data
        //byte buffer[] = new byte[1024];
        int port = 8020;

        // Server Socket and client
        ServerSocket serverSocket = null;
        Socket client = null;

        try {
            // The group owner is the server, therefore if the connected device is not the group owner, this is the server.
            // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
            if (!connectedDeviceIsGroupOwner)
            {
                System.out.println("Server Setup Step 1");
                // 8080 will be the send channel
                serverSocket = new ServerSocket(8020);
                client = serverSocket.accept();
                System.out.println("Server Setup Step 2");

                // Setup the input streams
                connectioninputStream = client.getInputStream();
                connectionOutputStream = client.getOutputStream();
                connected.set(true);
                continueThreadExecution.set(true);
                setupThreadCompleted.set(true);
                System.out.println("Server connection to itself suceeded");
            }
            // Otherwise, this is the client
            // https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
            else {
                // Initiate connection to server
                System.out.println("Client Setup Step 1");
                client = new Socket();
                client.bind(null);

                // Int connection attempt setup
                boolean stopTryingToConnect = false;
                int attempts = 0;

                // Keep trying to connect until it suceeds or until you hit the connection attempt theshold
                // This might be needed for hosts with slow load times
                while (!stopTryingToConnect) {
                    try {
                        client.connect(new InetSocketAddress(connectedDeviceAddress, 8020), 1000);
                        stopTryingToConnect = true;
                    } catch (IOException ex) {
                        ++attempts;
                        try {Thread.sleep(2000);} catch (InterruptedException intEx) {}
                        if (attempts > connectionAttemptLimit)
                            stopTryingToConnect = true;
                    }
                }
                System.out.println("Client Setup Step 2");

                // Setup the input and output streams (I think we can use one port for this?)
                connectioninputStream = client.getInputStream();
                connectionOutputStream = client.getOutputStream();
                connected.set(true);
                setupThreadCompleted.set(true);
                continueThreadExecution.set(true);
                System.out.println("Client connection to server suceeded");
            }

            // Lets the initialization thread know we got here
            setupThreadCompleted.set(true);

            // Thread waiting: https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#wait()
            while(continueThreadExecution.get()) {
                // Wait for a signal (to let this thread know that something has been done)
                try
                {
                    // Notifies the setup thread that we are done setting up. Because of timing issues, I had to move this here to execute every time
                    //if (!(setupThreadSignalRecieved.get())) {
                    //    synchronized (setupThreadCompleted) {
                    //        continueThreadExecution.notifyAll();
                    //    }
                    //}


                    // Notifies the setup thread that we are done setting up. Because of timing issues, I had to move this here to execute every time
                    synchronized(continueThreadExecution)
                    {
                        continueThreadExecution.wait();
                    }

                    //synchronized(continueThreadExecution)
                    //{
                    //    continueThreadExecution.wait();
                    //}
                }
                catch (InterruptedException ex) {}

                // Check to see if data structure (indicating that we need to send a move to the client) is updated to indicate that we do
                if (sendSignal.get()) {
                    // Send data
                    connectionOutputStream.write(sendData);

                    // Reset variables
                    sendData = -1;
                    sendSignal.set(false);

                    // Notifies the setup thread that we are done setting up. Because of timing issues, I had to move this here to execute every time
                    /*synchronized(continueThreadExecution)
                    {
                        continueThreadExecution.notifyAll();
                    }*/
                }

                // Receive signal
                //https://developer.android.com/guide/topics/connectivity/wifip2p.html#connecting
                else if (receiveSignal.get()) {
                    // Read data off of the input buffer (a.k.a: get it from the client socket)
                    // The example simply shows you waiting until you get data... A timer was put in an an attempt to mitigate that
                    // https://stackoverflow.com/questions/38163938/return-value-of-assignment-operation-in-java

                    while ((receiveData = connectioninputStream.read()) == -1);
                    receiveSignal.set(false);
                        //try {Thread.sleep(500);} catch(InterruptedException ex) {}

                    // Copy data to the move data string
                    // https://stackoverflow.com/questions/17354891/java-bytebuffer-to-string
                    //this.receiveData = -1;

                    // Update the appropriate signal variables
                    //receiveSignal.set(false);

                    // Notifies the setup thread that we are done setting up. Because of timing issues, I had to move this here to execute every time
                    //synchronized(continueThreadExecution)
                    /*{
                        continueThreadExecution.notifyAll();
                    }*/
                }
            }

        } catch (IOException ex) {
            // Lets the initialization thread know we got here
            setupThreadCompleted.set(true);
            sendSignal.set(false);
            receiveSignal.set(false);

            // Some things to occur upon connection failure
            System.out.println("Unable to create server socket");
            System.out.println("Eror: " + ex.getMessage());
            connected.set(false);
        }

        // Close sockets and I/O streams
        // I thought closing the individual sockets and streams would be better than putting all of them in one try catch (so if close() attempt fails, the rest don't have to)
        if (serverSocket != null && !serverSocket.isClosed())
            try {serverSocket.close();} catch (IOException ex) {};
        if (client != null && !client.isClosed())
            try {client.close();} catch (IOException ex) {};
        if (connectioninputStream != null)
            try {connectioninputStream.close();} catch (IOException ex) {};
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
            synchronized (continueThreadExecution) {
                continueThreadExecution.notifyAll();
                //try {continueThreadExecution.wait();} catch (InterruptedException ex) {};
            }

            // Keep spinning unitl the send signal command has been completed
            while (sendSignal.get());

            // Note that the operation status
            if (sendData == -1)
                sentSuccessfully = true;

            // Send string
            /*try {
                //this.connectionOutputStream.write(data.getBytes());
                this.connectionOutputStream.write(data);
                sentSuccessfully = true;
            } catch (IOException ex) {
                System.out.println("Error in MultiplayerSession.sendMoveToOtherPlayer: " + ex.getMessage());
            }*/

            //this.sendSignal.set(false);
            //this.sendSignal.notifyAll(); // Notifies all threads waiting for the notification on this variable

            //try {sendSignal.wait();} catch (InterruptedException ex) {}
        } else {
            System.out.println("Error in MultiplayerSession.sendMoveToOtherPlayer: No connected devices. Can't send data!");
        }

        // Return result
        return sentSuccessfully;
    }

    // Attempts to get a move from another player (connected via a socket)
    // INPUT: none
    // OUTPUT: the column number from the other player's turn (-1 if things didn't work)
    public int getMoveFromOtherPlayer() {
        // Temporary Variable
        int playerTurn = -1;

        // This function can only execute if our thread function (controlling socket functionality) is running
        if (this.connected.get()) {
            // Prep some variables and wait for the network thread to automatically update the variables
            receiveData = -1;
            receiveSignal.set(true);
            synchronized (continueThreadExecution) {
                continueThreadExecution.notifyAll();
                //try {continueThreadExecution.wait();} catch (InterruptedException ex) {};
            }

            // Keep spinning until we get the signal indicating that data has been recieved the data over
            while(this.receiveSignal.get())

            // Save the result once we get it
            playerTurn = receiveData;

            // Read data off of the input buffer (a.k.a: get it from the client socket)
            // The example: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
            // simply shows you waiting until you get data... A timer was put in an an attempt to mitigate that
            /*try {
                while ((playerTurn = this.connectioninputStream.read()) != -1) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println("Error in MultiplayerSession.getMoveFromOtherPlayer: " + ex.getMessage());
                    }
                }
            } catch(IOException ex) {
                System.out.println("Error in MultiplayerSession.getMoveFromOtherPlayer: " + ex.getMessage());
            }*/

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
}
