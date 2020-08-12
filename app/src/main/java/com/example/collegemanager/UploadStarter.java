package com.example.collegemanager;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class UploadStarter extends IntentService {

    private String HOSTNAME = "192.168.1.7";
    private int PORT = 1601;
    private int TIMEOUT = 10*1000;

    private int BUFFER_SIZE = 8*1024;

    private String CHANNEL_ID = "NetworkChannelIdentifier";
    private int notificationID = 12365;
    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;

    private int PROGRESS_MAX = 0;
    private int PROGRESS_CURR = 0;

    public UploadStarter() {
        super("UploadStarter");
    }

    private void showToast(final String toastText ) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
            }
        });

    }

    private Intent globalIntent;
    @Override
    protected void onHandleIntent(Intent data) {
        if (data != null) {

            globalIntent = data;

            System.out.println("Upload service started.");
            notification = createUploadNotification(R.drawable.notify, "Uploading File..", "Your Assignment is being uploaded.");
            startForeground(notificationID, notification);

            Uri FileUri = data.getData();
            uploadHandler(FileUri, data.getStringExtra("fileName"), data.getStringExtra("professorName"));
        }
    }

    private void uploadHandler(Uri FileUri, String FILE_NAME, String professorName) {
        if ( FileUri != null ) {
            InputStream byteStream = null;
            try {
                byteStream = getApplicationContext().getContentResolver().openInputStream(FileUri);
                PROGRESS_MAX = byteStream.available();
                if (byteStream != null) {

                    showToast("Uploading File..");
                    // connection() method is used instead of the Socket() constructor to allow timeout.
                    InetAddress inetAddress = InetAddress.getByName(HOSTNAME);
                    SocketAddress socketAddress = new InetSocketAddress(inetAddress, PORT);

                    Socket clientSocket = new Socket();
                    clientSocket.connect(socketAddress, TIMEOUT);

                    if (clientSocket != null) {
                        System.out.println("Connection Established!");

                        // Send file name
                        PrintWriter writer = new PrintWriter( clientSocket.getOutputStream() );
                        writer.println(professorName +";"+ FILE_NAME);
                        writer.flush();

                        //Send File
                        BufferedInputStream bufferedFileInput = new BufferedInputStream( byteStream, BUFFER_SIZE ); // default 2KB
                        BufferedOutputStream bufferedOutput = new BufferedOutputStream( clientSocket.getOutputStream(), BUFFER_SIZE ); // default 512 bytes

                        System.out.println("Copying to connection stream..");
                        // int byteRead = fileStream.read(); // reading 1 byte from BufferedInputStream's buffer. (inefficient)
                        byte[ ] buffer = new byte[BUFFER_SIZE];
                        int bytesRead = bufferedFileInput.read(buffer); // *read 4KB from the BufferedInputStream's buffer. (The internal buffer is refilled as soon as it is completely read)

                        while ( bytesRead > 0 ) {
                            // bufferedOutput.write(byteRead) // writing 1 byte to BufferedOutputStream's buffer.
                            bufferedOutput.write(buffer, 0, bytesRead); // *writing 4KB in one go to BufferedOutputStream's buffer.
                            // The internal buffer of BufferedOutputStream only writes to the underlying OutputStream when the buffer is full.
                            // Flushing flushes the internal buffer data to the underlying OutputStream forcefully.
                            bufferedOutput.flush(); // Otherwise synchronization problems possible?


                            PROGRESS_CURR = PROGRESS_CURR + bytesRead;
                            if ( PROGRESS_CURR <= PROGRESS_MAX ) {
                                notificationBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURR, false);
                                startForeground(notificationID, notificationBuilder.build());
                            }

                            bytesRead = bufferedFileInput.read(buffer);
                        }

                        showToast("Your assignment was successfully uploaded.");
                        bufferedFileInput.close();

                        // Send a broadcast
                        Intent uploadBroadcast = new Intent();
                        uploadBroadcast.setAction("com.example.collegemanager.UPLOAD_COMPLETE");
                        uploadBroadcast.putExtra("studentid", globalIntent.getIntExtra("studentid", 0) );
                        uploadBroadcast.putExtra("assignmentid", globalIntent.getIntExtra("assignmentid", 0));

                        sendBroadcast(uploadBroadcast);

                        if ( clientSocket != null ) {
                            clientSocket.close();
                        }
                    }
                }
            }
            catch (SocketTimeoutException e) {
                showToast("Connection Timed out.");
            }
            catch (FileNotFoundException e) {
                showToast("File not found.");
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private Notification createUploadNotification(int icon, String title, String text) {

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
