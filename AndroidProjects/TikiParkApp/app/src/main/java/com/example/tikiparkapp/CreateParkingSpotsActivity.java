package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CreateParkingSpotsActivity extends AppCompatActivity {

    private EditText editLocation, editPricePerHour, editLatitude, editLongitude;
    private Spinner spinnerStatus;

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
        Button btnCreateSpot = findViewById(R.id.btn_create_spot);
        Button btnBack = findViewById(R.id.btn_back);

        // Handle "Create" button click
        btnCreateSpot.setOnClickListener(v -> {
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
        });

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            Intent currentIntent = getIntent();
            String username = currentIntent.getStringExtra("username");
            String role = currentIntent.getStringExtra("role");

            Intent intent = new Intent(CreateParkingSpotsActivity.this, AdminWelcome.class);

            intent.putExtra("username", username);
            intent.putExtra("role", role);

            startActivity(intent);
            finish();
        });

    }

    // Creates a new parking spot by sending data to the server
    private void createParkingSpot(String location, String pricePerHour, String latitude, String longitude, String status) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/create_parking_spot.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // Prepare POST data
                String postData = buildPostData(location, pricePerHour, latitude, longitude, status);

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
                String message = jsonResponse.optString("message", "No message received");

                runOnUiThread(() -> handleCreateSpotResponse(message));

            } catch (Exception e) {
                e.printStackTrace();
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

     //Builds the URL-encoded POST data string.
    private String buildPostData(String location, String pricePerHour, String latitude, String longitude, String status) throws UnsupportedEncodingException {
        return "location=" + URLEncoder.encode(location, "UTF-8") +
                "&price_per_hour=" + URLEncoder.encode(pricePerHour, "UTF-8") +
                "&latitude=" + URLEncoder.encode(latitude, "UTF-8") +
                "&longitude=" + URLEncoder.encode(longitude, "UTF-8") +
                "&status=" + URLEncoder.encode(status, "UTF-8");
    }

     //Handles the server response after creating a parking spot.
    private void handleCreateSpotResponse(String message) {
        Toast.makeText(CreateParkingSpotsActivity.this, message, Toast.LENGTH_SHORT).show();
        if ("Parking spot created successfully".equals(message)) {
            finish();
        }
    }

     //Displays an error message via Toast.
    private void showError(String error) {
        runOnUiThread(() -> Toast.makeText(CreateParkingSpotsActivity.this, error, Toast.LENGTH_SHORT).show());
    }

}