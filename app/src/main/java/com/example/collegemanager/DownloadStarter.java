package com.example.collegemanager;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.collegemanager.assignment.pending.ActivityMessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class DownloadStarter extends IntentService {

    private static final String HOSTNAME = "192.168.1.7";
    private static final int PORT = 1600;
    private static final int TIMEOUT = 10*1000; // seconds

    private static final int BUFFER_SIZE = 8*1024; // bytes

    // Method getExternalStoragePublicDirectory has become deprecated with API 29, so this might not work with future android versions.
    //private static final File FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    // Hence, we will use the ACTION_CREATE_DOCUMENT Intent to allow the user to specify a file path
    private Uri FILE_PATH = null;

    // Provided by Database
    private String FILE_NAME;

    private String CHANNEL_ID = "NetworkChannelIdentifier";
    private int notificationID = 12365;
    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;

    // Download Progress
    private int PROGRESS_MAX = 0;
    private int PROGRESS_CURR = 0;

    //private ActivityMessenger activityMessenger;

    public DownloadStarter() {

        super("DownloadHandler");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            FILE_NAME = intent.getStringExtra("fileName");
            PROGRESS_MAX =  intent.getIntExtra("fileSize", 0);
            FILE_PATH = intent.getData();

            System.out.println(FILE_NAME + " "+ PROGRESS_MAX/1024);

            notification = createDownloadNotification(R.drawable.notify, "Downloading File..", "Your Assignment is being downloaded.");
            startForeground(notificationID, notification);

            // The IntentService executes on a different thread from the
            // main thread of the application, so safe to perform blocking
            // operations here.
            downloadHandler();
        }
    }

    private void showToast(final String toastText ) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void downloadHandler( ) {

        showToast("Downloading File.. ");

        try {
            // connection() method is used instead of the Socket() constructor to allow timeout.
            InetAddress inetAddress = InetAddress.getByName(HOSTNAME);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, PORT);

            // First send the File name
            Socket clientSocket = new Socket();
            clientSocket.connect(socketAddress, TIMEOUT); // Blocks for TIMEOUT seconds.
            if ( clientSocket != null ) {

                System.out.println("Connection Established.");

                PrintWriter writer = new PrintWriter( clientSocket.getOutputStream() );
                writer.println(FILE_NAME);
                writer.flush();

                /* Don't close this connection, otherwise if multiple clients are connecting you will be sent to
                // the back of the server connection accept queue when you create another request.

                dataSocket.close();

                // Now download the file
                Socket clientSocket = new Socket();
                clientSocket.connect(socketAddress, TIMEOUT);

                if (clientSocket != null) {
                    System.out.println("Connection Established.");
                 */
                BufferedInputStream bufferedInput = new BufferedInputStream(clientSocket.getInputStream(), BUFFER_SIZE);

                System.out.println("Creating file " + FILE_NAME + " on client at " + FILE_PATH + ".");
                //File outputFile = new File(FILE_PATH, FILE_NAME);
                BufferedOutputStream bufferedFileOutput = new BufferedOutputStream( getContentResolver().openOutputStream(FILE_PATH), BUFFER_SIZE);

                System.out.println("Copying file from server..");

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = bufferedInput.read(buffer);

                while (bytesRead > 0) {
                    // Third parameter important, otherwise unusable bytes written to stream resulting in file corruption.
                    bufferedFileOutput.write(buffer, 0, bytesRead); // BufferedOutputStream only writes to underlying OutputStream when internal buffer full.

                    // Update Progress bar
                    PROGRESS_CURR = PROGRESS_CURR + bytesRead;
                    if (PROGRESS_CURR <= PROGRESS_MAX) {
                        notificationBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURR, false);
                        startForeground(notificationID, notificationBuilder.build());
                    }

                    bytesRead = bufferedInput.read(buffer);
                }

                System.out.println("File Copied!");
                bufferedFileOutput.flush();
                bufferedFileOutput.close();
                // Closing the stream earlier also closes the underlying socket connection
                // writer.close(); // Close the character stream, no longer needed

                System.out.println("Connection Closed.");
                //activityMessenger.connectionOver();

                if (clientSocket != null)
                    clientSocket.close();
                //}
            }
        }
        catch ( SocketTimeoutException e) {
            showToast("Connection Timed out.");
            //activityMessenger.connectionOver();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Notification createDownloadNotification(int icon, String title, String text) {

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setSmallIcon(icon);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(text);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder.setAutoCancel(false);

        notificationBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURR, false);

        return notificationBuilder.build();
    }
}
