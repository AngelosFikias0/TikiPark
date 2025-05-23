<?php
header('Content-Type: application/json');

// Database connection
require_once 'config.php'; 

// Get and sanitize POST data
$username = trim($_POST['username'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';

$response = [];

// Basic validation
if (empty($username) || empty($email) || empty($password)) {
    echo json_encode(["success" => false, "message" => "All fields are required."]);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["success" => false, "message" => "Invalid email format."]);
    exit;
}

// Check if username or email already exists
$stmt = $conn->prepare("SELECT user_id FROM users WHERE username = ? OR email = ?");
$stmt->bind_param("ss", $username, $email);
$stmt->execute();
$stmt->store_result();
if ($stmt->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "Username or email already in use."]);
    exit;
}

// Hash password and insert user
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);
$defaultRole = "user";

$insertStmt = $conn->prepare("INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)");
$insertStmt->bind_param("ssss", $username, $email, $hashedPassword, $defaultRole);

if ($insertStmt->execute()) {
    $userId = $insertStmt->insert_id;

    // Insert row into user_statistics
    $statsStmt = $conn->prepare("INSERT INTO user_statistics (user_id) VALUES (?)");
    $statsStmt->bind_param("i", $userId);

    if ($statsStmt->execute()) {
        $response['success'] = true;
        $response['message'] = "User registered successfully.";
    } else {
        $response['success'] = false;
        $response['message'] = "Critical failure";
    }

    $statsStmt->close();
} else {
    $response['success'] = false;
    $response['message'] = "Error occurred during registration.";
}

// Return JSON response
echo json_encode($response);
$conn->close();
?>