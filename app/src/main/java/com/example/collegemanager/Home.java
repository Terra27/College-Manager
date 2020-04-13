package com.example.collegemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<HomeOptions> homeOptions = new ArrayList<HomeOptions>();
        CardAdapter cardAdapter = new CardAdapter(this, homeOptions);
        cardAdapter.add(new HomeOptions("Your Profile", R.drawable.user));
        cardAdapter.add(new HomeOptions("Attendance", R.drawable.attendance));
        cardAdapter.add(new HomeOptions("Results", R.drawable.results));
        cardAdapter.add(new HomeOptions("Assignments", R.drawable.assigns));
        cardAdapter.add(new HomeOptions("Ask a Question", R.drawable.question));
        cardAdapter.add(new HomeOptions("Notices", R.drawable.notice));

        ListView homeList = (ListView)findViewById(R.id.homeList);
        homeList.setAdapter(cardAdapter);
    }
}
