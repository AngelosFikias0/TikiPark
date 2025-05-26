package com.example.tikiparkapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import java.net.HttpURLConnection;
import java.net.URL;

public class Stats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_stats);

        TextView amountSpent = findViewById(R.id.stats_amountSpent_txt);
        TextView parkingTime = findViewById(R.id.stats_parkingTime_txt);
        TextView totalReservations = findViewById(R.id.stats_reservations_txt);
        Button backBtn = findViewById(R.id.stats_back_btn);

        getUserStatistics(amountSpent,parkingTime,totalReservations);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(Stats.this, UserWelcome.class));
            finish();
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void getUserStatistics(TextView amountSpent, TextView parkingTime, TextView totalReservations) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/getUserStats.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // Get username from intent
                String username = getIntent().getStringExtra("username");
                if (username == null) username = "";

                String postData = "username=" + java.net.URLEncoder.encode(username, "UTF-8");

                // Write POST data to request body
                conn.getOutputStream().write(postData.getBytes());

                int responseCode = conn.getResponseCode();
                is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    double spent = jsonResponse.getDouble("total_amount_spent");
                    int reservations = jsonResponse.getInt("total_reservations");
                    int time = jsonResponse.getInt("total_parking_time");

                    runOnUiThread(() -> {
                        amountSpent.append(String.format("%.2f €", spent));
                        totalReservations.append(String.valueOf(reservations));
                        parkingTime.append(time + " mins");
                    });
                } else {
                    runOnUiThread(() -> {
                        amountSpent.setText("Error loading stats");
                        totalReservations.setText("-");
                        parkingTime.setText("-");
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    amountSpent.setText("Error");
                    totalReservations.setText("-");
                    parkingTime.setText("-");
                });
            } finally {
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

}