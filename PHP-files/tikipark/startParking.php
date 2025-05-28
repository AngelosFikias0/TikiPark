<?php
header('Content-Type: application/json');
require_once 'config.php'; 
date_default_timezone_set("Europe/Athens"); 

// Get POST data
$username = $_POST['username'] ?? '';
$spotLocation = $_POST['location'] ?? '';
$startTime = $_POST['startTime'] ?? '';

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
$startTimeFormatted = date("Y-m-d H:i:s", $startTime / 1000);
$insert = $conn->prepare("INSERT INTO reservations (user_id, spot_id, start_time, total_amount, payment_status) VALUES (?, ?, ?, 0.00, 'pending')");
$insert->bind_param("iis", $user_id, $spot_id, $startTimeFormatted); 
$insert->execute();

// Update spot status to 'reserved'
$update = $conn->prepare("UPDATE parking_spots SET status = 'occupied' WHERE spot_id = ?");
$update->bind_param("i", $spot_id);
$update->execute();

echo json_encode(["success" => true, "message" => "Created Succesfully"]);

$conn->close();
?>