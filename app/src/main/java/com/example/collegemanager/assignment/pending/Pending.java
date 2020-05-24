package com.example.collegemanager.assignment.pending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.collegemanager.R;
import com.example.collegemanager.DownloadStarter;
import com.example.collegemanager.UploadStarter;
import com.example.collegemanager.DatabaseHandler;
import com.example.collegemanager.DatabaseHandler.MessageInterface;
import com.example.collegemanager.assignment.Assignment;
import com.example.collegemanager.assignment.AssignmentAdapter;
import com.example.collegemanager.assignment.AssignmentItem;
import com.example.collegemanager.assignment.submitted.Submitted;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.example.collegemanager.Helper.getFileNameFromUri;

public class Pending extends AppCompatActivity {

    private int PICK_FILE_REQUEST_CODE = 23;
    private int CREATE_FILE_REQUEST_CODE = 27;

    // Binding Status
    private ArrayList<ArrayList<String>> currResult = null;
    private MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    // Global references to the ListView Adapter and ArrayList
    ArrayList<AssignmentItem> pendingItems;
    AssignmentAdapter itemAdapter;

    // A Handler for posting instructions to the main thread
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private boolean loading = false;
    private float degrees = 0;
    private ImageView loader;
    private ImageView alert;
    private TextView errorText;
    private ImageView info;
    private TextView emptyText;

    private HandlerThread rpcThread;
    private Handler rpcHandler;

    private PendingUploadReceiver uploadReceiver;
    /*A way for the Service to send message to the Activity, not required when using RPC
    public class CallTaker implements ActivityMessenger {
        public void queryResult(String result) {

            currResult = result;
        }
    }
    */

    /* NESTED CLASSES */
    private ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (MessageInterface)theBinder;
                boundService = binder.getServiceInstance();

                // Blocking RPC calls to Service methods must be performed in separate thread
                if ( ( rpcThread == null ) ) {
                    rpcThread = new HandlerThread("PendingRPCThread");
                    rpcThread.start();

                    rpcHandler = new Handler(rpcThread.getLooper());
                }

                // Fetch Database result as soon as binded
                fetchPendingAssignments(Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id"))));
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
    private AssignmentItem lastClickedAssignment;
    private class ClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        public void onItemClick(AdapterView parent, View clickedItem, int position, long id) {

            latestClickedPosition = position;
            lastClickedAssignment = itemAdapter.getItem(position);

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");

            startActivityForResult(Intent.createChooser( intent, "Where do you wish to save your assignment?" ), CREATE_FILE_REQUEST_CODE);
        }

        public boolean onItemLongClick(AdapterView parent, View clickedItem, int position, long id) {

            latestClickedPosition = position;
            lastClickedAssignment = itemAdapter.getItem(position);
            
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

            Intent chooseFileWrapper = Intent.createChooser(chooseFile, "Where is your assignment located?");
            professorName = itemAdapter.getItem(position).assignmentProfessor;

            startActivityForResult(chooseFileWrapper, PICK_FILE_REQUEST_CODE);

            return true;
        }
    }

    /* ACTIVITY LIFECYCLE */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        loader = findViewById(R.id.loader);
        alert = findViewById(R.id.alert);
        errorText = findViewById(R.id.networkErrorText);
        info = findViewById(R.id.info);
        emptyText = findViewById(R.id.emptyText);

        // Create the Array and the Adapter that manages it.
        pendingItems = new ArrayList<AssignmentItem>();
        itemAdapter = new AssignmentAdapter(getApplicationContext(), pendingItems);

        // Assign created Adapter to ListView
        ListView pendingAList = (ListView) findViewById(R.id.pendingList);
        pendingAList.setAdapter(itemAdapter);

        // File Upload and Download listeners
        pendingAList.setOnItemLongClickListener(new ClickListener());
        pendingAList.setOnItemClickListener(new ClickListener());

