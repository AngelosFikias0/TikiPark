package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FindParkingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_find_parking);

        ImageButton cancelSearchBtn = findViewById(R.id.findParkingClearSearchBtn);
        Button findParkingBtn = findViewById(R.id.findParkingConfirmBtn);
        Button cancelBtn = findViewById(R.id.findParkingCancelBtn);
        EditText searchInput = findViewById(R.id.findParkingSearchInputTxt);

        cancelSearchBtn.setOnClickListener(view -> {
            searchInput.setText("");
        });

        findParkingBtn.setOnClickListener(view -> {

        });

        cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(FindParkingActivity.this, UserWelcome.class));
            finish();
        });
    }
}