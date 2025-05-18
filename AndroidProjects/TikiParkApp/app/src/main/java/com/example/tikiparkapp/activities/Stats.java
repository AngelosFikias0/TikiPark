package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

import org.w3c.dom.Text;

public class Stats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_stats);

        TextView amountSpent = findViewById(R.id.stats_amountSpent_txt);
        TextView parkingTime = findViewById(R.id.stats_parkingTime_txt);
        TextView totalReservations = findViewById(R.id.stats_reservations_txt);
        Button backBtn = findViewById(R.id.stats_back_btn);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(Stats.this, UserWelcome.class));
            finish();
        });
    }
}