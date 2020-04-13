package com.example.collegemanager;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
// Testing direct Git Integration from Android Studio

import java.util.ArrayList;

public class CardAdapter extends ArrayAdapter<HomeOptions> {
   public CardAdapter(Context context, ArrayList<HomeOptions> homeOptions) {
        super(context, 0, homeOptions);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        HomeOptions option = getItem(position);
        if ( convertView == null )
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.option_card, parent, false);

        TextView optionText = (TextView)convertView.findViewById(R.id.optionTitle);
        optionText.setText(option.optionTitle);

        ImageView optionImage = (ImageView)convertView.findViewById(R.id.optionImage);
        optionImage.setImageResource(option.optionPicture);

        return convertView;
    }
}
