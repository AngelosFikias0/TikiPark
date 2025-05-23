<?php
//Reuse database connection

//header('Content-Type: application/json');
//require_once 'config.php'; 

//These are place holder values. Put yours
$host = 'localhost';
$user = 'root';
$pass = '123';
$dbname = 'tikipark';

// Create connection
$conn = new mysqli($host, $user, $pass, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode([
        "success" => false,
        "message" => "Database connection failed: " . $conn->connect_error
    ]));
}
?>
