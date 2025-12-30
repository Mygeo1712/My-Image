<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}

require_once '../backend/dbconnect.php'; 


function log_admin_activity($conn, $admin_id, $activity, $activity_type) {
    $stmt = $conn->prepare("INSERT INTO admin_logs (admin_id, activity_time, activity, activity_type) VALUES (?, NOW(), ?, ?)");
    $stmt->bind_param("iss", $admin_id, $activity, $activity_type);
    $stmt->execute();
    $stmt->close();
}

if (isset($_POST['add'])) {
    $name        = trim($_POST['name']);
    $category    = trim($_POST['category']);
    $description = trim($_POST['description']);
    $price       = floatval($_POST['price']);
    $stock       = intval($_POST['stock']);
    $image       = $_FILES['image']['name'];
    $slug        = createSlug($name);
    $uploadOk    = 1;
    $target      = "../uploads/" . basename($image);

    if ($_FILES['image']['error'] === UPLOAD_ERR_OK) {
        if (!move_uploaded_file($_FILES['image']['tmp_name'], $target)) {
            $uploadOk = 0;
            $_SESSION['notification'] = ['type' => 'danger', 'message' => 'Maaf, terjadi kesalahan saat mengupload gambar.'];
        }
    } else {
        $uploadOk = 0;
        $_SESSION['notification'] = ['type' => 'danger', 'message' => 'Tidak ada gambar yang diupload atau terjadi error.'];
    }

    if ($uploadOk) {
        $stmt = $conn->prepare("INSERT INTO products (name, slug, category, description, price, stock, image) VALUES (?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("ssssdss", $name, $slug, $category, $description, $price, $stock, $image);

        if ($stmt->execute()) {
            log_admin_activity($conn, $_SESSION['user_id'], "Menambah produk: $name", "add_product");
          
            $_SESSION['notification'] = ['type' => 'success', 'message' => "Produk '" . htmlspecialchars($name) . "' berhasil ditambahkan."];
        } else {
            $_SESSION['notification'] = ['type' => 'danger', 'message' => "Gagal menyimpan produk ke database: " . $stmt->error];
        }
        $stmt->close();
    }

    header("Location: edit_product.php");
    exit();
}

$result = $conn->query("SELECT * FROM products ORDER BY id DESC");
?>
<!DOCTYPE html>
<html lang="id">

<head>
    <meta charset="UTF-8">
    <title>Edit Produk</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>

<body class="bg-dark text-light">
    <div class="container mt-5">
        <h1>Manajemen Produk</h1>
        <a href="dashboard.php" class="btn btn-secondary mb-4">← Kembali ke Dashboard</a>

        <h3>Tambah Produk Baru</h3>
        <form method="POST" enctype="multipart/form-data" class="mb-5">
            <div class="mb-3">
                <input type="text" name="name" placeholder="Nama Produk" required class="form-control">
            </div>
            <div class="mb-3">
                <select name="category" class="form-control" required>
                    <option value="">-- Pilih Kategori --</option>
                    <option value="beras">Beras</option>
                    <option value="telur">Telur</option>
                    <option value="tepung">Tepung</option>
                    <option value="mie">Mie</option>
                    <option value="minyak">Minyak</option>
                    <option value="susu">Susu</option>
                    <option value="gula">Gula</option>
                    <option value="daging">Daging</option>
                    <option value="ikan">Ikan</option>
                    <option value="bumbu">Bumbu Dapur</option>
                </select>
            </div>
            <div class="mb-3">
                <textarea name="description" placeholder="Deskripsi" class="form-control"></textarea>
            </div>
            <div class="mb-3">
                <input type="number" step="0.01" name="price" placeholder="Harga" required class="form-control">
            </div>
            <div class="mb-3">
                <input type="number" name="stock" placeholder="Stok" required min="0" class="form-control">
            </div>
            <div class="mb-3">
                <input type="file" name="image" required class="form-control" accept="image/*">
            </div>
            <button type="submit" name="add" class="btn btn-success">Tambah Produk</button>
        </form>

        <h3>Daftar Produk</h3>
        <div class="table-responsive">
            <table class="table table-bordered table-striped table-dark">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nama</th>
                        <th>Slug</th>
                        <th>Kategori</th>
                        <th>Harga</th>
                        <th>Stok</th>
                        <th>Gambar</th>
                        <th>Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <?php while ($row = $result->fetch_assoc()): ?>
                        <tr>
                            <td><?= $row['id'] ?></td>
                            <td><?= htmlspecialchars($row['name']) ?></td>
                            <td><?= htmlspecialchars($row['slug']) ?></td>
                            <td><?= htmlspecialchars($row['category']) ?></td>
                            <td>Rp<?= number_format($row['price'], 2, ',', '.') ?></td>
                            <td><?= $row['stock'] ?></td>
                            <td><img src="../uploads/<?= htmlspecialchars($row['image']) ?>" width="80" alt=""></td>
                            <td>
                                <div class="d-flex flex-column">
                                    <a href="action/edit_single_product.php?id=<?= urlencode($row['id']) ?>" class="btn btn-warning btn-sm mb-2">Edit</a>
                                    <a href="action/delete_product.php?id=<?= urlencode($row['id']) ?>" class="btn btn-danger btn-sm" onclick="return confirm('Yakin ingin menghapus?')">Hapus</a>
                                </div>
                            </td>
                        </tr>
                    <?php endwhile; ?>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Modal Konfirmasi -->
    <div class="modal fade" id="confirmationModal" tabindex="-1" aria-labelledby="confirmationModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content bg-dark text-light">
                <div class="modal-header border-0">
                    <h5 class="modal-title" id="confirmationModalLabel"></h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body text-center fs-5">
                    <i id="modalIcon" class="bi mb-3" style="font-size: 3rem;"></i>
                    <p id="modalMessage"></p>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Tutup</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>

    <?php if (isset($_SESSION['notification'])): ?>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const notification = <?= json_encode($_SESSION['notification']) ?>;
            const modalElement = document.getElementById('confirmationModal');
            const modalTitle = modalElement.querySelector('.modal-title');
            const modalMessage = modalElement.querySelector('#modalMessage');
            const modalIcon = modalElement.querySelector('#modalIcon');
            
            if (notification.type === 'success') {
                modalTitle.innerText = 'Berhasil!';
                modalIcon.className = 'bi bi-check-circle-fill text-success mb-3';
            } else {
                modalTitle.innerText = 'Gagal!';
                modalIcon.className = 'bi bi-x-circle-fill text-danger mb-3';
            }
            
            modalMessage.innerText = notification.message;

            const confirmationModal = new bootstrap.Modal(modalElement);
            confirmationModal.show();
        });
    </script>
    <?php
        // Hapus notifikasi dari session setelah digunakan
        unset($_SESSION['notification']);
    ?>
    <?php endif; ?>

</body>
</html>
