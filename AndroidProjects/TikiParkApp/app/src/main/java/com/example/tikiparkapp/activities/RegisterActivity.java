package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.R;
import com.example.tikiparkapp.UserWelcome;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register_screen);

        EditText emailInput = findViewById(R.id.emailInputRegisterTxt);
        EditText usernameInput = findViewById(R.id.usernameInputRegisterTxt);
        EditText passwordInput = findViewById(R.id.passwordInputRegisterTxt);
        Button okBtn = findViewById(R.id.okBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);

        okBtn.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            registerUser(email, username, password);
        });

        cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, EntryActivity.class));
        });

    }

    private void registerUser(String email, String username, String password) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/register_user.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&username=" + URLEncoder.encode(username, "UTF-8") +
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
                String message = response.getString("message");

                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show());
                goToWelcomeScreen(username, "user");

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    // Currently you can only register Users. There is no option to register an admin.
    // (It makes sense. Admins are probably going to be created from some other screen.)
    private void goToWelcomeScreen(String username, String role) {
        Intent intent;
        intent = new Intent(RegisterActivity.this, UserWelcome.class);

        intent.putExtra("username", username);
        intent.putExtra("role", role);
        startActivity(intent);
        finish(); // Prevent going back to login screen
    }
}
