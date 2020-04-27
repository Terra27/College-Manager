package com.example.collegemanager.profile;

import android.os.Bundle;
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

        TextView stuName = (TextView)findViewById(R.id.stuName);
        stuName.setText("Ayush");

        TextView stuRoll = (TextView)findViewById(R.id.stuRoll);
        stuRoll.setText("1605210015");

        // From this part all other details

        TextView registration = (TextView)findViewById(R.id.registration);
        registration.setText("Registration");

        TextView yearL = (TextView)findViewById(R.id.yearOfAdmissionL);
        yearL.setText("Year of Admission");

        TextView yearR = (TextView)findViewById(R.id.yearOfAdmissionR);
        yearR.setText("2016");

        TextView courseL = (TextView)findViewById(R.id.courseL);
        courseL.setText("Course");

        TextView courseR = (TextView)findViewById(R.id.courseR);
        courseR.setText("B.Tech");

        TextView branchL = (TextView)findViewById(R.id.branchL);
        branchL.setText("Branch");

        TextView branchR = (TextView)findViewById(R.id.branchR);
        branchR.setText("Computer Science and Engineering");

        TextView currentSemL = (TextView)findViewById(R.id.currentSemL);
        currentSemL.setText("Current Semester");

        // ONLY DATA VARIABLE IN THIS WINDOW IS CURRENT SEMESTER

        TextView currentSemR = (TextView)findViewById(R.id.currentSemR);
        currentSemR.setText("8");

        TextView enrollmentL = (TextView)findViewById(R.id.enrollmentL);
        enrollmentL.setText("Enrollment number");

        TextView enrollmentR = (TextView)findViewById(R.id.enrollmentR);
        enrollmentR.setText("1164646449+6568");

        TextView personalDetail = (TextView)findViewById(R.id.personalDetail);
        personalDetail.setText("Personla Details");

        TextView studentNameL = (TextView)findViewById(R.id.studentNameL);
        studentNameL.setText("Student's Name");

        TextView dobL = (TextView)findViewById(R.id.dobL);
        dobL.setText("Date of Birth");

        TextView dobR = (TextView)findViewById(R.id.dobR);
        dobR.setText("23/04/1998");

        TextView genderL = (TextView)findViewById(R.id.genderR);
        genderL.setText("Gender");

        TextView genderR = (TextView)findViewById(R.id.genderR);
        genderR.setText("Male");

        TextView fatherNameL = (TextView)findViewById(R.id.fatherNameL);
        fatherNameL.setText("Father's Name");

        TextView fatherNameR = (TextView)findViewById(R.id.fatherNameR);
        fatherNameR.setText("Mr.Vijay Kmumar Sharma");

        TextView motherNameL = (TextView)findViewById(R.id.motherNameL);
        motherNameL.setText("Mother's Name");

        TextView motherNameR = (TextView)findViewById(R.id.motherNameR);
        motherNameR.setText("Mrs.Mithilesh Sharma");




    }
}
