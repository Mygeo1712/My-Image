<?php
if (!isset($conn)) {
    require_once 'backend/dbconnect.php';
}

$featured_post_query = $conn->query("SELECT p.id, p.title, p.slug, p.content, p.image, p.created_at, u.username as author_name
                                     FROM posts p
                                     LEFT JOIN users u ON p.author_id = u.id
                                     ORDER BY p.created_at DESC LIMIT 1");
$featured_post = $featured_post_query->fetch_assoc();
$featured_post_id = $featured_post['id'] ?? null;

$other_posts_query = "SELECT p.id, p.title, p.slug, p.content, p.image, p.created_at, u.username as author_name
                      FROM posts p
                      LEFT JOIN users u ON p.author_id = u.id";
if ($featured_post_id) {
    $other_posts_query .= " WHERE p.id != " . $featured_post_id;
}
$other_posts_query .= " ORDER BY p.created_at DESC";
$other_posts = $conn->query($other_posts_query);
?>

<section class="berita-section py-5 bg-light">
    <div class="container">
        <h1 class="mb-5 text-center text-dark fw-bold">Berita & Artikel Terbaru</h1>

        <?php if ($featured_post): ?>
            <div class="row justify-content-center mb-5">
                <div class="col-md-10">
                    <div class="card featured-card shadow-lg border-0 bg-white">
                        <div class="row g-0">
                            <div class="col-md-5">
                                <?php if($featured_post['image']): ?>
                                    <img src="uploads/posts/<?= htmlspecialchars($featured_post['image']) ?>" class="img-fluid rounded-start featured-img" alt="<?= htmlspecialchars($featured_post['title']) ?>">
                                <?php else: ?>
                                    <div class="featured-img-placeholder d-flex align-items-center justify-content-center bg-secondary text-white-50">
                                        <i class="bi bi-image fs-1"></i>
                                    </div>
                                <?php endif; ?>
                            </div>
                            <div class="col-md-7">
                                <div class="card-body d-flex flex-column h-100 p-4 p-md-5">
                                    <h2 class="card-title fw-bold text-primary mb-3">
                                        <a href="<?= $base_url ?>konten-berita/<?= $featured_post['slug'] ?>" class="text-decoration-none text-primary hover-dark">
                                            <?= htmlspecialchars($featured_post['title']) ?>
                                        </a>
                                    </h2>
                                    <p class="card-text text-muted small mb-2">
                                        <i class="bi bi-pencil-square me-1"></i>Oleh <?= htmlspecialchars($featured_post['author_name'] ?? 'Admin') ?>
                                        <i class="bi bi-calendar-event ms-3 me-1"></i><?= date('d M Y', strtotime($featured_post['created_at'])) ?>
                                    </p>
                                    <p class="card-text flex-grow-1 text-dark article-excerpt">
                                        <?= substr(strip_tags($featured_post['content']), 0, 250) ?><?= strlen(strip_tags($featured_post['content'])) > 250 ? '...' : '' ?>
                                    </p>
                                    <a href="<?= $base_url ?>konten-berita/<?= $featured_post['slug'] ?>" class="btn btn-primary mt-auto align-self-start read-more-btn">Baca Selengkapnya <i class="bi bi-arrow-right"></i></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <hr class="my-5 border-2 opacity-25"> <?php endif; ?>

        <?php if ($other_posts->num_rows > 0): ?>
            <h2 class="mb-4 text-center text-dark">Artikel Lainnya</h2>
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                <?php while($post = $other_posts->fetch_assoc()): ?>
                <div class="col">
                    <div class="card h-100 shadow-sm border-0 news-grid-card bg-white">
                        <?php if($post['image']): ?>
                            <img src="uploads/posts/<?= htmlspecialchars($post['image']) ?>" class="card-img-top news-grid-img" alt="<?= htmlspecialchars($post['title']) ?>">
                        <?php else: ?>
                            <div class="news-grid-img-placeholder d-flex align-items-center justify-content-center bg-secondary text-white-50">
                                <i class="bi bi-image fs-3"></i>
                            </div>
                        <?php endif; ?>
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title fw-bold mb-2">
                                <a href="<?= $base_url ?>konten-berita/<?= $post['slug'] ?>" class="text-decoration-none text-dark hover-primary">
                                    <?= htmlspecialchars($post['title']) ?>
                                </a>
                            </h5>
                            <p class="card-text text-muted small mb-2">
                                <i class="bi bi-calendar-event me-1"></i><?= date('d M Y', strtotime($post['created_at'])) ?>
                            </p>
                            <p class="card-text flex-grow-1 text-secondary article-excerpt-grid">
                                <?= substr(strip_tags($post['content']), 0, 120) ?><?= strlen(strip_tags($post['content'])) > 120 ? '...' : '' ?>
                            </p>
                            <a href="<?= $base_url ?>konten-berita/<?= $post['slug'] ?>" class="btn btn-outline-primary btn-sm mt-auto align-self-start">Baca Selengkapnya</a>
                        </div>
                        <div class="card-footer bg-light border-top">
                            <small class="text-secondary">Oleh <?= htmlspecialchars($post['author_name'] ?? 'Admin') ?></small>
                        </div>
                    </div>
                </div>
                <?php endwhile; ?>
            </div>
        <?php elseif (!$featured_post): ?>
            <div class="col-12">
                <p class="text-center fs-5 text-muted">Belum ada berita atau artikel yang tersedia.</p>
            </div>
        <?php endif; ?>
    </div>
</section>

<style>
    .berita-section {
        background: linear-gradient(to bottom, #f8f9fa, #e9ecef);
    }

    .featured-card {
        border-radius: 1rem;
        overflow: hidden;
        transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
    }
    .featured-card:hover {
        transform: translateY(-8px);
        box-shadow: 0 1.5rem 3rem rgba(0, 0, 0, 0.2) !important;
    }
    .featured-img {
        height: 100%;
        width: 100%;
        object-fit: cover;
        border-radius: 1rem 0 0 1rem;
    }
    .featured-img-placeholder {
        height: 100%;
        min-height: 250px;
        background-color: #dee2e6;
        color: #6c757d;
        border-radius: 1rem 0 0 1rem;
    }
    .featured-card .card-body a.hover-dark:hover {
        color: #212529 !important;
    }
    .featured-card .read-more-btn {
        padding: 0.75rem 1.5rem;
        font-weight: bold;
    }

    .article-excerpt {
        display: -webkit-box;
        -webkit-line-clamp: 5;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        font-size: 1.05rem;
        line-height: 1.6;
    }
    .article-excerpt-grid {
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        font-size: 0.95rem;
        line-height: 1.5;
    }

    .news-grid-card {
        border-radius: 0.75rem;
        transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
    }
    .news-grid-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.1) !important;
    }
    .news-grid-img {
        height: 220px;
        object-fit: cover;
        border-radius: 0.75rem 0.75rem 0 0;
    }
    .news-grid-img-placeholder {
        height: 220px;
        background-color: #e9ecef;
        color: #adb5bd;
        border-radius: 0.75rem 0.75rem 0 0;
    }
    .news-grid-card .card-title a {
        min-height: 52px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }
    .news-grid-card .card-title a.hover-primary:hover {
        color: #007bff !important;
    }

    @media (max-width: 767.98px) {
        .featured-card .row.g-0 {
            flex-direction: column;
        }
        .featured-img, .featured-img-placeholder {
            border-radius: 1rem 1rem 0 0;
            height: 250px;
            min-height: 250px;
        }
        .featured-card .card-body {
            padding: 1.5rem;
        }
        .featured-card .card-title {
            font-size: 1.5rem;
        }
        .article-excerpt {
            -webkit-line-clamp: 4;
        }
    }
</style>