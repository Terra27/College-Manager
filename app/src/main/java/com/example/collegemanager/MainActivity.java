package com.example.collegemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.collegemanager.home.Home;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Binding Status
    private ArrayList<ArrayList<String>> currResult = null;
    private DatabaseHandler.MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    private ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (DatabaseHandler.MessageInterface)theBinder;
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

    private class ClickListener implements View.OnClickListener {
        public void onClick(View clickedItem) {
            if ( clickedItem.getId() == R.id.loginButton ) {

                EditText usernameEdit = (EditText)findViewById(R.id.usernameEdit);
                String username = usernameEdit.getText().toString();

                EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
                String password = passwordEdit.getText().toString();

                if ( username.length() > 0 && password.length() > 0 ) {

                    Thread transactionThread = new Thread() {
                        public void run() {
                            currResult = boundService.selectQuery("SELECT studentid, A.classid, name, rollno, admissionyear, enrollmentno, dob, gender, fathername, mothername, year, course, branch, semester FROM students AS A, classes AS T WHERE A.username='"+ username +"' AND A.password='"+ password +"' AND A.classid = T.classid");
                        }
                    };
                    transactionThread.start();

                    //Sleep till the transaction thread arrives with data
                    try {
                        transactionThread.join();
                    }
                    catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    /* Sleep till the transaction thread arrives with data
                    while ( transactionThread.isAlive() ) {
                        try {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                     */

                    if ( currResult != null && currResult.size() == 1 ) {

                        Intent intent = new Intent(getApplicationContext(), Home.class);

                        // Pack this data into the intent and pass to the next activity so that we don't have to fetch it multiple times
                        for ( int i= 0; i < GlobalKeys.dataKeys.length; i++) {
                            if ( i == 4 ) {
                                intent.putExtra(GlobalKeys.dataKeys[i], currResult.get(0).get(i).substring(0, currResult.get(0).get(i).indexOf('-')));
                                continue;
                            }
                            intent.putExtra(GlobalKeys.dataKeys[i], currResult.get(0).get(i));
                        }

                        usernameEdit.setText("");
                        passwordEdit.setText("");

                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Your username or password cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //private static final int PICK_FILE = 27;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, PICK_FILE);
        */

        // Bind to the Database Handler
        Intent databaseService = new Intent(getApplicationContext(), DatabaseHandler.class);
        bindService(databaseService, abstractConnection, BIND_AUTO_CREATE);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new ClickListener());

        // Register Application Notification Channel
        Intent intent = new Intent(getApplicationContext(), NotificationChannelRegister.class);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if ( activityBinded ) {
            unbindService(abstractConnection);
            activityBinded = false;
        }
    }

    /*
    // Example code for opening a file stream from a URI or extracting file name from URI of
    // a document managed by a content provider, i.e with a content:// URI scheme
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == PICK_FILE && resultCode == RESULT_OK) {
            if ( data != null ) {
                Uri uri = data.getData();

                try {
                    PrintWriter writer = new PrintWriter( getContentResolver().openOutputStream(uri) );
                    writer.println("What a fuckin' cool guy!");
                    writer.flush();
                    writer.close();
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                String[ ] projection = { DocumentsContract.Document.COLUMN_DISPLAY_NAME };
                Cursor result = getContentResolver().query(uri, projection, null, null, null);
                while( result.moveToNext() ) {
                    System.out.println( result.getString(0));
                }


            }
        }
    }
      */
}
