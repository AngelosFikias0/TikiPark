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

// Create database if not exists
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
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    status ENUM('active', 'completed') DEFAULT 'active',
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

// Insert predefined parking spots (Honolulu-based) if not already inserted
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

echo json_encode(["message" => "Database and tables initialized successfully. Default data inserted."]);
$conn->close();
?>