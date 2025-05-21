package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class PaymentForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_payment_form);

        TextView cost = findViewById(R.id.payment_cost_txt);
        EditText cardNumber = findViewById(R.id.payment_cardNum_editTxt);
        EditText cardholderName = findViewById(R.id.payment_cardholderName_editTxt);
        EditText expirtaionMonth = findViewById(R.id.payment_expirationMonth_editTxt);
        EditText expirationYear = findViewById(R.id.payment_expirationYear_editTxt);
        Button confirmBtn = findViewById(R.id.payment_confirm_btn);
        Button declineBtn = findViewById(R.id.payment_decline_btn);
        Intent intent = getIntent();
        double amount = intent.getDoubleExtra("amount",0.0);
        String username = intent.getStringExtra("username");

        confirmBtn.setOnClickListener(v -> {
            String cardNumberStr = cardNumber.getText().toString().trim();
            String cardholderNameStr = cardholderName.getText().toString().trim();
            String expirationMonthStr = expirtaionMonth.getText().toString().trim();
            String expirationYearStr = expirationYear.getText().toString().trim();

            if (!cardNumberStr.isEmpty() &&
                    !cardholderNameStr.isEmpty() &&
                    !expirationMonthStr.isEmpty() &&
                    !expirationYearStr.isEmpty()) {

                startActivity(new Intent(PaymentForm.this, AddFunds.class)
                        .putExtra("amount", amount)
                        .putExtra("cause", "Deposit")
                        .putExtra("username", username));
                finish();

            } else {
                Toast.makeText(PaymentForm.this, "Please fill in all payment fields", Toast.LENGTH_SHORT).show();
            }
        });

        declineBtn.setOnClickListener(v -> {
            startActivity(new Intent(PaymentForm.this, UserWelcome.class));
            finish();
        });

    }
}
