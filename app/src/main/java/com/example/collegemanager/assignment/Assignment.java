package com.example.collegemanager.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.collegemanager.assignment.pending.Pending;

import com.example.collegemanager.R;

public class Assignment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        Button pendingButton = (Button)findViewById(R.id.pendingButton);
        pendingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View clickedButton) {
                Intent intent = new Intent(getApplicationContext(), Pending.class);

                // Pass on my ID to fetch my pending assignments
                Intent data = getIntent();
                intent.putExtra("id", data.getStringExtra("id"));

                startActivity(intent);
            }
        });

    }
    
}
