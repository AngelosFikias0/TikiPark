<?php
header('Content-Type: application/json');
require_once 'config.php';

// Check POST param
if (!isset($_POST['username'])) {
    echo json_encode(["success" => false, "error" => "Missing username parameter"]);
    exit;
}

$username = $_POST['username'];

// Prepare statement
$stmt = $conn->prepare("SELECT wallet_balance FROM users WHERE username = ? LIMIT 1");
if (!$stmt) {
    echo json_encode(["success" => false, "error" => "Prepare failed: " . $conn->error]);
    exit;
}

$stmt->bind_param("s", $username);
$stmt->execute();
$stmt->bind_result($wallet_balance);

if ($stmt->fetch()) {
    echo json_encode(["success" => true, "wallet_balance" => (double)$wallet_balance]);
} else {
    echo json_encode(["success" => false, "error" => "User not found"]);
}

$stmt->close();
$conn->close();
?>
