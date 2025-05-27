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

//          if (validatePaymentFields(cardNumberStr, cardholderNameStr, expirationMonthStr, expirationYearStr)) {
            if (true) {
                // Proceed to add funds
                Intent addFundsIntent = new Intent(PaymentForm.this, AddFunds.class);
                addFundsIntent.putExtra("fee", fee);
                addFundsIntent.putExtra("cause", cause);
                addFundsIntent.putExtra("username", username);
                addFundsIntent.putExtra("balance", balance);
                startActivity(addFundsIntent);
                finish();
            } else {
                Toast.makeText(PaymentForm.this, "Please enter valid card details.", Toast.LENGTH_SHORT).show();
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

    /*private boolean validatePaymentFields(String cardNumber, String cardholder, String month, String year) {
        if (cardNumber.length() < 13 || cardNumber.length() > 19 || !cardNumber.matches("\\d+")) return false;
        if (cardholder.length() < 3) return false;
        if (!month.matches("\\d{1,2}") || !year.matches("\\d{2,4}")) return false;

        int mm = Integer.parseInt(month);
        int yy = Integer.parseInt(year.length() == 2 ? "20" + year : year);

        return mm >= 1 && mm <= 12 && yy >= 2024;
    }*/
}