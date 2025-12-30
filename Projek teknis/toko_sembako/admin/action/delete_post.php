<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') { exit('Akses ditolak'); }
require_once '../../backend/dbconnect.php';

if (isset($_GET['id'])) {
    $id = $_GET['id'];
    $stmt = $conn->prepare("SELECT image FROM posts WHERE id = ?");
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $result = $stmt->get_result()->fetch_assoc();
    if ($result && !empty($result['image'])) {
        unlink('../../uploads/posts/' . $result['image']);
    }
    $stmt->close();
    
    $stmt_delete = $conn->prepare("DELETE FROM posts WHERE id = ?");
    $stmt_delete->bind_param("i", $id);
    $stmt_delete->execute();
    $stmt_delete->close();
}
header("Location: ../manage_posts.php");
?>
