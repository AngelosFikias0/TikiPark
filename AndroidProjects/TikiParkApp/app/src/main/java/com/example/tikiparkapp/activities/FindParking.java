package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

public class FindParking extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private MapView mMapView;
    private GoogleMap gMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    ParkingSpotManager pManager = new ParkingSpotManager();

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

//        EditText searchInputText = findViewById(R.id.findParking_Search_InputTxt);
//        ImageButton clearSearchBtn = findViewById(R.id.findParking_ClearSearch_ImgBtn);
        Button findParkingBtn = findViewById(R.id.findParking_confirm_Btn);
        Button cancelBtn = findViewById(R.id.findParking_cancel_Btn);
        Spinner parkingSpots = findViewById(R.id.findParking_parkingSpots_spinner);
        parkingSpots.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        fillSpots(parkingSpots);

        findParkingBtn.setOnClickListener(v -> {
            startActivity(new Intent(FindParking.this, ParkCompletion.class).putExtra("username",username));
            finish();
            gMap.addMarker(new MarkerOptions().position(new LatLng(25, 25)).title("Marker"));
        });

        cancelBtn.setOnClickListener(v -> {
            startActivity(new Intent(FindParking.this, UserWelcome.class));
            finish();
        });
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
        //gMap.addMarker(new MarkerOptions().position(new LatLng(39.362356, 22.946812)).title("Marker"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.315603, -157.858093), 16.0f));
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
        gMap.clear();
        ParkingSpot selectedSpot = pManager.getParkingSpot(adapterView.getItemAtPosition(i).toString());
        LatLng position = new LatLng(selectedSpot.getLat(), selectedSpot.getLon());
        gMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        gMap.addMarker(new MarkerOptions().position(position)).setTitle("Parking Spot");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}