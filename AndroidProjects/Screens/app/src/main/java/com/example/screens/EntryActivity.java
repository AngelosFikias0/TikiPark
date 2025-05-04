package com.example.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_screen);

        Dialog dialog = new Dialog(EntryActivity.this);
        dialog.setContentView(R.layout.custom_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_popup_bg));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button acceptBtn = dialog.findViewById(R.id.popupDecline);
        Button declineBtn = dialog.findViewById(R.id.popupAccept);
        Button temp = findViewById(R.id.registerButton);

        temp.setOnClickListener(view -> {
            dialog.show();
        });

        acceptBtn.setOnClickListener(view -> {
            Toast.makeText(EntryActivity.this, "Accepted!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        declineBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }
}