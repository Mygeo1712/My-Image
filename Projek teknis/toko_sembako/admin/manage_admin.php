<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
include('includes/header.php');
include('../backend/dbconnect.php');

$error = '';
$success = '';
$input_username = '';
$input_email = '';

function log_admin_activity($conn, $admin_id, $activity, $activity_type)
{
    $stmt = $conn->prepare("INSERT INTO admin_logs (admin_id, activity_time, activity, activity_type) VALUES (?, NOW(), ?, ?)");
    $stmt->bind_param("iss", $admin_id, $activity, $activity_type);
    $stmt->execute();
    $stmt->close();
}

// Handle create admin baru
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $input_username = trim($_POST['username']);
    $input_email = trim($_POST['email']);
    $password = $_POST['password'];
    $password_confirm = $_POST['password_confirm'];

    if ($password !== $password_confirm) {
        $error = "Password konfirmasi tidak cocok.";
    } elseif (empty($input_username) || empty($input_email) || empty($password)) {
        $error = "Semua field harus diisi.";
    } elseif (strlen($password) > 4) {
        $error = "Password admin maksimal 4 karakter.";
    } else {
        $stmt = $conn->prepare("SELECT id FROM users WHERE username = ?");
        $stmt->bind_param("s", $input_username);
        $stmt->execute();
        $stmt->store_result();
        if ($stmt->num_rows > 0) {
            $error = "Username sudah digunakan.";
        } else {
            $stmt->close();
            $password_hash = password_hash($password, PASSWORD_DEFAULT);

            $stmt = $conn->prepare("INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'admin')");
            $stmt->bind_param("sss", $input_username, $password_hash, $input_email);
            if ($stmt->execute()) {
                // Log aktivitas tambah admin sebelum variabel dikosongkan
                log_admin_activity($conn, $_SESSION['user_id'], "Menambah admin baru: $input_username", "add_admin");
                $success = "Admin baru berhasil dibuat.";
                $input_username = '';
                $input_email = '';
            } else {
                $error = "Gagal membuat admin baru.";
            }
        }
        $stmt->close();
    }
}

// Ambil data admin dan user
$admins = $conn->query("SELECT * FROM users WHERE role = 'admin' ORDER BY created_at DESC");

$total_admin = $conn->query("SELECT COUNT(*) as total FROM users WHERE role = 'admin'")->fetch_assoc()['total'];

$admin_log = $conn->query("SELECT * FROM users WHERE role = 'admin' ORDER BY created_at DESC");
?>




<div class="container mt-5">
    <a href="dashboard.php" class="btn btn-secondary mb-4 mt-4">Kembali ke Dashboard</a>
    <h2>Kelola Admin Account</h2>

    <?php if ($error): ?>
        <div class="alert alert-danger"><?= htmlspecialchars($error) ?></div>
    <?php endif; ?>
    <?php if ($success): ?>
        <div class="alert alert-success"><?= htmlspecialchars($success) ?></div>
    <?php endif; ?>

    <h4>Buat Admin Baru</h4>

    <form method="POST" action="manage_admin.php" class="mb-4" style="max-width: 400px;">
        <div class="mb-3">
            <label>Username</label>
            <input type="text" name="username" class="form-control" required
                value="<?= htmlspecialchars($input_username) ?>">
        </div>
        <div class="mb-3">
            <label>Email</label>
            <input type="email" name="email" class="form-control" required
                value="<?= htmlspecialchars($input_email) ?>">
        </div>

        <div class="mb-3">
            <label for="password">Password</label>
            <input type="password" name="password" id="password" class="form-control" required>
        </div>

        <div class="form-check text-start mb-3">
            <input type="checkbox" class="form-check-input" id="showPassword" onclick="togglePassword()">
            <label for="showPassword" class="form-check-label checkbox-label">Show Password</label>
        </div>

        <div class="mb-3">
            <label>Konfirmasi Password</label>
            <input type="password" name="password_confirm" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary">Buat Admin</button>
    </form>


    <h4>Daftar Admin</h4>
    <p>Total Admin Terdaftar: <strong><?= $total_admin ?></strong></p>

    <table class="table table-bordered text-white mb-5">
        <thead>
            <tr class="bg-danger">
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Tanggal Daftar</th>
            </tr>
        </thead>
        <tbody>
            <?php while ($admin = $admins->fetch_assoc()): ?>
                <tr>
                    <td><?= $admin['id'] ?></td>
                    <td><?= htmlspecialchars($admin['username']) ?></td>
                    <td><?= htmlspecialchars($admin['email']) ?></td>
                    <td><?= $admin['created_at'] ?></td>
                </tr>
            <?php endwhile; ?>
        </tbody>
    </table>
    <h4>Riwayat Aktivitas Admin</h4>
    <table class="table table-bordered text-white">
        <thead>
            <tr class="bg-info">
                <th>ID Log</th>
                <th>Admin</th>
                <th>Waktu</th>
                <th>Login / Logout</th>
                <th>Aktivitas Lain</th>
            </tr>
        </thead>
        <tbody>
            <?php
            $log_query = "
            SELECT al.*, u.username 
            FROM admin_logs al 
            JOIN users u ON al.admin_id = u.id 
            ORDER BY al.activity_time DESC
        ";
            $logs = $conn->query($log_query);
            while ($log = $logs->fetch_assoc()):
                $activity_type = $log['activity_type'];
                $activity_detail = nl2br(htmlspecialchars($log['activity']));

                // Login/Logout hanya ditampilkan di kolom khusus
                $login_logout = in_array($activity_type, ['login', 'logout']) ? $activity_detail : '';
                $other_activity = !in_array($activity_type, ['login', 'logout']) ? $activity_detail : '';
            ?>
                <tr>
                    <td><?= $log['id'] ?></td>
                    <td><?= htmlspecialchars($log['username']) ?></td>
                    <td><?= $log['activity_time'] ?></td>
                    <td><?= $login_logout ?></td>
                    <td><?= $other_activity ?></td>
                </tr>
            <?php endwhile; ?>
        </tbody>
    </table>



</div>


<script>
    function togglePassword() {
        const pwField = document.getElementById("password");
        pwField.type = pwField.type === "password" ? "text" : "password";
    }
</script>