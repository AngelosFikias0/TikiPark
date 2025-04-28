<?php
header('Content-Type: application/json');

// Database connection
//These are placeholder, put actual values here:
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

// Create connection
$conn = new mysqli($host, $user, $pass, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Connection failed: " . $conn->connect_error]));
}

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
