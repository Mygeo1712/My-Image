<?php
session_start();

if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
require_once '../backend/dbconnect.php';

?>
<?php include('includes/header.php'); ?>

<div class="container mt-5">
    <h1>Selamat Datang, <?= htmlspecialchars($_SESSION['username']) ?></h1>
    <p>Anda login sebagai <strong>Admin</strong>.</p>
    <a href="../logout.php" class="btn btn-danger mb-4">Logout</a>

    <div class="row g-4">
        <!-- Manage Post -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Manage Post</h5>
                    <p class="card-text">Edit Berita,Blog/ Artikel dan Gambar</p>
                    <div class="mt-auto">
                        <a href="manage_posts.php" class="btn btn-light">Edit Produk</a>
                    </div>

                </div>
            </div>
        </div>
        <!-- Manage Gallery -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Manage Gallery</h5>
                    <p class="card-text">Edit  Gambar</p>
                    <div class="mt-auto">
                        <a href="manage_gallery.php" class="btn btn-light">Edit Produk</a>
                    </div>

                </div>
            </div>
        </div>

        <!-- Kontak -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Manajemen Informasi Kontak</h5>
                    <p class="card-text">Ubah alamat, telepon, email, dan tautan sosial media.</p>
                    <div class="mt-auto">
                        <a href="manage_contact_info.php" class="btn btn-light">Kelola Kontak</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Edit Product -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Kelola Produk</h5>
                    <p class="card-text">Edit, tambah, atau hapus produk dalam database..</p>
                    <div class="mt-auto">
                        <a href="edit_product.php" class="btn btn-light">Edit Produk</a>
                    </div>

                </div>
            </div>
        </div>

        <!-- Edit Stok Khusus -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Edit Stok Khusus</h5>
                    <p class="card-text">Kelola stok produk di gudang secara spesifik berdasarkan item.</p>
                    <div class="mt-auto">
                        <a href="edit_stock.php" class="btn btn-light">Edit Stok</a>
                    </div>
                </div>
            </div>
        </div>

        
        <!-- Manage Orders -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Manage Orders</h5>
                    <p class="card-text">Detail status pesanan</p>
                    <div class="mt-auto">
                        <a href="manage_orders.php" class="btn btn-light">Edit Transaksi</a>
                    </div>

                </div>
            </div>
        </div>

        <!-- Manage user account -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Manage User Account</h5>
                    <p class="card-text">Lihat data user, hapus akun user, dan lihat statistik user.</p>
                    <div class="mt-auto">
                        <a href="manage_user.php" class="btn btn-light">Kelola User</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Manage courrier account -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Manage courrier Account</h5>
                    <p class="card-text">Lihat data Kurir, hapus akun user, dan lihat statistik user.</p>
                    <div class="mt-auto">
                        <a href="register_courier.php" class="btn btn-light">Kelola User</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Manage Admin Account -->
        <div class="col-md-4">
            <div class="card bg-secondary text-white mb-3 h-100">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">Manage Admin Account</h5>
                    <p class="card-text">Buat akun admin baru dan lihat data user & admin.</p>
                    <div class="mt-auto">
                        <a href="manage_admin.php" class="btn btn-light">Kelola Admin</a>
                    </div>
                </div>
            </div>
        </div>


    </div>
</div>
</body>

</html>

<?php include('includes/footer.php');?>