<?php
header('Content-Type: application/json');

// Database connection
require_once 'config.php'; 

// Get data from POST
$spot_id = $_POST['spot_id'];
$status = $_POST['status'];
$price = $_POST['price'];

// SQL query to update the parking spot
$sql = "UPDATE parking_spots SET status = ?, price_per_hour = ? WHERE spot_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sdi", $status, $price, $spot_id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Spot updated successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Failed to update spot"]);
}

$stmt->close();
$conn->close();
?>
