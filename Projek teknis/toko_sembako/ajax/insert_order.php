<?php
session_start();
include('../backend/dbconnect.php');

header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Silakan login terlebih dahulu.'
    ]);
    exit;
}

$user_id = $_SESSION['user_id'];
$product_id = (int) $_POST['product_id'];
$quantity = (int) $_POST['quantity'];

// 1. Cek stok produk saat ini
$sql = "SELECT stock FROM products WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $product_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Produk tidak ditemukan.'
    ]);
    exit;
}

$row = $result->fetch_assoc();
$current_stock = (int) $row['stock'];

if ($quantity > $current_stock) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Stok tidak cukup. Stok saat ini: ' . $current_stock
    ]);
    exit;
}

// 2. Insert data order
$sql = "INSERT INTO orders (user_id, product_id, quantity) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iii", $user_id, $product_id, $quantity);

if ($stmt->execute()) {
    // 3. Kurangi stok produk
    $new_stock = $current_stock - $quantity;
    $sql_update = "UPDATE products SET stock = ? WHERE id = ?";
    $stmt_update = $conn->prepare($sql_update);
    $stmt_update->bind_param("ii", $new_stock, $product_id);
    $stmt_update->execute();

    echo json_encode([
        'status' => 'success',
        'message' => 'Pesanan berhasil ditambahkan dan stok berhasil diperbarui.'
    ]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Gagal menambahkan pesanan.'
    ]);
}
