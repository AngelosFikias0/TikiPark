package com.example.authtestproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Correct layout reference

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Handle login button click
        loginButton.setOnClickListener(v -> {
            // Get username and password inputs
            String username = usernameEditText.getText().toString().trim(); // Trim extra spaces
            String password = passwordEditText.getText().toString().trim(); // Trim extra spaces

            new Thread(() -> {
                boolean isValid = DatabaseHelper.validateUser(username, password);

                if (username.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                } else {
                    runOnUiThread(() -> {
                        if (isValid) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            // If username and password are both valid, navigate to the WelcomeActivity
                            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                            intent.putExtra("USERNAME", username);
                            intent.putExtra("ROLE", "user"); // Example role, modify as needed
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }).start();

        });
    }
}