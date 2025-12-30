<?php
session_start();
require_once '../backend/dbconnect.php';
header('Content-Type: application/json');

// Keamanan dasar
if (!isset($_SESSION['user_id']) || empty($_POST['group_id']) || !isset($_POST['items'])) {
    echo json_encode(['status' => 'error', 'message' => 'Data tidak lengkap atau sesi tidak valid.']);
    exit;
}

$user_id = $_SESSION['user_id'];
$group_id = $_POST['group_id'];
$new_items = $_POST['items']; // Ini adalah array [product_id => quantity]
$new_schedule = date('Y-m-d H:i:s', strtotime($_POST['delivery_date'] . ' ' . $_POST['delivery_time']));

$conn->begin_transaction();

try {
    // Ambil tanggal pesanan asli untuk dipertahankan
    $stmt_get_date = $conn->prepare("SELECT order_date FROM orders WHERE order_group_id = ? LIMIT 1");
    $stmt_get_date->bind_param("s", $group_id);
    $stmt_get_date->execute();
    $original_order_date = $stmt_get_date->get_result()->fetch_assoc()['order_date'];
    $stmt_get_date->close();

    if (!$original_order_date) {
        throw new Exception("Order group tidak ditemukan.");
    }

    // 1. Ambil item lama untuk mengembalikan stok
    $stmt_old = $conn->prepare("SELECT product_id, quantity FROM orders WHERE order_group_id = ? AND user_id = ?");
    $stmt_old->bind_param("si", $group_id, $user_id);
    $stmt_old->execute();
    $old_items_result = $stmt_old->get_result();
    while ($item = $old_items_result->fetch_assoc()) {
        $conn->query("UPDATE products SET stock = stock + {$item['quantity']} WHERE id = {$item['product_id']}");
    }
    $stmt_old->close();

    // 2. Hapus semua entri pesanan lama dari grup ini
    $stmt_delete = $conn->prepare("DELETE FROM orders WHERE order_group_id = ? AND user_id = ?");
    $stmt_delete->bind_param("si", $group_id, $user_id);
    $stmt_delete->execute();
    $stmt_delete->close();

    // 3. Loop dan masukkan item baru, sambil mengurangi stok
    foreach ($new_items as $product_id => $quantity) {
        $product_res = $conn->query("SELECT price, stock FROM products WHERE id = $product_id");
        $product = $product_res->fetch_assoc();
        
        if ($product['stock'] < $quantity) {
            throw new Exception("Stok untuk salah satu produk tidak mencukupi.");
        }
        
        $total_price = $product['price'] * $quantity;
        
        $conn->query("UPDATE products SET stock = stock - $quantity WHERE id = $product_id");

        // Masukkan kembali ke tabel orders dengan data baru dan tanggal pesanan asli
        $stmt_insert = $conn->prepare(
            "INSERT INTO orders (order_group_id, user_id, product_id, quantity, total_price, payment_method, delivery_schedule, status, order_date) 
             VALUES (?, ?, ?, ?, ?, 'COD', ?, 'pending', ?)"
        );
        // --- PERBAIKAN DI SINI ---
        // Tipe string 'siiidsss' (8 karakter) salah, seharusnya 'siiidss' (7 karakter) untuk 7 variabel.
        $stmt_insert->bind_param("siiidss", $group_id, $user_id, $product_id, $quantity, $total_price, $new_schedule, $original_order_date);
        $stmt_insert->execute();
        $stmt_insert->close();
    }

    $conn->commit();
    echo json_encode(['status' => 'success', 'message' => 'Pesanan berhasil diperbarui!']);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}

$conn->close();

