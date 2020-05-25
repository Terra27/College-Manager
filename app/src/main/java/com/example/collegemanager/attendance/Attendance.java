package com.example.collegemanager.attendance;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collegemanager.DatabaseHandler;
import com.example.collegemanager.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Attendance extends AppCompatActivity {

    /* CLASS VARIABLES */
    private ArrayList<ArrayList<String>> currResult = null;
    private DatabaseHandler.MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    private HandlerThread rpcThread;
    private Handler rpcHandler;

    private Handler mainHandler = new Handler( Looper.getMainLooper() );

    AttendanceCardAdapter attendanceAdapter;
    ArrayList<AttendanceOptions> attendanceOption;

    private boolean loading = false;
    private float degrees = 0;
    private ImageView loader;
    private ImageView alert;
    private TextView errorText;

    /* NESTED CLASSES */
    private ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (DatabaseHandler.MessageInterface)theBinder;
                boundService = binder.getServiceInstance();

                if ( rpcThread == null ) {
                    rpcThread = new HandlerThread("AttendanceRPCThread");
                    rpcThread.start();

                    rpcHandler = new Handler( rpcThread.getLooper() );
                }

                fetchAttendanceDetails();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        loader = findViewById(R.id.loader);
        alert = findViewById(R.id.alert);
        errorText = findViewById(R.id.networkErrorText);

        attendanceOption = new ArrayList<AttendanceOptions>();
        attendanceAdapter = new AttendanceCardAdapter(this, attendanceOption);

        ListView attendanceList = (ListView)findViewById(R.id.attendanceList);
        attendanceList.setAdapter(attendanceAdapter);

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

        if ( rpcThread.isAlive() )
            rpcThread.quit();
    }

    /* INTERNAL METHODS */
    public void fetchAttendanceDetails( ) {

        startLoaderAnimation();

        rpcHandler.post( new Runnable() {
            public void run() {

                currResult = boundService.executeQuery("SELECT C.subjectid, D.subjectname, D.subjectcode, C.count, C.complete FROM (SELECT A.subjectid, A.count, B.complete FROM (SELECT subjectid, count FROM attendance WHERE studentid="+ Integer.parseInt( getIntent().getStringExtra("id") ) +") AS A, (SELECT subjectid, complete FROM lectures WHERE classid="+ Integer.parseInt( getIntent().getStringExtra("classID") ) +") AS B WHERE A.subjectid=B.subjectid) AS C, subjects AS D WHERE C.subjectid=D.subjectid", 0);
                endLoaderAnimation();
                postToMain( 0 );

                if ( currResult != null ) {

                    for ( int i = 0; i < currResult.size(); i++ ) {
                        attendanceOption.add(new AttendanceOptions( currResult.get(i).get(1) + " (" + currResult.get(i).get(2) +")", Integer.parseInt( currResult.get(i).get(3) ), Integer.parseInt( currResult.get(i).get(4) ) ));
                    }

                    mainHandler.post(new Runnable() { public void run() { attendanceAdapter.notifyDataSetChanged(); } } );

                }
                else
                    postToMain( 3 );
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
                    case 2:
                        loader.setRotation(degrees);
                        break;
                    case 3:
                        alert.setVisibility(View.VISIBLE);
                        errorText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
}
