<?php
session_start();

if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

include_once("backend/dbconnect.php");

$page = isset($_GET['page']) ? $_GET['page'] : 'home';

$pages_requiring_complete_profile = ['cart', 'payment', 'detail']; 
if (in_array($page, $pages_requiring_complete_profile)) {
    if (isset($_SESSION['user_id']) && !isProfileComplete($conn, $_SESSION['user_id'])) {
        $_SESSION['profile_alert'] = "Mohon lengkapi data profil Anda (Nama Lengkap, Telepon, dan Alamat Lengkap) sebelum melanjutkan transaksi.";
        $page = 'profile'; 
    }
}


switch ($page) {
    case 'home':
        $pageTitle = "Beranda";
        $pageContent = "page/home.php";
        break;

    case 'detail':
        if (isset($_GET['slug'])) {
            $pageTitle = "Detail Produk";
            $pageContent = "page/detail.php";
        } else {
            $pageTitle = "Produk Tidak Ditemukan";
            $pageContent = "page/404.php";
        }
        break;
    
    case 'edit-order':
        if (isset($_GET['group_id'])) {
            $pageTitle = "Edit Jadwal Pesanan";
            $pageContent = "page/edit_order.php";
        } else {
            $pageTitle = "Pesanan Tidak Ditemukan";
            $pageContent = "page/404.php";
        }
        break;

    case 'payment':
        $pageTitle = "Metode Pembayaran";
        $pageContent = "page/payment.php";
        break;

    case 'cart':
        $pageTitle = "Keranjang";
        $pageContent = "page/cart.php";
        break;

    case 'profile':
        $pageTitle = "Profil";
        $pageContent = "page/profile_view.php";
        break;

    case 'transaksi':
        $pageTitle = "Riwayat Transaksi";
        $pageContent = "page/transaksi.php";
        break;
        
    case 'berita':
        $pageTitle = "Berita & Artikel";
        $pageContent = "page/berita.php";
        break;
    
    case 'artikel':
        $pageTitle = "Detail Artikel";
        $pageContent = "page/artikel.php";
        break;

    case 'konten_berita':
        if (isset($_GET['slug'])) {
            $pageTitle = "Detail Berita"; 
            $pageContent = "page/konten_berita.php";
        } else {
            $pageTitle = "Berita Tidak Ditemukan";
            $pageContent = "page/404.php";
        }
        break;

    case 'galeri':
        $pageTitle = "Galeri Foto";
        $pageContent = "page/galeri.php";
        break;

    case 'kontak':
        $pageTitle = "Hubungi Kami";
        $pageContent = "page/kontak.php";
        break;

    default:
        $pageTitle = "Halaman Tidak Ditemukan";
        $pageContent = "page/404.php";
        http_response_code(404);
        break;
}

$currentPage = $page;

include("includes/header.php");

include("includes/footer.php");