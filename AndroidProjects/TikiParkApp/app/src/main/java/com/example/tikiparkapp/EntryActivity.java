package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;

public class EntryActivity extends AppCompatActivity {

    private LocalCache localCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_entry_screen);

        localCache = new LocalCache(this);

        Button loginBtn = findViewById(R.id.loginBtn);
        Button registerBtn = findViewById(R.id.registerBtn);

        //Creates DB once the app starts for the first time
        createDB();

        // Auto-login if session is cached
        LocalCache.UserSession session = localCache.getUserSession();
        if (session != null) {
            String cachedUsername = session.username;
            String cachedRole = session.role;
            goToWelcomeScreen(cachedUsername, cachedRole);
            return; // Skip login screen
        }

        loginBtn.setOnClickListener(view -> {
            startActivity(new Intent(EntryActivity.this, LoginActivity.class));
            finish();
        });

        registerBtn.setOnClickListener(view -> {
            startActivity(new Intent(EntryActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void goToWelcomeScreen(String username, String role) {
        Intent intent;
        if (role.equalsIgnoreCase("admin")) {
            intent = new Intent(EntryActivity.this, AdminWelcome.class);
        } else {
            intent = new Intent(EntryActivity.this, UserWelcome.class);
        }

        intent.putExtra("username", username);
        intent.putExtra("role", role);
        startActivity(intent);
        finish(); // Prevent going back to login screen
    }

    public void createDB() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/init_db.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                Log.d("DB_INIT", "Response Code: " + responseCode);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}