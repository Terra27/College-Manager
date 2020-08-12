package com.example.collegemanager.attendance;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.collegemanager.R;



import java.util.ArrayList;

public class AttendanceCardAdapter extends ArrayAdapter<AttendanceOptions> {

    public AttendanceCardAdapter(Context context , ArrayList<AttendanceOptions> attendanceOptions){
        super(context , 0 , attendanceOptions);
    }

    // Override the getView() method in the superclass to modify the View returned for each Array object.
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get a reference to the AttendanceOptions object in the ArrayList at position.
        final AttendanceOptions info = getItem(position);

        // Inflate the Layout specified in attendance_card.xml to memory, the root element of the layout is returned.
        if ( convertView == null )
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attendance_card, parent, false);


        //TextView for Subject Code
        TextView subCode = (TextView)convertView.findViewById(R.id.subCode);
        subCode.setText(info.subCode);

        int percent = (int)(((float)info.present/(float)info.totalClass)*100.00);

        //TextView for Attendance : Present/Total
        TextView attendance = (TextView)convertView.findViewById(R.id.attendance);
        attendance.setText("Attendance : "+ info.present + "/" + info.totalClass + " (" + percent +"%)");

        //Progress Bar to show the percentage
        ProgressBar progress = (ProgressBar)convertView.findViewById(R.id.progressBar);
        progress.setProgress(percent);

        //Imageview for showing warning sign if attendance is below 75%
        ImageView warningSign = (ImageView)convertView.findViewById(R.id.warningSign);

        //Warning text to be shown if attendance is below 75%
        TextView warningText = (TextView)convertView.findViewById(R.id.warningText);
        warningText.setText("Your attendance is low");

        //Hide the warning if attendance is above 75%
        if(progress.getProgress()>75){
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
                progress.setProgressTintList(ColorStateList.valueOf(Color.rgb(4,142,6)));

            warningSign.setVisibility(View.INVISIBLE);
            warningText.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }


}
