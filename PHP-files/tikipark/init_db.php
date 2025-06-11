<?php
header('Content-Type: application/json');

// Database connection
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

// Establish connection
$conn = new mysqli($host, $user, $pass);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

//<---------------------------CREATION--------------------------->
// Securely create database
$conn->query("CREATE DATABASE IF NOT EXISTS `$dbname`");
$conn->select_db($dbname);

// Create 'users' table
$conn->query("
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    wallet_balance DECIMAL(10, 2) DEFAULT 0.00,
    role ENUM('user', 'admin') DEFAULT 'user',
    INDEX (username),
    INDEX (email)
)
");

// Create 'parking_spots' table
$conn->query("
CREATE TABLE IF NOT EXISTS parking_spots (
    spot_id INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    status ENUM('available', 'occupied', 'reserved') DEFAULT 'available',
    price_per_hour DECIMAL(10, 2) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    INDEX (location)
)
");

// Create 'reservations' table
$conn->query("
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    spot_id INT NULL,
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
    user_snapshot VARCHAR(255) NOT NULL,
    spot_snapshot VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id) ON DELETE SET NULL,
    INDEX (user_id),
    INDEX (spot_id)
)
");

// Create 'user_statistics' table for user stats
$conn->query("
CREATE TABLE IF NOT EXISTS user_statistics (
    stat_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    username_snapshot VARCHAR(255) NOT NULL,
    total_amount_spent DECIMAL(10, 2) DEFAULT 0.00,
    total_reservations INT DEFAULT 0,
    total_parking_time INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX (username_snapshot)
)
");

// Create 'transactions' table
$conn->query("
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    reservation_id INT NOT NULL,
    username_snapshot VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    INDEX (user_id),
    INDEX (reservation_id),
    INDEX (user_id, timestamp),
    INDEX (timestamp)
)
");

// Create 'user_card_info' table
$conn->query("
CREATE TABLE IF NOT EXISTS user_card_info (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    card_holder_name VARCHAR(255),
    card_token VARCHAR(255),
    last_four_digits CHAR(4),
    expiry_month CHAR(2),
    expiry_year CHAR(4),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX (user_id),
    UNIQUE (card_token)
)
");

//<---------------------------INSERTIONS--------------------------->

// Insert default admin if not exists
$checkAdmin = $conn->prepare("SELECT user_id FROM users WHERE username = ?");
$adminUsername = 'admin';
$checkAdmin->bind_param("s", $adminUsername);
$checkAdmin->execute();
$result = $checkAdmin->get_result();

if ($result->num_rows === 0) {
    $hashedPassword = password_hash('admin123', PASSWORD_DEFAULT);
    $email = 'admin@example.com';
    $role = 'admin';
    $stmt = $conn->prepare("INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssss", $role, $hashedPassword, $role, $email);
    $stmt->execute();
    $stmt->close();
}
$checkAdmin->close();

// Insert predefined parking spots if none exist
$checkSpots = $conn->query("SELECT COUNT(*) as count FROM parking_spots");
$row = $checkSpots->fetch_assoc();

if ((int)$row['count'] === 0) {
    $spots = [
        ['Ala Moana Mall', 3.50, 21.291982, -157.843144],
        ['Waikiki Beachfront', 4.00, 21.275760, -157.827507],
        ['Downtown Honolulu', 3.00, 21.306944, -157.858333],
        ['University of Hawaii Manoa', 2.50, 21.300855, -157.817347],
        ['Diamond Head Trail Lot', 2.75, 21.261563, -157.805661],
    ];

    $stmt = $conn->prepare("INSERT INTO parking_spots (location, price_per_hour, latitude, longitude) VALUES (?, ?, ?, ?)");
    foreach ($spots as $spot) {
        $stmt->bind_param("sddd", $spot[0], $spot[1], $spot[2], $spot[3]);
        $stmt->execute();
    }
    $stmt->close();
}
