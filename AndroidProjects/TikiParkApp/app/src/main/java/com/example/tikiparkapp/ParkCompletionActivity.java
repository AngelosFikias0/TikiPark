package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ParkCompletionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_park_completion);

        Button backHome = findViewById(R.id.parkCompletionBackBtn);

        backHome.setOnClickListener(view -> {
            startActivity(new Intent(ParkCompletionActivity.this, UserWelcome.class));
            finish();
        });
    }
}
