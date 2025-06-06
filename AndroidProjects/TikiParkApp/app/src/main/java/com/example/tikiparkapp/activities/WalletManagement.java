package com.example.tikiparkapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class WalletManagement extends AppCompatActivity {
    private double balance = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wallet);

        EditText amountTxt = findViewById(R.id.wallet_amount_editTxt);
        Button backBtn = findViewById(R.id.wallet_back_btn);
        Button depositBtn = findViewById(R.id.wallet_deposit_btn);
        Button withdrawBtn = findViewById(R.id.wallet_withdraw_btn);
        TextView txtBalance = findViewById(R.id.manage_wallet_balance);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        //Returns user's balance
        balance = getBalance(username);

        txtBalance.setText("Balance: €" + balance);
        //Back to the Main User Screen
        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(WalletManagement.this, UserWelcome.class).putExtra("username",username));
            finish();
        });

        //1. Scenario
        depositBtn.setOnClickListener(v -> {
            if (!amountTxt.getText().toString().isEmpty()) {
                double amount = Double.parseDouble(amountTxt.getText().toString());
                //1.1 Scenario -> PayForm
                startActivity(new Intent(WalletManagement.this, PaymentForm.class)
                        .putExtra("fee", amount)
                        .putExtra("cause", "Deposit")
                        .putExtra("username", username)
                        .putExtra("balance", balance));
                finish();
            } else
            //1.2 Scenario -> Toast
            {
                Toast.makeText(WalletManagement.this, "Please Put the amount first!", Toast.LENGTH_SHORT).show();
            }
        });

        //2. Scenario
        withdrawBtn.setOnClickListener(v -> {
            if (!amountTxt.getText().toString().isEmpty()) {
                double fee = Double.parseDouble(amountTxt.getText().toString());
                //2.1 Scenario -> PaymentDone
                if (fee < balance) {
                    startActivity(new Intent(WalletManagement.this, PaymentDone.class)
                            .putExtra("username", username)
                            .putExtra("balance",balance-fee));
                } //2.2 Scenario -> Insufficient
                else{
                    startActivity(new Intent(WalletManagement.this, InsufficientFunds.class)
                            .putExtra("fee", fee)
                            .putExtra("username", username)
                            .putExtra("cause", "withdraw")
                            .putExtra("balance",balance));
                }
                finish();
            } else
            //2.3 Scenario -> Toast
            {
                Toast.makeText(WalletManagement.this, "Please Put the amount first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Returns user's balance
    public double getBalance(String username){
        final double[] balance = { 0 };
        Thread thread = new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream is = null;

            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/getBalance.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "username=" + URLEncoder.encode(username, "UTF-8");

                os = conn.getOutputStream();
                os.write(postData.getBytes());
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
                Log.d("getSpotFee", "Server response: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.optBoolean("success", false);
                if (success) {
                    balance[0] = jsonResponse.optDouble("wallet_balance", 0);
                } else {
                    Log.e("getSpotFee", "Fee fetch failed: " + jsonResponse.optString("message", "No message"));
                }

            } catch (Exception e) {
                Log.e("getBalance", "Exception occurred", e);
            } finally {
                try { if (os != null) os.close(); } catch (IOException ignored) {}
                try { if (reader != null) reader.close(); } catch (IOException ignored) {}
                try { if (is != null) is.close(); } catch (IOException ignored) {}
                if (conn != null) conn.disconnect();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return balance[0];
    }
}