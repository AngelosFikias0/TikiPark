<?php
header('Content-Type: application/json');

// Database connection
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit;
}

// Get data from POST
$username = $_POST['username'];
$newRole = $_POST['role'];

// Validate role
if (!in_array($newRole, ['user', 'admin'])) {
    echo json_encode(["success" => false, "message" => "Invalid role"]);
    exit;
}

// SQL query to update user's role
$sql = "UPDATE users SET role = ? WHERE username = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $newRole, $username);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "User role updated successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Failed to update user role"]);
}

$stmt->close();
$conn->close();
?>