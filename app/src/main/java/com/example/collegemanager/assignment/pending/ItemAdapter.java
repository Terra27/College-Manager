package com.example.collegemanager.assignment.pending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import com.example.collegemanager.R;

public class ItemAdapter extends ArrayAdapter<PendingItem> {
    public ItemAdapter(Context context, ArrayList<PendingItem> itemPending) {
        super(context, 0, itemPending);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        PendingItem item = getItem(position);

        if ( convertView == null )
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pending, parent, false);

        // Set the values for the TextView and ImageView in the inflated Layout using the data in the object.
        TextView titleText = (TextView)convertView.findViewById(R.id.itemTitle);
        titleText.setText(item.pendingTitle);

        TextView dueDateText = (TextView)convertView.findViewById(R.id.itemDueDate);
        dueDateText.setText(item.pendingDueDate);

        TextView professorText = (TextView)convertView.findViewById(R.id.itemProfessor);
        professorText.setText(item.pendingProfessor);

        ImageView itemImage = (ImageView)convertView.findViewById(R.id.subjectImage);
        itemImage.setImageResource(item.pendingImage);

        CardView itemCard = (CardView)convertView.findViewById(R.id.itemPending);
        itemCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View clickedItem) {

                //
               return true;
            }
        });

        return convertView;
    }
}
