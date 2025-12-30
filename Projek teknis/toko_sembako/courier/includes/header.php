<!DOCTYPE html>
<html lang="id">

<head>
    <meta charset="UTF-8">
    <title>Kurir Dashboard - <?= htmlspecialchars($pageTitle ?? 'Dashboard') ?></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <!-- Tambahkan JQuery di sini -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <!-- Bootstrap JS (sebaiknya setelah JQuery) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>

    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f4f7f6;
        }

        .sidebar {
            background-color: #2c3e50;
            min-height: 100vh;
        }

        .sidebar .nav-link {
            color: #ecf0f1;
            padding: 10px 15px;
            transition: 0.2s;
        }

        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            background-color: #34495e;
            color: #fff;
        }

        .content {
            width: 100%;
        }
    </style>
</head>

<body>
    <div class="d-flex">
        <!-- Sidebar -->
        <div class="sidebar text-white p-3 d-flex flex-column">
            <a href="dashboard.php" class="text-white text-decoration-none d-flex align-items-center mb-4">
                <i class="bi bi-truck fs-4 me-2"></i>
                <span class="fs-5">Kurir Panel</span>
            </a>
            <ul class="nav nav-pills flex-column mb-auto">
                <li class="nav-item">
                    <a href="dashboard.php?page=delivery_list" class="nav-link <?= $page == 'delivery_list' ? 'active' : '' ?>">
                        <i class="bi bi-box-seam me-2"></i> Daftar Pengantaran
                    </a>
                </li>
                <li class="nav-item">
                    <a href="dashboard.php?page=history" class="nav-link <?= $page == 'history' ? 'active' : '' ?>">
                        <i class="bi bi-clock-history me-2"></i> Riwayat
                    </a>
                </li>
            </ul>
            <hr>
            <div>
                <a href="../logout.php" class="nav-link">
                    <i class="bi bi-box-arrow-left me-2"></i> Logout
                </a>
            </div>
            
        </div>

        <!-- Main Content -->
        <div class="content p-4">
            <h2 class="mb-4"><?= htmlspecialchars($pageTitle) ?></h2>
            
            
            <div id="alert-container-courier"></div>
            <?php
            // Memuat file konten halaman
            if (isset($pageContent) && file_exists($pageContent)) {
                include($pageContent);
            } else {
                echo '<div class="alert alert-danger">Halaman tidak ditemukan.</div>';
            }
            ?>
        </div>
    </div>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html> 