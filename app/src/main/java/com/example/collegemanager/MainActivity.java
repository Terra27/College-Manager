package com.example.collegemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.collegemanager.home.Home;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind to the Database Handler
        Intent databaseService = new Intent(getApplicationContext(), DatabaseHandler.class);
        bindService(databaseService, abstractConnection, BIND_AUTO_CREATE);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new ClickListener());

        // Register Application Notification Channel
        Intent intent = new Intent(getApplicationContext(), NotificationChannelRegister.class);
        startService(intent);

        //Removed Testing message
    }

    @Override
    protected void onStop() {
        super.onStop();

        if ( activityBinded ) {
            unbindService(abstractConnection);
            activityBinded = false;
        }
    }
}
