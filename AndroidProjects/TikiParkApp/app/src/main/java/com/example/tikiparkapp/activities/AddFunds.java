package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class AddFunds extends AppCompatActivity {

    private static final double EPSILON = 0.001;

    private EditText amountInput;
    private double currentBalance;
    private double requiredFee;
    private String username;
    private String cause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_funds);

        // UI references
        amountInput = findViewById(R.id.addFunds_Amount_EdTxt);
        Button confirmBtn = findViewById(R.id.addFunds_Confirm_Btn);
        Button declineBtn = findViewById(R.id.insufFunds_decline_Btn);

        // Get Intent data
        Intent intent = getIntent();
        cause = intent.getStringExtra("cause");
        currentBalance = intent.getDoubleExtra("balance", 0.0);
        requiredFee = intent.getDoubleExtra("fee", 0.0);
        username = intent.getStringExtra("username");

        // Autofill the fee
        amountInput.setText(String.format("%.2f", requiredFee));

        // Decline button logic
        declineBtn.setOnClickListener(v -> {
            if ("Deposit".equalsIgnoreCase(cause)) {
                startActivity(new Intent(AddFunds.this, UserWelcome.class));
                finish();
            } else if ("Pay".equalsIgnoreCase(cause)) {
                Toast.makeText(this, "You must complete your payment before leaving.", Toast.LENGTH_SHORT).show();
            }
        });

        // Confirm button logic
        confirmBtn.setOnClickListener(v -> {
            String input = amountInput.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
                return;
            }

            double enteredAmount;
            try {
                enteredAmount = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredAmount <= 0) {
                Toast.makeText(this, "Amount must be greater than zero.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Route based on cause
            if ("Deposit".equalsIgnoreCase(cause)) {
                handleDeposit(enteredAmount);
            } else if ("Pay".equalsIgnoreCase(cause)) {
                handlePayment(enteredAmount);
            }
        });
    }

    //Handles the deposit
    private void handleDeposit(double amount) {
        double newBalance = currentBalance + amount;
        Intent intent = new Intent(this, PaymentDone.class);
        intent.putExtra("username", username);
        intent.putExtra("balance", newBalance);
        startActivity(intent);
        finish();
    }

    //Handles the payment
    private void handlePayment(double amount) {
        if ((currentBalance + amount + EPSILON) < requiredFee) {
            Toast.makeText(this, "Even after adding funds, balance is insufficient.", Toast.LENGTH_LONG).show();
            return;
        }

        double finalBalance = (currentBalance + amount) - requiredFee;
        Intent intent = new Intent(this, PaymentDone.class);
        intent.putExtra("username", username);
        intent.putExtra("balance", finalBalance);
        intent.putExtra("cause", "pay");
        startActivity(intent);
        finish();
    }
}