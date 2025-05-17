package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.R;
import com.example.tikiparkapp.db.LocalCache;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private LocalCache localCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localCache = new LocalCache(this);

        Button createBut = findViewById(R.id.create_but);
        Button loginBut = findViewById(R.id.login_but);
        EditText emailInput = findViewById(R.id.email_input);
        EditText usernameInput = findViewById(R.id.username_input);
        EditText passwordInput = findViewById(R.id.password_input);

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

        // Event listener for register button
        createBut.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            registerUser(email, username, password);
        });

        // Event listener for login button
        loginBut.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            loginUser(username, password);
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

                runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    private void loginUser(String username, String password) {
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
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        goToWelcomeScreen(userFromServer, role);
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
            intent = new Intent(MainActivity.this, AdminWelcomeActivity.class);
        } else {
            intent = new Intent(MainActivity.this, UserWelcome.class);
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