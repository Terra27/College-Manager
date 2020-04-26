package com.example.collegemanager;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class NotificationChannelRegister extends IntentService {

    public String CHANNEL_ID = "NetworkChannelIdentifier";
    public CharSequence CHANNEL_NAME = "NetworkChannel";

    public NotificationChannelRegister() {

        super("NotificationChannelRegister");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Channel for network notifications.");

                // Register Notification Channel
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
