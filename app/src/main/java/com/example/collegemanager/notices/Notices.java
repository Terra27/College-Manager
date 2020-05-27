package com.example.collegemanager.notices;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collegemanager.DatabaseHandler;
import com.example.collegemanager.DownloadStarter;
import com.example.collegemanager.R;

import java.util.ArrayList;

public class Notices extends AppCompatActivity {

    /* CLASS VARIABLES */
    private ArrayList<ArrayList<String>> currResult = null;
    private DatabaseHandler.MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    private HandlerThread rpcThread;
    private Handler rpcHandler;

    private Handler mainHandler = new Handler( Looper.getMainLooper() );

    ArrayList<NoticeItem> departmentNoticesList;
    ArrayAdapter<NoticeItem> departmentNoticesAdapter;
    ListView departmentNoticeView;

    ArrayList<NoticeItem> collegeNoticesList;
    ArrayAdapter<NoticeItem> collegeNoticesAdapter;
    ListView collegeNoticeView;

    private boolean loading = false;
    private float degrees = 0;
    private ImageView loader;
    private ImageView alert;
    private TextView errorText;

    private Context activityContext = this;
    int NOTICE_DOWNLOAD_REQUEST_CODE = 1210;
    private NoticeItem lastClickedItem;

    /* NESTED CLASSES */
    private ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (DatabaseHandler.MessageInterface)theBinder;
                boundService = binder.getServiceInstance();

                if ( rpcThread == null ) {
                    rpcThread = new HandlerThread("NoticesRPCThread");
                    rpcThread.start();

                    rpcHandler = new Handler( rpcThread.getLooper() );
                }

                fetchNotices();
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

    private class ClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick( AdapterView parent, View clickedItem, int position, long id) {

            if ( parent == collegeNoticeView ) {
                lastClickedItem = collegeNoticesAdapter.getItem(position);
            }
            else if ( parent == departmentNoticeView ) {
                lastClickedItem = departmentNoticesAdapter.getItem(position);
            }

            Intent chooseFile = new Intent();
            chooseFile.setAction(Intent.ACTION_CREATE_DOCUMENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("application/pdf");

            startActivityForResult(chooseFile, NOTICE_DOWNLOAD_REQUEST_CODE);
        }
    }

    /* ACTIVITY LIFECYCLE */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        loader = findViewById(R.id.loader);
        alert = findViewById(R.id.alert);
        errorText = findViewById(R.id.networkErrorText);

        // issue of landscape screen orientation

        departmentNoticesList = new ArrayList<NoticeItem>();
        departmentNoticesAdapter = new NoticesAdapter(getApplicationContext(), departmentNoticesList);

        collegeNoticesList = new ArrayList<NoticeItem>();
        collegeNoticesAdapter = new NoticesAdapter(getApplicationContext(), collegeNoticesList);

        departmentNoticeView = findViewById(R.id.departmentNoticesList);
        departmentNoticeView.setAdapter(departmentNoticesAdapter);
        departmentNoticeView.setOnItemClickListener(new ClickListener());

        collegeNoticeView = findViewById(R.id.collegeNoticesList);
        collegeNoticeView.setAdapter(collegeNoticesAdapter);
        collegeNoticeView.setOnItemClickListener(new ClickListener());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == NOTICE_DOWNLOAD_REQUEST_CODE ) {
            if ( resultCode == RESULT_OK ) {

                Uri uri = data.getData();
                String noticeURL = "Notices\\"+ lastClickedItem.noticeURL; // URL for the file on the server
                int noticeSize = lastClickedItem.noticeSize;
                int sizeUnit = lastClickedItem.sizeUnit;
                if ( sizeUnit == 1 )
                    noticeSize = noticeSize * 1024; // Convert to KB

                Intent downloadService = new Intent(activityContext, DownloadStarter.class);
                downloadService.setData(uri);
                downloadService.putExtra("fileName", noticeURL);
                downloadService.putExtra("fileSize", noticeSize);

                startService(downloadService);
            }
        }
    }


    /* INTERNAL METHODS */
    public void fetchNotices( ) {

        startLoaderAnimation();

        rpcHandler.post( new Runnable() {
            public void run() {

                currResult = boundService.executeQuery("SELECT noticetitle, noticedate, noticebranch, noticeurl, noticesize, sizeunit FROM notices ORDER BY noticedate DESC", 0);
                endLoaderAnimation();
                postToMain( 0 );

                if ( ( currResult != null ) && ( currResult.size() > 0 ) ) {

                    for ( int i = 0; i < currResult.size(); i++ ) {

                        int branch = Integer.parseInt( currResult.get(i).get(2) );
                        int size = Integer.parseInt( currResult.get(i).get(4) );
                        int unit = Integer.parseInt( currResult.get(i).get(5) );
                        if ( branch > 0 ) // Department Notice
                            departmentNoticesList.add(new NoticeItem( currResult.get(i).get(0), currResult.get(i).get(1), branch, currResult.get(i).get(3), size, unit ));
                        else
                            collegeNoticesList.add(new NoticeItem( currResult.get(i).get(0), currResult.get(i).get(1), branch, currResult.get(i).get(3), size, unit ));
                    }

                    mainHandler.post(new Runnable() { public void run() { departmentNoticesAdapter.notifyDataSetChanged(); collegeNoticesAdapter.notifyDataSetChanged(); } } );

                }
                else if ( currResult == null ) { // null, network error
                    postToMain( 3 );
                }
                else { // empty result // issue with premature closing
                   postToMain( 1 );
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
                        Toast.makeText(activityContext, "No notices available!", Toast.LENGTH_SHORT).show();
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