package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class ParkCompletion extends AppCompatActivity {

    private TextView durationTextView, costTextView, startTimeTextView;
    private Button payButton;

    private double feePerHour;
    private long startTimeMillis;
    private double balance;

    private Handler handler = new Handler();
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_park_completion);

        // Initialize UI components
        durationTextView = findViewById(R.id.parkCompletion_duration_txt);
        costTextView = findViewById(R.id.parkCompletion_cost_txt);
        startTimeTextView = findViewById(R.id.parkCompletion_startTime_txt);
        payButton = findViewById(R.id.parkCompletion_confirm_btn);

        // Extract data from Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String spot = intent.getStringExtra("selectedSpotTxt");
        feePerHour = intent.getDoubleExtra("fee", 0);
        balance = intent.getDoubleExtra("balance", 0);

        startTimeMillis = System.currentTimeMillis() - 1000 * 60 * 1; // Simulate start 1 minute ago
        startTimeTextView.setText(DateFormat.format("HH:mm:ss", startTimeMillis));

        // Payment button logic
        payButton.setOnClickListener(view -> {
            //Close transaction
            double currentCost = calculateCost();
            if (balance < currentCost) {
                Intent insufficientIntent = new Intent(ParkCompletion.this, InsufficientFunds.class);
                insufficientIntent.putExtra("fee", currentCost);
                insufficientIntent.putExtra("cause", "Pay");
                insufficientIntent.putExtra("username", username);
                insufficientIntent.putExtra("balance", balance);
                startActivity(insufficientIntent);
            } else {
                //Update Wallet
                updateWallet(balance-currentCost);
                startActivity(new Intent(ParkCompletion.this, PaymentDone.class));
            }
            finish();
        });

        // Start real-time updates
        startUpdatingDurationAndCost();
    }

    private void updateWallet(double newBalance) {

    }

    private void startUpdatingDurationAndCost() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateDurationAndCostUI();
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        };
        handler.post(updateRunnable);
    }

    private void updateDurationAndCostUI() {
        long now = System.currentTimeMillis();
        long durationMillis = now - startTimeMillis;

        long totalMinutes = durationMillis / (60 * 1000);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        double cost = calculateCost();

        durationTextView.setText(String.format("%02d:%02d", hours, minutes));
        costTextView.setText(String.format("€%.2f", cost));
    }

    private double calculateCost() {
        long now = System.currentTimeMillis();
        long durationMillis = now - startTimeMillis;
        return (durationMillis / (3600.0 * 1000.0)) * feePerHour;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}