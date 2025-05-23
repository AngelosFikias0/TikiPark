<?php
header('Content-Type: application/json');

// Database connection
require_once 'config.php'; 

// Get data from POST
$spot_id = $_POST['spot_id'];

// SQL query to delete the parking spot
$sql = "DELETE FROM parking_spots WHERE spot_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $spot_id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Spot deleted successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Failed to delete spot"]);
}

$stmt->close();
$conn->close();
?>
