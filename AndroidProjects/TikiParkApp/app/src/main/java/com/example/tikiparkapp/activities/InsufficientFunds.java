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

        Intent intent = getIntent();
        String cause = intent.getStringExtra("cause");
        double amount = intent.getDoubleExtra("amount",0.0);
        String username = intent.getStringExtra("username");

        if(cause.equalsIgnoreCase("Withdraw")){
            addMoneyBtn.setText("Okay");
        }
        addMoneyBtn.setOnClickListener(v -> {
            if(cause.equalsIgnoreCase("Withdraw")){
                startActivity(new Intent(InsufficientFunds.this, UserWelcome.class));
            }else{
                startActivity(new Intent(InsufficientFunds.this, PaymentForm.class).putExtra("amount",amount).putExtra("cause",cause).putExtra("username",username));
            }
        });

    }
}
