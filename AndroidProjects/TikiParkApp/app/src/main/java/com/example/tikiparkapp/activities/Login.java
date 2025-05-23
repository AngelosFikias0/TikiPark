package com.example.tikiparkapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.R;
import com.example.tikiparkapp.db.LocalCache;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity {

    private LocalCache localCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        localCache = new LocalCache(this);

        TextInputEditText usernameInputTxt = findViewById(R.id.login_username_editTxt);
        TextInputEditText passwordInputTxt = findViewById(R.id.login_password_editTxt);
        Button okBtn = findViewById(R.id.login_confirm_btn);
        Button registerBtn = findViewById(R.id.login_register_btn);
        Button passBut = findViewById(R.id.forgot_pass);

        okBtn.setOnClickListener(view -> {
            String username = usernameInputTxt.getText().toString();
            String password = passwordInputTxt.getText().toString();
            loginUser(username, password);
        });

        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });


        passBut.setOnClickListener(view -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(dialogView);

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Button passOkBtn = dialogView.findViewById(R.id.popupOkBtn);
            passOkBtn.setOnClickListener(v -> dialog.dismiss());

            // Show dialog
            dialog.show();
        });

    }

    public void loginUser(String username, String password) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/login.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                if (success) {
                    String role = response.getString("role");
                    String userFromServer = response.getString("username");

                    // Save to local SQLite cache
                    localCache.cacheUserSession(userFromServer, role);

                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        goToWelcomeScreen(userFromServer, role);
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {}
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) {}
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void goToWelcomeScreen(String username, String role) {
        Intent intent;
        if (role.equalsIgnoreCase("admin")) {
            intent = new Intent(Login.this, AdminWelcome.class);
        } else {
            intent = new Intent(Login.this, UserWelcome.class);
        }

        intent.putExtra("username", username);
        intent.putExtra("role", role);
        startActivity(intent);
        finish(); // Prevent going back to login screen
    }
}