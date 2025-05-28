package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
    private double balance = 0;
    private double fee = 0;
    private long startTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_find_parking);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.findParking_map_gmap);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        Button findParkingBtn = findViewById(R.id.findParking_confirm_Btn);
        Button cancelBtn = findViewById(R.id.findParking_cancel_Btn);
        selectedSpotTxt = findViewById(R.id.findParking_location_txt);
        Spinner parkingSpots = findViewById(R.id.findParking_parkingSpots_spinner);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        fillSpots(parkingSpots);

        balance = getBalance(username);

        startTimeMillis = System.currentTimeMillis();

        findParkingBtn.setOnClickListener(v -> {
            startParking(username, selectedSpotTxt.getText().toString(),startTimeMillis);
            fee = getSpotFee(selectedSpotTxt.getText().toString());
            startActivity(new Intent(FindParking.this, ParkCompletion.class)
                    .putExtra("username", username)
                    .putExtra("selectedSpotTxt", selectedSpotTxt.getText().toString())
                    .putExtra("fee", fee)
                    .putExtra("balance", balance)
                    .putExtra("startTime",startTimeMillis));
            finish();
        });

        cancelBtn.setOnClickListener(v -> {
            startActivity(new Intent(FindParking.this, UserWelcome.class).putExtra("username",username));
            finish();
        });
    }

    private double getSpotFee(String spot) {
        final double[] cost = { 0 };
        Thread thread = new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/getSpotFee.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "spot=" + URLEncoder.encode(spot, "UTF-8");

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
                Log.d("getSpotFee", "Server response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.optBoolean("success", false);
                if (success) {
                    cost[0] = jsonResponse.optDouble("price_per_hour", 0);
                } else {
                    Log.e("getSpotFee", "Fee fetch failed: " + jsonResponse.optString("message", "No message"));
                }

            } catch (Exception e) {
                Log.e("getSpotFee", "Exception occurred", e);
            } finally {
                try { if (os != null) os.close(); } catch (IOException ignored) {}
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cost[0];
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

                    pManager.addParkingSpot(location, lat, lon);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FindParking.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            pManager.getParkingSpotNames());

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    parkingSpots.setAdapter(adapter);
                    parkingSpots.setOnItemSelectedListener(FindParking.this);
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

    public double getBalance(String username){
        final double[] balance = { 0 };
        Thread thread = new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/getBalance.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "username=" + URLEncoder.encode(username, "UTF-8");

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
                Log.d("getSpotFee", "Server response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.optBoolean("success", false);
                if (success) {
                    balance[0] = jsonResponse.optDouble("wallet_balance", 0);
                } else {
                    Log.e("getSpotFee", "Fee fetch failed: " + jsonResponse.optString("message", "No message"));
                }

            } catch (Exception e) {
                Log.e("getBalance", "Exception occurred", e);
            } finally {
                try { if (os != null) os.close(); } catch (IOException ignored) {}
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return balance[0];
    }

    private void startParking(String username, String selectedSpot, long startTimeMillis) {
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
                        "&location=" + URLEncoder.encode(selectedSpot, "UTF-8")+
                        "&startTime=" + URLEncoder.encode(String.valueOf(startTimeMillis), "UTF-8");

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

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        gMap = map;
        isMapReady = true;

        HashMap<String, ParkingSpot> pSpots = pManager.getParkingSpots();
        for (ParkingSpot spot : pSpots.values()) {
            LatLng position = new LatLng(spot.getLat(), spot.getLon());
            gMap.addMarker(new MarkerOptions().position(position).title(spot.getName()));
        }

        LatLng initialPosition = new LatLng(21.291982, -157.843144);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 16.0f));

        gMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            selectedSpotTxt.setText(title);

            Spinner spinner = findViewById(R.id.findParking_parkingSpots_spinner);
            SpinnerAdapter rawAdapter = spinner.getAdapter();

            if (rawAdapter instanceof ArrayAdapter) {
                @SuppressWarnings("unchecked")
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) rawAdapter;
                int pos = adapter.getPosition(title);
                if (pos >= 0) spinner.setSelection(pos);
            } else {
                Log.e("SpinnerSetup", "Unexpected adapter type for spinner");
            }
            return false; // allow default behavior (show info window)
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (!isMapReady) return;

        ParkingSpot selectedSpot = pManager.getParkingSpot(adapterView.getItemAtPosition(i).toString());
        System.out.println(selectedSpot.getName());

        selectedSpotTxt.setText(selectedSpot.getName());

        gMap.clear();
        LatLng position = new LatLng(selectedSpot.getLat(), selectedSpot.getLon());
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16.0f));
        gMap.addMarker(new MarkerOptions().position(position).title(selectedSpot.getName()));
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
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}