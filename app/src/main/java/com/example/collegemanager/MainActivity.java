package com.example.collegemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.collegemanager.home.Home;

public class MainActivity extends AppCompatActivity {

    private class ClickListener implements View.OnClickListener {
        public void onClick(View clickedItem) {
            if ( clickedItem.getId() == R.id.loginButton ) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new ClickListener());

        // Register Application Notification Channel
        Intent intent = new Intent(getApplicationContext(), NotificationChannelRegister.class);
        startService(intent);
    }
}
