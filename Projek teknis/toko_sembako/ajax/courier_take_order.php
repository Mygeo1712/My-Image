<?php
session_start();
require_once '../backend/dbconnect.php';

header('Content-Type: application/json');

if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'courier') {
    echo json_encode(['status' => 'error', 'message' => 'Akses tidak diizinkan. Silakan login sebagai kurir.']);
    exit;
}

if (empty($_POST['group_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'ID Pesanan tidak valid.']);
    exit;
}

$courier_id = $_SESSION['user_id'];
$group_id = $_POST['group_id'];

$stmt = $conn->prepare(
    "UPDATE orders SET courier_id = ? 
     WHERE order_group_id = ? 
     AND status = 'dikirim' 
     AND courier_id IS NULL"
);

$stmt->bind_param("is", $courier_id, $group_id);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(['status' => 'success']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Pesanan ini sudah diambil oleh kurir lain atau tidak lagi tersedia.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Gagal memperbarui database.']);
}

$stmt->close();
$conn->close();
?>