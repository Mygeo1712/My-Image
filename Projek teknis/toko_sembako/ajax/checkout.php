<?php
session_start();
include('../backend/dbconnect.php');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Anda harus login terlebih dahulu.']);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id = $_SESSION['user_id'];
    $cart_ids = $_POST['cart_ids'] ?? [];

    if (!is_array($cart_ids) || count($cart_ids) === 0) {
        echo json_encode(['status' => 'error', 'message' => 'Tidak ada produk yang dipilih untuk checkout.']);
        exit;
    }

    $placeholders = implode(',', array_fill(0, count($cart_ids), '?'));
    $types = str_repeat('i', count($cart_ids) + 1);
    $params = array_merge([$user_id], $cart_ids);

    $sql = "SELECT c.id as cart_id, c.product_id, c.quantity FROM cart c WHERE c.user_id = ? AND c.id IN ($placeholders)";
    $stmt = $conn->prepare($sql);
    $bind_names[] = & $params[0];
    for ($i = 1; $i < count($params); $i++) {
        $bind_names[] = & $params[$i];
    }
    $stmt->bind_param($types, ...$bind_names);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        echo json_encode(['status' => 'error', 'message' => 'Produk yang dipilih tidak valid.']);
        exit;
    }

    $conn->begin_transaction();

    try {
        $insert_sql = "INSERT INTO orders (user_id, product_id, quantity, order_date) VALUES (?, ?, ?, NOW())";
        $insert_stmt = $conn->prepare($insert_sql);

        while ($row = $result->fetch_assoc()) {
            $insert_stmt->bind_param("iii", $user_id, $row['product_id'], $row['quantity']);
            $insert_stmt->execute();
        }

        $delete_sql = "DELETE FROM cart WHERE user_id = ? AND id IN ($placeholders)";
        $delete_stmt = $conn->prepare($delete_sql);
        $delete_stmt->bind_param($types, ...$bind_names);
        $delete_stmt->execute();

        $conn->commit();

        echo json_encode(['status' => 'success', 'message' => 'Checkout berhasil!']);
    } catch (Exception $e) {
        $conn->rollback();
        echo json_encode(['status' => 'error', 'message' => 'Checkout gagal: ' . $e->getMessage()]);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}
?>
