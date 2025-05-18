package com.example.tikiparkapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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

        declineBtn.setOnClickListener(v -> {
            // TODO startActivity(); Go to previous screen.
            finish();
        });

        confirmBtn.setOnClickListener(v -> {
            float amount = Float.parseFloat(amountInput.getText().toString()); // Amount to add to account.

            // Add confirmation code.
        });
    }
}
