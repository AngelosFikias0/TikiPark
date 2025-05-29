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
        TextView currentAmount = findViewById(R.id.insufFunds_currentAmount_Txt); // ✅ Fixed ID
        TextView difference = findViewById(R.id.insufFunds_difference_Txt);
        Button addMoneyBtn = findViewById(R.id.insufFunds_addMoney_Btn);

        Intent intent = getIntent();
        String cause = intent.getStringExtra("cause");
        double fee = intent.getDoubleExtra("fee", 0.0);
        double balance = intent.getDoubleExtra("balance", 0.0);
        String username = intent.getStringExtra("username");

        // Calculate difference
        double diff = Math.max(fee - balance, 0.0);

        // Populate values in TextViews
        requiredAmount.setText(String.format("Required: €%.2f", fee));
        currentAmount.setText(String.format("Current: €%.2f", balance));
        difference.setText(String.format("Deficit: €%.2f", diff));

        //Withdraw option
        if ("Withdraw".equalsIgnoreCase(cause)) {
            addMoneyBtn.setText("Okay");
        }

        //Add money option
        addMoneyBtn.setOnClickListener(v -> {
            if ("Withdraw".equalsIgnoreCase(cause)) {
                startActivity(new Intent(InsufficientFunds.this, UserWelcome.class).putExtra("username",username));
            } else {
                startActivity(new Intent(InsufficientFunds.this, PaymentForm.class)
                        .putExtra("fee", fee)
                        .putExtra("cause", cause)
                        .putExtra("balance", balance)
                        .putExtra("username", username));
            }
        });
    }
}