package com.example.authtestproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeMessageTextView = findViewById(R.id.welcomeMessageTextView);

        // Retrieve username and role from the Intent
        String username = getIntent().getStringExtra("USERNAME");
        String role = getIntent().getStringExtra("ROLE");

        // Display the welcome message
        if (username != null && role != null) {
            String welcomeMessage = "Welcome, " + username + "\nRole: " + role;
            welcomeMessageTextView.setText(welcomeMessage);
        }
    }
}
