<?php
header('Content-Type: application/json');

// Database connection
require_once 'config.php'; 

// Get data from POST
$username = $_POST['username'];

// SQL query to delete the user
$sql = "DELETE FROM users WHERE username = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $username);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "User deleted successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Failed to delete user"]);
}

$stmt->close();
$conn->close();
?>