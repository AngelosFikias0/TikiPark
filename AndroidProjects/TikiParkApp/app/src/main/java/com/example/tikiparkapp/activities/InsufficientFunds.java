package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class InsufficientFunds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_insufficient_funds);

        TextView requiredAmount = findViewById(R.id.insufFunds_requiredAmount_Txt);
        TextView currentAmount = findViewById(R.id.insufFunds_requiredAmount_Txt);
        TextView difference = findViewById(R.id.insufFunds_difference_Txt);
        Button addMoneyBtn = findViewById(R.id.insufFunds_addMoney_Btn);
        Button declineBtn = findViewById(R.id.insufFunds_decline_Btn);

        Intent intent = getIntent();
        String cause = intent.getStringExtra("cause");
        double amount = intent.getDoubleExtra("amount",0.0);
        String username = intent.getStringExtra("username");

        addMoneyBtn.setOnClickListener(v -> {
            startActivity(new Intent(InsufficientFunds.this, AddFunds.class).putExtra("amount",amount).putExtra("cause","Deposit").putExtra("username",username));
        });

        declineBtn.setOnClickListener(v -> {
            // TODO startActivity(); go to previous activity.
            finish();
        });
    }
}
