<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (isset($_SESSION['user_id'])) {
    $user_id = $_SESSION['user_id'];
    if (!isset($conn)) {
        require_once 'backend/dbconnect.php';
    }
    $stmt = $conn->prepare("SELECT u.username, up.profile_photo FROM users u LEFT JOIN user_profile up ON u.id = up.user_id WHERE u.id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $user = $result->fetch_assoc();
    $stmt->close();
}

if (!isset($pageTitle)) $pageTitle = "Halaman";
if (!isset($pageContent)) $pageContent = "page/404.php";
if (!isset($currentPage)) $currentPage = 'home';

$base_url = rtrim('/toko_sembako/', '/') . '/';
?>

<div class="wrapper">
    <div class="bg-dark-blue text-white sidebar d-none d-lg-flex flex-column" id="sidebar">
        <div class="p-3">
            <div class="d-flex justify-content-center mb-3"> <a href="<?= $base_url ?>home" class="nav-link logo-link">
                    <img src="<?= $base_url ?>uploads/Logo3.png" alt="Logo Website" style="width: 70px; height: 70px;">
                </a>
            </div>
            <h6 class="mb-3 text-secondary text-center">Selamat Datang, <?= htmlspecialchars($_SESSION['username']) ?></h6>
            <nav class="nav flex-column">
                <a href="<?= $base_url ?>home" class="nav-link <?= $currentPage == 'home' ? 'active' : '' ?>"><i class="bi bi-house-door me-2"></i> Beranda</a>
                <a href="<?= $base_url ?>cart" class="nav-link <?= $currentPage == 'cart' ? 'active' : '' ?>"><i class="bi bi-cart3 me-2"></i> Keranjang</a>
                <a href="<?= $base_url ?>profile" class="nav-link <?= $currentPage == 'profile' ? 'active' : '' ?>"><i class="bi bi-person-circle me-2"></i> Profil</a>
                <a href="<?= $base_url ?>transaksi" class="nav-link <?= $currentPage == 'transaksi' ? 'active' : '' ?>"><i class="bi bi-receipt-cutoff me-2"></i> Transaksi</a>
                <a href="<?= $base_url ?>berita" class="nav-link <?= $currentPage == 'berita' ? 'active' : '' ?>"><i class="bi bi-newspaper me-2"></i> Berita & Artikel</a>
                <a href="<?= $base_url ?>galeri" class="nav-link <?= $currentPage == 'galeri' ? 'active' : '' ?>"><i class="bi bi-images me-2"></i> Galeri</a>
                <a href="<?= $base_url ?>kontak" class="nav-link <?= $currentPage == 'kontak' ? 'active' : '' ?>"><i class="bi bi-envelope-fill me-2"></i> Kontak</a>
            </nav>
        </div>
    </div>

    <div class="offcanvas offcanvas-start offcanvas-sidebar bg-dark text-white" tabindex="-1" id="mobileSidebar" aria-labelledby="mobileSidebarLabel">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title" id="mobileSidebarLabel">Menu</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="offcanvas-body">
            <nav class="nav flex-column">
                <a href="<?= $base_url ?>home" class="nav-link <?= $currentPage == 'home' ? 'active' : '' ?>"><i class="bi bi-house-door me-2"></i> Beranda</a>
                <a href="<?= $base_url ?>cart" class="nav-link <?= $currentPage == 'cart' ? 'active' : '' ?>"><i class="bi bi-cart3 me-2"></i> Keranjang</a>
                <a href="<?= $base_url ?>profile" class="nav-link <?= $currentPage == 'profile' ? 'active' : '' ?>"><i class="bi bi-person-circle me-2"></i> Profil</a>
                <a href="<?= $base_url ?>transaksi" class="nav-link <?= $currentPage == 'transaksi' ? 'active' : '' ?>"><i class="bi bi-receipt-cutoff me-2"></i> Transaksi</a>
                <a href="<?= $base_url ?>berita" class="nav-link <?= $currentPage == 'berita' ? 'active' : '' ?>"><i class="bi bi-newspaper me-2"></i> Berita & Artikel</a>
                <a href="<?= $base_url ?>galeri" class="nav-link <?= $currentPage == 'galeri' ? 'active' : '' ?>"><i class="bi bi-images me-2"></i> Galeri</a>
                <a href="<?= $base_url ?>kontak" class="nav-link <?= $currentPage == 'kontak' ? 'active' : '' ?>"><i class="bi bi-envelope-fill me-2"></i> Kontak</a>
            </nav>
        </div>
    </div>

    <div class="main-content">
        <div class="topbar d-flex justify-content-between align-items-center px-4">

            <button class="btn btn-sm btn-outline-primary d-lg-none" type="button" data-bs-toggle="offcanvas" data-bs-target="#mobileSidebar">
                <i class="bi bi-list" style="font-size: 1.5rem;"></i>
            </button>
            <h2 class="fw-bold mb-0 flex-grow-1 text-truncate px-3"><?= htmlspecialchars($pageTitle) ?></h2>
            <div class="d-flex align-items-center gap-3">
                <div class="dropdown">
                    <a href="#" class="text-decoration-none text-secondary position-relative" id="notificationDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-bell-fill fs-4"></i>
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger border border-light" id="notification-count" style="display: none;"></span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end shadow-lg" aria-labelledby="notificationDropdown" id="notification-list" style="width: 350px; max-height: 400px; overflow-y: auto;">
                        <li>
                            <p class="text-center p-3 text-muted small">Memuat...</p>
                        </li>
                    </ul>
                </div>
                <?php if (!empty($user['profile_photo'])): ?>
                    <img src="<?= htmlspecialchars($user['profile_photo']) ?>" alt="Profile Photo" class="rounded-circle border border-2 border-secondary" width="36" height="36">
                <?php else: ?>
                    <div class="rounded-circle border border-2 border-secondary bg-light d-flex align-items-center justify-content-center" style="width:36px; height:36px;">
                        <i class="bi bi-person" style="font-size: 1.5rem; color: #6c757d;"></i>
                    </div>
                <?php endif; ?>
                <a href="<?= $base_url ?>logout.php" class="btn btn-sm btn-logout d-none d-sm-block">
                    <i class="bi bi-box-arrow-right me-1"></i> Logout
                </a>
            </div>
        </div>

        <div class="page-scrollable px-4 py-3">
            <?php if (isset($pageContent) && file_exists($pageContent)) include($pageContent); ?>
        </div>
    </div>
</div>

<style>
    body {
        font-family: 'Inter', sans-serif;
        margin: 0;
        padding: 0;
    }

    .wrapper {
        display: flex;
        height: 100vh;
        overflow: hidden;
    }

    .sidebar {
        width: 230px;
        background-color: #1e2a38;
        color: #fff;
        position: sticky;
        top: 0;
        height: 100vh;
        overflow-y: auto;
        z-index: 1000;
    }

    .main-content {
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        height: 100vh;
        overflow: hidden;
        background-color: #f4f7fc;
    }

    .topbar {
        position: sticky;
        top: 0;
        background-color: #f4f7fc;
        padding: 1rem 0;
        z-index: 999;
    }

    .page-scrollable {
        flex-grow: 1;
        overflow-y: auto;
    }

    .nav-link {
        color: #ffffffcc;
        font-weight: 500;
        padding: 0.5rem 1rem;
        border-radius: 0.375rem;
        margin-bottom: 0.3rem;
        transition: background-color 0.3s, color 0.3s;
    }

    .nav-link.active,
    .nav-link:hover {
        background-color: #ffffff22;
        color: #ffffff;
    }

    .btn-logout {
        background-color: #dc3545;
        color: #fff;
        padding: 6px 12px;
        border: none;
        border-radius: 0.375rem;
        transition: background-color 0.3s ease;
        font-weight: 500;
    }

    .btn-logout:hover {
        background-color: #bb2d3b;
    }

    .logo-link.nav-link:hover,
    .logo-link.nav-link.active {
        background-color: transparent !important;
        color: inherit !important;
    }

    .logo-link i,
    .logo-link span,
    .logo-link img {
        transition: none !important;
    }

    @media (max-width: 991.98px) {
        .offcanvas-sidebar {
            width: 66vw !important;
        }

        .topbar h2 {
            font-size: 1.2rem;
        }

        .btn-logout {
            padding: 4px 8px;
            font-size: 0.8rem;
        }
    }

    @media (min-width: 576px) {
        .btn-logout {
            font-size: 0.9rem;
            padding: 5px 10px;
        }
    }

    @media (min-width: 768px) {
        .btn-logout {
            font-size: 1rem;
            padding: 6px 12px;
        }
    }

    @media (min-width: 1200px) {
        h2 {
            font-size: 2rem;
        }

        .btn-logout {
            font-size: 1.1rem;
        }
    }
</style>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    $(document).ready(function() {
        function fetchNotifications() {
            $.getJSON('/toko_sembako/ajax/fetch_notifications.php', function(data) {
                console.log("Data diterima:", data);

                if (data && data.status === 'success') {
                    const notifList = $('#notification-list');
                    notifList.empty();

                    if (data.notifications && data.notifications.length > 0) {
                        $.each(data.notifications, function(i, notif) {
                            const isUnreadClass = notif.is_read == 0 ? 'bg-light fw-bold' : 'text-muted';
                            const notifHtml = `
                            <li>
                                <a class="dropdown-item p-3 ${isUnreadClass}" href="/toko_sembako/transaksi">
                                    <p class="mb-1 small">${notif.message}</p>
                                    <small class="opacity-75">${new Date(notif.created_at).toLocaleString('id-ID', {day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'})}</small>
                                </a>
                            </li>`;
                            notifList.append(notifHtml);
                        });
                    } else {
                        notifList.append('<li><p class="text-center p-3 text-muted small">Tidak ada notifikasi.</p></li>');
                    }

                    const notifCount = $('#notification-count');
                    if (data.unread_count > 0) {
                        notifCount.text(data.unread_count).show();
                    } else {
                        notifCount.hide();
                    }
                } else {
                    console.error("Gagal mengambil data notifikasi:", data ? data.message : "Data tidak diterima.");
                }
            }).fail(function(jqXHR, textStatus, errorThrown) {
                console.error("AJAX Error:", textStatus, errorThrown);
                $('#notification-list').html('<li><p class="text-center p-3 text-danger small">Gagal memuat notifikasi.</p></li>');
            });
        }

        fetchNotifications();

        $('#notificationDropdown').on('show.bs.dropdown', function() {
            if ($('#notification-count').is(':visible')) {
                $.getJSON('/toko_sembako/ajax/fetch_notifications.php?action=mark_read', function(data) {
                    if (data.status === 'success') {
                        $('#notification-count').fadeOut('slow');
                        $('#notification-list .bg-light').removeClass('bg-light fw-bold').addClass('text-muted');
                    }
                });
            }
        });

        setInterval(fetchNotifications, 60000);
    });
</script>