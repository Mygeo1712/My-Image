<?php
include('../backend/dbconnect.php');

// Ambil parameter filter
$category = isset($_GET['category']) ? trim($_GET['category']) : '';
$keyword = isset($_GET['keyword']) ? trim($_GET['keyword']) : '';
// Ambil parameter konteks untuk menentukan jenis output
$context = isset($_GET['context']) ? $_GET['context'] : 'home';

// Query dasar
$sql = "SELECT id, name, description, image, price, stock, slug FROM products WHERE 1=1";
$params = [];
$types = '';

// Tambahkan filter kategori jika ada
if (!empty($category)) {
    $sql .= " AND category = ?";
    $params[] = $category;
    $types .= 's';
}

// Tambahkan filter pencarian jika ada
if (!empty($keyword)) {
    $sql .= " AND name LIKE ?";
    $params[] = '%' . $keyword . '%';
    $types .= 's';
}

$sql .= " ORDER BY created_at DESC";

$stmt = $conn->prepare($sql);

if ($types && !empty($params)) {
    $stmt->bind_param($types, ...$params);
}

$stmt->execute();
$result = $stmt->get_result();

$output = '';

if ($result->num_rows > 0) {
    if ($context === 'edit_order') {
        $output .= '<div class="list-group">';
        while ($row = $result->fetch_assoc()) {
            $output .= '
                <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center btn-add-to-order"
                data-product-id="' . $row['id'] . '"
                data-name="' . htmlspecialchars($row['name']) . '"
                data-price="' . $row['price'] . '">
                    <div>
                        <img src="/toko_sembako/uploads/' . htmlspecialchars($row['image']) . '" width="40" class="me-2 rounded">
                        ' . htmlspecialchars($row['name']) . '
                    </div>
                    <span class="badge bg-success">Tambah</span>
                </a>';
        }
        $output .= '</div>';
    }
    else {
        $output .= '
        <style>
            /* Custom CSS for product cards on home page */
            .product-card {
                border: 1px solid #e0e0e0;
                border-radius: 0.75rem;
                overflow: hidden;
                transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
            }
            .product-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
            }
            .product-card-img {
                height: 180px; /* Tinggi gambar konsisten */
                width: 100%; /* Lebar gambar konsisten */
                object-fit: contain; /* Jaga rasio aspek dan sesuaikan dalam kotak */
                padding: 10px; /* Padding di sekitar gambar */
                background-color: #fff; /* Latar belakang gambar */
            }
            .product-card-body {
                padding: 12px; /* Padding card body */
                display: flex;
                flex-direction: column;
                flex-grow: 1; /* Biarkan body mengisi ruang vertikal */
            }
            .product-card-title {
                font-size: 1rem; /* Ukuran judul produk */
                font-weight: 600;
                line-height: 1.3;
                height: 2.6em; /* 2 baris (2 * 1.3em) */
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2; /* Batasi judul ke 2 baris */
                -webkit-box-orient: vertical;
                margin-bottom: 0.5rem;
            }
            .product-card-description {
                font-size: 0.875rem; /* Ukuran deskripsi */
                color: #6c757d; /* Warna teks abu-abu */
                line-height: 1.4;
                height: 4.2em; /* 3 baris (3 * 1.4em) */
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 3; /* Batasi deskripsi ke 3 baris */
                -webkit-box-orient: vertical;
                margin-bottom: 0.5rem;
            }
            .product-card-price {
                font-size: 1.1rem; /* Ukuran harga lebih besar */
                font-weight: bold;
                color: #28a745; /* Warna hijau untuk harga */
                margin-top: auto; /* Dorong harga ke bawah */
                margin-bottom: 0.5rem;
            }
            .product-card-stock {
                font-size: 0.8rem; /* Ukuran status stok */
                font-weight: 500;
                margin-bottom: 0.5rem;
            }
            .product-card-footer {
                padding: 12px;
                background-color: #f8f9fa;
                border-top: 1px solid #e9ecef;
            }
            .product-card .btn {
                font-size: 0.9rem;
                padding: 0.6rem 1rem;
                border-radius: 0.5rem;
            }
        </style>';

        $output .= '<div class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-5 g-3">'; // Menggunakan g-3 untuk jarak

        while ($row = $result->fetch_assoc()) {
            $name = htmlspecialchars($row['name']);
            // Potong deskripsi jika terlalu panjang, tapi biarkan CSS line-clamp yang menanganinya
            $desc = htmlspecialchars($row['description']);
            $img = htmlspecialchars($row['image']);
            $slug = htmlspecialchars($row['slug']);
            $price_formatted = number_format($row['price'], 0, ',', '.');
            $stock = intval($row['stock']);

            if ($stock > 0) {
                $stockStatus = '<p class="text-success product-card-stock mb-0">Stok: ' . $stock . '</p>';
                $button = '<a href="/toko_sembako/detail/' . $slug . '" class="btn btn-primary w-100">Lihat Detail</a>';
            } else {
                $stockStatus = '<p class="text-danger fw-bold product-card-stock mb-0">Stok Habis</p>';
                $button = '<button class="btn btn-secondary w-100" disabled>Stok Habis</button>';
            }

            $output .= '
                <div class="col">
                    <div class="card h-100 product-card">
                        <img src="uploads/' . $img . '" class="card-img-top product-card-img" alt="' . $name . '">
                        <div class="card-body product-card-body">
                            <h5 class="card-title product-card-title text-primary">' . $name . '</h5>
                            <p class="card-text product-card-description">' . $desc . '</p>
                            <p class="product-card-price mb-0">Rp ' . $price_formatted . '</p>
                            ' . $stockStatus . '
                        </div>
                        <div class="product-card-footer">
                            ' . $button . '
                        </div>
                    </div>
                </div>';
        }
        $output .= '</div>';
    }
} else {
    $output = '<div class="text-center p-5 fs-4 text-muted">Produk tidak ditemukan.</div>';
}

echo $output;
?>