<?php
session_start();
require_once '../backend/dbconnect.php';
header('Content-Type: application/json');

// Keamanan & Validasi Input
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'courier') {
    echo json_encode(['status' => 'error', 'message' => 'Akses tidak diizinkan.']);
    exit;
}
if (empty($_POST['group_id']) || !isset($_FILES['proof_image'])) {
    echo json_encode(['status' => 'error', 'message' => 'Data tidak lengkap.']);
    exit;
}

$courier_id = $_SESSION['user_id'];
$group_id = $_POST['group_id'];
$proof_file = $_FILES['proof_image'];

// Logika Upload Gambar
if ($proof_file['error'] !== UPLOAD_ERR_OK) {
    echo json_encode(['status' => 'error', 'message' => 'Gagal mengupload gambar. Kode Error: ' . $proof_file['error']]);
    exit;
}
$file_extension = pathinfo($proof_file['name'], PATHINFO_EXTENSION);
$new_filename = 'proof_' . $group_id . '_' . time() . '.' . $file_extension;
$upload_dir = '../Bukti_Konfirmasi_Pesanan/';
$target_file = $upload_dir . $new_filename;

if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0755, true);
}

// Pindahkan file
if (move_uploaded_file($proof_file['tmp_name'], $target_file)) {
    // Ambil user_id dari pesanan untuk target notifikasi
    $user_id_stmt = $conn->prepare("SELECT user_id FROM orders WHERE order_group_id = ? LIMIT 1");
    $user_id_stmt->bind_param("s", $group_id);
    $user_id_stmt->execute();
    $target_user_id = $user_id_stmt->get_result()->fetch_assoc()['user_id'];
    $user_id_stmt->close();

    // Update status pesanan
    $stmt = $conn->prepare(
        "UPDATE orders SET status = 'selesai', proof_of_delivery = ? 
         WHERE order_group_id = ? AND courier_id = ?"
    );
    $stmt->bind_param("ssi", $new_filename, $group_id, $courier_id);

    if ($stmt->execute()) {
        // --- BUAT NOTIFIKASI UNTUK PENGGUNA ---
        $order_short_id = substr(str_replace('ORDER-', '', $group_id), 0, 8);
        $message = "Pesanan #" . $order_short_id . " telah Anda terima. Terima kasih telah berbelanja!";
        $notif_stmt = $conn->prepare("INSERT INTO notifications (user_id, order_group_id, message) VALUES (?, ?, ?)");
        $notif_stmt->bind_param("iss", $target_user_id, $group_id, $message);
        $notif_stmt->execute();
        $notif_stmt->close();

        echo json_encode(['status' => 'success']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Gagal memperbarui status pesanan.']);
    }
    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Gagal menyimpan file bukti pengiriman.']);
}
$conn->close();
?>
