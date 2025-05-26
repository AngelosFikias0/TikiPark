package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class PaymentDone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_payment_done);

        Button confirmBtn = findViewById(R.id.paymentDone_confirm_btn);

        confirmBtn.setOnClickListener(v -> {
            //Update users wallet
            startActivity(new Intent(PaymentDone.this, UserWelcome.class));
            finish();
        });
    }
}
