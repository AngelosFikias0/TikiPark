package com.example.tikiparkapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.R;
import com.example.tikiparkapp.db.LocalCache;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminWelcome extends AppCompatActivity {

    // Declare statistics TextViews
    private TextView totalUsersTextView;
    private TextView totalSpotsTextView;
    private TextView totalAvailableSpotsTextView;
    private TextView totalAmountGainedTextView;
    private TextView totalReservationsTextView;
    private TextView totalParkingTimeTextView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_admin_welcome);

        // Initialize views
        TextView welcomeTextView = findViewById(R.id.welcomeAdminTextview);
        Button exitButton = findViewById(R.id.exit);
        Button createParkingSpotsButton = findViewById(R.id.create_parking_spots_button);
        Button viewAllButton = findViewById(R.id.view_all_button);
        Button manageUsersButton = findViewById(R.id.manage_users_button);

        // Initialize statistics TextViews
        totalUsersTextView = findViewById(R.id.total_users);
        totalSpotsTextView = findViewById(R.id.total_spots);
        totalAvailableSpotsTextView = findViewById(R.id.total_available_spots);
        totalAmountGainedTextView = findViewById(R.id.total_amount_gained);
        totalReservationsTextView = findViewById(R.id.total_reservations);
        totalParkingTimeTextView = findViewById(R.id.total_parking_time);

        // Get intent extras (username and role)
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        if (username != null && role != null) {
            // Display the welcome message using the username and role passed via the intent
            welcomeTextView.setText("Welcome, " + role + " \"" + username + "\"!");
        } else {
            // Handle case where the session is not valid (username or role is not passed)
            Toast.makeText(AdminWelcome.this, "Session expired or invalid", Toast.LENGTH_LONG).show();
            // Optionally, redirect to the MainActivity (login screen)
            Intent redirectIntent = new Intent(AdminWelcome.this, Entry.class);
            startActivity(redirectIntent);
            finish(); // Finish current activity to prevent returning to AdminWelcome
        }

        // Update the statistics TextViews
        updateStatistics();

        // Handle exit button click
        exitButton.setOnClickListener(v -> {
            // Clear session and navigate to MainActivity (login screen)
            LocalCache localCache = new LocalCache(this);
            localCache.clearSession();
            Intent exitIntent = new Intent(AdminWelcome.this, Entry.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exitIntent);
            finish(); // Finish current activity to prevent returning to AdminWelcome
        });

        // Handle create parking spots button click
        createParkingSpotsButton.setOnClickListener(v -> {
            Intent createParkingIntent = new Intent(AdminWelcome.this, CreateParkingSpots.class);
            createParkingIntent.putExtra("username", username);
            createParkingIntent.putExtra("role", role);
            startActivity(createParkingIntent);
        });

        // Handle view all button click
        viewAllButton.setOnClickListener(v -> {
            Intent viewAllIntent = new Intent(AdminWelcome.this, ViewAllActivity.class);
            viewAllIntent.putExtra("username", username);
            viewAllIntent.putExtra("role", role);
            startActivity(viewAllIntent);
        });

        // Handle manage users button click
        manageUsersButton.setOnClickListener(v -> {
            Intent manageUsersIntent = new Intent(AdminWelcome.this, ManageUsers.class);
            manageUsersIntent.putExtra("username", username);
            manageUsersIntent.putExtra("role", role);
            startActivity(manageUsersIntent);
        });
    }

    // Updates the admin dashboard statistics from the server
    private void updateStatistics() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/welcome_admin.php");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send POST parameters
                String postData = "action=getStatistics";
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

                // Extract statistics
                String totalUsers = jsonResponse.optString("total_users", "0");
                String totalSpots = jsonResponse.optString("total_spots", "0");
                String totalAvailableSpots = jsonResponse.optString("total_available_spots", "0");
                String totalAmountGained = jsonResponse.optString("total_amount_gained", "0");
                String totalReservations = jsonResponse.optString("total_reservations", "0");
                String totalParkingTime = jsonResponse.optString("total_parking_time", "0");

                // Update UI on the main thread
                runOnUiThread(() -> updateStatisticsUI(
                        totalUsers,
                        totalSpots,
                        totalAvailableSpots,
                        totalAmountGained,
                        totalReservations,
                        totalParkingTime
                ));
            } catch (Exception e) {
                showError("Error: " + e.getMessage());
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (is != null) {
                    try { is.close(); } catch (IOException ignored) {}
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

     //Updates the TextViews with fetched statistics.
    @SuppressLint("SetTextI18n")
    private void updateStatisticsUI(String totalUsers, String totalSpots, String totalAvailableSpots,
                                    String totalAmountGained, String totalReservations, String totalParkingTime) {
        totalUsersTextView.setText("Total Users: " + totalUsers);
        totalSpotsTextView.setText("Total Parking Spots: " + totalSpots);
        totalAvailableSpotsTextView.setText("Total Available Spots: " + totalAvailableSpots);
        totalAmountGainedTextView.setText("Total Amount Gained: $" + totalAmountGained);
        totalReservationsTextView.setText("Total Reservations: " + totalReservations);
        totalParkingTimeTextView.setText("Total Parking Time: " + totalParkingTime + " hrs");
    }

     //Displays an error message via a Toast on the UI thread.
    private void showError(String message) {
        runOnUiThread(() -> Toast.makeText(AdminWelcome.this, message, Toast.LENGTH_LONG).show());
    }
}