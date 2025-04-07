package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserWelcome extends AppCompatActivity {
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_welcome);

        welcomeTextView = findViewById(R.id.welcomeUserTextview);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        // Make network request to fetch user welcome message
        fetchWelcomeMessage(username, role);
    }

    private void fetchWelcomeMessage(String username, String role) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.226/tikipark/welcome_user.php");
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
                        Toast.makeText(UserWelcome.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(UserWelcome.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}