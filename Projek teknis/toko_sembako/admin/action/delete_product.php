<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}

require_once '../../backend/dbconnect.php';

function log_admin_activity($conn, $admin_id, $activity, $activity_type) {
    $stmt = $conn->prepare("INSERT INTO admin_logs (admin_id, activity_time, activity, activity_type) VALUES (?, NOW(), ?, ?)");
    $stmt->bind_param("iss", $admin_id, $activity, $activity_type);
    $stmt->execute();
    $stmt->close();
}

if (isset($_GET['id'])) {
    $id = intval($_GET['id']);

    $stmt = $conn->prepare("SELECT image, name FROM products WHERE id = ?");
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $stmt->bind_result($image, $product_name);
    $stmt->fetch();
    $stmt->close();

    $stmt = $conn->prepare("DELETE FROM products WHERE id = ?");
    $stmt->bind_param("i", $id);
    if ($stmt->execute()) {

        $imagePath = "../uploads/" . $image;
        if (file_exists($imagePath)) {
            unlink($imagePath);
        }

        log_admin_activity($conn, $_SESSION['user_id'], "Menghapus produk: $product_name", "delete_product");
    }
    $stmt->close();
}

header("Location: ../edit_product.php");
exit();
