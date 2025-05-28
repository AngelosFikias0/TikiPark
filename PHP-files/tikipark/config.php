<?php
// config.php
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);

// Safe connection check
if ($conn->connect_error) {
    // Optional: log this error somewhere, don't output
    error_log("Database connection failed: " . $conn->connect_error);
    http_response_code(500);
    echo json_encode(['error' => 'Database connection failed.']);
    exit;
}
?>