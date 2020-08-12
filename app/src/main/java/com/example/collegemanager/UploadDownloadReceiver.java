package com.example.collegemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UploadDownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( intent.getAction().equals("com.example.collegemanager.UPLOAD_COMPLETE") ) {

            Intent databaseService = new Intent(context.getApplicationContext(), DatabaseHandler.class);
            databaseService.putExtra("message", "UploadComplete");
            databaseService.putExtra("studentid", intent.getIntExtra("studentid", 0) );
            databaseService.putExtra("assignmentid", intent.getIntExtra("assignmentid", 0));

            context.getApplicationContext().startService(databaseService);
        }
        else if ( intent.getAction().equals("com.example.collegemanager.DOWNLOAD_COMPLETE") ) {
            //
        }
    }
}
