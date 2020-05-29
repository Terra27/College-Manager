package com.example.collegemanager.result.showresult;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.collegemanager.R;

import java.util.ArrayList;

public class ShowResultAdapter extends ArrayAdapter<ShowResultOptions> {

    public ShowResultAdapter(Context context , ArrayList<ShowResultOptions> resultOptions){
        super(context , 0 , resultOptions);
    }

    // Override the getView() method in the superclass to modify the View returned for each Array object.
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get a reference to the AttendanceOptions object in the ArrayList at position.
        final ShowResultOptions info = getItem(position);

        // Inflate the Layout specified in attendance_card.xml to memory, the root element of the layout is returned.
        if ( convertView == null )
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.show_result_list, parent, false);

        TextView subject = (TextView)convertView.findViewById(R.id.subject);
        subject.setText(info.subjectCode);


        TextView external = (TextView)convertView.findViewById(R.id.externalMarksObt);
        external.setText(info.extMarks);

        TextView sessional = (TextView)convertView.findViewById(R.id.sessionalMarksObt);
        sessional.setText(info.sessionalMarks);

        TextView grade = (TextView)convertView.findViewById(R.id.gradeObt);
        grade.setText(info.grade);

        TextView credit = (TextView)convertView.findViewById(R.id.creditObt);
        credit.setText(info.credit);

        if(position == 0){
            subject.setTextColor(Color.parseColor("#FF8E0704"));
            external.setTextColor(Color.parseColor("#FF8E0704"));
            sessional.setTextColor(Color.parseColor("#FF8E0704"));
            grade.setTextColor(Color.parseColor("#FF8E0704"));
            credit.setTextColor(Color.parseColor("#FF8E0704"));
        }



        return convertView;



    }
}
