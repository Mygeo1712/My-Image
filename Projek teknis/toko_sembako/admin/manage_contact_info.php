<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
require_once '../backend/dbconnect.php';

$contact_info = null;
$result = $conn->query("SELECT * FROM contact_info ORDER BY id ASC LIMIT 1");
if ($result && $result->num_rows > 0) {
    $contact_info = $result->fetch_assoc();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $address = $_POST['address'] ?? '';
    $phone_number = $_POST['phone_number'] ?? '';
    $email = $_POST['email'] ?? '';
    $facebook_url = $_POST['facebook_url'] ?? null;
    $instagram_url = $_POST['instagram_url'] ?? null;
    $twitter_url = $_POST['twitter_url'] ?? null;
    $whatsapp_number = $_POST['whatsapp_number'] ?? null;
    $Maps_embed = $_POST['Maps_embed'] ?? null;
    $updated_by = $_SESSION['user_id'];

    if ($contact_info) {
        // Update data yang sudah ada
        $stmt = $conn->prepare("UPDATE contact_info SET address=?, phone_number=?, email=?, facebook_url=?, instagram_url=?, twitter_url=?, whatsapp_number=?, Maps_embed=?, updated_by=? WHERE id=?");
        $stmt->bind_param("ssssssssii", $address, $phone_number, $email, $facebook_url, $instagram_url, $twitter_url, $whatsapp_number, $Maps_embed, $updated_by, $contact_info['id']);
    } else {
        // Masukkan data baru jika belum ada
        $stmt = $conn->prepare("INSERT INTO contact_info (address, phone_number, email, facebook_url, instagram_url, twitter_url, whatsapp_number, Maps_embed, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("ssssssssi", $address, $phone_number, $email, $facebook_url, $instagram_url, $twitter_url, $whatsapp_number, $Maps_embed, $updated_by);
    }

    if ($stmt->execute()) {
        $_SESSION['success_message'] = "Informasi kontak berhasil diperbarui!";
        // Ambil ulang data setelah update
        $result = $conn->query("SELECT * FROM contact_info ORDER BY id ASC LIMIT 1");
        if ($result && $result->num_rows > 0) {
            $contact_info = $result->fetch_assoc();
        }
    } else {
        $_SESSION['error_message'] = "Gagal memperbarui informasi kontak: " . $stmt->error;
    }
    $stmt->close();
    header("Location: manage_contact_info.php"); // Redirect to prevent form resubmission
    exit();
}
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Manajemen Informasi Kontak</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <style>
        body { background-color: #343a40; color: #f8f9fa; }
        .card { background-color: #495057; border: none; }
        label { font-weight: bold; }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h1>Manajemen Informasi Kontak</h1>
        <a href="dashboard.php" class="btn btn-secondary mb-4">← Kembali ke Dashboard</a>

        <?php if (isset($_SESSION['success_message'])): ?>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <?= $_SESSION['success_message'] ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <?php unset($_SESSION['success_message']); ?>
        <?php endif; ?>

        <?php if (isset($_SESSION['error_message'])): ?>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <?= $_SESSION['error_message'] ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <?php unset($_SESSION['error_message']); ?>
        <?php endif; ?>

        <div class="card p-4">
            <form action="manage_contact_info.php" method="POST">
                <div class="mb-3">
                    <label for="address" class="form-label">Alamat Lengkap</label>
                    <textarea class="form-control" id="address" name="address" rows="3"><?= htmlspecialchars($contact_info['address'] ?? '') ?></textarea>
                </div>
                <div class="mb-3">
                    <label for="phone_number" class="form-label">Nomor Telepon</label>
                    <input type="text" class="form-control" id="phone_number" name="phone_number" value="<?= htmlspecialchars($contact_info['phone_number'] ?? '') ?>">
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Email Kontak</label>
                    <input type="email" class="form-control" id="email" name="email" value="<?= htmlspecialchars($contact_info['email'] ?? '') ?>">
                </div>
                <div class="mb-3">
                    <label for="whatsapp_number" class="form-label">Nomor WhatsApp (format internasional, misal: 62812xxxx)</label>
                    <input type="text" class="form-control" id="whatsapp_number" name="whatsapp_number" value="<?= htmlspecialchars($contact_info['whatsapp_number'] ?? '') ?>" placeholder="misal: 6281234567890">
                    <small class="form-text text-muted">Akan digunakan untuk tautan "Chat via WhatsApp".</small>
                </div>
                <hr class="my-4 border-light">
                <h5 class="mb-3">Tautan Media Sosial (Opsional)</h5>
                <div class="mb-3">
                    <label for="facebook_url" class="form-label">URL Facebook</label>
                    <input type="url" class="form-control" id="facebook_url" name="facebook_url" value="<?= htmlspecialchars($contact_info['facebook_url'] ?? '') ?>" placeholder="https://facebook.com/namapage">
                </div>
                <div class="mb-3">
                    <label for="instagram_url" class="form-label">URL Instagram</label>
                    <input type="url" class="form-control" id="instagram_url" name="instagram_url" value="<?= htmlspecialchars($contact_info['instagram_url'] ?? '') ?>" placeholder="https://instagram.com/namaakun">
                </div>
                <div class="mb-3">
                    <label for="twitter_url" class="form-label">URL Twitter/X</label>
                    <input type="url" class="form-control" id="twitter_url" name="twitter_url" value="<?= htmlspecialchars($contact_info['twitter_url'] ?? '') ?>" placeholder="https://twitter.com/namaakun">
                </div>
                <hr class="my-4 border-light">
                <div class="mb-3">
                    <label for="Maps_embed" class="form-label">Kode Embed Google Maps</label>
                    <textarea class="form-control" id="Maps_embed" name="Maps_embed" rows="5" placeholder="Salin kode iframe dari Google Maps di sini"><?= htmlspecialchars($contact_info['Maps_embed'] ?? '') ?></textarea>
                    <small class="form-text text-muted">Kunjungi Google Maps, cari lokasi Anda, klik 'Share', lalu 'Embed a map', dan salin kode HTML iframe.</small>
                </div>

                <button type="submit" class="btn btn-primary">Simpan Perubahan</button>
            </form>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>