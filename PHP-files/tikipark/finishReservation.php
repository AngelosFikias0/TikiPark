<?php
header('Content-Type: application/json');
require_once 'config.php'; // contains $conn connection setup, similar to your initialization

// Validate input
$spotLocation = $_POST['spot'] ?? '';
$username = $_POST['username'] ?? '';
$cost = $_POST['cost'] ?? '';

if (!$spotLocation || !$username || !is_numeric($cost)) {
    echo json_encode(["success" => false, "error" => "Missing or invalid parameters"]);
    exit;
}

$cost = floatval($cost);

$conn->begin_transaction();

try {
    // Get user_id and wallet_balance
    $stmt = $conn->prepare("SELECT user_id, wallet_balance FROM users WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->bind_result($user_id, $wallet_balance);
    if (!$stmt->fetch()) {
        throw new Exception("User not found");
    }
    $stmt->close();

    // Get spot_id
    $stmt = $conn->prepare("SELECT spot_id FROM parking_spots WHERE location = ?");
    $stmt->bind_param("s", $spotLocation);
    $stmt->execute();
    $stmt->bind_result($spot_id);
    if (!$stmt->fetch()) {
        throw new Exception("Parking spot not found");
    }
    $stmt->close();

    // Get the active reservation for user & spot with payment_status = 'pending'
    $stmt = $conn->prepare("SELECT reservation_id, start_time FROM reservations WHERE user_id = ? AND spot_id = ? AND payment_status = 'pending' ORDER BY start_time DESC LIMIT 1");
    $stmt->bind_param("ii", $user_id, $spot_id);
    $stmt->execute();
    $stmt->bind_result($reservation_id, $start_time);
    if (!$stmt->fetch()) {
        throw new Exception("Active reservation not found");
    }
    $stmt->close();

    // Calculate parking duration in seconds
    $startTimestamp = strtotime($start_time);
    $endTimestamp = time();
    $durationSeconds = $endTimestamp - $startTimestamp;

    // Update reservation with end_time, total_amount and payment_status = completed
    $stmt = $conn->prepare("UPDATE reservations SET end_time = NOW(), total_amount = ?, payment_status = 'completed' WHERE reservation_id = ?");
    $stmt->bind_param("di", $cost, $reservation_id);
    $stmt->execute();
    $stmt->close();

    // Deduct from user's wallet
    $newBalance = $wallet_balance - $cost;
    $stmt = $conn->prepare("UPDATE users SET wallet_balance = ? WHERE user_id = ?");
    $stmt->bind_param("di", $newBalance, $user_id);
    $stmt->execute();
    $stmt->close();

    // Update user_statistics: total_amount_spent, total_reservations, total_parking_time
    // If user_statistics row does not exist, insert it
    $stmt = $conn->prepare("SELECT total_amount_spent, total_reservations, total_parking_time FROM user_statistics WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $stmt->bind_result($totalAmountSpent, $totalReservations, $totalParkingTime);

    if ($stmt->fetch()) {
        $stmt->close();
        $totalAmountSpent += $cost;
        $totalReservations += 1;
        $totalParkingTime += $durationSeconds;

        $stmt = $conn->prepare("UPDATE user_statistics SET total_amount_spent = ?, total_reservations = ?, total_parking_time = ? WHERE user_id = ?");
        $stmt->bind_param("diii", $totalAmountSpent, $totalReservations, $totalParkingTime, $user_id);
        $stmt->execute();
        $stmt->close();
    } else {
        $stmt->close();
        $stmt = $conn->prepare("INSERT INTO user_statistics (user_id, total_amount_spent, total_reservations, total_parking_time) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("idii", $user_id, $cost, 1, $durationSeconds);
        $stmt->execute();
        $stmt->close();
    }

    // Set parking spot status back to 'available'
    $stmt = $conn->prepare("UPDATE parking_spots SET status = 'available' WHERE spot_id = ?");
    $stmt->bind_param("i", $spot_id);
    $stmt->execute();
    $stmt->close();

    $conn->commit();

    echo json_encode(["success" => true, "message" => "Reservation finished and payment processed", "new_balance" => $newBalance]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}

$conn->close();
?>