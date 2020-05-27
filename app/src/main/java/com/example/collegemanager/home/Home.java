package com.example.collegemanager.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collegemanager.GlobalKeys;
import com.example.collegemanager.R;
import com.example.collegemanager.assignment.Assignment;
import com.example.collegemanager.attendance.Attendance;
import com.example.collegemanager.notices.Notices;
import com.example.collegemanager.profile.StudentDetails;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

   private class ClickListener implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView parent, final View clickedItem, int position, long id) {

            int viewID = position + 1;
            switch (viewID) {
                case 1: { // Profile
                    Intent profile = new Intent(getApplicationContext(), StudentDetails.class);

                    Intent data = getIntent();
                    for (int i = 0; i < GlobalKeys.dataKeys.length; i++) {
                        profile.putExtra(GlobalKeys.dataKeys[i], data.getStringExtra(GlobalKeys.dataKeys[i]));
                    }

                    startActivity(profile);
                    break;
                }
                case 2: { // Attendance
                    Intent attendance = new Intent(getApplicationContext(), Attendance.class);

                    Intent data = getIntent();
                    attendance.putExtra("id", data.getStringExtra("id") );
                    attendance.putExtra("classID", data.getStringExtra("classID") );

                    startActivity(attendance);
                    break;
                }
                case 4: { // Assignments
                    Intent assignment = new Intent(getApplicationContext(), Assignment.class);

                    // Take the data this activity has and pass it to the next
                    Intent data = getIntent();
                    for (int i = 0; i < GlobalKeys.dataKeys.length; i++) {
                        assignment.putExtra(GlobalKeys.dataKeys[i], data.getStringExtra(GlobalKeys.dataKeys[i]));
                    }

                    startActivity(assignment);
                    break;
                }
                case 6: { // Notices
                    Intent notices = new Intent(getApplicationContext(), Notices.class);

                    startActivity(notices);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView welcomeText = findViewById(R.id.greetingText);
        welcomeText.setText("Welcome to your profile "+ getIntent().getStringExtra("name"));
        // The data source for the ArrayAdapter.
        ArrayList<HomeOptions> homeOptions = new ArrayList<HomeOptions>();

        // Create ArrayAdapter (CardAdapter, a subclass of ArrayAdapter)
        CardAdapter cardAdapter = new CardAdapter(this, homeOptions);

        // Add HomeOptions Objects to the ArrayList of the Adapter.
        cardAdapter.add(new HomeOptions(1, "Your Profile", R.drawable.user));
        cardAdapter.add(new HomeOptions(2, "Attendance", R.drawable.attendance));
        cardAdapter.add(new HomeOptions(3, "Results", R.drawable.results));
        cardAdapter.add(new HomeOptions(4, "Assignments", R.drawable.assigns));
        cardAdapter.add(new HomeOptions(5, "Ask a Question", R.drawable.question));
        cardAdapter.add(new HomeOptions(6, "Notices", R.drawable.notice));

        // Get a reference to the ListView and attach the ArrayAdapter created above.
        ListView homeList = (ListView)findViewById(R.id.homeList);
        homeList.setAdapter(cardAdapter);

        homeList.setOnItemClickListener(new ClickListener());
    }
}
