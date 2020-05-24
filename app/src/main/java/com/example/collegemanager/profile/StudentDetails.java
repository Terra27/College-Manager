package com.example.collegemanager.profile;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collegemanager.DatabaseHandler;
import com.example.collegemanager.R;

import java.util.ArrayList;


public class StudentDetails extends AppCompatActivity {

    /* CLASS VARIABLES */
    private ArrayList<ArrayList<String>> currResult = null;
    private DatabaseHandler.MessageInterface binder;
    private DatabaseHandler boundService;
    private boolean activityBinded = false;

    private HandlerThread rpcThread;
    private Handler rpcHandler;

    private ArrayList<String> subjectItems;
    private ArrayAdapter subjectAdapter;

    private String[ ] genders = { "Male", "Female" };
    private String[ ] courses = { "Bachelor of Technology", "Master of Technology", "Master of Business Administration", "Master of Computer Applications" };
    private String[ ] branches = { "Computer Science & Engineering", "Information Technology", "Electronics & Communication Engineering", "Electronics & Instrumentation Engineering", "Electrical Engineering", "Civil Engineering", "Mechanical Engineering", "Chemical Engineering"};

    /* NESTED CLASSES */
    private ServiceConnection abstractConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder theBinder) {
            if ( !activityBinded ) {
                activityBinded = true;

                binder = (DatabaseHandler.MessageInterface)theBinder;
                boundService = binder.getServiceInstance();

                if ( rpcThread == null ) {
                    rpcThread = new HandlerThread("ProfileRPCThread");
                    rpcThread.start();

                    rpcHandler = new Handler( rpcThread.getLooper() );
                }

                fetchSubjectDetails();
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
        setContentView(R.layout.activity_student_details);

        Intent databaseService = new Intent(getApplicationContext(), DatabaseHandler.class);
        bindService(databaseService, abstractConnection, BIND_AUTO_CREATE);

        ImageView stuImage = (ImageView)findViewById(R.id.stuImage);
        stuImage.setImageResource(R.drawable.imagestudent);

        TextView stuName = (TextView)findViewById(R.id.stuName);
        stuName.setText(getIntent().getStringExtra("name"));

        TextView stuRoll = (TextView)findViewById(R.id.stuRoll);
        stuRoll.setText(getIntent().getStringExtra("rollno"));

        TextView registration = (TextView)findViewById(R.id.registration);
        registration.setText("Registration");

        TextView year = (TextView)findViewById(R.id.yearOfAdmission);
        year.setText("Year of Admission: "+ getIntent().getStringExtra("admissionyear"));

        TextView course = (TextView)findViewById(R.id.course);
        course.setText("Course: "+ courses[ Integer.parseInt( getIntent().getStringExtra("course") ) - 1 ]);

        TextView branch = (TextView)findViewById(R.id.branch);
        branch.setText("Branch: "+ branches[ Integer.parseInt( getIntent().getStringExtra("branch")) - 1] );

        TextView currentSem = (TextView)findViewById(R.id.currentSem);
        currentSem.setText("Current Semester: "+ getIntent().getStringExtra("semester"));

        TextView enrollment = (TextView)findViewById(R.id.enrollment);
        enrollment.setText("Enrollment number: "+ getIntent().getStringExtra("enrollmentno"));

        TextView personalDetail = (TextView)findViewById(R.id.personalDetails);
        personalDetail.setText("Personal Details");

        TextView studentName = (TextView)findViewById(R.id.studentName);
        studentName.setText("Student's Name: "+ getIntent().getStringExtra("name"));

        TextView dob = (TextView)findViewById(R.id.dob);
        dob.setText("Date of Birth: "+ getIntent().getStringExtra("dob"));

        TextView gender = (TextView)findViewById(R.id.gender);
        gender.setText("Gender: "+ genders[ Integer.parseInt( getIntent().getStringExtra("gender") ) - 1 ] );
        TextView fatherName = (TextView)findViewById(R.id.fatherName);
        fatherName.setText("Father's Name: "+ getIntent().getStringExtra("fathername"));

        TextView motherName = (TextView)findViewById(R.id.motherName);
        motherName.setText("Mother's Name: "+ getIntent().getStringExtra("mothername"));

        // This ListView can only accommodate 6 subjects, for more subjects, expand it's height in the .xml file
        subjectItems = new ArrayList<String>();
        subjectAdapter = new ArrayAdapter(getApplicationContext(), R.layout.subjectlist_views, subjectItems);
        ListView subjectList = findViewById(R.id.subjectList);
        subjectList.setAdapter(subjectAdapter);
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
    public void fetchSubjectDetails( ) {

        rpcHandler.post( new Runnable() {
           public void run() {

               String[] faculty = {"Pawan Kumar Tiwari", "S. P. Tripathi", "Ram Kumar", "Ayush Mehta", "Tihar Singh"};

               currResult = boundService.executeQuery("SELECT subjectname, facultyid FROM (SELECT subjectid FROM attendance WHERE studentid="+ Integer.parseInt(getIntent().getStringExtra("id")) +") AS A, subjects AS T WHERE A.subjectid = T.subjectid", 0);
               if ( currResult != null ) {

                    for ( int i = 0; i < currResult.size(); i++ ) {

                        subjectItems.add(currResult.get(i).get(0) + " - " + faculty[ Integer.parseInt( currResult.get(i).get(1) ) - 1 ]);
                    }

                    new Handler(Looper.getMainLooper() ).post( new Runnable() { public void run() { subjectAdapter.notifyDataSetChanged(); } } );
               }
           }
        });
    }
}
