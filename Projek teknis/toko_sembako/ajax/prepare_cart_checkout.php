<?php
session_start();
require_once '../backend/dbconnect.php';

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Silakan login terlebih dahulu.']);
    exit;
}

if (empty($_POST['cart_ids']) || !is_array($_POST['cart_ids'])) {
    echo json_encode(['status' => 'error', 'message' => 'Tidak ada produk yang dipilih untuk checkout.']);
    exit;
}

$user_id = $_SESSION['user_id'];
$cart_ids = $_POST['cart_ids'];

$placeholders = implode(',', array_fill(0, count($cart_ids), '?'));
$types = str_repeat('i', count($cart_ids));

$sql = "SELECT 
            p.id AS product_id, p.name, p.price, p.stock, p.image, 
            c.quantity
        FROM cart c
        JOIN products p ON c.product_id = p.id
        WHERE c.user_id = ? AND c.id IN ($placeholders)";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i" . $types, $user_id, ...$cart_ids);
$stmt->execute();
$result = $stmt->get_result();

$checkout_items = [];
$grand_total = 0;

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        if ($row['stock'] < $row['quantity']) {
            echo json_encode(['status' => 'error', 'message' => 'Stok untuk produk "' . htmlspecialchars($row['name']) . '" tidak mencukupi.']);
            exit;
        }

        $checkout_items[] = [
            'product_id' => $row['product_id'],
            'name'       => $row['name'],
            'price'      => $row['price'],
            'image'      => $row['image'],
            'quantity'   => $row['quantity'],
            'subtotal'   => $row['price'] * $row['quantity']
        ];
        $grand_total += $row['price'] * $row['quantity'];
    }

    $_SESSION['checkout_items'] = [
        'items' => $checkout_items,
        'grand_total' => $grand_total,
        'cart_ids_to_delete' => $cart_ids
    ];
    
    unset($_SESSION['checkout_item']);

    echo json_encode(['status' => 'success']);

} else {
    echo json_encode(['status' => 'error', 'message' => 'Produk yang dipilih tidak ditemukan.']);
}

$stmt->close();
$conn->close();
?>