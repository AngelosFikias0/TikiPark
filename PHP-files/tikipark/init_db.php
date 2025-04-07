<?php
$host = 'localhost';
$user = 'root';
$pass = '';
$dbname = 'tikipark';

// Establish connection
$conn = new mysqli($host, $user, $pass);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Create the database if not exists
$conn->query("CREATE DATABASE IF NOT EXISTS $dbname");
$conn->select_db($dbname);

// Create 'users' table
$conn->query("
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    wallet_balance DECIMAL(10, 2) DEFAULT 0.00,
    role ENUM('user', 'admin') DEFAULT 'user'
)");

// Create 'parking_spots' table
$conn->query("
CREATE TABLE IF NOT EXISTS parking_spots (
    spot_id INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    status ENUM('available', 'occupied', 'reserved') DEFAULT 'available',
    price_per_hour DECIMAL(10, 2) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL
)");

// Create 'reservations' table
$conn->query("
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    spot_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status ENUM('active', 'completed', 'cancelled') DEFAULT 'active',
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id)
)");

// Create 'user_statistics' table
$conn->query("
CREATE TABLE IF NOT EXISTS user_statistics (
    user_id INT PRIMARY KEY,
    total_amount_spent DECIMAL(10, 2) DEFAULT 0.00,
    total_reservations INT DEFAULT 0,
    total_parking_time INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
)");

// Insert default admin if not exists
$checkAdmin = $conn->query("SELECT user_id FROM users WHERE username = 'admin'");
if ($checkAdmin->num_rows == 0) {
    $hashedPassword = password_hash('admin123', PASSWORD_DEFAULT);
    $email = 'admin@example.com';
    $role = 'admin';

    $conn->query("INSERT INTO users (username, password, role, email) VALUES ('$role', '$hashedPassword', '$role', '$email')");
}

echo "Database and tables initialized successfully.";
$conn->close();
?>