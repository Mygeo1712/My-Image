<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
require_once '../backend/dbconnect.php';

$upload_dir = '../uploads/gallery/';

if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0755, true);
}

$alert_message = '';
$alert_type = '';

$categories = ['Umum', 'Produk Segar', 'Proses Pengemasan', 'Pengiriman', 'Testimoni', 'Kegiatan Sosial', 'Tim Kami', 'Promo'];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $title = $_POST['title'] ?? 'Gambar Galeri';
    $description = $_POST['description'] ?? '';
    $category = $_POST['category'] ?? 'Umum';
    $image_id = $_POST['image_id'] ?? null;

    if (isset($_FILES['gallery_image']) && $_FILES['gallery_image']['error'] == 0) {
        $uploaded_by = $_SESSION['user_id'];
        $image_name = time() . '_' . basename($_FILES['gallery_image']['name']);
        $target_file = $upload_dir . $image_name;
        $imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

        $allow_types = ['jpg', 'jpeg', 'png', 'gif'];
        if (!in_array($imageFileType, $allow_types)) {
            $alert_message = 'Maaf, hanya file JPG, JPEG, PNG & GIF yang diizinkan.';
            $alert_type = 'danger';
        } else if ($_FILES['gallery_image']['size'] > 5000000) {
            $alert_message = 'Maaf, ukuran file terlalu besar (maks 5MB).';
            $alert_type = 'danger';
        } else {
            if (move_uploaded_file($_FILES['gallery_image']['tmp_name'], $target_file)) {
                if ($image_id) {
                    $stmt_old_img = $conn->prepare("SELECT image_name FROM gallery_images WHERE id = ?");
                    $stmt_old_img->bind_param("i", $image_id);
                    $stmt_old_img->execute();
                    $result_old_img = $stmt_old_img->get_result();
                    if ($row_old_img = $result_old_img->fetch_assoc()) {
                        $old_file = $upload_dir . $row_old_img['image_name'];
                        if (file_exists($old_file)) {
                            unlink($old_file);
                        }
                    }
                    $stmt_old_img->close();

                    $stmt = $conn->prepare("UPDATE gallery_images SET title = ?, description = ?, image_name = ?, category = ? WHERE id = ?");
                    $stmt->bind_param("ssssi", $title, $description, $image_name, $category, $image_id);
                } else {
                    $stmt = $conn->prepare("INSERT INTO gallery_images (title, description, image_name, category, uploaded_by) VALUES (?, ?, ?, ?, ?)");
                    $stmt->bind_param("ssssi", $title, $description, $image_name, $category, $uploaded_by);
                }

                if ($stmt->execute()) {
                    $alert_message = $image_id ? 'Gambar berhasil diperbarui!' : 'Gambar berhasil diunggah!';
                    $alert_type = 'success';
                } else {
                    $alert_message = 'Error menyimpan data gambar ke database: ' . $stmt->error;
                    $alert_type = 'danger';
                }
                $stmt->close();
            } else {
                $alert_message = 'Maaf, terjadi kesalahan saat mengunggah gambar Anda.';
                $alert_type = 'danger';
            }
        }
    } else if ($image_id && (!isset($_FILES['gallery_image']) || $_FILES['gallery_image']['error'] == 4)) {
        $stmt = $conn->prepare("UPDATE gallery_images SET title = ?, description = ?, category = ? WHERE id = ?");
        $stmt->bind_param("sssi", $title, $description, $category, $image_id);
        if ($stmt->execute()) {
            $alert_message = 'Detail gambar berhasil diperbarui!';
            $alert_type = 'success';
        } else {
            $alert_message = 'Error memperbarui detail gambar di database: ' . $stmt->error;
            $alert_type = 'danger';
        }
        $stmt->close();
    } else if ($_FILES['gallery_image']['error'] != 0 && $_FILES['gallery_image']['error'] != 4) {
        $alert_message = 'Terjadi kesalahan saat mengunggah file: Kode Error ' . $_FILES['gallery_image']['error'];
        $alert_type = 'danger';
    }
    
    header("Location: manage_gallery.php?status=$alert_type&message=" . urlencode($alert_message));
    exit();
}

if (isset($_GET['action']) && $_GET['action'] == 'delete' && isset($_GET['id'])) {
    $image_id = $_GET['id'];
    $stmt = $conn->prepare("SELECT image_name FROM gallery_images WHERE id = ?");
    $stmt->bind_param("i", $image_id);
    $stmt->execute();
    $result_img = $stmt->get_result(); 
    
    if ($row = $result_img->fetch_assoc()) {
        $file_to_delete = $upload_dir . $row['image_name'];
        if (file_exists($file_to_delete)) {
            unlink($file_to_delete);
        }
        $delete_stmt = $conn->prepare("DELETE FROM gallery_images WHERE id = ?");
        $delete_stmt->bind_param("i", $image_id);
        if ($delete_stmt->execute()) {
            $alert_message = 'Gambar berhasil dihapus!';
            $alert_type = 'success';
        } else {
            $alert_message = 'Error menghapus data gambar dari database: ' . $delete_stmt->error;
            $alert_type = 'danger';
        }
        $delete_stmt->close();
    } else {
        $alert_message = 'Gambar tidak ditemukan di database.';
        $alert_type = 'danger';
    }
    $stmt->close();
    header("Location: manage_gallery.php?status=$alert_type&message=" . urlencode($alert_message));
    exit();
}

