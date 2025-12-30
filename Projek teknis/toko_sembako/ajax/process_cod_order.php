<?php
session_start();
require_once '../backend/dbconnect.php';

// Validasi Input Dasar
if (!isset($_SESSION['user_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Sesi pengguna tidak ditemukan. Silakan login kembali.']);
    exit;
}
if (empty($_POST['delivery_date']) || empty($_POST['delivery_time'])) {
    echo json_encode(['status' => 'error', 'message' => 'Silakan pilih tanggal dan waktu pengiriman.']);
    exit;
}

// Menyatukan data checkout dari dua kemungkinan sumber (session)
$checkout_data = null;
if (isset($_SESSION['checkout_items'])) {
    $checkout_data = $_SESSION['checkout_items'];
} else if (isset($_SESSION['checkout_item'])) {
    $item = $_SESSION['checkout_item'];
    $checkout_data = [
        'items' => [$item],
        'grand_total' => $item['total_price']
    ];
}


if ($checkout_data === null) {
    echo json_encode(['status' => 'error', 'message' => 'Sesi tidak valid. Silakan ulangi proses checkout.']);
    exit;
}

$userId = $_SESSION['user_id'];
$deliveryDate = $_POST['delivery_date'];
$deliveryTime = $_POST['delivery_time'];
$deliverySchedule = date('Y-m-d H:i:s', strtotime($deliveryDate . ' ' . $deliveryTime));
$paymentMethod = 'COD';
$status = 'pending';
$order_group_id = uniqid('ORDER-', true);

$conn->begin_transaction();

try {
    foreach ($checkout_data['items'] as $item) {
        $productId = $item['product_id'];
        $quantity = $item['quantity'];
        $totalPrice = isset($item['subtotal']) ? $item['subtotal'] : $item['total_price'];

        // Kurangi stok produk
        $updateStockStmt = $conn->prepare("UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?");
        $updateStockStmt->bind_param("iii", $quantity, $productId, $quantity);
        $updateStockStmt->execute();

        if ($updateStockStmt->affected_rows === 0) {
            throw new Exception('Stok untuk produk "' . htmlspecialchars($item['name']) . '" tidak mencukupi.');
        }
        $updateStockStmt->close();

        // Masukkan data pesanan ke tabel orders
        $insertOrderStmt = $conn->prepare(
            "INSERT INTO orders (order_group_id, user_id, product_id, quantity, total_price, payment_method, delivery_schedule, status, order_date) 
             VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())"
        );
        $insertOrderStmt->bind_param("siiidsss", $order_group_id, $userId, $productId, $quantity, $totalPrice, $paymentMethod, $deliverySchedule, $status);
        $insertOrderStmt->execute();
        $insertOrderStmt->close();
    }

    // --- LOGIKA BARU UNTUK MENGHAPUS KERANJANG ---
    // Cek apakah ada ID keranjang yang perlu dihapus (hanya jika checkout dari cart)
    if (isset($checkout_data['cart_ids_to_delete'])) {
        $cart_ids_to_delete = $checkout_data['cart_ids_to_delete'];

        $placeholders = implode(',', array_fill(0, count($cart_ids_to_delete), '?'));
        $types = str_repeat('i', count($cart_ids_to_delete));

        // Query untuk menghapus item yang sudah di-checkout dari keranjang
        $deleteCartStmt = $conn->prepare("DELETE FROM cart WHERE user_id = ? AND id IN ($placeholders)");
        $deleteCartStmt->bind_param("i" . $types, $userId, ...$cart_ids_to_delete);
        $deleteCartStmt->execute();
        $deleteCartStmt->close();
    }

    $conn->commit();

    // Hapus sesi checkout setelah berhasil
    unset($_SESSION['checkout_item']);
    unset($_SESSION['checkout_items']);

    echo json_encode(['status' => 'success']);
} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}

$conn->close();
?>
