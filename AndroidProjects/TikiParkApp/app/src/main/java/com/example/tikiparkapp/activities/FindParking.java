package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.ParkingSpot;
import com.example.tikiparkapp.ParkingSpotManager;
import com.example.tikiparkapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.HashMap;

public class FindParking extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private MapView mMapView;
    private GoogleMap gMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    ParkingSpotManager pManager = new ParkingSpotManager();
    private TextView selectedSpotTxt;
    private boolean isMapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_find_parking);

        Bundle mapViewBundle = null;
        if(savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.findParking_map_gmap);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
//        applyInsets(map);

        Button findParkingBtn = findViewById(R.id.findParking_confirm_Btn);
        Button cancelBtn = findViewById(R.id.findParking_cancel_Btn);
        selectedSpotTxt = findViewById(R.id.findParking_location_txt);
        Spinner parkingSpots = findViewById(R.id.findParking_parkingSpots_spinner);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        //fillSpots(parkingSpots); --> Temporarily disabled to test things.
        pManager.addParkingSpot("PAMAK", 40.625511, 22.960412);
        pManager.addParkingSpot("Kaftanzogleio", 40.625096, 22.965412);
        pManager.addParkingSpot("Kaftanzogleio2", 40.625006, 22.965509);

        // Note: If two or more markers have the same name, they will overwrite one another. Save different marker names
        // even if the locations are close to each other.

        ArrayAdapter<String> adapter = new ArrayAdapter<>(FindParking.this,
                android.R.layout.simple_spinner_dropdown_item,
                pManager.getParkingSpotNames());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parkingSpots.setAdapter(adapter);

        findParkingBtn.setOnClickListener(v -> {
            startParking(username, selectedSpotTxt.getText().toString());
            updateUserStats();
            ParkingSpot selectedSpot = pManager.getParkingSpot(selectedSpotTxt.getText().toString());
            startActivity(new Intent(FindParking.this, ParkCompletion.class)
                    .putExtra("username",username).putExtra("selectedSpotTxt", selectedSpotTxt.getText().toString())
                    .putExtra("fee", selectedSpot.getFeePerHour()));
            finish();
        });

        cancelBtn.setOnClickListener(v -> {
            startActivity(new Intent(FindParking.this, UserWelcome.class));
            finish();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        isMapReady = true;
        // Optional: initial camera setup

        HashMap<String, ParkingSpot> pSpots = pManager.getParkingSpots();
        for(ParkingSpot i : pSpots.values()) {
            LatLng position = new LatLng(i.getLat(), i.getLon());
            gMap.addMarker(new MarkerOptions().position(position)).setTitle(i.getName());
        }

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.625511, 22.960412), 16.0f));
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                selectedSpotTxt.setText(marker.getTitle());
                return true;
            }
        });
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        gMap.clear();
//        ParkingSpot selectedSpotTxt = pManager.getParkingSpot(adapterView.getItemAtPosition(i).toString());
//        LatLng position = new LatLng(selectedSpotTxt.getLat(), selectedSpotTxt.getLon());
//        gMap.moveCamera(CameraUpdateFactory.newLatLng(position));
//        gMap.addMarker(new MarkerOptions().position(position)).setTitle("Parking Spot");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void fillSpots(Spinner parkingSpots) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/fillSpots.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
                JSONArray spotsArray = jsonResponse.getJSONArray("spots");

                for (int i = 0; i < spotsArray.length(); i++) {
                    JSONObject spot = spotsArray.getJSONObject(i);
                    String location = spot.getString("location");
                    double lat = spot.getDouble("latitude");
                    double lon = spot.getDouble("longitude");

                    // Add to your manager (adjust based on your implementation)
                    pManager.addParkingSpot(location, lat, lon);
                }

                // UI update must be run on main thread
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FindParking.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            pManager.getParkingSpotNames());

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    parkingSpots.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void updateUserStats() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/updateUserStats.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.connect();

                int responseCode = conn.getResponseCode();
                is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                String response = responseBuilder.toString();
                Log.d("updateUserStats", "Server response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                String message = jsonResponse.optString("message", "");
                String error = jsonResponse.optString("error", "");

                if (!error.isEmpty()) {
                    Log.e("updateUserStats", "Failed to update stats: " + error);
                } else {
                    Log.i("updateUserStats", "Success: " + message);
                }

            } catch (Exception e) {
                Log.e("updateUserStats", "Exception occurred", e);
            } finally {
                try { if (os != null) os.close(); } catch (IOException ignored) {}
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void startParking(String username, String selectedSpot) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/startParking.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare POST data
                String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&location=" + URLEncoder.encode(selectedSpot, "UTF-8");

                os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();

                int responseCode = conn.getResponseCode();
                is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                String response = responseBuilder.toString();
                Log.d("startParking", "Server response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.optBoolean("success", false);
                String message = jsonResponse.optString("message", "No message returned");

                if (!success) {
                    Log.e("startParking", "Parking failed: " + message);
                }

            } catch (Exception e) {
                Log.e("startParking", "Exception occurred", e);
            } finally {
                try { if (os != null) os.close(); } catch (IOException ignored) {}
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}