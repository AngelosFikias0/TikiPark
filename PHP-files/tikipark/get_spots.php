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

// SQL query to fetch all parking spots (spot_id, location, status, price)
$sql = "SELECT spot_id, location, status, price_per_hour FROM parking_spots";
$result = $conn->query($sql);

$spots = [];

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $spots[] = [
            'spot_id' => $row['spot_id'],
            'name' => $row['location'],
            'status' => $row['status'],
            'price' => $row['price_per_hour']
        ];
    }
    echo json_encode(["success" => true, "spots" => $spots]);
} else {
    echo json_encode(["success" => false, "message" => "No spots found"]);
}

$conn->close();
?>