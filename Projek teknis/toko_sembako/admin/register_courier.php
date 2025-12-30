<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
require_once '../backend/dbconnect.php';

$message = '';
$message_type = '';

if (isset($_POST['register_courier'])) {
    $username = trim($_POST['username']);
    $password = trim($_POST['password']);
    $fullName = trim($_POST['full_name']);

    if (strlen($password) !== 5) {
        $message = "Password untuk kurir harus terdiri dari 5 karakter.";
        $message_type = 'danger';
    } else {
        // Validasi: Cek apakah username sudah ada
        $check_stmt = $conn->prepare("SELECT id FROM users WHERE username = ?");
        $check_stmt->bind_param("s", $username);
        $check_stmt->execute();
        $check_stmt->store_result();
        if ($check_stmt->num_rows > 0) {
            $message = "Gagal membuat akun. Username sudah ada.";
            $message_type = 'danger';
        } else {
            $hashed_password = password_hash($password, PASSWORD_DEFAULT);
            $role = 'courier';

            $stmt = $conn->prepare("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
            $stmt->bind_param("sss", $username, $hashed_password, $role);
            
            if ($stmt->execute()) {
                $new_user_id = $stmt->insert_id;
                // Masukkan juga ke user_profile
                $profile_stmt = $conn->prepare("INSERT INTO user_profile (user_id, full_name) VALUES (?, ?)");
                $profile_stmt->bind_param("is", $new_user_id, $fullName);
                
                if ($profile_stmt->execute()) {
                    $message = "Akun kurir untuk " . htmlspecialchars($username) . " berhasil dibuat.";
                    $message_type = 'success';
                } else {
                    // Jika gagal menyimpan nama lengkap, berikan peringatan
                    $message = "Akun kurir berhasil dibuat, tetapi gagal menyimpan nama lengkap. Silakan edit profil kurir.";
                    $message_type = 'warning';
                }
                $profile_stmt->close();
            } else {
                $message = "Gagal membuat akun. Terjadi kesalahan database.";
                $message_type = 'danger';
            }
        }
        $check_stmt->close();
        $stmt->close();
    }
}

// --- Ambil data kurir dari database (BAGIAN INI TETAP ADA) ---
$couriers = [];
$sql_couriers = "SELECT u.id, u.username, up.full_name 
                 FROM users u
                 LEFT JOIN user_profile up ON u.id = up.user_id
                 WHERE u.role = 'courier'
                 ORDER BY up.full_name ASC"; 
$result_couriers = $conn->query($sql_couriers);

if ($result_couriers->num_rows > 0) {
    while ($row = $result_couriers->fetch_assoc()) {
        $couriers[] = $row;
    }
}
$conn->close(); // Tutup koneksi database setelah semua operasi selesai
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Registrasi Kurir</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css">
</head>
<body class="bg-dark text-light">
    <div class="container mt-5">
        <h1>Registrasi Akun Kurir</h1>
        <a href="dashboard.php" class="btn btn-secondary mb-4">← Kembali ke Dashboard</a>

        <?php if ($message): ?>
            <div class="alert alert-<?= $message_type ?> alert-dismissible fade show" role="alert">
                <?= $message ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        <?php endif; ?>

        <form method="POST" class="card bg-secondary p-4">
            <div class="mb-3">
                <label for="full_name" class="form-label">Nama Lengkap Kurir</label>
                <input type="text" name="full_name" id="full_name" class="form-control" required>
            </div>
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input type="text" name="username" id="username" class="form-control" required>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password (Wajib 5 Karakter)</label>
                <input type="text" name="password" id="password" class="form-control" required maxlength="5" minlength="5">
            </div>
            <button type="submit" name="register_courier" class="btn btn-primary">Daftarkan Kurir</button>
        </form>


        <h2 class="mt-5 mb-3 text-primary">Daftar Kurir Terdaftar</h2>
        <?php if (empty($couriers)): ?>
            <div class="alert alert-info">Belum ada kurir yang terdaftar.</div>
        <?php else: ?>
            <div class="table-responsive">
                <table class="table table-dark table-striped table-hover align-middle">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Nama Lengkap</th>
                            <th scope="col">Username</th>
                            <th scope="col">Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php $no = 1; ?>
                        <?php foreach ($couriers as $courier): ?>
                            <tr>
                                <th scope="row"><?= $no++ ?></th>
                                <td><?= htmlspecialchars($courier['full_name']) ?></td>
                                <td><?= htmlspecialchars($courier['username']) ?></td>
                                <td>
                                    <button class="btn btn-sm btn-danger ms-2" disabled><i class="bi bi-trash"></i> Hapus</button>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        <?php endif; ?>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>