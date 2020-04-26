package com.example.collegemanager.assignment.pending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.collegemanager.DownloadStarter;
import com.example.collegemanager.NotificationChannelRegister;
import com.example.collegemanager.R;
import com.example.collegemanager.UploadStarter;

import android.Manifest;

import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/*
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
*/

import java.util.ArrayList;

public class Pending extends AppCompatActivity { //implements ActivityMessenger {

    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private int PICK_FILE_REQUEST_CODE = 23;

    private String FILE_NAME = "assignment.txt";
    private int FILE_SIZE = (int)(7.6*1024*1024); // megabytes

    /*
    A way for the Activity and Service to communicate, not required.
    private boolean activityBinded = false;

    public void connectionOver( ) {
        FILE_DOWNLOADING = false;
    }

    ServiceConnection abstractConnection = new ServiceConnection() {
         @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            if ( !activityBinded ) {
                activityBinded = true;

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if ( activityBinded ) {
                activityBinded = false;
            }
        }
    };

     */

    protected class ClickListener implements View.OnClickListener, View.OnLongClickListener {

        public void onClick(final View clickedItem) {


            clickedItem.setBackgroundColor(getResources().getColor(R.color.cardClickColor));
            // If you define the run() method in this thread, then call it after a loop of x seconds, all the other
            // instructions scheduled on this thread also execute after x seconds, why is this happening?

            // Timer thread for reverting the color.
            new Thread ( ) {
                public void run( ) {
                    try {
                        Thread.sleep(150);
                        clickedItem.setBackgroundColor(getResources().getColor(R.color.cardIdleColor));
                    }
                    catch ( InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }.start();

            Intent intent = new Intent(getApplicationContext(), DownloadStarter.class);
            intent.putExtra("fileName", FILE_NAME);
            intent.putExtra("fileSize", FILE_SIZE);

            startService(intent);

            /*
            if ( FILE_SIZE > 4*1024*1024 ) // We have the time to Bind.
                bindService(intent, abstractConnection, BIND_AUTO_CREATE);
            }

             */
        }

        public boolean onLongClick(View clickedItem) {

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            Intent chooseFileWrapper = Intent.createChooser(chooseFile, "Choose a file");

            startActivityForResult(chooseFileWrapper, PICK_FILE_REQUEST_CODE);

            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        // Create the Array and the Adapter that manages it.
        ArrayList<PendingItem> pendingItems = new ArrayList<PendingItem>();
        ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext(), pendingItems, new ClickListener());

        // Assign created Adapter to ListView
        ListView pendingAList = (ListView)findViewById(R.id.pendingList);
        pendingAList.setAdapter(itemAdapter);

        // Add objects to the Array
        itemAdapter.add(new PendingItem(1, R.drawable.subject, "Assignment on Deep Learning", "25-04-2020", "Pawan Kumar Tiwari"));
        itemAdapter.add(new PendingItem(2, R.drawable.subject, "Assignment on Neural Networks", "26-04-2020", "Y. N. Singh"));
        itemAdapter.add(new PendingItem(3, R.drawable.subject, "Assignment on Database Management", "27-04-2020", "S. P. Tripathi"));
        itemAdapter.add(new PendingItem(4, R.drawable.subject, "Assignment on Operating System", "28-04-2020", "D. S. Yadav"));
        itemAdapter.add(new PendingItem(5, R.drawable.subject, "Assignment on Engineering Mathematics", "29-04-2020", "Ram Kumar"));

        // Ask for External Storage Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    // TODO: Add explanation when user declines permission.
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if ( requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE ) {
            if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED ) { // Permission Denied.
                Toast.makeText(this, "You cannot download your assignments without granting permission.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Intent intent = new Intent(getApplicationContext(), UploadStarter.class);
                intent.setData(data.getData());

                startService(intent);
            }
        }
    }
}
