package com.example.tikiparkapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class InsufficientFundsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_insufficient_funds);

        TextView requiredAmount = findViewById(R.id.insufFunds_requiredAmount_Txt);
        TextView currentAmount = findViewById(R.id.insufFunds_requiredAmount_Txt);
        TextView difference = findViewById(R.id.insufFunds_difference_Txt);
        Button addMoneyBtn = findViewById(R.id.insufFunds_addMoney_Btn);
        Button declineBtn = findViewById(R.id.insufFunds_decline_Btn);

        addMoneyBtn.setOnClickListener(v -> {
            // TODO Add money code.
        });

        declineBtn.setOnClickListener(v -> {
            // TODO startActivity(); go to previous activity.
            finish();
        });
    }
}
