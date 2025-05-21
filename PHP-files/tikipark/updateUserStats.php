<?php
header('Content-Type: application/json');

// Database connection
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) {
    echo json_encode(["error" => "Connection failed"]);
    exit;
}

// Function to update user statistics based on reservations
function updateUserStatistics($conn) {
    // Start transaction
    $conn->begin_transaction();

    // Get aggregated stats from reservations
    $sql = "
        SELECT
            user_id,
            COUNT(*) AS total_reservations,
            COALESCE(SUM(total_amount), 0) AS total_amount_spent,
            COALESCE(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) AS total_parking_time_minutes
        FROM reservations
        WHERE payment_status = 'completed'
        GROUP BY user_id
    ";

    $result = $conn->query($sql);
    if (!$result) {
        $conn->rollback();
        echo json_encode(["error" => "Aggregation query failed: " . $conn->error]);
        exit;
    }

    // Prepare insert/update statement
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
        echo json_encode(["error" => "Prepare statement failed: " . $conn->error]);
        exit;
    }

    while ($row = $result->fetch_assoc()) {
        $user_id = (int)$row['user_id'];
        $total_reservations = (int)$row['total_reservations'];
        $total_amount_spent = (float)$row['total_amount_spent'];
        $total_parking_time = (int)$row['total_parking_time_minutes'];

        $stmt->bind_param("idii", $user_id, $total_amount_spent, $total_reservations, $total_parking_time);

        if (!$stmt->execute()) {
            $conn->rollback();
            echo json_encode(["error" => "Execute failed: " . $stmt->error]);
            exit;
        }
    }

    $stmt->close();
    $conn->commit();
    echo json_encode(["message" => "User statistics updated successfully"]);
}

// Call the function
updateUserStatistics($conn);

$conn->close();