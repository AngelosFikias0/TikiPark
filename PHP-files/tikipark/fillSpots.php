<?php
header('Content-Type: application/json');

//DB Connection
require_once 'config.php'; 

$result = $conn->query("SELECT location, latitude, longitude FROM parking_spots");

$spots = [];
while ($row = $result->fetch_assoc()) {
    $spots[] = $row;
}

echo json_encode(["spots" => $spots]);
$conn->close();
?>
