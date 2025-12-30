<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
require_once '../backend/dbconnect.php';

// Ambil semua post
$posts = $conn->query("SELECT p.id, p.title, p.created_at, u.username as author_name 
                       FROM posts p 
                       LEFT JOIN users u ON p.author_id = u.id 
                       ORDER BY p.created_at DESC");
?>
<!DOCTYPE html>
<html lang="id">
<head>  
    <meta charset="UTF-8">
    <title>Manajemen Artikel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
</head>
<body class="bg-dark text-light">
    <div class="container mt-5">
        <h1>Manajemen Artikel</h1>
        <a href="dashboard.php" class="btn btn-secondary mb-4">← Kembali ke Dashboard</a>
        <a href="action/post_editor.php" class="btn btn-success mb-4">Tambah Artikel Baru</a>

        <table class="table table-dark table-striped">
            <thead>
                <tr>
                    <th>Judul</th>
                    <th>Penulis</th>
                    <th>Tanggal Dibuat</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <?php while($post = $posts->fetch_assoc()): ?>
                <tr>
                    <td><?= htmlspecialchars($post['title']) ?></td>
                    <td><?= htmlspecialchars($post['author_name'] ?? 'N/A') ?></td>
                    <td><?= date('d M Y', strtotime($post['created_at'])) ?></td>
                    <td>
                        <a href="action/post_editor.php?id=<?= $post['id'] ?>" class="btn btn-sm btn-primary">Edit</a>
                        <a href="action/delete_post.php?id=<?= $post['id'] ?>" class="btn btn-sm btn-danger" onclick="return confirm('Anda yakin ingin menghapus artikel ini?')">Hapus</a>
                    </td>
                </tr>
                <?php endwhile; ?>
            </tbody>
        </table>
    </div>
</body>
</html>
