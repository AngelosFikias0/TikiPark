package com.example.tikiparkapp;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewAllActivity extends AppCompatActivity {

    LinearLayout spotContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        spotContainer = findViewById(R.id.spot_list_container);

        // Fetch parking spots from the Database.
                // (Created temporarily)
        ParkingSpot[] spotArray = new ParkingSpot[2];
        spotArray[0] = new ParkingSpot(1, "Temp",
                STATUS.AVAILABLE, 0.3f,
                123.24f, 145.54f);

        spotArray[1] = new ParkingSpot(2, "Temp",
                STATUS.OCCUPIED, 0.2f,
                156.75f, 199.23f);

        for(int i = 0; i < 2; i++) {
            TextView spotText = new TextView(ViewAllActivity.this);
            spotText.setText(spotArray[i].id);
            spotContainer.addView(spotText);
        }
    }
}