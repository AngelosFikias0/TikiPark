package com.example.tikiparkapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ParkCompletion extends AppCompatActivity {

    private TextView durationTextView;
    private TextView costTextView;

    private double feePerHour;
    private double prevBalance;

    private final Handler handler = new Handler();
    private Runnable updateRunnable;
    private long startTimeMillis;
    private long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_park_completion);

        // Initialize UI components
        durationTextView = findViewById(R.id.parkCompletion_duration_txt);
        costTextView = findViewById(R.id.parkCompletion_cost_txt);
        TextView startTimeTextView = findViewById(R.id.parkCompletion_startTime_txt);
        Button payButton = findViewById(R.id.parkCompletion_confirm_btn);

        // Extract data from Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String spot = intent.getStringExtra("selectedSpotTxt");
        feePerHour = intent.getDoubleExtra("fee", 0);
        prevBalance = intent.getDoubleExtra("balance", 0);
        startTimeMillis = intent.getLongExtra("startTime",0);

        startTimeTextView.setText(DateFormat.format("HH:mm:ss", startTimeMillis));

        // Payment button logic
        payButton.setOnClickListener(view -> {
            double currentCost = calculateCost();
            endTime = System.currentTimeMillis()+1000 * 60;
            finishReservation(spot,username,currentCost,endTime);
            if (prevBalance < currentCost) {
                Intent insufficientIntent = new Intent(ParkCompletion.this, InsufficientFunds.class);
                insufficientIntent.putExtra("fee", currentCost);
                insufficientIntent.putExtra("cause", "Pay");
                insufficientIntent.putExtra("username", username);
                insufficientIntent.putExtra("balance", prevBalance);
                startActivity(insufficientIntent);
            } else {
                startActivity(new Intent(ParkCompletion.this, PaymentDone.class)
                        .putExtra("balance",prevBalance-currentCost)
                        .putExtra("username",username)
                        .putExtra("cause","pay"));
            }
            finish();
        });

        // Start real-time updates
        startUpdatingDurationAndCost();
    }

    private void finishReservation(String spot, String username, double currentCost, long endTime){
        Thread thread = new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            OutputStream os = null;

            try {
                // Make sure this matches your PHP filename
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/finishReservation.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "spot=" + URLEncoder.encode(spot, "UTF-8") +
                        "&username=" + URLEncoder.encode(username, "UTF-8") +
                        "&cost=" + URLEncoder.encode(String.valueOf(currentCost), "UTF-8") +
                        "&time=" + URLEncoder.encode(String.valueOf(endTime), "UTF-8");

                os = conn.getOutputStream();
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();

                int responseCode = conn.getResponseCode();
                is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                String response = responseBuilder.toString();
                Log.d("finishReservation", "Server response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                    Log.i("finishReservation", "Wallet updated successfully");
                } else {
                    String error = jsonResponse.optString("error", "Unknown error");
                    Log.e("finishReservation", "Server error: " + error);
                }

            } catch (Exception e) {
                Log.e("finishReservation", "Exception occurred", e);
            } finally {
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                try { if (os != null) os.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
        thread.start();
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

    @SuppressLint("DefaultLocale")
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