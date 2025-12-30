<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
include('includes/header.php');
include('../backend/dbconnect.php');

function log_admin_activity($conn, $admin_id, $activity, $activity_type) {
    $stmt = $conn->prepare("INSERT INTO admin_logs (admin_id, activity_time, activity, activity_type) VALUES (?, NOW(), ?, ?)");
    $stmt->bind_param("iss", $admin_id, $activity, $activity_type);
    $stmt->execute();
    $stmt->close();
}

if (isset($_GET['delete_id'])) {
    $delete_id = (int)$_GET['delete_id'];
    if ($delete_id !== (int)$_SESSION['user_id']) {
        
        $stmt = $conn->prepare("SELECT username FROM users WHERE id = ? AND role = 'user'");
        $stmt->bind_param("i", $delete_id);
        $stmt->execute();
        $stmt->bind_result($deleted_username);
        $stmt->fetch();
        $stmt->close();

        $stmt = $conn->prepare("DELETE FROM users WHERE id = ? AND role = 'user'");
        $stmt->bind_param("i", $delete_id);
        $stmt->execute();
        $stmt->close();

        
        log_admin_activity($conn, $_SESSION['user_id'], "Menghapus user: $deleted_username (ID: $delete_id)", "delete_user");

        header("Location: manage_user.php");
        exit();
    } else {
        $error = "Tidak bisa menghapus akun admin sendiri.";
    }
}


$users = $conn->query("SELECT * FROM users WHERE role = 'user' ORDER BY created_at DESC");

$total_user = $conn->query("SELECT COUNT(*) as total FROM users WHERE role = 'user'")->fetch_assoc()['total'];
?>

<div class="container mt-5">
    
    <a href="dashboard.php" class="btn btn-secondary mb-4 mt-4">Kembali ke Dashboard</a>
    <h2>Kelola User</h2>

    <?php if (isset($error)): ?>
        <div class="alert alert-danger"><?= htmlspecialchars($error) ?></div>
    <?php endif; ?>

    <p>Total User Terdaftar: <strong><?= $total_user ?></strong></p>

    <table class="table table-bordered  text-white">
        <thead>
            <tr class="bg-success">
                <th>ID</th><th>Username</th><th>Email</th><th>Tanggal Daftar</th><th>Aksi</th>
            </tr>
        </thead>
        <tbody>
            <?php while ($row = $users->fetch_assoc()): ?>
                <tr>
                    <td><?= $row['id'] ?></td>
                    <td><?= htmlspecialchars($row['username']) ?></td>
                    <td><?= htmlspecialchars($row['email']) ?></td>
                    <td><?= $row['created_at'] ?></td>
                    <td>
                        <a href="manage_user.php?delete_id=<?= $row['id'] ?>" onclick="return confirm('Hapus user ini?')" class="btn btn-danger btn-sm">Hapus</a>
                    </td>
                </tr>
            <?php endwhile; ?>
        </tbody>
    </table>

</div>
