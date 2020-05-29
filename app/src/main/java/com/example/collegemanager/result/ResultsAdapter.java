package com.example.collegemanager.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collegemanager.R;

import java.util.ArrayList;

public class ResultsAdapter extends ArrayAdapter<String> {

    public ResultsAdapter( Context context, ArrayList<String> sem) {
        super(context, 0, sem);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get a reference to the String object in the ArrayList at position.
        final String currentSem = getItem(position);

        // Inflate the Layout specified in result_grid.xml to memory, the root element of the layout is returned.
        if ( convertView == null )
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.results_card, parent, false);

        TextView semester = (TextView)convertView.findViewById(R.id.semester);
        semester.setText(currentSem);

        return convertView;
    }
}
