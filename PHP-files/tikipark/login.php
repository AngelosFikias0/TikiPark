<?php
header('Content-Type: application/json');
require_once 'config.php'; 

// Get and sanitize POST data
$username = trim($_POST['username'] ?? '');
$password = $_POST['password'] ?? '';

$response = array();

// Validate input
if (empty($username) || empty($password)) {
    $response['success'] = false;
    $response['message'] = "Username and password are required.";
    echo json_encode($response);
    exit;
}

// Prepare SQL query to get stored hash for the user
$stmt = $conn->prepare("SELECT password, role, user_id FROM users WHERE username = ?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

// Check if user exists and verify password
if ($result->num_rows === 1) {
    $row = $result->fetch_assoc();
    $storedHash = $row['password'];

    // Verify password using the stored hash
    if (password_verify($password, $storedHash)) {
        // Send success response with user details
        $response['success'] = true;
        $response['message'] = "Login successful.";
        $response['role'] = $row['role']; 
        $response['user_id'] = $row['user_id']; 
        $response['username'] = $username; 
    } else {
        $response['success'] = false;
        $response['message'] = "Invalid credentials.";
    }
} else {
    $response['success'] = false;
    $response['message'] = "Invalid credentials.";
}

// Output the response
echo json_encode($response);

// Close database connection
$conn->close();
?>