package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.ParkingSpot;
import com.example.tikiparkapp.ParkingSpotManager;
import com.example.tikiparkapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;

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

        pManager.addParkingSpot("Gkaite Tzovaropoulou 12", 40.623737, 22.964687);
        pManager.addParkingSpot("Lysimachou Kaftanzoglou", 40.624566, 22.964454);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                pManager.getParkingSpotNames());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parkingSpots.setAdapter(adapter);

//        clearSearchBtn.setOnClickListener(v -> {
//            searchInputText.setText("");
//        });

        findParkingBtn.setOnClickListener(v -> {
            // TODO Find parking code.
            gMap.addMarker(new MarkerOptions().position(new LatLng(25, 25)).title("Marker"));
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
        //gMap.addMarker(new MarkerOptions().position(new LatLng(39.362356, 22.946812)).title("Marker"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.362356, 22.946812), 16.0f));
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
        ParkingSpot selectedSpot = pManager.getParkingSpot(adapterView.getItemAtPosition(i).toString());
        LatLng position = new LatLng(selectedSpot.getLat(), selectedSpot.getLon());
        gMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        gMap.addMarker(new MarkerOptions().position(position)).setTitle("Parking Spot");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}