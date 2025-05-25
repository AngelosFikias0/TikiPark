<?php
header('Content-Type: application/json');
require_once 'config.php'; 

// Validate and sanitize input
if (!isset($_POST['spot']) || empty(trim($_POST['spot']))) {
    echo json_encode(['success' => false, 'message' => 'Missing or empty spot parameter']);
    exit;
}

$spot = trim($_POST['spot']);
$spot = $conn->real_escape_string($spot);

// Query for spot fee
$query = "SELECT price_per_hour FROM parking_spots WHERE location = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $spot);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(['success' => false, 'message' => 'Spot not found']);
} else {
    $row = $result->fetch_assoc();
    echo json_encode([
        'success' => true,
        'message' => 'Spot fee retrieved successfully',
        'price_per_hour' => (float)$row['price_per_hour']
    ]);
}

$stmt->close();
$conn->close();
?>
