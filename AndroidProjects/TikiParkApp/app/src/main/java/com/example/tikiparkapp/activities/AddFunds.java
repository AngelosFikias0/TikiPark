package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;

public class AddFunds extends AppCompatActivity {

    EditText amountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_funds);

        Button confirmBtn = findViewById(R.id.addFunds_Confirm_Btn);
        Button declineBtn = findViewById(R.id.insufFunds_decline_Btn);
        amountInput = findViewById(R.id.addFunds_Amount_EdTxt);
        Intent intent = getIntent();
        String cause = intent.getStringExtra("cause");
        double amount = intent.getDoubleExtra("amount",0.0);
        String username = intent.getStringExtra("username");

        declineBtn.setOnClickListener(v -> {
            if(cause.equalsIgnoreCase("Deposit")){
                startActivity(new Intent(AddFunds.this, UserWelcome.class));
                finish();
            }else if(cause.equalsIgnoreCase("Pay")){
                Toast.makeText(AddFunds.this, "You have to complete your payment first!", Toast.LENGTH_SHORT).show();
            }
        });

        confirmBtn.setOnClickListener(v -> {
            if(cause.equalsIgnoreCase("Deposit")){
                deposit(amount,username);
            }else if(cause.equalsIgnoreCase("Pay")){
                if(check(username)){
                    pay(username);
                }
            }
        });
    }

    public boolean check(String username){
        double balance=0;

        return false;
    }

    public void pay(String username){

        startActivity(new Intent(AddFunds.this, PaymentDone.class));
    }

    public void deposit(double amount, String username){
        startActivity(new Intent(AddFunds.this, PaymentDone.class));
    }

}
