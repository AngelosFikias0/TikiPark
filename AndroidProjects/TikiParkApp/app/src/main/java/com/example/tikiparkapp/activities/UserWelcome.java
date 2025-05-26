package com.example.tikiparkapp.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.R;
import com.example.tikiparkapp.db.LocalCache;

public class UserWelcome extends AppCompatActivity {

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_welcome);

        // Initialize views
        TextView welcomeTextView = findViewById(R.id.userWeclome_welcome_txt);
        ImageButton exitButton = findViewById(R.id.userWelcome_logout_imgBtn);
        Button search = findViewById(R.id.userWeclome_findParking_btn);
        ImageButton wallet = findViewById(R.id.userWelcome_wallet_imgBtn);
        ImageButton stats = findViewById(R.id.userWelcome_stats_imgBtn);

        // Popup and views
        Dialog popLocationAllow = new Dialog(UserWelcome.this);
        popLocationAllow.setContentView(R.layout.pop_location_allow);
        popLocationAllow.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popLocationAllow.getWindow().setBackgroundDrawable(getDrawable(R.drawable.pop_location_access_bg));
        popLocationAllow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button popAccept = popLocationAllow.findViewById(R.id.popAcceptBtn);
        Button popDecline = popLocationAllow.findViewById(R.id.popDeclineBtn);

        // Get intent extras (username, role)
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        // Check if username and role are valid
        if (username != null && role != null) {
            // Use the passed username and role to show the welcome message
            welcomeTextView.setText("Welcome, " + role + ": \"" + username + "\"!");
        } else {
            // Handle case where username or role is not passed correctly
            //Toast.makeText(UserWelcome.this, "Session expired or invalid", Toast.LENGTH_LONG).show();
            // Redirect to MainActivity if session data is invalid
            Intent redirectIntent = new Intent(UserWelcome.this, Entry.class);
            startActivity(redirectIntent);
            finish();
        }

        // Handle Search Logic
        search.setOnClickListener(v -> popLocationAllow.show());

        popAccept.setOnClickListener(view -> {
            // Create an Intent to pass the location to a new activity (SearchActivity)
            Intent searchIntent = new Intent(UserWelcome.this, FindParking.class);
            searchIntent.putExtra("username",username);
            startActivity(searchIntent);
            popLocationAllow.dismiss();
            finish();
        });

        popDecline.setOnClickListener(view -> popLocationAllow.dismiss());

        // Handle Wallet Management Logic
        wallet.setOnClickListener(v -> {
            // Create an Intent to go to the wallet management screen
            Intent walletIntent = new Intent(UserWelcome.this, WalletManagement.class);
            walletIntent.putExtra("username",username);
            startActivity(walletIntent);
        });

        // Handle Statistics Logic
        stats.setOnClickListener(v -> {
            // Create an Intent to go to the statistics screen
            Intent statsIntent = new Intent(UserWelcome.this, Stats.class);
            statsIntent.putExtra("username",username);
            startActivity(statsIntent);
        });


        // Handle exit button click
        exitButton.setOnClickListener(v -> {
            try {
                LocalCache localCache = null;
                try{
                    localCache = new LocalCache(UserWelcome.this);
                }catch (Exception e){
                    Log.d("Cache","Cache failed");
                }
                if (localCache != null) {
                    localCache.clearSession();
                } else {
                    Log.e("Cache", "localCache is null. Cannot clear session.");
                }

                Intent exitIntent = new Intent(UserWelcome.this, Entry.class);
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
                finish(); // Kill UserWelcome to avoid returning
            } catch (Exception e) {
                Toast.makeText(UserWelcome.this, "Error clearing session. Please try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            }
        });

    }
}