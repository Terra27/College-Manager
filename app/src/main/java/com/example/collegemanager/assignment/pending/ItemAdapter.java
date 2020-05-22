package com.example.collegemanager.assignment.pending;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import com.example.collegemanager.DownloadStarter;
import com.example.collegemanager.R;
import com.example.collegemanager.UploadStarter;

public class ItemAdapter extends BaseAdapter {

    private Context context; //context
    private ArrayList<PendingItem> items; //data source of the list adapter

    public ItemAdapter(Context context, ArrayList<PendingItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public PendingItem getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
    public ItemAdapter(Context context, ArrayList<PendingItem> itemPending, Pending.ClickListener listener) {
        super(context, 0, itemPending);
        parentActivityClickListener = listener;
    }
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        PendingItem item = getItem(position);

        if ( convertView == null )
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pending, parent, false);

        // Set the values for the TextView and ImageView in the inflated Layout using the data in the object.
        TextView titleText = (TextView)convertView.findViewById(R.id.itemTitle);
        titleText.setText(item.pendingTitle);

        TextView dueDateText = (TextView)convertView.findViewById(R.id.itemDueDate);
        dueDateText.setText(item.pendingDueDate);

        TextView professorText = (TextView)convertView.findViewById(R.id.itemProfessor);
        professorText.setText(item.pendingProfessor);

        ImageView itemImage = (ImageView)convertView.findViewById(R.id.subjectImage);
        itemImage.setImageResource(item.pendingImage);

        //ListView itemList = (ListView)parentActivity.findViewById(R.id.pendingList);

        // File Upload
        //itemList.setOnItemLongClickListener(parentActivityClickListener);

        // File Download
        //itemList.setOnItemClickListener(parentActivityClickListener);

        return convertView;
    }
}
