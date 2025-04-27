package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CreateParkingSpotsActivity extends AppCompatActivity {

    private EditText editLocation, editPricePerHour, editLatitude, editLongitude;
    private Spinner spinnerStatus;
    private Button btnCreateSpot, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parking_spots);

        // Initialize views
        editLocation = findViewById(R.id.edit_location);
        editPricePerHour = findViewById(R.id.edit_price_per_hour);
        editLatitude = findViewById(R.id.edit_latitude);
        editLongitude = findViewById(R.id.edit_longitude);
        spinnerStatus = findViewById(R.id.spinner_status);
        btnCreateSpot = findViewById(R.id.btn_create_spot);
        btnBack = findViewById(R.id.btn_back);

        // Handle "Create" button click
        btnCreateSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the data from the inputs
                String location = editLocation.getText().toString();
                String pricePerHour = editPricePerHour.getText().toString();
                String latitude = editLatitude.getText().toString();
                String longitude = editLongitude.getText().toString();
                String status = spinnerStatus.getSelectedItem().toString();

                // Validate inputs (basic validation)
                if (location.isEmpty() || pricePerHour.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
                    Toast.makeText(CreateParkingSpotsActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create parking spot on the server
                createParkingSpot(location, pricePerHour, latitude, longitude, status);
            }
        });

        // Back button click listener
        // Back button click listener
        btnBack.setOnClickListener(v -> {
            // Get the current intent (to retrieve the username and role from the current activity)
            Intent currentIntent = getIntent();
            String username = currentIntent.getStringExtra("username");
            String role = currentIntent.getStringExtra("role");

            // Create a new intent to navigate back to AdminWelcome activity
            Intent intent = new Intent(CreateParkingSpotsActivity.this, AdminWelcome.class);

            // Pass the username and role to the new activity
            intent.putExtra("username", username);
            intent.putExtra("role", role);

            // Start AdminWelcome activity
            startActivity(intent);
            finish(); // Finish the current activity to prevent returning to ViewAllActivity
        });

    }

    private void createParkingSpot(final String location, final String pricePerHour, final String latitude, final String longitude, final String status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {
                    // Define the URL for the PHP script
                    URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/create_parking_spot.php");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(5000); // 5 seconds timeout
                    conn.setReadTimeout(5000);    // 5 seconds timeout
                    conn.setDoOutput(true);

                    // Prepare POST data
                    String postData = "location=" + URLEncoder.encode(location, "UTF-8") +
                            "&price_per_hour=" + URLEncoder.encode(pricePerHour, "UTF-8") +
                            "&latitude=" + URLEncoder.encode(latitude, "UTF-8") +
                            "&longitude=" + URLEncoder.encode(longitude, "UTF-8") +
                            "&status=" + URLEncoder.encode(status, "UTF-8");

                    // Send POST data
                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    // Read response
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Handle the response
                    JSONObject response = new JSONObject(result.toString());
                    String message = response.getString("message");

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateParkingSpotsActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (message.equals("Parking spot created successfully")) {
                                finish(); // Finish activity if successful
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateParkingSpotsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }
}