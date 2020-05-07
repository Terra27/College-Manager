package com.example.collegemanager.attendance;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collegemanager.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Attendance extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        ArrayList<AttendanceOptions> attendanceOption = new ArrayList<AttendanceOptions>();

        AttendanceCardAdapter attendanceAdapter = new AttendanceCardAdapter(this, attendanceOption);

        attendanceAdapter.add(new AttendanceOptions("RCS-701", 30 ,25));
        attendanceAdapter.add(new AttendanceOptions("RCS-702", 30 ,20));
        attendanceAdapter.add(new AttendanceOptions("RCS-703", 30 ,15));
        attendanceAdapter.add(new AttendanceOptions("RCS-704", 30 ,10));

        ListView attendanceList = (ListView)findViewById(R.id.attendanceList);
        attendanceList.setAdapter(attendanceAdapter);

    }
}