$current_image_data = null;
if (isset($_GET['action']) && $_GET['action'] == 'edit' && isset($_GET['id'])) {
    $edit_image_id = $_GET['id'];
    $stmt_edit = $conn->prepare("SELECT id, title, description, image_name, category FROM gallery_images WHERE id = ?");
    $stmt_edit->bind_param("i", $edit_image_id);
    $stmt_edit->execute();
    $result_edit = $stmt_edit->get_result();
    if ($result_edit->num_rows > 0) {
        $current_image_data = $result_edit->fetch_assoc();
    } else {
        $alert_message = 'Gambar yang ingin diedit tidak ditemukan.';
        $alert_type = 'danger';
    }
    $stmt_edit->close();
}


if (isset($_GET['status']) && isset($_GET['message'])) {
    $alert_type = htmlspecialchars($_GET['status']);
    $alert_message = htmlspecialchars($_GET['message']);
}


$gallery_images = $conn->query("SELECT gi.id, gi.title, gi.description, gi.image_name, gi.category, gi.uploaded_at, u.username as uploader_name
                                     FROM gallery_images gi
                                     LEFT JOIN users u ON gi.uploaded_by = u.id
                                     ORDER BY gi.uploaded_at DESC");

?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Manajemen Galeri</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #212529; color: #e9ecef; }
        .container { max-width: 960px; }
        
        .card { 
            border-radius: 0.75rem; 
            border: none; 
            background-color: #343a40; 
            color: #e9ecef; 
        }
        .card-header { 
            border-bottom: none; 
            background-color: #007bff; 
            color: white; 
            border-radius: 0.75rem 0.75rem 0 0; 
            font-weight: bold; 
        }
        .card-title { font-weight: bold; color: #fff; }
        
        .form-label { color: #ced4da; }
        .form-control, .form-select {
            background-color: #495057; 
            color: #e9ecef; 
            border-color: #6c757d; 
        }
        .form-control::placeholder { color: #adb5bd; }
        .form-control:focus, .form-select:focus {
            background-color: #495057;
            color: #e9ecef;
            border-color: #007bff; 
            box-shadow: 0 0 0 0.25rem rgba(0, 123, 255, 0.25);
        }
        .text-muted { color: #adb5bd !important; }

        .img-preview { max-width: 150px; height: 100px; object-fit: cover; border-radius: 0.5rem; }
        
        .table { 
            color: #e9ecef; 
            background-color: #343a40; 
            border-radius: 0.5rem;
            overflow: hidden; 
        } 
        .table thead {
            background-color: #495057; 
            color: #fff;
        }
        .table-hover tbody tr:hover { background-color: #5a6268; }
        .table th, .table td { 
            border-top: 1px solid #495057; 
        }
        .table-bordered { border-color: #495057; }
        .table-bordered th, .table-bordered td { border-color: #495057; }

        .btn-primary { background-color: #007bff; border-color: #007bff; }
        .btn-primary:hover { background-color: #0056b3; border-color: #0056b3; }
        .btn-secondary { background-color: #6c757d; border-color: #6c757d; }
        .btn-secondary:hover { background-color: #5a6268; border-color: #545b62; }
        .btn-danger { background-color: #dc3545; border-color: #dc3545; }
        .btn-danger:hover { background-color: #c82333; border-color: #bd2130; }
        .btn-info { background-color: #17a2b8; border-color: #17a2b8; }
        .btn-info:hover { background-color: #138496; border-color: #117a8b; }

        h1, h2 { color: #fff; }
        .alert { 
            color: #212529; 
            background-color: #e2e3e5; 
            border-color: #d3d4d5; 
        }
        .alert-success { background-color: #d1e7dd; border-color: #badbcc; color: #0f5132; }
        .alert-danger { background-color: #f8d7da; border-color: #f5c2c7; color: #842029; }
        .alert-info { background-color: #dbeeff; border-color: #b8daff; color: #004085; }
        .alert .btn-close { filter: invert(0) grayscale(100%) brightness(0); }
    </style>
</head>
<body class="bg-dark text-light">
    <div class="container mt-5 mb-5">
        <h1 class="mb-4 text-center">Manajemen Galeri Website</h1>
        <a href="dashboard.php" class="btn btn-secondary mb-4"><i class="bi bi-arrow-left me-2"></i>Kembali ke Dashboard</a>

        <?php if ($alert_message): ?>
            <div class="alert alert-<?= $alert_type ?> alert-dismissible fade show" role="alert">
                <?= $alert_message ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <?php endif; ?>

        <div class="card shadow-lg mb-4">
            <div class="card-header">
                <h5 class="mb-0"><?= $current_image_data ? 'Edit Gambar Galeri' : 'Unggah Gambar Galeri Baru' ?></h5>
            </div>
            <div class="card-body p-4">
                <form action="manage_gallery.php" method="POST" enctype="multipart/form-data">
                    <?php if ($current_image_data): ?>
                        <input type="hidden" name="image_id" value="<?= htmlspecialchars($current_image_data['id']) ?>">
                    <?php endif; ?>
                    <div class="mb-3">
                        <label for="title" class="form-label">Judul Gambar <small class="text-muted">(Opsional)</small></label>
                        <input type="text" class="form-control" id="title" name="title" placeholder="Misal: Proses Pengemasan Kopi Pilihan" value="<?= htmlspecialchars($current_image_data['title'] ?? '') ?>">
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Deskripsi <small class="text-muted">(Opsional)</small></label>
                        <textarea class="form-control" id="description" name="description" rows="3" placeholder="Jelaskan detail gambar ini..."><?= htmlspecialchars($current_image_data['description'] ?? '') ?></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="category" class="form-label">Kategori</label>
                        <select class="form-select" id="category" name="category" required>
                            <?php foreach ($categories as $cat): ?>
                                <option value="<?= htmlspecialchars($cat) ?>" <?= ($current_image_data['category'] ?? '') == $cat ? 'selected' : '' ?>><?= htmlspecialchars($cat) ?></option>
                            <?php endforeach; ?>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="gallery_image" class="form-label">Pilih Gambar <small class="text-muted">(JPG, JPEG, PNG, GIF, maks 5MB) <?= $current_image_data ? '(Biarkan kosong jika tidak ingin mengubah gambar)' : '' ?></small></label>
                        <input type="file" class="form-control" id="gallery_image" name="gallery_image" accept="image/*" <?= $current_image_data ? '' : 'required' ?>>
                        <?php if ($current_image_data && $current_image_data['image_name']): ?>
                            <img id="image-preview" src="../uploads/gallery/<?= htmlspecialchars($current_image_data['image_name']) ?>" alt="Pratinjau Gambar Saat Ini" class="img-fluid mt-2 rounded" style="max-height:150px;">
                        <?php else: ?>
                            <img id="image-preview" src="#" alt="Pratinjau Gambar" class="img-fluid mt-2 rounded" style="display:none; max-height:150px;">
                        <?php endif; ?>
                    </div>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-<?= $current_image_data ? 'save' : 'upload' ?> me-2"></i><?= $current_image_data ? 'Perbarui Gambar' : 'Unggah Gambar' ?></button>
                    <?php if ($current_image_data): ?>
                        <a href="manage_gallery.php" class="btn btn-warning ms-2"><i class="bi bi-x-circle me-2"></i>Batal Edit</a>
                    <?php endif; ?>
                </form>
            </div>
        </div>

        <h2 class="mt-5 mb-3 text-center">Daftar Gambar Galeri</h2>
        <?php if ($gallery_images->num_rows > 0): ?>
            <div class="table-responsive shadow-sm rounded">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th class="text-center">Gambar</th>
                            <th>Judul & Deskripsi</th>
                            <th>Kategori</th>
                            <th>Diunggah Oleh</th>
                            <th class="text-center">Tanggal</th>
                            <th class="text-center">Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php while($image = $gallery_images->fetch_assoc()): ?>
                        <tr>
                            <td>
                                <img src="../uploads/gallery/<?= htmlspecialchars($image['image_name']) ?>" alt="<?= htmlspecialchars($image['title']) ?>" class="img-preview">
                            </td>
                            <td>
                                <strong><?= htmlspecialchars($image['title']) ?></strong><br>
                                <small class="text-muted"><?= substr(htmlspecialchars($image['description']), 0, 80) ?><?= strlen(htmlspecialchars($image['description'])) > 80 ? '...' : '' ?></small>
                            </td>
                            <td><?= htmlspecialchars($image['category'] ?? 'N/A') ?></td>
                            <td><?= htmlspecialchars($image['uploader_name'] ?? 'N/A') ?></td>
                            <td class="text-center"><small><?= date('d M Y', strtotime($image['uploaded_at'])) ?></small></td>
                            <td class="text-center action-buttons">
                                <a href="manage_gallery.php?action=edit&id=<?= $image['id'] ?>" class="btn btn-info btn-sm me-2 mb-1"><i class="bi bi-pencil-square"></i> Edit</a>
                                <a href="manage_gallery.php?action=delete&id=<?= $image['id'] ?>" class="btn btn-danger btn-sm mb-1" onclick="return confirm('Anda yakin ingin menghapus gambar ini?')"><i class="bi bi-trash"></i> Hapus</a>
                            </td>
                        </tr>
                        <?php endwhile; ?>
                    </tbody>
                </table>
            </div>
        <?php else: ?>
            <div class="alert alert-info text-center">Belum ada gambar dalam galeri.</div>
        <?php endif; ?>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $("#gallery_image").change(function() {
            if (this.files && this.files[0]) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    $('#image-preview').attr('src', e.target.result).show();
                };
                reader.readAsDataURL(this.files[0]);
            } else {
                <?php if (!$current_image_data): ?>
                    $('#image-preview').hide();
                <?php endif; ?>
            }
        });
    </script>
</body>
</html>