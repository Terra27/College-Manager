package com.example.collegemanager.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collegemanager.GlobalKeys;
import com.example.collegemanager.R;
import com.example.collegemanager.assignment.Assignment;
import com.example.collegemanager.attendance.Attendance;
import com.example.collegemanager.profile.StudentDetails;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

   public class ClickListener implements View.OnClickListener {

       private int viewID;
        public ClickListener(int id ){
           viewID = id;
        }

        public void onClick(final View clickedItem) {

            clickedItem.setBackgroundColor(getResources().getColor(R.color.cardClickColor));
            // Timer thread for reverting the color.
            new Thread ( ) {
                public void run( ) {
                    try {
                        Thread.sleep(150);
                        clickedItem.setBackgroundColor(getResources().getColor(R.color.cardIdleColor));
                    }
                    catch ( InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }.start();

            switch (viewID) {
                case 4:
                    Intent assignment = new Intent(getApplicationContext(), Assignment.class);

                    // Take the data this activity has and pass it to the next
                    Intent data = getIntent();
                    for ( int i= 0; i < GlobalKeys.dataKeys.length; i++) {
                        assignment.putExtra(GlobalKeys.dataKeys[i], data.getStringExtra(GlobalKeys.dataKeys[i]));
                    }

                    startActivity(assignment);
                    break;
                case 1:
                    Intent profile = new Intent(getApplicationContext(), StudentDetails.class);
                    startActivity(profile);
                    break;
                case 2:
                    Intent attendance = new Intent(getApplicationContext(), Attendance.class);
                    startActivity(attendance);
                    break;
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
        cardAdapter.add(new HomeOptions(1, "Your Profile", R.drawable.user, new Home.ClickListener( 1 )));
        cardAdapter.add(new HomeOptions(2, "Attendance", R.drawable.attendance, new Home.ClickListener(2 )));
        cardAdapter.add(new HomeOptions(3, "Results", R.drawable.results, new Home.ClickListener(3)));
        cardAdapter.add(new HomeOptions(4, "Assignments", R.drawable.assigns, new Home.ClickListener(4)));
        cardAdapter.add(new HomeOptions(5, "Ask a Question", R.drawable.question, new Home.ClickListener(5)));
        cardAdapter.add(new HomeOptions(6, "Notices", R.drawable.notice, new Home.ClickListener(6)));

        // I provide every object a reference to it's own listener, so that I can be set later in getView().

        // Get a reference to the ListView and attach the ArrayAdapter created above.
        ListView homeList = (ListView)findViewById(R.id.homeList);
        homeList.setAdapter(cardAdapter);
    }
}
