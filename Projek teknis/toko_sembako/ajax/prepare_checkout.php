<?php
session_start();
require_once '../backend/dbconnect.php';

// Pastikan user sudah login
if (!isset($_SESSION['user_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Silakan login terlebih dahulu.']);
    exit;
}

// Validasi input
if (!isset($_POST['product_id']) || !isset($_POST['quantity'])) {
    echo json_encode(['status' => 'error', 'message' => 'Data produk tidak lengkap.']);
    exit;
}

$productId = intval($_POST['product_id']);
$quantity = intval($_POST['quantity']);

if ($quantity <= 0) {
    echo json_encode(['status' => 'error', 'message' => 'Jumlah pembelian tidak valid.']);
    exit;
}

// Ambil data produk dari database untuk validasi
$stmt = $conn->prepare("SELECT id, name, price, stock, image FROM products WHERE id = ?");
$stmt->bind_param("i", $productId);
$stmt->execute();
$result = $stmt->get_result();
$product = $result->fetch_assoc();

// Cek apakah produk ada dan stok mencukupi
if (!$product || $product['stock'] < $quantity) {
    echo json_encode(['status' => 'error', 'message' => 'Stok produk tidak mencukupi.']);
    exit;
}

// Simpan detail item yang akan di-checkout ke dalam session
$_SESSION['checkout_item'] = [
    'product_id' => $product['id'],
    'name'       => $product['name'],
    'price'      => $product['price'],
    'image'      => $product['image'],
    'quantity'   => $quantity,
    'total_price'=> $product['price'] * $quantity
];

// Kirim respons sukses
echo json_encode(['status' => 'success', 'message' => 'Produk siap untuk di-checkout.']);
?>
