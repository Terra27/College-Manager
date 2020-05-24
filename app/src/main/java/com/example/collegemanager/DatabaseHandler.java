package com.example.collegemanager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class DatabaseHandler extends Service {
    public DatabaseHandler() {
    }

    private static final String HOSTNAME = "192.168.1.7";
    private static final int PORT = 1602;
    private static final int TIMEOUT = 10*1000;

    // A way for the Service to communicate with the Activity, not required when using RPC.
    // ActivityMessenger activityMessenger;

    public class MessageInterface extends Binder {

        public DatabaseHandler getServiceInstance( ) {
            return DatabaseHandler.this;
        }

        /*
        public void setListener (ActivityMessenger messenger) {
            activityMessenger = messenger;
        }
         */
    }

    @Override
    public IBinder onBind(Intent intent) {
        // As soon as this method is returned, the bindSerivce() method returns and the service leaves the main thread
        // Hence, only binding is performed on the main thread.
        return new MessageInterface();
    }

    private void showToast(final String toastText ) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
            }
        });

    }

    // All these methods are called via RPC on manually created threads, to avoid blocking the main thread
    public ArrayList<ArrayList<String>> executeQuery (String query, int queryType) {

        String resultString = null;
        ArrayList<ArrayList<String>> resultTable = new ArrayList<ArrayList<String>>();
        try {

            // connection() method is used instead of the Socket() constructor to allow timeout.
            InetAddress inetAddress = InetAddress.getByName(HOSTNAME);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, PORT);

            Socket clientSocket = new Socket();
            clientSocket.connect(socketAddress, TIMEOUT);

            if (clientSocket != null) {
                System.out.println("Connection Established.");

                PrintWriter queryWriter = new PrintWriter(clientSocket.getOutputStream());
                BufferedReader resultReader = new BufferedReader(new InputStreamReader((clientSocket.getInputStream())));

                queryWriter.println(queryType); // SELECT or UPDATE
                queryWriter.flush();

                queryWriter.println(query);
                queryWriter.flush();

                // Socket I/O is blocking I/O, the thread blocks on this statement till some readable
                // data appears in the socket's input buffer, so this is safe and synchronized.
                resultString = resultReader.readLine();

                //activityMessenger.queryResult(result);

                resultReader.close();
                queryWriter.close();
                clientSocket.close();
            }
        }
        catch( SocketTimeoutException e )
        {
            showToast("Connection Timed out.");
            return null; // Null implies connection error, empty resultTable implies empty result.
        }
        catch ( IOException e ) {
            System.out.println(e.getMessage());
        }

        // Extract data from the string and create a 2D ArrayList
        if ( resultString != null && resultString.length() > 0 ) {

            int i = 1;
            char lastChar = resultString.charAt(0);
            int lastSemicolonIndex = -1;

            ArrayList<String> currentRow = new ArrayList<String>();
            while ( i < resultString.length() ) {
                if ( resultString.charAt(i) == ';') {
                    if ( lastChar != ';' ) { // Still in the same row
                        currentRow.add(resultString.substring(lastSemicolonIndex + 1, i));
                    }
                    else { // row break
                        resultTable.add(currentRow);
                        currentRow = new ArrayList<String>();
                    }

                    lastSemicolonIndex = i;
                }
                lastChar = resultString.charAt(i);
                i++;
            }

            if ( currentRow.size() > 0 )
                resultTable.add(currentRow);
        }

        return resultTable;
    }
}
