<?php
if (!isset($conn)) {
    if (session_status() === PHP_SESSION_NONE) {
        session_start();
    }
    require_once 'backend/dbconnect.php';
}

$selected_category = isset($_GET['category']) ? htmlspecialchars($_GET['category']) : '';

$sql = "SELECT id, title, description, image_name, category FROM gallery_images";
$params = [];
$types = '';

if (!empty($selected_category) && $selected_category !== 'Semua') {
    $sql .= " WHERE category = ?";
    $params[] = $selected_category;
    $types .= 's';
}
$sql .= " ORDER BY uploaded_at DESC";

$stmt = $conn->prepare($sql);
if ($types) {
    $stmt->bind_param($types, ...$params);
}
$stmt->execute();
$gallery_images = $stmt->get_result();
$stmt->close();

$all_categories_query = $conn->query("SELECT DISTINCT category FROM gallery_images WHERE category IS NOT NULL AND category != '' ORDER BY category ASC");
$all_categories = ['Semua'];
while ($row = $all_categories_query->fetch_assoc()) {
    $all_categories[] = $row['category'];
}

function slugify($text) {
    $text = preg_replace('~[^\pL\d]+~u', '-', $text);
    $text = iconv('utf-8', 'us-ascii//TRANSLIT', $text);
    $text = preg_replace('~[^-\w]+~', '', $text);
    $text = trim($text, '-');
    $text = preg_replace('~-+~', '-', $text);
    $text = strtolower($text);

    if (empty($text)) {
        return 'n-a';
    }

    return $text;
}

?>

<link href="https://cdnjs.cloudflare.com/ajax/libs/lightbox2/2.11.3/css/lightbox.min.css" rel="stylesheet" />
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<div class="container py-5">
    <h1 class="mb-4 text-center text-primary fw-bold">Galeri Foto Kami</h1>
    <p class="text-center text-muted mb-5">Jelajahi berbagai momen dan proses di balik layanan toko sembako kami.</p>

    <?php if (count($all_categories) > 1): ?>
        <div class="d-flex flex-wrap justify-content-center gap-2 mb-5">
            <?php foreach ($all_categories as $cat):
                $category_slug = slugify($cat);
                $current_category_slug = slugify($selected_category);
            ?>
                <a href="<?= isset($base_url) ? $base_url . 'galeri/' . $category_slug : '/toko_sembako/galeri/' . $category_slug ?>"
                   class="btn <?= $current_category_slug === $category_slug || (empty($selected_category) && $cat === 'Semua') ? 'btn-primary' : 'btn-outline-primary' ?> rounded-pill px-4">
                    <?= htmlspecialchars($cat) ?>
                </a>
            <?php endforeach; ?>
        </div>
    <?php endif; ?>
    
    <?php if ($gallery_images->num_rows > 0): ?>
        <div class="row row-cols-2 row-cols-md-2 row-cols-lg-3 g-4 "> <?php while($image = $gallery_images->fetch_assoc()): ?>
            <div class="col">
                <div class="card h-100 shadow-sm border-0 gallery-item">
                    <a href="<?= isset($base_url) ? $base_url : '/toko_sembako/' ?>uploads/gallery/<?= htmlspecialchars($image['image_name']) ?>"
                        data-lightbox="gallery"
                        data-title="<?= htmlspecialchars($image['title']) ?> - <?= htmlspecialchars($image['description']) ?>">
                        <img src="<?= isset($base_url) ? $base_url : '/toko_sembako/' ?>uploads/gallery/<?= htmlspecialchars($image['image_name']) ?>"
                             class="card-img-top gallery-img"
                             alt="<?= htmlspecialchars($image['title']) ?>">
                    </a>
                    <div class="card-body">
                        <h5 class="card-title text-dark mb-1"><?= htmlspecialchars($image['title']) ?></h5>
                        <p class="card-text text-muted small gallery-description">
                            <?= substr(htmlspecialchars($image['description']), 0, 100) ?><?= strlen(htmlspecialchars($image['description'])) > 100 ? '...' : '' ?>
                        </p>
                        <?php if ($image['category']): ?>
                            <span class="badge bg-secondary text-white rounded-pill mt-2"><?= htmlspecialchars($image['category']) ?></span>
                        <?php endif; ?>
                    </div>
                </div>
            </div>
            <?php endwhile; ?>
        </div>
    <?php else: ?>
        <div class="alert alert-info text-center mt-5" role="alert">
            Tidak ada gambar di galeri <?= !empty($selected_category) && $selected_category !== 'Semua' ? "untuk kategori '" . $selected_category . "'" : '' ?>.
        </div>
    <?php endif; ?>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/lightbox2/2.11.3/js/lightbox.min.js"></script>

<style>
    body { background-color: #f8f9fa; }
    .container { max-width: 1140px; }

    .gallery-item {
        border-radius: 1rem;
        overflow: hidden;
        transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
        cursor: pointer;
    }
    .gallery-item:hover {
        transform: translateY(-8px);
        box-shadow: 0 1rem 2rem rgba(0, 0, 0, 0.15) !important;
    }

    .gallery-img {
        width: 100%;
        height: 250px;
        object-fit: cover;
        transition: transform 0.3s ease;
    }
    .gallery-item:hover .gallery-img {
        transform: scale(1.05);
    }

    .gallery-item .card-body {
        padding: 1.25rem;
    }
    .gallery-item .card-title {
        min-height: 40px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        font-weight: 600;
    }
    .gallery-description {
        min-height: 45px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
    }

    .lightbox .lb-data .lb-details .lb-caption {
        font-size: 1.2rem;
        font-weight: bold;
    }

    @media (min-width: 768px) {
        .gallery-grid {
            grid-template-columns: repeat(2, 1fr);
        }
    }

    @media (min-width: 992px) {
        .gallery-grid {
            grid-template-columns: repeat(3, 1fr);
        }
        .container {
            max-width: 1200px;
        }
    }

    @media (min-width: 1400px) {
        .container {
            max-width: 1400px;
        }
    }
</style>