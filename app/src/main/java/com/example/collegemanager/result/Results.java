package com.example.collegemanager.result;

import android.os.Bundle;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collegemanager.R;

import java.util.ArrayList;

public class Results extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        ArrayList<String> semester = new ArrayList<String>();

        // fetch last result entry from database and store it into sem.
        String sem = "5";

        int lastSem = Integer.parseInt(sem);

        for(int currentSem = 1 ; currentSem <=lastSem ; currentSem++){
            String currentSemester = "Semester " + currentSem;
            semester.add(currentSemester);
        }

        ResultsAdapter adapter = new ResultsAdapter(this , semester);

        GridView attendanceList = (GridView)findViewById(R.id.resultGrid);
        attendanceList.setAdapter(adapter);


    }
}
