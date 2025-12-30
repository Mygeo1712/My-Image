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
$product_id = $_POST['product_id'];
$quantity = $_POST['quantity'];

// Cek apakah produk sudah ada di keranjang
$check = $conn->prepare("SELECT id, quantity FROM cart WHERE user_id = ? AND product_id = ?");
$check->bind_param("ii", $user_id, $product_id);
$check->execute();
$result = $check->get_result();

if ($result->num_rows > 0) {
    // Jika sudah ada, update jumlah
    $row = $result->fetch_assoc();
    $new_quantity = $row['quantity'] + $quantity;

    $update = $conn->prepare("UPDATE cart SET quantity = ? WHERE id = ?");
    $update->bind_param("ii", $new_quantity, $row['id']);
    $success = $update->execute();
} else {
    // Jika belum ada, insert baru
    $insert = $conn->prepare("INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)");
    $insert->bind_param("iii", $user_id, $product_id, $quantity);
    $success = $insert->execute();
}

if ($success) {
    echo json_encode([
        'status' => 'success',
        'message' => 'Produk berhasil ditambahkan ke keranjang.'
    ]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Gagal menambahkan ke keranjang.'
    ]);
}
?>
