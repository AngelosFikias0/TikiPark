<?php
header('Content-Type: application/json');
require_once 'config.php'; 

// Check if username is provided
if (!isset($_POST['username'])) {
    echo json_encode([
        "success" => false,
        "message" => "Missing username"
    ]);
    exit;
}

$username = $_POST['username'];

// Prepare and execute query securely
$stmt = $conn->prepare("SELECT wallet_balance FROM users WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "success" => true,
        "wallet_balance" => floatval($row['wallet_balance'])
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "User not found"
    ]);
}

$stmt->close();
$conn->close();
?>