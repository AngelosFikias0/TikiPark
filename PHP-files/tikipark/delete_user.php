<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');
require_once 'config.php';

$username = $_POST['username'] ?? null;
if (!$username) {
    echo json_encode(["success" => false, "message" => "No username provided"]);
    exit();
}

$transactionStarted = false;

try {
    // Start transaction
    if (!$conn->begin_transaction()) {
        throw new Exception("Failed to start transaction: " . $conn->error);
    }
    $transactionStarted = true;

    // Delete related reservations
    $sqlReservations = "DELETE r FROM reservations r JOIN users u ON r.user_id = u.user_id WHERE u.username = ?";
    $stmt1 = $conn->prepare($sqlReservations);
    if (!$stmt1) throw new Exception("Prepare reservations delete failed: " . $conn->error);
    $stmt1->bind_param("s", $username);
    if (!$stmt1->execute()) throw new Exception("Execute reservations delete failed: " . $stmt1->error);
    $stmt1->close();

    // Delete related user_statistics
    $sqlStats = "DELETE us FROM user_statistics us JOIN users u ON us.user_id = u.user_id WHERE u.username = ?";
    $stmtStats = $conn->prepare($sqlStats);
    if (!$stmtStats) throw new Exception("Prepare user_statistics delete failed: " . $conn->error);
    $stmtStats->bind_param("s", $username);
    if (!$stmtStats->execute()) throw new Exception("Execute user_statistics delete failed: " . $stmtStats->error);
    $stmtStats->close();

    // Delete the user
    $sqlUser = "DELETE FROM users WHERE username = ?";
    $stmt2 = $conn->prepare($sqlUser);
    if (!$stmt2) throw new Exception("Prepare user delete failed: " . $conn->error);
    $stmt2->bind_param("s", $username);
    if (!$stmt2->execute()) throw new Exception("Execute user delete failed: " . $stmt2->error);

    if ($stmt2->affected_rows > 0) {
        $conn->commit();
        echo json_encode(["success" => true, "message" => "User and related data deleted successfully"]);
    } else {
        $conn->rollback();
        echo json_encode(["success" => false, "message" => "User not found"]);
    }
    $stmt2->close();

    $conn->close();

} catch (Exception $e) {
    if ($transactionStarted) {
        $conn->rollback();
    }
    echo json_encode(["success" => false, "message" => "Delete failed: " . $e->getMessage()]);
    $conn->close();
}