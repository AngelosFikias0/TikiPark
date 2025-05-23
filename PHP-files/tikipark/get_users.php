<?php
header('Content-Type: application/json');

// Database connection
require_once 'config.php'; 

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