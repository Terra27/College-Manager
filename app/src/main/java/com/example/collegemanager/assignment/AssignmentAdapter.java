package com.example.collegemanager.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.collegemanager.R;

import java.util.ArrayList;

public class AssignmentAdapter extends BaseAdapter {

    private Context context; //context
    private ArrayList<AssignmentItem> items; //data source of the list adapter
    private int type = 0;

    public AssignmentAdapter(Context context, ArrayList<AssignmentItem> items) {
        this.context = context;
        this.items = items;
    }

    public AssignmentAdapter(Context context, ArrayList<AssignmentItem> items, int type) {
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public AssignmentItem getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        AssignmentItem item = getItem(position);

        if ( convertView == null )
            convertView = LayoutInflater.from(context).inflate(R.layout.assignment_list, parent, false);

        // Set the values for the TextView and ImageView in the inflated Layout using the data in the object.
        TextView titleText = (TextView) convertView.findViewById(R.id.assignmentTitle);
        titleText.setText(item.assignmentTitle);

        TextView dueDateText = (TextView) convertView.findViewById(R.id.assignmentDueDate);
        if ( type != 0 ) // upload
            dueDateText.setText("Submitted on: " + item.assignmentDueDate);
        else
            dueDateText.setText("Due Date: " + item.assignmentDueDate);

        TextView professorText = (TextView) convertView.findViewById(R.id.assignmentProfessor);
        professorText.setText(item.assignmentProfessor);

        ImageView itemImage = (ImageView) convertView.findViewById(R.id.assignmentImage);
        itemImage.setImageResource(item.assignmentImage);

        ImageView downloadImage = convertView.findViewById(R.id.downloadImage);
        if ( type != 0 ) // upload
            downloadImage.setRotation(180.0f);

        return convertView;
    }
}
