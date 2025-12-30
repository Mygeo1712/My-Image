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

    if (!$cart_id) {
        echo json_encode(['status' => 'error', 'message' => 'Data tidak valid.']);
        exit;
    }

    $sql = "DELETE FROM cart WHERE id = ? AND user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ii", $cart_id, $user_id);
    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Produk berhasil dihapus dari keranjang.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Gagal menghapus produk.']);
    }
    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}
?>
