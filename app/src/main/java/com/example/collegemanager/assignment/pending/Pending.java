package com.example.collegemanager.assignment.pending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.collegemanager.R;
import com.example.collegemanager.DownloadStarter;
import com.example.collegemanager.UploadStarter;
import com.example.collegemanager.DatabaseHandler;
import com.example.collegemanager.DatabaseHandler.MessageInterface;

import android.Manifest;

import android.content.ComponentName;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Pending extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private int PICK_FILE_REQUEST_CODE = 23;

    //
    private int studentID = 2;

    // Binding Status
    private ArrayList<ArrayList<String>> currResult = null;
    private MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    // Global references to the ListView Adapter and ArrayList
    ArrayList<PendingItem> pendingItems;
    ItemAdapter itemAdapter;

    /*A way for the Service to send message to the Activity, not required when using RPC
    public class CallTaker implements ActivityMessenger {
        public void queryResult(String result) {

            currResult = result;
        }
    }
    */

    ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (MessageInterface)theBinder;
                boundService = binder.getServiceInstance();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if ( activityBinded ) {
                // Unexpected Disconnection
                abstractConnection = null;
                activityBinded = false;
            }
        }
    };

    private String professorName = null;
    protected class ClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        public void onItemClick(AdapterView parent, View clickedItem, int position, long id) {


            clickedItem.findViewById(R.id.itemPending).setBackgroundColor(getResources().getColor(R.color.cardClickColor));
            // If you define the run() method in this thread, then call it after a loop of x seconds, all the other
            // instructions scheduled on this thread also execute after x seconds, why is this happening?

            // Timer thread for reverting the color.
            new Thread ( ) {
                public void run( ) {
                    try {
                        Thread.sleep(150);
                        clickedItem.findViewById(R.id.itemPending).setBackgroundColor(getResources().getColor(R.color.cardIdleColor));
                    }
                    catch ( InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }.start();

            Intent intent = new Intent(getApplicationContext(), DownloadStarter.class);
            intent.putExtra("fileName", itemAdapter.getItem(position).pendingFileName);
            intent.putExtra("fileSize", itemAdapter.getItem(position).pendingFileSize);
            professorName = itemAdapter.getItem(position).pendingProfessor;

            startService(intent);

            /*
            if ( FILE_SIZE > 4*1024*1024 ) // We have the time to Bind.
                bindService(intent, abstractConnection, BIND_AUTO_CREATE);
            }

             */
        }

        public boolean onItemLongClick(AdapterView parent, View clickedItem, int position, long id) {

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

            Intent chooseFileWrapper = Intent.createChooser(chooseFile, "Choose a file");
            professorName = itemAdapter.getItem(position).pendingProfessor;

            startActivityForResult(chooseFileWrapper, PICK_FILE_REQUEST_CODE);

            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        // Create the Array and the Adapter that manages it.
        pendingItems = new ArrayList<PendingItem>();
        itemAdapter = new ItemAdapter(getApplicationContext(), pendingItems, new ClickListener(), this);

        // Assign created Adapter to ListView
        ListView pendingAList = (ListView) findViewById(R.id.pendingList);
        pendingAList.setAdapter(itemAdapter);

        // Bind to the Database Handler
        Intent databaseService = new Intent(getApplicationContext(), DatabaseHandler.class);
        bindService(databaseService, abstractConnection, BIND_AUTO_CREATE);
        fetchDatabaseResult();

        // Ask for External Storage Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void fetchDatabaseResult() {

        // Perform the RPC on another thread to avoid blocking the main thread
        new Thread() {
            public void run() {

                try {
                    // Sleep till the database result arrives
                    while (binder == null)
                        Thread.sleep(100);

                    currResult = boundService.selectQuery("SELECT A.assignmentid, facultyid, title, due, filesize, filetype, fileurl, classid FROM hassubmitted AS T, assignments AS A WHERE T.assignmentid = A. assignmentid AND T.studentid="+ studentID +" AND T.status=1");
                    //currResult = boundService.selectQuery("SELECT assignmentid, facultyid, title, due, filesize, filetype, fileurl, classid FROM assignments");

                    String[ ] faculty = {"Pawan Kumar Tiwari", "S. P Tripathi", "Ram Kumar", "Ayush Mehta", "Tihar Singh"};

                    // Add objects to the Array
                    int i = 0;
                    while ( i < currResult.size() ) {

                        int id = 0;
                        String title = "";
                        String due = "";
                        String facultyname = "";
                        int filetype = 0;
                        String FILE_NAME = "";
                        int CLASS_ID = 0;
                        int FILE_SIZE = 0; // bytes

                        int j = 0;
                        while ( j < currResult.get(i).size( ) ) {

                            switch (j) {
                                case 0:
                                    id = Integer.parseInt(currResult.get(i).get(j));
                                    break;
                                case 1:
                                    facultyname = faculty[Integer.parseInt(currResult.get(i).get(j)) - 1];
                                    break;
                                case 2:
                                    title = currResult.get(i).get(j);
                                    break;
                                case 3:
                                    due = currResult.get(i).get(j);
                                    break;
                                case 4:
                                    FILE_SIZE = Integer.parseInt(currResult.get(i).get(j));
                                    break;
                                case 5:
                                    filetype = Integer.parseInt(currResult.get(i).get(j));
                                    break;
                                case 6:
                                    FILE_NAME = currResult.get(i).get(j);
                                    break;
                                case 7:
                                    CLASS_ID = Integer.parseInt(currResult.get(i).get(j));
                                    break;
                                default:
                                    break;
                            }
                            j++;
                        }

                        // Convert to Kilobytes
                        if ( filetype == 2 )
                            FILE_SIZE = FILE_SIZE * 1024;

                        pendingItems.add(new PendingItem(id, R.drawable.subject, title, due, facultyname, FILE_SIZE, FILE_NAME, CLASS_ID));
                        i++;
                    }

                     // Only the Main Thread can touch Views and their adapters.
                    // Schedule on the main thread.
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            itemAdapter.notifyDataSetChanged();
                        }
                    });
                }
                catch (InterruptedException e) {
                    System.out.println("Database fetch thread interrupted.");
                }
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if ( activityBinded ) {
            unbindService(abstractConnection);
            activityBinded = false;
        }
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
                // Set URI
                Uri uri = data.getData();
                intent.setData(uri);
                intent.putExtra("fileName", getFileNameFromUri(uri));
                intent.putExtra("professorName", professorName);

                startService(intent);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
}
