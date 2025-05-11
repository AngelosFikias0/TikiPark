package com.example.tikiparkapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tikiparkapp.BuildConfig;
import com.example.tikiparkapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    private ListView usersListView;
    private EditText editRoleEditText;

    private String selectedUsername = null; // Only keep username after selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        usersListView = findViewById(R.id.listView_users);
        editRoleEditText = findViewById(R.id.edit_role);
        Button updateUserButton = findViewById(R.id.btn_update_user);
        Button deleteUserButton = findViewById(R.id.btn_delete_user);
        Button backButton = findViewById(R.id.btn_back_user);

        //Populates the listView with the DB data
        fetchUsers();

        //Update Button Listener
        updateUserButton.setOnClickListener(v -> updateUserRole());

        //Delete Button Listener
        deleteUserButton.setOnClickListener(v -> deleteUser());

        // Back button click listener
        backButton.setOnClickListener(v -> {
            Intent currentIntent = getIntent();
            String username = currentIntent.getStringExtra("username");
            String role = currentIntent.getStringExtra("role");

            Intent intent = new Intent(ManageUsersActivity.this, AdminWelcomeActivity.class);

            intent.putExtra("username", username);
            intent.putExtra("role", role);

            startActivity(intent);
            finish();
        });
    }

    private void fetchUsers() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/get_users.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET"); // GET request
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(false);

                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                if (response.getBoolean("success")) {
                    JSONArray usersArray = response.getJSONArray("users");

                    ArrayList<String> usernames = new ArrayList<>();
                    ArrayList<String> roles = new ArrayList<>();

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userObj = usersArray.getJSONObject(i);
                        usernames.add(userObj.getString("username"));
                        roles.add(userObj.getString("role"));
                    }

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ManageUsersActivity.this, android.R.layout.simple_list_item_1, usernames);
                        usersListView.setAdapter(adapter);

                        // Listen to clicks
                        usersListView.setOnItemClickListener((parent, view, position, id) -> {
                            selectedUsername = usernames.get(position);
                            editRoleEditText.setText(roles.get(position));
                        });
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(ManageUsersActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ManageUsersActivity.this, "Error fetching users.", Toast.LENGTH_SHORT).show());
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (is != null) {
                    try { is.close(); } catch (IOException ignored) {}
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void updateUserRole() {
        if (selectedUsername == null) {
            Toast.makeText(this, "Select a user first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newRole = editRoleEditText.getText().toString().trim();
        if (!(newRole.equals("user") || newRole.equals("admin"))) {
            Toast.makeText(this, "Role must be 'user' or 'admin'.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/update_user.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "username=" + URLEncoder.encode(selectedUsername, "UTF-8") +
                        "&role=" + URLEncoder.encode(newRole, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                String message = response.getString("message");

                runOnUiThread(() -> {
                    Toast.makeText(ManageUsersActivity.this, message, Toast.LENGTH_SHORT).show();
                    fetchUsers(); // Refresh the user list after update
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ManageUsersActivity.this, "Error updating user.", Toast.LENGTH_SHORT).show());
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (is != null) {
                    try { is.close(); } catch (IOException ignored) {}
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void deleteUser() {
        if (selectedUsername == null) {
            Toast.makeText(this, "Select a user first.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            InputStream is = null;
            try {
                URL url = new URL("http://" + BuildConfig.LOCAL_IP + "/tikipark/delete_user.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "username=" + URLEncoder.encode(selectedUsername, "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                is = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());
                String message = response.getString("message");

                runOnUiThread(() -> {
                    Toast.makeText(ManageUsersActivity.this, message, Toast.LENGTH_SHORT).show();
                    selectedUsername = null; // Reset selection
                    editRoleEditText.setText(""); // Clear edit text
                    fetchUsers(); // Refresh the user list after deletion
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ManageUsersActivity.this, "Error deleting user.", Toast.LENGTH_SHORT).show());
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignored) {}
                }
                if (is != null) {
                    try { is.close(); } catch (IOException ignored) {}
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}