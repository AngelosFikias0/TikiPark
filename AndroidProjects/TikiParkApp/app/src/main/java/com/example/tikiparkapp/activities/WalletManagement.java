package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class WalletManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wallet);

        EditText amountTxt = findViewById(R.id.wallet_amount_editTxt);
        Button backBtn = findViewById(R.id.wallet_back_btn);
        Button depositBtn = findViewById(R.id.wallet_deposit_btn);
        Button withdrawBtn = findViewById(R.id.wallet_withdraw_btn);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(WalletManagement.this, UserWelcome.class));
            finish();
        });

        depositBtn.setOnClickListener(v -> {
            if(!amountTxt.getText().toString().isEmpty()){
                double amount = Double.parseDouble(amountTxt.getText().toString());
                startActivity(new Intent(WalletManagement.this, PaymentForm.class).putExtra("amount",amount).putExtra("cause","Deposit").putExtra("username",username));
                finish();
            }else{
                Toast.makeText(WalletManagement.this, "Please Put the amount first!", Toast.LENGTH_SHORT).show();
            }
        });

        withdrawBtn.setOnClickListener(v -> {
            if(!amountTxt.getText().toString().isEmpty()){
                double amount = Double.parseDouble(amountTxt.getText().toString());
                //Insufficient
                startActivity(new Intent(WalletManagement.this, PaymentDone.class).putExtra("amount",amount).putExtra("cause","Withdraw").putExtra("username",username));
                finish();
            }else{
                Toast.makeText(WalletManagement.this, "Please Put the amount first!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}