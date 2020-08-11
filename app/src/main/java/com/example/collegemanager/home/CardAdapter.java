package com.example.collegemanager.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// Testing direct Git Integration from Android Studio

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.collegemanager.R;
import com.example.collegemanager.assignment.Assignment;

import java.util.ArrayList;

    public class CardAdapter extends ArrayAdapter<HomeOptions> {

    public CardAdapter(Context context, ArrayList<HomeOptions> homeOptions) {
        super(context, 0, homeOptions);
    }

    // Override the getView() method in the superclass to modify the View returned for each Array object.
    public View getView(int position, View convertView, ViewGroup parent) {

       // Get a reference to the HomeOptions object in the ArrayList at position.
        final HomeOptions option = getItem(position);

        // Inflate the Layout specified in option_card.xml to memory, the root element of the layout is returned.
        if ( convertView == null )
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.option_card, parent, false);

        // Set the values for the TextView and ImageView in the inflated Layout using the data in the object.
        TextView optionText = (TextView)convertView.findViewById(R.id.optionTitle);
        optionText.setText(option.optionTitle);

        ImageView optionImage = (ImageView)convertView.findViewById(R.id.optionImage);
        optionImage.setImageResource(option.optionPicture);

        // Add a listener to the card within the list entry.
        CardView optionCard = (CardView)convertView.findViewById(R.id.optionCard);
        optionCard.setOnClickListener(option.getOptionListener());

        return convertView;
    }
}
