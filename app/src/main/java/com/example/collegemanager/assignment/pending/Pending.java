package com.example.collegemanager.assignment.pending;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collegemanager.R;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class Pending extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        // Create the Array and the Adapter that manages it.
        ArrayList<PendingItem> pendingItems = new ArrayList<PendingItem>();
        ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext(), pendingItems);

        // Assign created Adapter to ListView
        ListView pendingAList = (ListView)findViewById(R.id.pendingList);
        pendingAList.setAdapter(itemAdapter);

        // Add objects to the Array
        itemAdapter.add(new PendingItem(1, R.drawable.subject, "Assignment on Deep Learning", "25-04-2020", "Pawan Kumar Tiwari"));
        itemAdapter.add(new PendingItem(2, R.drawable.subject, "Assignment on Neural Networks", "26-04-2020", "Y. N. Singh"));
        itemAdapter.add(new PendingItem(3, R.drawable.subject, "Assignment on Database Management", "27-04-2020", "S. P. Tripathi"));
        itemAdapter.add(new PendingItem(4, R.drawable.subject, "Assignment on Operating System", "28-04-2020", "D. S. Yadav"));
        itemAdapter.add(new PendingItem(5, R.drawable.subject, "Assignment on Engineering Mathematics", "29-04-2020", "Ram Kumar"));

    }
}
