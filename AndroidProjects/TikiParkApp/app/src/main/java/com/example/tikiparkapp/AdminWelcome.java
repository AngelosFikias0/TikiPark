package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminWelcome extends AppCompatActivity {
    private TextView welcomeTextView;

    // Declare statistics TextViews
    private TextView totalUsersTextView;
    private TextView totalSpotsTextView;
    private TextView totalAvailableSpotsTextView;
    private TextView totalAmountGainedTextView;
    private TextView totalReservationsTextView;
    private TextView totalParkingTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_welcome);

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeAdminTextview);
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
            Intent redirectIntent = new Intent(AdminWelcome.this, MainActivity.class);
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
            Intent exitIntent = new Intent(AdminWelcome.this, MainActivity.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(exitIntent);
            finish(); // Finish current activity to prevent returning to AdminWelcome
        });

        // Handle create parking spots button click
        createParkingSpotsButton.setOnClickListener(v -> {
            Intent createParkingIntent = new Intent(AdminWelcome.this, CreateParkingSpotsActivity.class);
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
            Intent manageUsersIntent = new Intent(AdminWelcome.this, ManageUsersActivity.class);
            manageUsersIntent.putExtra("username", username);
            manageUsersIntent.putExtra("role", role);
            startActivity(manageUsersIntent);
        });
    }

    // Method to update the statistics
    private void updateStatistics() {
        // Start a new thread to perform the network operation (to avoid blocking the UI thread)
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader in = null;
            try {
                // Construct the URL to the PHP file (update the URL as needed)
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/welcome_admin.php");

                // Open a connection to the server
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send a request to the server (you can send any parameters if needed)
                conn.getOutputStream().write("action=getStatistics".getBytes());

                // Check the response code
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the server
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                    in = new BufferedReader(inputStreamReader);
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Assuming the response is a JSON string with the statistics
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    // Extract values from the JSON response
                    String totalUsers = jsonResponse.getString("total_users");
                    String totalSpots = jsonResponse.getString("total_spots");
                    String totalAvailableSpots = jsonResponse.getString("total_available_spots");
                    String totalAmountGained = jsonResponse.getString("total_amount_gained");
                    String totalReservations = jsonResponse.getString("total_reservations");
                    String totalParkingTime = jsonResponse.getString("total_parking_time");

                    // Update the UI on the main thread using runOnUiThread
                    runOnUiThread(() -> {
                        totalUsersTextView.setText("Total Users: " + totalUsers);
                        totalSpotsTextView.setText("Total Parking Spots: " + totalSpots);
                        totalAvailableSpotsTextView.setText("Total Available Spots: " + totalAvailableSpots);
                        totalAmountGainedTextView.setText("Total Amount Gained: $" + totalAmountGained);
                        totalReservationsTextView.setText("Total Reservations: " + totalReservations);
                        totalParkingTimeTextView.setText("Total Parking Time: " + totalParkingTime + " hrs");
                    });
                } else {
                    // Handle server error or invalid response
                    runOnUiThread(() -> {
                        Toast.makeText(AdminWelcome.this, "Failed to fetch statistics. Try again.", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                // Handle network or JSON parsing errors
                runOnUiThread(() -> {
                    Toast.makeText(AdminWelcome.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start(); // Start the thread
    }

}