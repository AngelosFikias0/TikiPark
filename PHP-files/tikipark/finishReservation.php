<?php
header('Content-Type: application/json');
require_once 'config.php'; 
date_default_timezone_set("Europe/Athens");

// Validate input
$spotLocation = $_POST['spot'] ?? '';
$username = $_POST['username'] ?? '';
$cost = $_POST['cost'] ?? '';
$endtime = $_POST['time'] ?? '';

if (!$spotLocation || !$username || !is_numeric($cost) || !$endtime) {
    echo json_encode(["success" => false, "error" => "Missing or invalid parameters"]);
    exit;
}

$cost = floatval($cost);
$endtime = (int)$endtime;

$conn->begin_transaction();

try {
    // Get user_id
    $stmt = $conn->prepare("SELECT user_id FROM users WHERE username = ?");
    if (!$stmt) throw new Exception("Prepare failed: " . $conn->error);
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->bind_result($user_id);
    if (!$stmt->fetch()) throw new Exception("User not found");
    $stmt->close();

    // Get spot_id
    $stmt = $conn->prepare("SELECT spot_id FROM parking_spots WHERE location = ?");
    if (!$stmt) throw new Exception("Prepare failed: " . $conn->error);
    $stmt->bind_param("s", $spotLocation);
    $stmt->execute();
    $stmt->bind_result($spot_id);
    if (!$stmt->fetch()) throw new Exception("Parking spot not found");
    $stmt->close();

    // Get active reservation
    $stmt = $conn->prepare("
        SELECT reservation_id, start_time 
        FROM reservations 
        WHERE user_id = ? AND spot_id = ? AND payment_status = 'pending' 
        ORDER BY start_time DESC 
        LIMIT 1
    ");
    if (!$stmt) throw new Exception("Prepare failed: " . $conn->error);
    $stmt->bind_param("ii", $user_id, $spot_id);
    $stmt->execute();
    $stmt->bind_result($reservation_id, $start_time);
    if (!$stmt->fetch()) throw new Exception("Active reservation not found");
    $stmt->close();

    // Format end time from milliseconds to MySQL DATETIME
    $endTimeFormatted = date("Y-m-d H:i:s", intval($endtime / 1000));

    // Update reservation
    $stmt = $conn->prepare("
        UPDATE reservations 
        SET end_time = ?, total_amount = ?, payment_status = 'completed' 
        WHERE reservation_id = ?
    ");
    if (!$stmt) throw new Exception("Prepare failed: " . $conn->error);
    $stmt->bind_param("sdi", $endTimeFormatted, $cost, $reservation_id);
    $stmt->execute();
    $stmt->close();

    // Update parking spot status
    $stmt = $conn->prepare("UPDATE parking_spots SET status = 'available' WHERE spot_id = ?");
    if (!$stmt) throw new Exception("Prepare failed: " . $conn->error);
    $stmt->bind_param("i", $spot_id);
    $stmt->execute();
    $stmt->close();

    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "Reservation completed successfully",
        "reservation_id" => $reservation_id,
        "end_time" => $endTimeFormatted,
        "total_cost" => $cost
    ]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
} finally {
    $conn->close();
}
?>