package com.example.tikiparkapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class PaymentForm extends AppCompatActivity {

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_payment_form);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String cause = intent.getStringExtra("cause");
        double fee = intent.getDoubleExtra("fee", 0.0);
        String username = intent.getStringExtra("username");
        double balance = intent.getDoubleExtra("balance", 0.0);

        if (cause == null || username == null) {
            finish();
            return;
        }

        TextView cost = findViewById(R.id.payment_cost_txt);
        cost.setText(String.format("%.2f €", fee));

        EditText cardNumber = findViewById(R.id.payment_cardNum_editTxt);
        EditText cardholderName = findViewById(R.id.payment_cardholderName_editTxt);
        EditText expirationMonth = findViewById(R.id.payment_expirationMonth_editTxt);
        EditText expirationYear = findViewById(R.id.payment_expirationYear_editTxt);

        Button confirmBtn = findViewById(R.id.payment_confirm_btn);
        Button declineBtn = findViewById(R.id.payment_decline_btn);

        confirmBtn.setOnClickListener(v -> {
            String cardNumberStr = cardNumber.getText().toString().trim();
            String cardholderNameStr = cardholderName.getText().toString().trim();
            String expirationMonthStr = expirationMonth.getText().toString().trim();
            String expirationYearStr = expirationYear.getText().toString().trim();

            if (!cardNumberStr.isEmpty() &&
                    !cardholderNameStr.isEmpty() &&
                    !expirationMonthStr.isEmpty() &&
                    !expirationYearStr.isEmpty()) {

                //Add Funds
                Intent addFundsIntent = new Intent(PaymentForm.this, AddFunds.class);
                addFundsIntent.putExtra("fee", fee);
                addFundsIntent.putExtra("cause", cause);
                addFundsIntent.putExtra("username", username);
                addFundsIntent.putExtra("balance", balance);
                startActivity(addFundsIntent);
                finish();

            } else {
                Toast.makeText(PaymentForm.this, "Please fill in all payment fields!", Toast.LENGTH_SHORT).show();
            }
        });

        declineBtn.setOnClickListener(v -> {
            if (!cause.equalsIgnoreCase("Pay")) {
                startActivity(new Intent(PaymentForm.this, UserWelcome.class));
                finish();
            } else {
                Toast.makeText(PaymentForm.this, "You have to complete the payment.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}