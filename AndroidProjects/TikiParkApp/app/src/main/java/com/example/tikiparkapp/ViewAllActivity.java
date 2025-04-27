package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ViewAllActivity extends AppCompatActivity {

    // Declare UI components
    private TextView editSpotTitle;
    private EditText editStatus, editPrice;
    private ListView listSpots;

    // List of String arrays to hold data from JSON
    private final ArrayList<String[]> spotList = new ArrayList<>();
    private int selectedSpotPosition = -1;

    private int selectedSpotId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        // Initialize UI components
        editSpotTitle = findViewById(R.id.edit_spot_title);
        editStatus = findViewById(R.id.edit_status);
        editPrice = findViewById(R.id.edit_price);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnBack = findViewById(R.id.btn_back);
        listSpots = findViewById(R.id.list_spots);

        // Fetch spots from the PHP backend
        fetchSpots();

        // ListView item click listener
        listSpots.setOnItemClickListener((parentView, view, position, id) -> {
            selectedSpotPosition = position;
            String[] selectedSpot = spotList.get(position);
            editSpotTitle.setText(selectedSpot[0]);   // name
            editStatus.setText(selectedSpot[1]);      // status
            editPrice.setText(selectedSpot[2]);       // price
            selectedSpotId = Integer.parseInt(selectedSpot[3]); //Id
        });


        // Update button click listener
        btnUpdate.setOnClickListener(v -> {
            if (selectedSpotPosition != -1) {
                updateSpot(selectedSpotId);
            } else {
                Toast.makeText(ViewAllActivity.this, "Please select a spot to update", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button click listener
        btnDelete.setOnClickListener(v -> {
            if (selectedSpotPosition != -1) {
                deleteSpot(selectedSpotId);
            } else {
                Toast.makeText(ViewAllActivity.this, "Please select a spot to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            Intent currentIntent = getIntent();
            String username = currentIntent.getStringExtra("username");
            String role = currentIntent.getStringExtra("role");

            Intent intent = new Intent(ViewAllActivity.this, AdminWelcome.class);

            intent.putExtra("username", username);
            intent.putExtra("role", role);

            startActivity(intent);
            finish();
        });


    }

    private void fetchSpots() {
        // Perform the HTTP request to fetch spots from the backend
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/get_spots.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                boolean success = response.getBoolean("success");

                if (success) {
                    JSONArray spots = response.getJSONArray("spots");
                    spotList.clear();

                    // Parse the spots from the JSON response
                    for (int i = 0; i < spots.length(); i++) {
                        JSONObject spot = spots.getJSONObject(i);
                        String name = spot.getString("name");
                        String status = spot.getString("status");
                        String price = spot.getString("price");
                        String id = spot.getString("spot_id");

                        spotList.add(new String[]{name, status, price,id});
                    }

                    // Update the ListView on the main thread
                    runOnUiThread(() -> {
                        ArrayList<String> spotStrings = new ArrayList<>();
                        for (String[] spot : spotList) {
                            spotStrings.add("Name: " + spot[0] + " | Status: " + spot[1] + " | Price: $" + spot[2]);
                        }
                        listSpots.setAdapter(new ArrayAdapter<>(ViewAllActivity.this, android.R.layout.simple_list_item_1, spotStrings));
                    });
                } else {
                    final String message = response.getString("message");
                    runOnUiThread(() -> Toast.makeText(ViewAllActivity.this, message, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ViewAllActivity.this, "Error fetching spots", Toast.LENGTH_SHORT).show());
            }finally {
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

    private void updateSpot(int spotId) {
        String status = editStatus.getText().toString().trim().toLowerCase();
        String price = editPrice.getText().toString().trim();

        if (!(status.equals("available") || status.equals("occupied") || status.equals("reserved"))) {
            runOnUiThread(() ->
                    Toast.makeText(ViewAllActivity.this, "Status must be 'available', 'occupied', or 'reserved'", Toast.LENGTH_SHORT).show()
            );
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null ;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/update_spot.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000); // 5 seconds timeout
                conn.setReadTimeout(5000);    // 5 seconds timeout
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "spot_id=" + URLEncoder.encode(String.valueOf(spotId), "UTF-8") +
                        "&status=" + URLEncoder.encode(status, "UTF-8") +
                        "&price=" + URLEncoder.encode(price, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                is = conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                String message = response.getString("message");

                runOnUiThread(() -> Toast.makeText(ViewAllActivity.this, message, Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ViewAllActivity.this, "Error updating spot", Toast.LENGTH_SHORT).show());
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

    private void deleteSpot(int spotId) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/delete_spot.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "spot_id=" + URLEncoder.encode(String.valueOf(spotId), "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                is = conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                runOnUiThread(() -> {
                    Toast.makeText(ViewAllActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (success) {
                        spotList.remove(selectedSpotPosition);
                        updateListView();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ViewAllActivity.this, "Error deleting spot", Toast.LENGTH_SHORT).show());
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

    private void updateListView() {
        ArrayList<String> spotStrings = new ArrayList<>();
        for (String[] spot : spotList) {
            spotStrings.add("Status: " + spot[0] + " - Price: $" + spot[1]);
        }
        listSpots.setAdapter(new ArrayAdapter<>(ViewAllActivity.this, android.R.layout.simple_list_item_1, spotStrings));
    }
}
