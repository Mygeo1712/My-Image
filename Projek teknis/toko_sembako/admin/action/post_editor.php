<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    exit('Akses ditolak');
}
require_once '../../backend/dbconnect.php';

$post = ['id' => '', 'title' => '', 'content' => '', 'image' => ''];
$form_title = "Tambah Artikel Baru";

if (isset($_GET['id'])) {
    $stmt = $conn->prepare("SELECT * FROM posts WHERE id = ?");
    $stmt->bind_param("i", $_GET['id']);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows > 0) {
        $post = $result->fetch_assoc();
        $form_title = "Edit Artikel";
    }
}
?>
<!DOCTYPE html>
<html lang="id">

<head>
    <meta charset="UTF-8">
    <title><?= $form_title ?></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
</head>

<body class="bg-dark text-light">
    <div class="container mt-5">
        <h1><?= $form_title ?></h1>
        <a href="../manage_posts.php" class="btn btn-secondary mb-4">Batal</a>

        <form action="save_post.php" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="id" value="<?= $post['id'] ?>">
            <div class="mb-3">
                <label for="title" class="form-label">Judul Artikel</label>
                <input type="text" class="form-control" id="title" name="title" value="<?= htmlspecialchars($post['title']) ?>" required>
            </div>
            <div class="mb-3">
                <label for="content" class="form-label">Konten</label>
                <textarea class="form-control" id="content" name="content" rows="10" required><?= htmlspecialchars($post['content']) ?></textarea>
            </div>
            <div class="mb-3">
                <label for="image" class="form-label">Gambar Utama </label>
                <input type="file" class="form-control" id="image" name="image" accept="image/*">
                <?php if ($post['image']): ?>
                    <p class="mt-2">Gambar saat ini: <img src="../../uploads/posts/<?= $post['image'] ?>" height="100"></p>
                <?php endif; ?>

            </div>
            <div class="d-flex justify-content-end py-2">
                <button type="submit" class="btn btn-primary ">Simpan Artikel</button>
                
            </div>
        </form>
    </div>
</body>

</html>