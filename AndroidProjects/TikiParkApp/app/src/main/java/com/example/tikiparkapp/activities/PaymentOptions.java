package com.example.tikiparkapp.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class PaymentOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_payment_options);

        TextView duration = findViewById(R.id.paymentOptions_duration_txt);
        TextView cost = findViewById(R.id.paymentOptions_cost_txt);
        ImageButton paypalBtn = findViewById(R.id.paymentOptions_paypal_imgBtn);
        ImageButton mastercardBtn = findViewById(R.id.paymentOptions_mastercard_imgBtn);
        ImageButton visaBtn = findViewById(R.id.paymentOptions_visa_imgBtn);

        mastercardBtn.setOnClickListener(v -> {

        });

        paypalBtn.setOnClickListener(v -> {

        });

        visaBtn.setOnClickListener(v -> {

        });
    }
}
