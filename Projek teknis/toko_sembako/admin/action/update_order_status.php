<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
header('Content-Type: application/json');

if (!isset($_SESSION['user_id']) || !isset($_SESSION['role']) || $_SESSION['role'] !== 'admin') {
    echo json_encode(['status' => 'error', 'message' => 'Akses tidak diizinkan.']);
    exit;
}

if (empty($_POST['group_id']) || empty($_POST['status'])) {
    echo json_encode(['status' => 'error', 'message' => 'Data pesanan tidak lengkap.']);
    exit;
}

require_once '../../backend/dbconnect.php';

$group_id = $_POST['group_id'];
$new_status = $_POST['status'];
$allowed_statuses = ['dikirim', 'dibatalkan'];

if (!in_array($new_status, $allowed_statuses)) {
    echo json_encode(['status' => 'error', 'message' => 'Aksi status tidak valid.']);
    exit;
}

if ($new_status === 'dikirim') {
    date_default_timezone_set('Asia/Jakarta');

    $schedule_stmt = $conn->prepare("SELECT delivery_schedule FROM orders WHERE order_group_id = ? LIMIT 1");
    if ($schedule_stmt === false) {
        echo json_encode(['status' => 'error', 'message' => 'Database error: Gagal menyiapkan query jadwal.']);
        exit;
    }
    $schedule_stmt->bind_param("s", $group_id);
    $schedule_stmt->execute();
    $schedule_result = $schedule_stmt->get_result();
    
    if ($schedule_result->num_rows > 0) {
        $order = $schedule_result->fetch_assoc();
        $delivery_date = new DateTime($order['delivery_schedule']);
        $current_date = new DateTime();

        if ($current_date->format('Y-m-d') !== $delivery_date->format('Y-m-d')) {
            echo json_encode([
                'status' => 'error', 
                'message' => 'Pesanan ini hanya bisa diproses pada hari pengiriman yang dijadwalkan, yaitu tanggal ' . $delivery_date->format('d M Y') . '.'
            ]);
            exit;
        }
    }
    $schedule_stmt->close();
}

$user_id_stmt = $conn->prepare("SELECT user_id FROM orders WHERE order_group_id = ? LIMIT 1");
$user_id_stmt->bind_param("s", $group_id);
$user_id_stmt->execute();
$order_user = $user_id_stmt->get_result()->fetch_assoc();
$user_id_stmt->close();

if (!$order_user) {
    echo json_encode(['status' => 'error', 'message' => 'User pesanan tidak ditemukan.']);
    exit;
}
$target_user_id = $order_user['user_id'];
$order_short_id = substr(str_replace('ORDER-', '', $group_id), 0, 8);

$stmt = $conn->prepare("UPDATE orders SET status = ? WHERE order_group_id = ?");
$stmt->bind_param("ss", $new_status, $group_id);

if ($stmt->execute()) {

    $message = "";
    if ($new_status === 'dikirim') {
        $message = "Pesanan Anda #" . $order_short_id . " telah dikirim dan sedang dalam perjalanan!";
    } elseif ($new_status === 'dibatalkan') {
        $message = "Dengan berat hati, pesanan Anda #" . $order_short_id . " telah dibatalkan.";
    }
    
    if ($message) {
        $notif_stmt = $conn->prepare("INSERT INTO notifications (user_id, order_group_id, message) VALUES (?, ?, ?)");
        $notif_stmt->bind_param("iss", $target_user_id, $group_id, $message);
        $notif_stmt->execute();
        $notif_stmt->close();
    }
    echo json_encode(['status' => 'success']);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Gagal memperbarui database: ' . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