        // Bind to the Database Handler
        Intent databaseService = new Intent(getApplicationContext(), DatabaseHandler.class);
        bindService(databaseService, abstractConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if ( activityBinded ) {
            unbindService(abstractConnection);
            activityBinded = false;
        }

        // Ensure no thread leakage occurs if the user decides to quit midway
        loading = false;

        if ( rpcThread.isAlive() )
            rpcThread.quit();


        if ( uploadReceiver != null )
            this.unregisterReceiver(uploadReceiver);
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
                intent.putExtra("fileName", getFileNameFromUri(uri, getContentResolver()));
                intent.putExtra("professorName", professorName);

                intent.putExtra("studentid", Integer.parseInt(getIntent().getStringExtra("id")));
                intent.putExtra("assignmentid", lastClickedAssignment.assignmentID);

                // A receiver for updating the pending assignments list
                uploadReceiver = new PendingUploadReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction("com.example.collegemanager.UPLOAD_COMPLETE");

                this.registerReceiver(uploadReceiver, filter);

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
                intent.putExtra("fileName", itemAdapter.getItem(position).assignmentFileName);
                intent.putExtra("fileSize", itemAdapter.getItem(position).assignmentFileSize);

                startService(intent);
            }
        }
    }

    /* BROADCAST RECEIVER */
    public class PendingUploadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ( intent.getAction().equals("com.example.collegemanager.UPLOAD_COMPLETE") ) {

                if (pendingItems.size() > 0) {

                    pendingItems.remove(latestClickedPosition);
                    itemAdapter.notifyDataSetChanged();

                   if (pendingItems.size() == 0) {

                        // main thred can post to it's own task queue
                        postToMain( 4 );
                    }
                }
            }
        }
    }

    private void fetchPendingAssignments(int studentID) {

        startLoaderAnimation();

        rpcHandler.post( new Runnable() {
            public void run() {

                currResult = boundService.executeQuery("SELECT A.assignmentid, facultyid, title, due, filesize, filetype, fileurl, classid, branch FROM hassubmitted AS T, assignments AS A WHERE T.assignmentid = A.assignmentid AND T.studentid="+ studentID +" AND T.status=1", 0);

                // remove loader
                endLoaderAnimation();
                postToMain( 0 );

                if ( (currResult != null) && (currResult.size() > 0) ) {

                    String[] faculty = {"Pawan Kumar Tiwari", "S. P. Tripathi", "Ram Kumar", "Ayush Mehta", "Tihar Singh"};
                    int[] branches = {R.drawable.computers, R.drawable.electronics, R.drawable.electrical, R.drawable.civil, R.drawable.mechanical, R.drawable.chemical, R.drawable.science};

                    // Add objects to the ArrayAdapter
                    int i = 0;
                    while (i < currResult.size()) {
                        int id = Integer.parseInt(currResult.get(i).get(0));

                        int icon = Integer.parseInt(currResult.get(i).get(8));
                        String facultyname = faculty[Integer.parseInt(currResult.get(i).get(1)) - 1];
                        String title = currResult.get(i).get(2);
                        String due = currResult.get(i).get(3);

                        int FILE_SIZE = Integer.parseInt(currResult.get(i).get(4)); // bytes
                        int filetype = Integer.parseInt(currResult.get(i).get(5));
                        String FILE_NAME = currResult.get(i).get(6);

                        int CLASS_ID = Integer.parseInt(currResult.get(i).get(7));

                        // Convert Kilobytes to Bytes
                        if (filetype == 2)
                            FILE_SIZE = FILE_SIZE * 1024;

                        pendingItems.add(new AssignmentItem(id, branches[icon - 1], title, due, facultyname, FILE_SIZE, FILE_NAME, CLASS_ID));
                        i++;
                    }

                    postToMain( 1 ); // notifyDataSetChanged()
                }
                else if ( ( currResult != null ) && ( currResult.size() == 0 ) ) { // Empty Result

                    postToMain( 4 );

                }
                else { // Network Error

                    postToMain( 3 );
                }
            }
        });
    }

    private void startLoaderAnimation( ) {

        loading = true;

        new Thread () {
            public void run() {
                try {
                    while (loading) {

                        postToMain(2 );
                        degrees = (degrees + 5) % 360;

                        Thread.sleep(50);
                    }
                }
                catch ( InterruptedException e ) {
                    System.out.println(e.getMessage());
                }
            }
        }.start();
    }

    private void endLoaderAnimation( ) {
        loading = false;
    }

    // Post tasks to message queue of main thread
    private void postToMain( int instructionID ) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (instructionID) {
                    case 0: // removeLoader()
                        loader.setVisibility(View.GONE);
                        break;
                    case 1:
                        itemAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        loader.setRotation(degrees);
                        break;
                    case 3:
                        alert.setVisibility(View.VISIBLE);
                        errorText.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        info.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
}
