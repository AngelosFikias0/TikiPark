<?php
header('Content-Type: application/json');

// Database connection
//These are placeholder, put actual values here:
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Connection failed: " . $conn->connect_error]));
}

$sql = "SELECT username, role FROM users";
$result = $conn->query($sql);

$users = [];

while ($row = $result->fetch_assoc()) {
    $users[] = $row; 
}

echo json_encode([
    "success" => true,
    "users" => $users
]);

$conn->close();
?>