package com.example.tikiparkapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createBut = findViewById(R.id.create_but);
        Button loginBut = findViewById(R.id.login_but);
        EditText emailInput = findViewById(R.id.email_input);
        EditText usernameInput = findViewById(R.id.username_input);
        EditText passwordInput = findViewById(R.id.password_input);

        //Creates DB once the app starts for the first time
        createDB();

        //Event listener for register button
        createBut.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            registerUser(email, username, password);
        });

        //Event listener for login button
        loginBut.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            loginUser(username, password);
        });

    }

    private void registerUser(String email, String username, String password) {
        new Thread(() -> {
            try {
                //Connects to the url of the php file
                URL url = new URL("http://192.168.1.226/tikipark/register_user.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                //Prepares the data
                String postData = "email=" + URLEncoder.encode(email,"UTF-8") + "&username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                //Sends the data via outputStream
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                //Gets response of php
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                //Makes JSON object to handle response
                JSONObject response = new JSONObject(result.toString());
                String message = response.getString("message");

                runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loginUser(String username, String password) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.226/tikipark/login.php"); // use 10.0.2.2 for localhost in emulator
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                if(success){
                    String role = response.getString("role");

                    if (role.equalsIgnoreCase("user")) {
                        // Pass username and role to UserWelcome activity
                        Intent userIntent = new Intent(MainActivity.this, UserWelcome.class);
                        userIntent.putExtra("username", response.getString("username"));
                        userIntent.putExtra("role", response.getString("role"));
                        startActivity(userIntent);
                    } else if (role.equalsIgnoreCase("admin")) {
                        // Pass username and role to AdminWelcome activity
                        Intent adminIntent = new Intent(MainActivity.this, AdminWelcome.class);
                        adminIntent.putExtra("username", response.getString("username"));
                        adminIntent.putExtra("role",  response.getString("role"));
                        startActivity(adminIntent);
                    }

                }

                runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void createDB(){
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.226/tikipark/init_db.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                Log.d("DB_INIT", "Response Code: " + responseCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}