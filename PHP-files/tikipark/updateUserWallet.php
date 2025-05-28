<?php
header('Content-Type: application/json');
require_once 'config.php';

try {
    // Check if required parameters are provided
    if (!isset($_POST['username']) || !isset($_POST['balance'])) {
        throw new Exception("Missing required parameters: username and balance");
    }

    $username = $_POST['username'];
    $newBalance = floatval($_POST['balance']);

    // Validate balance (should be non-negative)
    if ($newBalance < 0) {
        throw new Exception("Balance cannot be negative");
    }

    // Prepare and execute the update query
    $stmt = $conn->prepare("UPDATE users SET wallet_balance = ? WHERE username = ?");
    if (!$stmt) {
        throw new Exception("Prepare failed: " . $conn->error);
    }

    $stmt->bind_param("ds", $newBalance, $username);
    
    if (!$stmt->execute()) {
        throw new Exception("Execute failed: " . $stmt->error);
    }

    // Fetch the updated balance to confirm
    $selectStmt = $conn->prepare("SELECT wallet_balance FROM users WHERE username = ?");
    $selectStmt->bind_param("s", $username);
    $selectStmt->execute();
    $result = $selectStmt->get_result();
    
    if ($row = $result->fetch_assoc()) {
        $updatedBalance = $row['wallet_balance'];
        
        // Return success response
        echo json_encode([
            "success" => true,
            "message" => "Wallet balance updated successfully",
            "wallet_balance" => floatval($updatedBalance),
            "username" => $username
        ]);
    } else {
        throw new Exception("Failed to retrieve updated balance");
    }

    $stmt->close();
    $selectStmt->close();

} catch (Exception $e) {
    // Return error response
    echo json_encode([
        "success" => false,
        "error" => $e->getMessage()
    ]);
} finally {
    if (isset($conn)) {
        $conn->close();
    }
}
?>