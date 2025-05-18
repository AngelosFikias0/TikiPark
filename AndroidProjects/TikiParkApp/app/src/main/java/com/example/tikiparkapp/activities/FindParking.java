package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class FindParking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_find_parking);

        ImageButton clearSearchBtn = findViewById(R.id.findParking_ClearSearch_ImgBtn);
        Button findParkingBtn = findViewById(R.id.findParking_Confirm_Btn);
        Button cancelBtn = findViewById(R.id.findParking_Cancel_Btn);
        EditText searchInputText = findViewById(R.id.findParking_Search_InputTxt);

        clearSearchBtn.setOnClickListener(view -> {
            searchInputText.setText("");
        });

        findParkingBtn.setOnClickListener(view -> {
            // TODO Find parking code.
        });

        cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(FindParking.this, UserWelcome.class));
            finish();
        });
    }
}