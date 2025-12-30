<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'courier') {
    header("Location: ../login.php");
    exit();
}
require_once '../backend/dbconnect.php';

$page = isset($_GET['page']) ? $_GET['page'] : 'delivery_list';

switch ($page) {
    case 'delivery_list':
        $pageTitle = "Daftar Pengantaran";
        $pageContent = "pages/delivery_list.php";
        break;
    case 'history':
        $pageTitle = "Riwayat Pengantaran";
        $pageContent = "pages/history.php";
        break;
    default:
        $pageTitle = "Halaman Tidak Ditemukan";
        $pageContent = "pages/404.php";
        break;
}

include('includes/header.php');
?>
