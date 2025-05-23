<?php
header('Content-Type: application/json');

// Database connection
require_once 'config.php'; 

// Get POST data
$username = $_POST['username'] ?? '';
$spotLocation = $_POST['location'] ?? '';

if (!$username || !$spotLocation) {
    echo json_encode(["error" => "Missing parameters"]);
    exit;
}

// Get user_id
$userResult = $conn->prepare("SELECT user_id FROM users WHERE username = ?");
$userResult->bind_param("s", $username);
$userResult->execute();
$userResult->bind_result($user_id);
$userResult->fetch();
$userResult->close();

if (!$user_id) {
    echo json_encode(["error" => "User not found"]);
    exit;
}

// Get spot_id
$spotResult = $conn->prepare("SELECT spot_id FROM parking_spots WHERE location = ?");
$spotResult->bind_param("s", $spotLocation);
$spotResult->execute();
$spotResult->bind_result($spot_id);
$spotResult->fetch();
$spotResult->close();

if (!$spot_id) {
    echo json_encode(["error" => "Spot not found"]);
    exit;
}

// Reserve the spot
$insert = $conn->prepare("INSERT INTO reservations (user_id, spot_id, start_time, total_amount, payment_status) VALUES (?, ?, NOW(), 0.00, 'pending')");
$insert->bind_param("ii", $user_id, $spot_id);
$insert->execute();

// Update spot status to 'reserved'
$update = $conn->prepare("UPDATE parking_spots SET status = 'reserved' WHERE spot_id = ?");
$update->bind_param("i", $spot_id);
$update->execute();

echo json_encode(["message" => "Reservation created", "reservation_id" => $conn->insert_id]);

$conn->close();
?>
