<?php
// Database connection
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

// Establish connection
$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Query to fetch statistics
$sql = "SELECT 
            (SELECT COUNT(*) FROM users) AS total_users,
            (SELECT COUNT(*) FROM parking_spots) AS total_spots,
            (SELECT COUNT(*) FROM parking_spots WHERE status = 'available') AS total_available_spots,
            (SELECT SUM(total_amount) FROM reservations WHERE status = 'completed') AS total_amount_gained,
            (SELECT COUNT(*) FROM reservations) AS total_reservations,
            (SELECT SUM(TIMESTAMPDIFF(HOUR, start_time, end_time)) FROM reservations WHERE status = 'completed') AS total_parking_time";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    
    // Ensure that null values are converted to 0 if any field is null
    $totalUsers = isset($row['total_users']) ? $row['total_users'] : 0;
    $totalSpots = isset($row['total_spots']) ? $row['total_spots'] : 0;
    $totalAvailableSpots = isset($row['total_available_spots']) ? $row['total_available_spots'] : 0;
    $totalAmountGained = isset($row['total_amount_gained']) ? $row['total_amount_gained'] : 0;
    $totalReservations = isset($row['total_reservations']) ? $row['total_reservations'] : 0;
    $totalParkingTime = isset($row['total_parking_time']) ? $row['total_parking_time'] : 0;

    // Output the statistics
    echo json_encode([
        'total_users' => $totalUsers,
        'total_spots' => $totalSpots,
        'total_available_spots' => $totalAvailableSpots,
        'total_amount_gained' => $totalAmountGained,
        'total_reservations' => $totalReservations,
        'total_parking_time' => $totalParkingTime
    ]);
} else {
    // In case no data is found in the query
    echo json_encode([
        'total_users' => 0,
        'total_spots' => 0,
        'total_available_spots' => 0,
        'total_amount_gained' => 0,
        'total_reservations' => 0,
        'total_parking_time' => 0
    ]);
}

$conn->close();
?>