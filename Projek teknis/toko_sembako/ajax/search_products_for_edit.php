<?php
include('../backend/dbconnect.php');

// Ambil keyword pencarian
$keyword = isset($_GET['keyword']) ? trim($_GET['keyword']) : '';

// Jika keyword kosong, jangan lakukan apa-apa
if (empty($keyword)) {
    exit;
}

// Menambahkan 'description' ke dalam query
$sql = "SELECT id, name, description, image, price, stock FROM products WHERE name LIKE ? AND stock > 0 ORDER BY name ASC LIMIT 10";
$params = ['%' . $keyword . '%'];

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", ...$params);
$stmt->execute();
$result = $stmt->get_result();

$output = '';

if ($result->num_rows > 0) {
    // Menggunakan grid Bootstrap untuk menata kartu
    $output .= '<div class="row g-2">';
    while ($row = $result->fetch_assoc()) {
        // Membuat potongan deskripsi agar tidak terlalu panjang
        $description_snippet = mb_strimwidth(htmlspecialchars($row['description']), 0, 70, "...");
        
        $output .= '
            <div class="col-12 col-md-6">
                <div class="card h-100 search-result-card" 
                     data-product-id="' . $row['id'] . '" 
                     data-name="' . htmlspecialchars($row['name']) . '" 
                     data-price="' . $row['price'] . '"
                     data-stock="' . $row['stock'] . '">
                    <div class="row g-0">
                        <div class="col-4">
                            <img src="/toko_sembako/uploads/' . htmlspecialchars($row['image']) . '" class="img-fluid rounded-start w-100" style="object-fit: cover; height: 100%;">
                        </div>
                        <div class="col-8">
                            <div class="card-body p-2 d-flex flex-column h-100">
                                <h6 class="card-title" style="font-size: 0.9rem; margin-bottom: 0.25rem;">' . htmlspecialchars($row['name']) . '</h6>
                                <p class="card-text text-muted" style="font-size: 0.8rem; flex-grow: 1;">' . $description_snippet . '</p>
                                <div class="mt-auto">
                                    <p class="mb-1 fw-bold text-success">Rp ' . number_format($row['price'], 0, ',', '.') . '</p>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <small class="text-muted">Stok: ' . $row['stock'] . '</small>
                                        <button type="button" class="btn btn-sm btn-success btn-add-to-order">Tambah</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>';
    }
    $output .= '</div>';
} else {
    $output = '<div class="text-center text-muted p-3">Produk tidak ditemukan.</div>';
}
echo $output;
?>
