<?php
header('Content-Type: application/json');

// Database credentials
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

// Connect to DB
$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "Database connection failed."]);
    exit;
}

// Get and sanitize POST input
$username = trim($_POST['username'] ?? '');

if (empty($username)) {
    echo json_encode(["success" => false, "message" => "Username is required."]);
    exit;
}

// Prepare and execute query to get user stats by username
$stmt = $conn->prepare("
    SELECT 
        us.total_amount_spent, 
        us.total_reservations, 
        us.total_parking_time 
    FROM users u
    LEFT JOIN user_statistics us ON u.user_id = us.user_id
    WHERE u.username = ?
    LIMIT 1
");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "User not found or no stats available."]);
    exit;
}

$data = $result->fetch_assoc();

// If user_statistics row is null (new user without stats), return zeros
$total_amount_spent = $data['total_amount_spent'] ?? 0.00;
$total_reservations = $data['total_reservations'] ?? 0;
$total_parking_time = $data['total_parking_time'] ?? 0;

// Respond with stats
echo json_encode([
    "success" => true,
    "total_amount_spent" => (float)$total_amount_spent,
    "total_reservations" => (int)$total_reservations,
    "total_parking_time" => (int)$total_parking_time
]);

$stmt->close();
$conn->close();
?>