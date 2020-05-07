package com.example.collegemanager.profile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collegemanager.R;


public class StudentDetails extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);


        // All details are INVARIABLE except CURRENT SEMESTER

        // These details are for top part containing student's name , roll number and image .

        ImageView stuImage = (ImageView)findViewById(R.id.stuImage);
        stuImage.setImageResource(R.drawable.imagestudent);

        TextView stuName = (TextView)findViewById(R.id.stuName);
        stuName.setText("Ayush");

        TextView stuRoll = (TextView)findViewById(R.id.stuRoll);
        stuRoll.setText("1605210015");

        // From this part all other details

        TextView registration = (TextView)findViewById(R.id.registration);
        registration.setText("Registration");

        TextView year = (TextView)findViewById(R.id.yearOfAdmission);
        year.setText("Year of Admission  :  2016");

        TextView course = (TextView)findViewById(R.id.course);
        course.setText("Course  :  B.Tech");

        TextView branch = (TextView)findViewById(R.id.branch);
        branch.setText("Branch : Computer Science and Engineering");

        // ONLY DATA VARIABLE IN THIS WINDOW IS CURRENT SEMESTER
        TextView currentSem = (TextView)findViewById(R.id.currentSem);
        currentSem.setText("Current Semester  :  8");


        TextView enrollment = (TextView)findViewById(R.id.enrollment);
        enrollment.setText("Enrollment number  :  5165435135435435");

        TextView personalDetail = (TextView)findViewById(R.id.personalDetails);
        personalDetail.setText("Personal Details");

        TextView studentName = (TextView)findViewById(R.id.studentName);
        studentName.setText("Student's Name  :  Ayush");

        TextView dob = (TextView)findViewById(R.id.dob);
        dob.setText("Date of Birth  :  23/04/1998");

        TextView gender = (TextView)findViewById(R.id.gender);
        gender.setText("Gender  :  Male");

        TextView fatherName = (TextView)findViewById(R.id.fatherName);
        fatherName.setText("Father's Name  :  Mr.Vijay Kumar Sharma");

        TextView motherName = (TextView)findViewById(R.id.motherName);
        motherName.setText("Mother's Name  :  Mrs.Mithilesh Sharma");



    }
}
