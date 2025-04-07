<?php
// Database connection
$host = 'localhost';
$user = 'root';
$pass = '';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    die(json_encode(["success" => false, "message" => "Database connection failed."]));
}

// Get the user data from POST request (no session, just checking against DB)
$username = $_POST['username'] ?? '';
$role = $_POST['role'] ?? '';  // Pass role from the frontend

$response = array();

// Check if username and role are provided
if (empty($username) || empty($role)) {
    $response['success'] = false;
    $response['message'] = "Invalid credentials.";
} else {
    // Validate role in the database
    $stmt = $conn->prepare("SELECT role FROM users WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 1) {
        $row = $result->fetch_assoc();

        // Check if the role matches
        if ($row['role'] === $role && $role === 'admin') {
            $response['success'] = true;
            $response['message'] = "Welcome, " . $role . " \"" . $username . "\"!";
            $response['username'] = $username;  // Include username in response
            $response['role'] = $role;          // Include role in response
        } else {
            $response['success'] = false;
            $response['message'] = "Access denied. Invalid role.";
        }
    } else {
        $response['success'] = false;
        $response['message'] = "Invalid credentials.";
    }
}

// Send the response as JSON
echo json_encode($response);

// Close database connection
$conn->close();
?>