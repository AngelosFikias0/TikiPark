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

            // Simple validation
            if (username.isEmpty()) {
                Toast.makeText(MainActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
            } else {
                // If username and password are both valid, navigate to the WelcomeActivity
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("ROLE", "admin"); // Example role, modify as needed
                startActivity(intent);

                // Optionally show a toast for a successful login
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}