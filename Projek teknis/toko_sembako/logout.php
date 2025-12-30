<?php
session_start();
include('backend/dbconnect.php');

function log_admin_logout($conn, $admin_id)
{
    $stmt = $conn->prepare("INSERT INTO admin_logs (admin_id, activity, activity_type, logout_time) VALUES (?, ?, 'logout', NOW())");
    $activity = 'Logout dari sistem';
    $stmt->bind_param("is", $admin_id, $activity);
    $stmt->execute();
    $stmt->close();
}

if (isset($_SESSION['user_id']) && $_SESSION['role'] === 'admin') {
    $admin_id = $_SESSION['user_id'];
    log_admin_logout($conn, $admin_id);
}

session_unset();
session_destroy();
header("Location: ../toko_sembako/login.php");
exit();