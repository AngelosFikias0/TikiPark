<?php
$host = 'localhost';
$user = 'root';
$pass = '';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) die("Connection failed: " . $conn->connect_error);

// Get POST data
$username = $_POST['username'];
$password = $_POST['password'];

// Simple query (NO HASHING for now)
$stmt = $conn->prepare("SELECT role FROM users WHERE username=? AND password=?");
$stmt->bind_param("ss", $username, $password);
$stmt->execute();
$result = $stmt->get_result();

$response = array();
if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $response['success'] = true;
    $response['message'] = "Login successful.";
    $response['role'] = $row['role'];  
} else {
    $response['success'] = false;
    $response['message'] = "Invalid credentials.";
}

echo json_encode($response);
$conn->close();
?>