<?php
session_start();
include('../backend/dbconnect.php');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Anda harus login terlebih dahulu.']);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id = $_SESSION['user_id'];
    $cart_id = $_POST['cart_id'] ?? null;
    $quantity = $_POST['quantity'] ?? 1;

    if (!$cart_id || $quantity < 1) {
        echo json_encode(['status' => 'error', 'message' => 'Data tidak valid.']);
        exit;
    }

    $sql = "UPDATE cart SET quantity = ? WHERE id = ? AND user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iii", $quantity, $cart_id, $user_id);
    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Jumlah produk berhasil diperbarui.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Gagal memperbarui jumlah produk.']);
    }
    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}
?>
