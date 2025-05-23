<?php
header('Content-Type: application/json');

// Database connection
require_once 'config.php'; 

// Check if POST data is received
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Collect POST data
    $location = isset($_POST['location']) ? $_POST['location'] : '';
    $price_per_hour = isset($_POST['price_per_hour']) ? $_POST['price_per_hour'] : '';
    $latitude = isset($_POST['latitude']) ? $_POST['latitude'] : '';
    $longitude = isset($_POST['longitude']) ? $_POST['longitude'] : '';
    $status = isset($_POST['status']) ? $_POST['status'] : 'available';  // default status to 'available'

    // Validate inputs
    if (empty($location) || empty($price_per_hour) || empty($latitude) || empty($longitude)) {
        echo json_encode(array("status" => "error", "message" => "All fields are required."));
        exit;
    }

    // Prepare the SQL query to insert the new parking spot
    $stmt = $conn->prepare("INSERT INTO parking_spots (location, status, price_per_hour, latitude, longitude) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("ssdds", $location, $status, $price_per_hour, $latitude, $longitude);

    // Execute the query
    if ($stmt->execute()) {
        echo json_encode(array("status" => "success", "message" => "Parking spot created successfully."));
    } else {
        echo json_encode(array("status" => "error", "message" => "Failed to create parking spot."));
    }

    // Close the statement and connection
    $stmt->close();
} else {
    echo json_encode(array("status" => "error", "message" => "Invalid request method."));
}

$conn->close();
?>