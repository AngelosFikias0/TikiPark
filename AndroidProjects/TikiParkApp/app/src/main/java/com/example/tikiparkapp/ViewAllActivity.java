package com.example.tikiparkapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewAllActivity extends AppCompatActivity {

    ListView spotsList;
    Button deleteSpot;

    ParkingSpot selectedItem;
    ArrayList<ParkingSpot> spots = new ArrayList<>();
    ArrayAdapter<ParkingSpot> spotsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        spotsList = findViewById(R.id.list_spots);
        deleteSpot = findViewById(R.id.btn_delete);

        // Fetch parking spots from the Database.
        // Store the data in ParkingSpot objects.
            // (Created locally for now)
        spots.add(new ParkingSpot(1, "Kato Toumpa",
                STATUS.AVAILABLE, 0.3f,
                123.24f, 145.54f));

        spots.add(new ParkingSpot(2, "Kato Toumpa",
                STATUS.OCCUPIED, 0.2f,
                156.75f, 199.23f));

        spots.add(new ParkingSpot(3, "Kalamaria",
                STATUS.AVAILABLE, 0.2f,
                249f, 28f));

        spots.add(new ParkingSpot(4, "PAMAK",
                STATUS.RESERVED, 0.2f,
                25f, 400f));

        spotsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, spots);
        spotsList.setAdapter(spotsAdapter);

        spotsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Toast.makeText(ViewAllActivity.this, spotsList.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                selectedItem = (ParkingSpot) spotsList.getItemAtPosition(i);
            }
        });

        deleteSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedItem != null) {
                    spots.remove(selectedItem);
                }

                spotsAdapter.notifyDataSetChanged();
            }
        });
    }
}