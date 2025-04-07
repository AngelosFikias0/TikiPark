<?php
// Database connection details
$host = 'localhost';
$user = 'root';
$pass = '';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    die(json_encode(["success" => false, "message" => "Database connection failed."]));
}

// Get and sanitize POST data
$username = trim($_POST['username'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';

$response = array();

// Basic validation to ensure no empty fields
if (empty($username) || empty($email) || empty($password)) {
    $response['success'] = false;
    $response['message'] = "All fields are required.";
    echo json_encode($response);
    exit;
}

// Validate email format
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $response['success'] = false;
    $response['message'] = "Invalid email format.";
    echo json_encode($response);
    exit;
}

// Check if username or email already exists in the database
$stmt = $conn->prepare("SELECT user_id FROM users WHERE username = ? OR email = ?");
$stmt->bind_param("ss", $username, $email);
$stmt->execute();
$stmt->store_result();

// If username or email already exists, return error
if ($stmt->num_rows > 0) {
    $response['success'] = false;
    $response['message'] = "Username or email already in use.";
    echo json_encode($response);
    exit;
}

// Hash the password for security
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

// Default role for new users
$defaultRole = "user";

// Insert new user into the database
$insertStmt = $conn->prepare("INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)");
$insertStmt->bind_param("ssss", $username, $email, $hashedPassword, $defaultRole);

if ($insertStmt->execute()) {
    // Successful registration
    $response['success'] = true;
    $response['message'] = "User registered successfully.";
} else {
    // Error during registration
    $response['success'] = false;
    $response['message'] = "Error occurred during registration.";
}

// Output response as JSON
echo json_encode($response);

// Close the database connection
$conn->close();
?>