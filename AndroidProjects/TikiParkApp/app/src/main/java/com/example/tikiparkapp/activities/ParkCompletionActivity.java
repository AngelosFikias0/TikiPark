package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class ParkCompletionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_park_completion);

        Button backHome = findViewById(R.id.parkCompletion_confirm_btn);
        TextView duration = findViewById(R.id.parkCompletion_duration_txt);
        TextView cost = findViewById(R.id.parkCompletion_cost_txt);
        TextView startTime = findViewById(R.id.parkCompletion_startTime_txt);

        backHome.setOnClickListener(view -> {
            startActivity(new Intent(ParkCompletionActivity.this, UserWelcome.class));
            finish();
        });
    }
}
