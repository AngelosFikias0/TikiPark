package com.example.tikiparkapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

public class PaymentDone extends AppCompatActivity {

    private Handler mainHandler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_payment_done);

        mainHandler = new Handler(Looper.getMainLooper());
        Button confirmBtn = findViewById(R.id.paymentDone_confirm_btn);

        //Gets intent and extras
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        double balance = intent.getDoubleExtra("balance", 0);

        confirmBtn.setOnClickListener(v -> {
            // Disable button to prevent multiple clicks
            confirmBtn.setEnabled(false);
            confirmBtn.setText("Processing...");

            //Updates the user's wallet
            updateWallet(username, balance);

            // Navigate back to the main user screen after a short delay to allow the update to process
            mainHandler.post(() -> {
                Toast.makeText(PaymentDone.this, "Payment completed successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PaymentDone.this, UserWelcome.class)
                        .putExtra("username",username));
                finish();
            });

        });
    }

    //Updates the user's wallet
    private void updateWallet(String username, double newBalance) {
        Thread thread = new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            OutputStream os = null;

            try {
                // Make sure this matches your PHP filename
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/updateUserWallet.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&balance=" + URLEncoder.encode(String.valueOf(newBalance), "UTF-8");

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
                Log.d("updateUserWallet", "Server response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                    Log.i("updateUserWallet", "Wallet updated successfully");

                    // Show success message on main thread
                    mainHandler.post(() -> Toast.makeText(PaymentDone.this, "Payment completed successfully!", Toast.LENGTH_SHORT).show());
                } else {
                    String error = jsonResponse.optString("error", "Unknown error");
                    Log.e("updateUserWallet", "Server error: " + error);

                    // Show error message on main thread
                    mainHandler.post(() -> Toast.makeText(PaymentDone.this, "Update failed: " + error, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("updateUserWallet", "Exception occurred", e);

                // Show error message on main thread
                mainHandler.post(() -> Toast.makeText(PaymentDone.this, "Network error occurred", Toast.LENGTH_LONG).show());
            } finally {
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                try { if (os != null) os.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
        thread.start();
    }
}