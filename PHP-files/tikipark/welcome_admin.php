<?php
header('Content-Type: application/json');

// Enable error reporting for development (disable in production)
error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once 'config.php'; // Ensure this only sets up $conn with no output

// Validate POST and 'action'
if ($_SERVER['REQUEST_METHOD'] !== 'POST' || !isset($_POST['action']) || $_POST['action'] !== 'getStatistics') {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid request']);
    exit;
}

// SQL for aggregated statistics
$sql = "
    SELECT
        (SELECT COUNT(*) FROM users) AS total_users,
        (SELECT COUNT(*) FROM parking_spots) AS total_spots,
        (SELECT COUNT(*) FROM parking_spots WHERE status = 'available') AS total_available_spots,
        (SELECT SUM(total_amount) FROM reservations WHERE payment_status = 'completed') AS total_amount_gained,
        (SELECT COUNT(*) FROM reservations) AS total_reservations,
        (SELECT SUM(TIMESTAMPDIFF(HOUR, start_time, end_time)) FROM reservations WHERE payment_status = 'completed') AS total_parking_time
";

$result = $conn->query($sql);

if (!$result) {
    http_response_code(500);
    echo json_encode(['error' => 'Database query failed', 'details' => $conn->error]);
    exit;
}

$row = $result->fetch_assoc();

// Prepare response with defaults
$response = [
    'total_users' => isset($row['total_users']) ? (int)$row['total_users'] : 0,
    'total_spots' => isset($row['total_spots']) ? (int)$row['total_spots'] : 0,
    'total_available_spots' => isset($row['total_available_spots']) ? (int)$row['total_available_spots'] : 0,
    'total_amount_gained' => isset($row['total_amount_gained']) ? (float)$row['total_amount_gained'] : 0.0,
    'total_reservations' => isset($row['total_reservations']) ? (int)$row['total_reservations'] : 0,
    'total_parking_time' => isset($row['total_parking_time']) ? (int)$row['total_parking_time'] : 0
];

echo json_encode($response);
$conn->close();