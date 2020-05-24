package com.example.collegemanager.assignment.submitted;

import com.example.collegemanager.DatabaseHandler;
import com.example.collegemanager.DatabaseHandler.MessageInterface;
import com.example.collegemanager.R;
import com.example.collegemanager.UploadStarter;
import com.example.collegemanager.assignment.AssignmentAdapter;
import com.example.collegemanager.assignment.AssignmentItem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.example.collegemanager.Helper.getFileNameFromUri;

public class Submitted extends AppCompatActivity {

    private AssignmentItem lastClickedAssignment;
    private View lastClickedView;
    private ArrayList<AssignmentItem> submittedItems;
    private AssignmentAdapter itemAdapter;

    private int PICK_FILE_REQUEST_CODE = 1498;
    private String professorName;

    private ArrayList<ArrayList<String>> currResult;

    private boolean loading = false;
    private float degrees = 0;
    private ImageView loader;
    private ImageView alert;
    private TextView errorText;
    private ImageView info;
    private TextView emptyText;

    private Handler mainHandler = new Handler( Looper.getMainLooper() );

    private MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    private HandlerThread rpcThread;
    private Handler rpcHandler;

    /* NESTED CLASSES */
    private class ClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View clickedItem, int position, long id) {

            lastClickedAssignment = itemAdapter.getItem(position);
            lastClickedView = clickedItem;

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

            Intent chooseFileWrapper = Intent.createChooser(chooseFile, "Where is your assignment located?");
            professorName = itemAdapter.getItem(position).assignmentProfessor;

            startActivityForResult(chooseFileWrapper, PICK_FILE_REQUEST_CODE);
        }
    }

    /* BOUND SERVICE LIFECYCLE */
    private ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (MessageInterface)theBinder;
                boundService = binder.getServiceInstance();

                // Blocking RPC calls to Service methods must be performed in separate thread
                if ( ( rpcThread == null ) ) {
                    rpcThread = new HandlerThread("RPCThread");
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

    /* ACTIVITY LIFECYCLE */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted);

        // Create the Array and the Adapter that manages it.
        submittedItems = new ArrayList<AssignmentItem>();
        itemAdapter = new AssignmentAdapter(getApplicationContext(), submittedItems, 1);

        // Assign created Adapter to ListView
        ListView submittedAList = (ListView)findViewById(R.id.submittedList);
        submittedAList.setAdapter(itemAdapter);

        // File Upload
        submittedAList.setOnItemClickListener(new ClickListener());

        // Bind to Database Service
        Intent databaseService = new Intent(getApplicationContext(), DatabaseHandler.class);
        bindService(databaseService, abstractConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if ( activityBinded ) {
            unbindService(abstractConnection);
            activityBinded = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Ensure no thread leakage occurs if the user decides to quit midway
        loading = false;

        if ( rpcThread.isAlive() )
            rpcThread.quit();
    }

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

                startService(intent);

                // to be executed after a broadcast is received confirming that file uploaded successfully
                rpcHandler.post( new Runnable() {
                    public void run() {

                        String pattern = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                        String currentDateTime = simpleDateFormat.format(new Date());
                        boundService.executeQuery("UPDATE hassubmitted SET submittime='" + currentDateTime + "' WHERE studentid=" + Integer.parseInt(getIntent().getStringExtra("id")) + " AND assignmentid=" + lastClickedAssignment.assignmentID + "", 1);

                        mainHandler.post( new Runnable() {
                            public void run() {
                                TextView text = (TextView)lastClickedView.findViewById((R.id.assignmentDueDate));
                                text.setText("Submitted on: " + currentDateTime);
                            }
                        });
                    }
                });
            }
        }
    }

    /* INTERNAL METHODS */

    // Fetch Pending Assignment Details from the Database
    private void fetchPendingAssignments( int studentID ) {
        loader = findViewById(R.id.loader);
        alert = findViewById(R.id.alert);
        errorText = findViewById(R.id.networkErrorText);
        info = findViewById(R.id.info);
        emptyText = findViewById(R.id.emptyText);

        loading = true;
        startLoaderAnimation();

        rpcHandler.post( new Runnable() {
            public void run() {

                currResult = boundService.executeQuery("SELECT A.assignmentid, facultyid, title, T.submittime, filesize, filetype, fileurl, classid, branch FROM hassubmitted AS T, assignments AS A WHERE T.assignmentid = A.assignmentid AND T.studentid=" + studentID + " AND T.status=0", 0);
                endLoaderAnimation();
                postToMain( 0 ); // removeLoader()

                if ( (currResult != null) && (currResult.size() > 0) ) {

                    String[] faculty = {"Pawan Kumar Tiwari", "S. P. Tripathi", "Ram Kumar", "Ayush Mehta", "Tihar Singh"};
                    int[] branches = { R.drawable.computers, R.drawable.electronics, R.drawable.electrical, R.drawable.civil, R.drawable.mechanical, R.drawable.chemical, R.drawable.science };

                    // Add objects to the Array
                    int i = 0;
                    while ( i < currResult.size() ) {
                        int id = Integer.parseInt(currResult.get(i).get(0));

                        int icon = Integer.parseInt(currResult.get(i).get(8));
                        String facultyname = faculty[Integer.parseInt(currResult.get(i).get(1)) - 1];
                        String title = currResult.get(i).get(2);
                        String submitTime = currResult.get(i).get(3);


                        int FILE_SIZE = Integer.parseInt(currResult.get(i).get(4)); // bytes
                        int filetype = Integer.parseInt(currResult.get(i).get(5));
                        String FILE_NAME = currResult.get(i).get(6);

                        int classID = Integer.parseInt(currResult.get(i).get(7));

                        // Convert Kilobytes to Bytes
                        if (filetype == 2)
                            FILE_SIZE = FILE_SIZE * 1024;

                        submittedItems.add(new AssignmentItem(id, branches[icon - 1], title, submitTime, facultyname, FILE_SIZE, FILE_NAME, classID));
                        i++;
                    }

                    postToMain( 1 ); // notifyDataSetChanged()

                } else if ( ( currResult != null ) && ( currResult.size() == 0 ) ) { // Empty Result

                    postToMain( 4 );

                } else { // Network Error

                   postToMain( 3 );
                }
            }
        });
    }

    private void startLoaderAnimation( ) {

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
