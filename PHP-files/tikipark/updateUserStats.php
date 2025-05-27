<?php
header('Content-Type: application/json');
require_once 'config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["error" => "Only POST requests allowed"]);
    exit;
}

$username = isset($_POST['username']) ? trim($_POST['username']) : '';
if ($username === '') {
    echo json_encode(["error" => "Invalid or missing username"]);
    exit;
}

// Step 1: Get user_id from username
$stmtUser = $conn->prepare("SELECT user_id FROM users WHERE username = ?");
$stmtUser->bind_param("s", $username);
$stmtUser->execute();
$stmtUser->bind_result($user_id);
if (!$stmtUser->fetch()) {
    echo json_encode(["error" => "User not found"]);
    $stmtUser->close();
    $conn->close();
    exit;
}
$stmtUser->close();

function updateUserStatistics($conn, $user_id) {
    $conn->begin_transaction();

    // Step 2: Aggregate only for this user_id
    $stmtAgg = $conn->prepare("
        SELECT
            COUNT(*) AS total_reservations,
            COALESCE(SUM(total_amount), 0) AS total_amount_spent,
            COALESCE(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) AS total_parking_time_minutes
        FROM reservations
        WHERE payment_status = 'completed' AND user_id = ?
    ");
    $stmtAgg->bind_param("i", $user_id);
    $stmtAgg->execute();
    $stmtAgg->bind_result($total_reservations, $total_amount_spent, $total_parking_time_minutes);

    if (!$stmtAgg->fetch()) {
        $conn->rollback();
        echo json_encode(["error" => "Failed to fetch aggregated stats"]);
        $stmtAgg->close();
        return;
    }
    $stmtAgg->close();

    // Step 3: Insert or update user_statistics
    $stmt = $conn->prepare("
        INSERT INTO user_statistics (user_id, total_amount_spent, total_reservations, total_parking_time)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            total_amount_spent = VALUES(total_amount_spent),
            total_reservations = VALUES(total_reservations),
            total_parking_time = VALUES(total_parking_time)
    ");
    if (!$stmt) {
        $conn->rollback();
        echo json_encode(["error" => "Prepare failed: " . $conn->error]);
        return;
    }

    $stmt->bind_param("idii", $user_id, $total_amount_spent, $total_reservations, $total_parking_time_minutes);
    if (!$stmt->execute()) {
        $conn->rollback();
        echo json_encode(["error" => "Execute failed: " . $stmt->error]);
        $stmt->close();
        return;
    }
    $stmt->close();

    $conn->commit();

    // Step 4: Return success message ONLY (no wallet_balance)
    echo json_encode([
        "message" => "User statistics updated successfully"
    ]);
}

updateUserStatistics($conn, $user_id);
$conn->close();
?>