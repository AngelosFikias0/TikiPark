package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class WalletManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wallet);

        EditText amount = findViewById(R.id.wallet_amount_editTxt);
        Button backBtn = findViewById(R.id.wallet_back_btn);
        Button depositBtn = findViewById(R.id.wallet_deposit_btn);
        Button withdrawBtn = findViewById(R.id.wallet_withdraw_btn);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(WalletManagement.this, UserWelcome.class));
            finish();
        });

        depositBtn.setOnClickListener(v -> {

        });

        withdrawBtn.setOnClickListener(v -> {

        });
    }
}