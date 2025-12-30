<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'User not logged in']);
    exit;
}

require_once '../backend/dbconnect.php';

$user_id = $_SESSION['user_id'];
$action = $_GET['action'] ?? 'fetch';

if ($action === 'mark_read') {
    $stmt = $conn->prepare("UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    echo json_encode(['status' => 'success']);
    
} else {
    $query = "SELECT id, message, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 10";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $notifications = [];
    while($row = $result->fetch_assoc()) {
        $notifications[] = $row;
    }
    
    $unread_count_query = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = 0";
    $stmt_count = $conn->prepare($unread_count_query);
    $stmt_count->bind_param("i", $user_id);
    $stmt_count->execute();
    $unread_count_result = $stmt_count->get_result()->fetch_assoc();
    $unread_count = $unread_count_result ? (int)$unread_count_result['count'] : 0;

    echo json_encode([
        'status' => 'success',
        'notifications' => $notifications,
        'unread_count' => $unread_count
    ]);
}

if (isset($stmt)) $stmt->close();
if (isset($stmt_count)) $stmt_count->close();
$conn->close();
?>