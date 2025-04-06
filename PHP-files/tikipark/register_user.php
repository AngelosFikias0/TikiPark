<?php
$host = 'localhost';
$user = 'root';
$pass = '';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) die("Connection failed: " . $conn->connect_error);

// Get POST data
$username = $_POST['username'];
$password = $_POST['password'];

// Check if username already exists
$stmt = $conn->prepare("SELECT user_id FROM users WHERE username=?");
$stmt->bind_param("s", $username);
$stmt->execute();
$stmt->store_result();

$response = array();

if ($stmt->num_rows > 0) {
    $response['success'] = false;
    $response['message'] = "Username already taken.";
} else {
    // Default role assignment
    $defaultRole = "user";

    // Insert new user with default role
    $insertStmt = $conn->prepare("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
    $insertStmt->bind_param("sss", $username, $password, $defaultRole); // No hashing for now

    if ($insertStmt->execute()) {
        $response['success'] = true;
        $response['message'] = "User registered successfully.";
    } else {
        $response['success'] = false;
        $response['message'] = "Failed to register user.";
    }
}

echo json_encode($response);
$conn->close();
?>