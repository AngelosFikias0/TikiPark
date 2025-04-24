package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserWelcome extends AppCompatActivity {
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_welcome);

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeUserTextview);
        Button exitButton = findViewById(R.id.exit);
        EditText locationInput = findViewById(R.id.locationInput);
        Button search = findViewById(R.id.search);
        Button wallet = findViewById(R.id.manageWallet);
        Button stats = findViewById(R.id.viewStats);

        // Get intent extras (username, role)
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        // Check if username and role are valid
        if (username != null && role != null) {
            // Use the passed username and role to show the welcome message
            welcomeTextView.setText("Welcome, " + role + ": \"" + username + "\"!");
        } else {
            // Handle case where username or role is not passed correctly
            Toast.makeText(UserWelcome.this, "Session expired or invalid", Toast.LENGTH_LONG).show();
            // Redirect to MainActivity if session data is invalid
            Intent redirectIntent = new Intent(UserWelcome.this, MainActivity.class);
            startActivity(redirectIntent);
            finish();
        }

        // Handle Search Logic
        search.setOnClickListener(v -> {
            // Get the location input
            String location = locationInput.getText().toString();

            // Create an Intent to pass the location to a new activity (SearchActivity)
            Intent searchIntent = new Intent(UserWelcome.this, SearchActivity.class);
            searchIntent.putExtra("location", location);
            searchIntent.putExtra("username",username);
            startActivity(searchIntent);
        });

        // Handle Wallet Management Logic
        wallet.setOnClickListener(v -> {
            // Create an Intent to go to the wallet management screen
            Intent walletIntent = new Intent(UserWelcome.this, WalletManagementActivity.class);
            walletIntent.putExtra("username",username);
            startActivity(walletIntent);
        });

        // Handle Statistics Logic
        stats.setOnClickListener(v -> {
            // Create an Intent to go to the statistics screen
            Intent statsIntent = new Intent(UserWelcome.this, StatsActivity.class);
            statsIntent.putExtra("username",username);
            startActivity(statsIntent);
        });


        // Handle exit button click
        exitButton.setOnClickListener(v -> {
            // Clear session data and navigate to MainActivity
            LocalCache localCache = new LocalCache(this);
            localCache.clearSession();
            Intent exitIntent = new Intent(UserWelcome.this, MainActivity.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exitIntent);
            finish(); // Finish current activity to prevent returning to UserWelcome
        });
    }
}