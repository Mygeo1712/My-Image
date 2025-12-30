<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

$sql_flash_sale = "
    SELECT p.id, p.name, p.slug, p.price, p.image, SUM(o.quantity) AS total_ordered
    FROM products p
    JOIN orders o ON p.id = o.product_id
    GROUP BY p.id
    ORDER BY total_ordered DESC
    LIMIT 7
";

$result_flash_sale = $conn->query($sql_flash_sale);
if (!$result_flash_sale) {
    die("Query flash sale failed: " . $conn->error);
}
?>

<div class="flash-sale-section container-fluid bg-light py-5">
    <div class="container">
        <h2 class="section-title text-center mb-4">🔥 Flash Sale Terlaris</h2>
        
        <div class="swiper flash-sale-swiper">
            <div class="swiper-wrapper">
                <?php
                $rank = 1;
                if ($result_flash_sale->num_rows > 0):
                    while ($row = $result_flash_sale->fetch_assoc()):
                ?>
                <div class="swiper-slide">
                    <a href="/toko_sembako/detail/<?= htmlspecialchars($row['slug']) ?>" class="text-decoration-none">
                        <div class="flash-card">
                            <span class="rank-badge">#<?= $rank ?></span>
                            <div class="flash-card-img-container">
                                <img src="/toko_sembako/uploads/<?= htmlspecialchars($row['image']) ?>" alt="<?= htmlspecialchars($row['name']) ?>" class="flash-card-img">
                            </div>
                            <div class="flash-card-body">
                                <h5 class="flash-card-title"><?= htmlspecialchars($row['name']) ?></h5>
                                <p class="flash-card-sold">Terjual: <strong><?= $row['total_ordered'] ?></strong></p>
                                <p class="flash-card-price">Rp <?= number_format($row['price'], 0, ',', '.') ?></p>
                            </div>
                        </div>
                    </a>
                </div>
                <?php
                    $rank++;
                    endwhile;
                endif;
                ?>
            </div>
            <div class="swiper-button-next"></div>
            <div class="swiper-button-prev"></div>
            <div class="swiper-pagination"></div>
        </div>
    </div>
</div>

<div class="container my-5">
    <h2 class="section-title text-center mb-4">Produk Tersedia</h2>

    <div class="filter-bar card card-body shadow-sm mb-4">
        <div class="row g-3 align-items-center">
            <div class="col-md-5">
                <input type="text" id="search-keyword" class="form-control" placeholder="🔍  Cari nama produk...">
            </div>
            <div class="col-md-5">
                <select id="filter-category" class="form-select">
                    <option value="">Semua Kategori</option>
                    <option value="beras">Beras</option>
                    <option value="telur">Telur</option>
                    <option value="tepung">Tepung</option>
                    <option value="mie">Mie</option>
                    <option value="minyak">Minyak</option>
                    <option value="susu">Susu</option>
                    <option value="gula">Gula</option>
                    <option value="daging">Daging</option>
                    <option value="ikan">Ikan</option>
                    <option value="bumbu">Bumbu Dapur</option>
                </select>
            </div>
            <div class="col-md-2 d-grid">
                <button id="reset-filter" class="btn btn-outline-secondary">Reset</button>
            </div>
        </div>
    </div>

    <div id="product-list">
        <div class="d-flex justify-content-center p-5">
            <div class="spinner-border" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    </div>
</div>

<link rel="stylesheet" href="https://unpkg.com/swiper/swiper-bundle.min.css" />
<script src="https://unpkg.com/swiper/swiper-bundle.min.js"></script>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
$(function() {

    var swiper = new Swiper(".flash-sale-swiper", {
        slidesPerView: 2,
        spaceBetween: 10,
        pagination: {
            el: ".swiper-pagination",
            clickable: true,
        },
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
        },
        breakpoints: {
            640: { slidesPerView: 2, spaceBetween: 20 },
            768: { slidesPerView: 3, spaceBetween: 20 },
            1024: { slidesPerView: 4, spaceBetween: 30 },
            1200: { slidesPerView: 5, spaceBetween: 30 },
        },
        autoplay: {
            delay: 3000,
            disableOnInteraction: false,
        },
    });

    function loadProducts(category = '', keyword = '') {
        
        $('#product-list').html('<div class="d-flex justify-content-center p-5"><div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div></div>');
        
        $.ajax({
            url: "ajax/fetch_items.php",
            method: "GET",
            data: { category, keyword },
            success(data) {
                $('#product-list').html(data);
            },
            error() {
                $('#product-list').html('<div class="alert alert-danger text-center">Gagal memuat produk.</div>');
            }
        });
    }

    loadProducts();

    function debounce(func, delay) {
        let timeout;
        return function(...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }

    $('#filter-category').change(function() {
        loadProducts($(this).val(), $('#search-keyword').val());
    });

    $('#search-keyword').on('input', debounce(function() {
        loadProducts($('#filter-category').val(), $(this).val());
    }, 500)); 

    $('#reset-filter').click(function() {
        $('#filter-category').val('');
        $('#search-keyword').val('');
        loadProducts();
    });
});
</script>

<style>
    .section-title {
        font-weight: 700;
        color: #2c3e50;
        position: relative;
        padding-bottom: 15px;
    }
    .section-title::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 80px;
        height: 4px;
        background-color: #3498db;
        border-radius: 2px;
    }

    .flash-sale-swiper {
        padding-bottom: 50px !important; 
    }
    .flash-card {
        background: #fff;
        border-radius: 15px;
        overflow: hidden;
        box-shadow: 0 4px 15px rgba(0,0,0,0.08);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        position: relative;
        height: 100%;
    }
    .flash-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.12);
    }
    .rank-badge {
        position: absolute;
        top: 10px;
        left: 10px;
        background: crimson;
        color: white;
        padding: 5px 10px;
        border-radius: 20px;
        font-size: 0.9rem;
        font-weight: 700;
        z-index: 2;
    }
    .flash-card-img-container {
        width: 100%;
        height: 180px;
        padding: 10px;
    }
    .flash-card-img {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }
    .flash-card-body {
        padding: 15px;
        text-align: left;
    }
    .flash-card-title {
        font-size: 1rem;
        font-weight: 600;
        color: #333;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    .flash-card-sold {
        font-size: 0.85rem;
        color: #777;
    }
    .flash-card-price {
        font-size: 1.1rem;
        font-weight: 700;
        color: #e74c3c;
        margin-top: 10px;
    }

    .filter-bar {
        background-color: #ffffff;
    }

    .swiper-button-next, .swiper-button-prev {
        color: #3498db;
        background-color: rgba(255, 255, 255, 0.8);
        border-radius: 50%;
        width: 40px;
        height: 40px;
    }
    .swiper-button-next::after, .swiper-button-prev::after {
        font-size: 1.2rem;
    }
    .swiper-pagination-bullet-active {
        background: #3498db;
    }
</style>
