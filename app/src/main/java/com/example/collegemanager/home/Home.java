package com.example.collegemanager.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.collegemanager.R;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // The data source for the ArrayAdapter.
        ArrayList<HomeOptions> homeOptions = new ArrayList<HomeOptions>();

        // Create ArrayAdapter (CardAdapter, a subclass of ArrayAdapter)
        CardAdapter cardAdapter = new CardAdapter(this, homeOptions);

        // Add HomeOptions Objects to the ArrayList of the Adapter.
        cardAdapter.add(new HomeOptions("Your Profile", R.drawable.user));
        cardAdapter.add(new HomeOptions("Attendance", R.drawable.attendance));
        cardAdapter.add(new HomeOptions("Results", R.drawable.results));
        cardAdapter.add(new HomeOptions("Assignments", R.drawable.assigns));
        cardAdapter.add(new HomeOptions("Ask a Question", R.drawable.question));
        cardAdapter.add(new HomeOptions("Notices", R.drawable.notice));

        // Get a reference to the ListView and attach the ArrayAdapter created above.
        ListView homeList = (ListView)findViewById(R.id.homeList);
        homeList.setAdapter(cardAdapter);
    }
}
