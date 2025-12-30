<?php
if (!isset($conn)) {
    if (session_status() === PHP_SESSION_NONE) {
        session_start();
    }
    require_once 'backend/dbconnect.php';
}

$slug = $_GET['slug'] ?? '';

if (empty($slug)) {
    $redirect_url = isset($base_url) ? $base_url . "404" : "/toko_sembako/404";
    header("Location: " . $redirect_url);
    exit();
}

$stmt = $conn->prepare("SELECT p.title, p.content, p.image, p.created_at, u.username as author_name
                         FROM posts p
                         LEFT JOIN users u ON p.author_id = u.id
                         WHERE p.slug = ?");
$stmt->bind_param("s", $slug);
$stmt->execute();
$result = $stmt->get_result();
$berita = $result->fetch_assoc();
$stmt->close();

if (!$berita) {
    $redirect_url = isset($base_url) ? $base_url . "404" : "/toko_sembako/404";
    header("Location: " . $redirect_url);
    exit();
}
?>

<div class="container py-5">
    <h1 class="mb-4 text-primary fw-bold"><?= htmlspecialchars($berita['title']) ?></h1>
    <p class="text-muted small mb-4">
        <i class="bi bi-pencil-square me-1"></i>Oleh <?= htmlspecialchars($berita['author_name'] ?? 'Admin') ?>
        <i class="bi bi-calendar-event ms-3 me-1"></i><?= date('d M Y', strtotime($berita['created_at'])) ?>
    </p>

    <?php if ($berita['image']): ?>
        <div class="text-center mb-5 article-image-container"> <img src="<?= isset($base_url) ? $base_url : '/toko_sembako/' ?>uploads/posts/<?= htmlspecialchars($berita['image']) ?>"
                     class="img-fluid rounded shadow-sm article-main-image"
                     alt="<?= htmlspecialchars($berita['title']) ?>">
        </div>
    <?php else: ?>
        <div class="text-center mb-5 article-image-container">
            <div class="no-image-placeholder d-flex align-items-center justify-content-center bg-light text-muted rounded shadow-sm">
                <i class="bi bi-image fs-1 me-2"></i>
                <span class="fs-4">Tidak Ada Gambar</span>
            </div>
        </div>
    <?php endif; ?>

    <div class="artikel-content mb-5 bg-white p-4 rounded shadow-sm">
        <?= nl2br(htmlspecialchars($berita['content'])) ?>
    </div>

    <a href="<?= isset($base_url) ? $base_url . 'berita' : '/toko_sembako/berita' ?>" class="btn btn-secondary">
        <i class="bi bi-arrow-left me-1"></i> Kembali ke Berita
    </a>
</div>

<style>
    .artikel-content {
        line-height: 1.8;
        font-size: 1.1rem;
        color: #333;
    }
    .artikel-content p {
        margin-bottom: 1em;
    }
    .artikel-content img {
        max-width: 100%;
        height: auto;
        display: block;
        margin: 1rem auto;
        border-radius: 8px;
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1);
    }

    .article-image-container {
    }

    .article-main-image {
        max-width: 100%;
        height: auto;
        max-height: 450px;
        object-fit: contain;
        display: block;
        margin: 0 auto;
        
        border-radius: 1rem !important;
        box-shadow: 0 1rem 2rem rgba(0, 0, 0, 0.15) !important;
        
        max-width: 800px;
    }

    .no-image-placeholder {
        width: 100%;
        max-width: 800px;
        height: 250px;
        margin: 0 auto;
        background-color: #f0f0f0;
        border: 2px dashed #ccc;
        color: #888;
        font-weight: bold;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 1rem;
    }
    .no-image-placeholder i {
        font-size: 3rem;
    }

    .btn-secondary {
        transition: background-color 0.2s ease, transform 0.2s ease;
    }
    .btn-secondary:hover {
        transform: translateY(-2px);
    }

    @media (max-width: 767.98px) {
        .article-main-image, .no-image-placeholder {
            max-width: 100%;
            height: auto;
            max-height: 300px;
        }
        .no-image-placeholder {
            height: 200px;
        }
    }
</style>