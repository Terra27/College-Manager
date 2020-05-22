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
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class Pending extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private int PICK_FILE_REQUEST_CODE = 23;
    private int CREATE_FILE_REQUEST_CODE = 27;

    // Binding Status
    private ArrayList<ArrayList<String>> currResult = null;
    private MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    // Global references to the ListView Adapter and ArrayList
    ArrayList<PendingItem> pendingItems;
    ItemAdapter itemAdapter;

    // A Handler for posting instructions to the main thread
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private boolean loading = false;
    private ImageView loader;
    private ImageView alert;

    /*A way for the Service to send message to the Activity, not required when using RPC
    public class CallTaker implements ActivityMessenger {
        public void queryResult(String result) {

            currResult = result;
        }
    }
    */

    private ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (MessageInterface)theBinder;
                boundService = binder.getServiceInstance();

                // Fetch Database result as soon as binded
                fetchDatabaseResult(Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id"))));
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
    private int latestClickedPosition = 0;
    private class ClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        public void onItemClick(AdapterView parent, View clickedItem, int position, long id) {

            latestClickedPosition = position;

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");

            startActivityForResult(Intent.createChooser( intent, "Where do you wish to save your assignment?" ), CREATE_FILE_REQUEST_CODE);
        }

        public boolean onItemLongClick(AdapterView parent, View clickedItem, int position, long id) {

            latestClickedPosition = position;
            
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

            Intent chooseFileWrapper = Intent.createChooser(chooseFile, "Where is your assignment located?");
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
        itemAdapter = new ItemAdapter(getApplicationContext(), pendingItems);

        // Assign created Adapter to ListView
        ListView pendingAList = (ListView) findViewById(R.id.pendingList);
        pendingAList.setAdapter(itemAdapter);

        // File Upload
        pendingAList.setOnItemLongClickListener(new ClickListener());

        // File Download
        pendingAList.setOnItemClickListener(new ClickListener());

        // Bind to the Database Handler
        Intent databaseService = new Intent(getApplicationContext(), DatabaseHandler.class);
        bindService(databaseService, abstractConnection, BIND_AUTO_CREATE);

        // Ask for External Storage Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void fetchDatabaseResult(int studentID) {

        loader = findViewById(R.id.loader);
        alert = findViewById(R.id.alert);

        // Perform the RPC on another thread to avoid blocking the main thread
        new Thread() {
            public void run() {

                // setLoader();
                postToMain(1);
                currResult = boundService.selectQuery("SELECT A.assignmentid, facultyid, title, due, filesize, filetype, fileurl, classid, branch FROM hassubmitted AS T, assignments AS A WHERE T.assignmentid = A. assignmentid AND T.studentid="+ studentID +" AND T.status=1");
                if (currResult != null) {

                    // removeLoader();
                    postToMain(2, false);

                    String[] faculty = {"Pawan Kumar Tiwari", "S. P. Tripathi", "Ram Kumar", "Ayush Mehta", "Tihar Singh"};
                    int[] branches = {R.drawable.computers, R.drawable.electronics, R.drawable.electrical, R.drawable.civil, R.drawable.mechanical, R.drawable.chemical, R.drawable.science};
                    // Add objects to the Array
                    int i = 0;
                    while (i < currResult.size()) {
                        int id = Integer.parseInt(currResult.get(i).get(0));
                        String facultyname = faculty[Integer.parseInt(currResult.get(i).get(1)) - 1];
                        String title = currResult.get(i).get(2);
                        String due = currResult.get(i).get(3);
                        int FILE_SIZE = Integer.parseInt(currResult.get(i).get(4)); // bytes
                        int filetype = Integer.parseInt(currResult.get(i).get(5));
                        String FILE_NAME = currResult.get(i).get(6);
                        int CLASS_ID = Integer.parseInt(currResult.get(i).get(7));
                        int icon = Integer.parseInt(currResult.get(i).get(8));

                        // Convert Kilobytes to Bytes
                        if (filetype == 2)
                            FILE_SIZE = FILE_SIZE * 1024;

                        pendingItems.add(new PendingItem(id, branches[icon - 1], title, due, facultyname, FILE_SIZE, FILE_NAME, CLASS_ID));
                        i++;
                    }

                    // Only the Main Thread can touch Views and their adapters.
                    // Schedule on the main thread.
                    // notifyDataSetChange()
                    postToMain(0);
                }
                else
                {
                    // removeLoader(true);
                    postToMain(2, true);
                }
            }
        }.start();
    }

    // Schedule these instructions on the main thread using the Looper
    private void postToMain(int instructionID, boolean showAlert) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (instructionID) {
                    case 2:
                        removeLoader(showAlert);
                }
            }
        });
    }

    private void postToMain(int instructionID) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (instructionID) {
                    case 0:
                        itemAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        setLoader();
                }
            }
        });
    }

    private void setLoader() {

        new Thread() {
            public void run() {
                try {
                    // Creating loading image..
                    loading = true;

                    float degrees = 0;
                    while (loading) {
                        loader.setRotation(degrees);
                        degrees = (degrees + 5) % 360;

                        Thread.sleep(50);
                    }

                }
                catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }.start();
    }

    // Kill the thread showing the loader followed by a choice of alert
    private void removeLoader(boolean showAlert) {
        loading = false;

        if ( showAlert) {
            alert.setVisibility(View.VISIBLE);
            findViewById(R.id.networkErrorText).setVisibility(View.VISIBLE);
        }
        else {
            alert.setVisibility(View.GONE);
            findViewById(R.id.networkErrorText).setVisibility(View.GONE);
        }

        loader.setVisibility(View.GONE);
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
                                                                    // Not the intent that was passed to startActivityForResult.
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
        else if ( requestCode == CREATE_FILE_REQUEST_CODE ) {
            if ( resultCode == RESULT_OK ) {

                Intent intent = new Intent(getApplicationContext(), DownloadStarter.class);
                // Set URI
                Uri uri = data.getData();
                intent.setData(uri);

                int position = latestClickedPosition;
                intent.putExtra("fileName", itemAdapter.getItem(position).pendingFileName);
                intent.putExtra("fileSize", itemAdapter.getItem(position).pendingFileSize);

                startService(intent);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;

        // File is managed by a Content Provider
        if (uri.getScheme().equals("content")) {
            // Query the content provider to get the value of the COLUMN_DISPLAY_NAME column
            Cursor cursor = getContentResolver().query(uri, new String[] { DocumentsContract.Document.COLUMN_DISPLAY_NAME }, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }

        // File not managed by a Content provider
        if (result == null) {
            result = uri.getPath();
            // The MIME type included at the end of the path includes file extension after the last occurrence of /
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
}
