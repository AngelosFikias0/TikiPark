package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminWelcome extends AppCompatActivity {
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_welcome);

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeAdminTextview);
        Button exitButton = findViewById(R.id.exit);

        // Get intent extras
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        // Fetch welcome message based on username and role
        fetchWelcomeMessage(username, role);
        LocalCache localCache = new LocalCache(this);

        // Handle exit button click
        exitButton.setOnClickListener(v -> {
            localCache.clearSession();
            Intent exitIntent = new Intent(AdminWelcome.this, MainActivity.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exitIntent);
            finish(); // Finish current activity
        });
    }


    private void fetchWelcomeMessage(String username, String role) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.226/tikipark/welcome_admin.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Prepare the POST data
                String postData = "username=" + username + "&role=" + role;
                conn.getOutputStream().write(postData.getBytes());

                // Get the response from the server
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Parse the JSON response
                JSONObject response = new JSONObject(result.toString());
                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                // Run UI updates on the main thread
                runOnUiThread(() -> {
                    if (success) {
                        welcomeTextView.setText(message);
                    } else {
                        Toast.makeText(AdminWelcome.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AdminWelcome.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}