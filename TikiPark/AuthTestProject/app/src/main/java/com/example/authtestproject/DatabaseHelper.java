package com.example.authtestproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHelper {

    // Database connection details
    static String URL = "jdbc:mysql://10.0.2.2:3306/android_login";
    static String USER = "root";
    static String PASSWORD = ""; // default if no password


    // This method is used to verify login credentials
    public static boolean validateUser(String username, String password) {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the database
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Create a SQL query
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if there's a matching user
            if (resultSet.next()) {
                connection.close();
                return true; // Valid user
            } else {
                connection.close();
                return false; // Invalid credentials
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred
        }
    }
}
