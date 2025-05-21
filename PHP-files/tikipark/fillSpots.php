<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Connection failed: " . $conn->connect_error]);
    exit;
}

$result = $conn->query("SELECT location, latitude, longitude FROM parking_spots");

$spots = [];
while ($row = $result->fetch_assoc()) {
    $spots[] = $row;
}

echo json_encode(["spots" => $spots]);
$conn->close();
?>
