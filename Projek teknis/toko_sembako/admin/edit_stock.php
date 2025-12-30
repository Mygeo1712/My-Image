<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}

require_once '../backend/dbconnect.php';
function log_admin_activity($conn, $admin_id, $activity, $activity_type)
{
    $stmt = $conn->prepare("INSERT INTO admin_logs (admin_id, activity, activity_type) VALUES (?, ?, ?)");
    $stmt->bind_param("iss", $admin_id, $activity, $activity_type);
    $stmt->execute();
    $stmt->close();
}

$success_msg = '';

// Proses update stok jika form disubmit
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['product_id'], $_POST['stock'])) {
    $product_id = intval($_POST['product_id']);
    $stock = intval($_POST['stock']);
    $stmt = $conn->prepare("UPDATE products SET stock = ? WHERE id = ?");
    $stmt->bind_param("ii", $stock, $product_id);
    if ($stmt->execute()) {
    $success_msg = "Stok produk berhasil diperbarui.";
    
    // Ambil nama produk untuk dicatat dalam log
    $product_stmt = $conn->prepare("SELECT name FROM products WHERE id = ?");
    $product_stmt->bind_param("i", $product_id);
    $product_stmt->execute();
    $product_stmt->bind_result($product_name);
    $product_stmt->fetch();
    $product_stmt->close();

    // Catat aktivitas ke dalam log
    $admin_id = $_SESSION['user_id'];
    $activity = "Mengubah stok produk '$product_name' (ID: $product_id) menjadi $stock";
    $activity_type = "update_stock";

    log_admin_activity($conn, $admin_id, $activity, $activity_type);
}

    $stmt->close();
}

// Ambil semua data produk
$query = "SELECT id, name, category, stock FROM products ORDER BY name ASC";
$result = $conn->query($query);
?>

<!DOCTYPE html>
<html lang="id">

<head>
    <meta charset="UTF-8" />
    <title>Edit Stok Produk</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        input:disabled {
            background-color: #ddd !important;
        }
    </style>
</head>

<body class="bg-dark text-light">
    <div class="container mt-5">
        <h1>Edit Stok Produk</h1>
        <a href="dashboard.php" class="btn btn-secondary mb-4">← Kembali ke Dashboard</a>

        <?php if ($success_msg): ?>
            <div class="alert alert-success"><?= htmlspecialchars($success_msg) ?></div>
        <?php endif; ?>

        <!-- Filter dan Pencarian -->
        <div class="row mb-4">
            <div class="col-md-3">
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
            <div class="col-md-3">
                <input type="text" id="search-input" class="form-control" placeholder="Cari produk...">
            </div>
        </div>

        <!-- Tabel Produk -->
        <table class="table table-striped table-dark" id="product-table">
            <thead>
                <tr>
                    <th>Nama Produk</th>
                    <th>Kategori</th>
                    <th>Stok Saat Ini</th>
                    <th>Update Stok</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                <?php while ($row = $result->fetch_assoc()): ?>
                    <tr class="product-row" data-name="<?= strtolower($row['name']) ?>" data-category="<?= strtolower($row['category']) ?>">
                        <form method="post" class="d-flex align-items-center">
                            <td class="align-middle"><?= htmlspecialchars($row['name']) ?></td>
                            <td class="align-middle"><?= htmlspecialchars($row['category']) ?></td>
                            <td class="align-middle"><?= intval($row['stock']) ?></td>
                            <td class="align-middle" style="max-width: 150px;">
                                <input type="number" name="stock" value="<?= intval($row['stock']) ?>" min="0" class="form-control form-control-sm stock-input" required disabled>
                                <input type="hidden" name="product_id" value="<?= intval($row['id']) ?>" />
                            </td>
                            <td class="align-middle">
                                <button type="button" class="btn btn-warning btn-sm btn-edit">Edit</button>
                                <button type="submit" class="btn btn-primary btn-sm d-none btn-save">Simpan</button>
                            </td>
                        </form>
                    </tr>
                <?php endwhile; ?>
            </tbody>
        </table>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const editButtons = document.querySelectorAll(".btn-edit");
            const stockInputs = document.querySelectorAll(".stock-input");
            const saveButtons = document.querySelectorAll(".btn-save");

            editButtons.forEach((btn, index) => {
                btn.addEventListener("click", function() {
                    // Disable semua input & tampilkan kembali tombol Edit
                    stockInputs.forEach(input => input.disabled = true);
                    saveButtons.forEach(btn => btn.classList.add("d-none"));
                    editButtons.forEach(btn => btn.classList.remove("d-none"));

                    // Aktifkan hanya pada baris ini
                    stockInputs[index].disabled = false;
                    saveButtons[index].classList.remove("d-none");
                    editButtons[index].classList.add("d-none");
                });
            });

            // Filter kategori dan pencarian
            const filterCategory = document.getElementById("filter-category");
            const searchInput = document.getElementById("search-input");
            const productRows = document.querySelectorAll(".product-row");

            function filterProducts() {
                const searchValue = searchInput.value.toLowerCase();
                const categoryValue = filterCategory.value;

                productRows.forEach(row => {
                    const name = row.getAttribute("data-name");
                    const category = row.getAttribute("data-category");

                    const matchName = name.includes(searchValue);
                    const matchCategory = !categoryValue || category === categoryValue;

                    row.style.display = (matchName && matchCategory) ? "" : "none";
                });
            }

            filterCategory.addEventListener("change", filterProducts);
            searchInput.addEventListener("input", filterProducts);
        });
    </script>
</body>

</html>