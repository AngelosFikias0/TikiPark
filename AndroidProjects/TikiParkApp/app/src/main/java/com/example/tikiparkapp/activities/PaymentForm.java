package com.example.tikiparkapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        confirmBtn.setOnClickListener(v -> {

        });

        declineBtn.setOnClickListener(v -> {
            // TODO return to previous screen.
            finish();
        });

    }
}
