package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Register screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        EditText emailInput = findViewById(R.id.register_email_editTxt);
        EditText usernameInput = findViewById(R.id.register_username_editTxt);
        EditText passwordInput = findViewById(R.id.register_password_editTxt);
        Button okBtn = findViewById(R.id.register_confirm_btn);
        Button cancelBtn = findViewById(R.id.register_back_btn);

        okBtn.setOnClickListener(view -> {
            if(!emailInput.getText().toString().isEmpty()&&!usernameInput.getText().toString().isEmpty()&&!passwordInput.getText().toString().isEmpty()){
                String email = emailInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                registerUser(email, username, password);
            }else{
                Toast.makeText(Register.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });

        //Cancels and returns to the entry screen
        cancelBtn.setOnClickListener(view -> startActivity(new Intent(Register.this, Entry.class)));

    }

    //Registers user
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

                runOnUiThread(() -> Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show());
                goToWelcomeScreen(username);

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
    private void goToWelcomeScreen(String username) {
        Intent intent;
        intent = new Intent(Register.this, UserWelcome.class);

        intent.putExtra("username", username);
        intent.putExtra("role", "user");
        startActivity(intent);
        finish(); // Prevent going back to login screen
    }
}
