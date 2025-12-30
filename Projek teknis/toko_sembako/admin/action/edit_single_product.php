<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../../login.php");
    exit();
}

require_once '../../backend/dbconnect.php'; // Pastikan dbconnect.php ada fungsi createSlug()

if (!isset($_GET['id'])) {
    // Redirect jika tidak ada ID
    header("Location: ../edit_product.php");
    exit();
}

$id = intval($_GET['id']);

// Ambil data produk
$stmt = $conn->prepare("SELECT * FROM products WHERE id = ?");
$stmt->bind_param("i", $id);
$stmt->execute();
$result = $stmt->get_result();
$product = $result->fetch_assoc();
$stmt->close();

if (!$product) {
    echo "Produk tidak ditemukan.";
    exit();
}

if (isset($_POST['update'])) {
    $name        = trim($_POST['name']);
    $category    = trim($_POST['category']);
    $description = trim($_POST['description']);
    $price       = floatval($_POST['price']);
    $stock       = intval($_POST['stock']);
    $slug        = createSlug($name);

    if (!empty($_FILES['image']['name'])) {
        $image  = $_FILES['image']['name'];
        $target = "../../uploads/" . basename($image);
        move_uploaded_file($_FILES['image']['tmp_name'], $target);

        $stmt = $conn->prepare("UPDATE products SET name=?, slug=?, category=?, description=?, price=?, stock=?, image=? WHERE id=?");
        $stmt->bind_param("ssssdisi", $name, $slug, $category, $description, $price, $stock, $image, $id);
    } else {
        $stmt = $conn->prepare("UPDATE products SET name=?, slug=?, category=?, description=?, price=?, stock=? WHERE id=?");
        $stmt->bind_param("ssssdii", $name, $slug, $category, $description, $price, $stock, $id);
    }

    if ($stmt->execute()) {
        $_SESSION['notification'] = ['type' => 'success', 'message' => "Produk '" . htmlspecialchars($name) . "' berhasil diperbarui."];
    } else {
        $_SESSION['notification'] = ['type' => 'danger', 'message' => "Gagal memperbarui produk: " . $stmt->error];
    }
    
    $stmt->close();

    header("Location: ../edit_product.php");
    exit();
}
?>

<!DOCTYPE html>
<html lang="id">

<head>
    <meta charset="UTF-8">
    <title>Edit Produk: <?= htmlspecialchars($product['name']) ?></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>

<body class="bg-dark text-light">
    <div class="container mt-5">
        <h1>Edit Produk</h1>
        <a href="../edit_product.php" class="btn btn-secondary mb-4">← Kembali</a>

        <form method="POST" enctype="multipart/form-data">
            <div class="mb-3">
                <label class="form-label">Nama Produk:</label>
                <input type="text" name="name" value="<?= htmlspecialchars($product['name']) ?>" required class="form-control">
            </div>
            <div class="mb-3">
                <label class="form-label">Kategori:</label>
                <select name="category" class="form-control" required>
                    <option value="">-- Pilih Kategori --</option>
                    <?php
                    $categories = ['beras', 'telur', 'tepung', 'mie', 'minyak', 'susu', 'gula', 'daging', 'ikan', 'bumbu'];
                    foreach ($categories as $cat) {
                        $selected = ($product['category'] === $cat) ? 'selected' : '';
                        echo "<option value=\"$cat\" $selected>" . ucfirst($cat) . "</option>";
                    }
                    ?>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Deskripsi:</label>
                <textarea name="description" class="form-control"><?= htmlspecialchars($product['description']) ?></textarea>
            </div>
            <div class="mb-3">
                <label class="form-label">Harga:</label>
                <input type="number" step="0.01" name="price" value="<?= $product['price'] ?>" required class="form-control">
            </div>
            <div class="mb-3">
                <label class="form-label">Stok:</label>
                <input type="number" name="stock" value="<?= $product['stock'] ?>" required min="0" class="form-control">
            </div>
            <div class="mb-3">
                <label class="form-label">Gambar Saat Ini:</label><br>
                <img src="../../uploads/<?= htmlspecialchars($product['image']) ?>" width="120" class="mb-2 img-thumbnail bg-light">
                <input type="file" name="image" class="form-control" accept="image/*">
                <small class="form-text text-muted">Kosongkan jika tidak ingin mengubah gambar.</small>
            </div>
            <button type="submit" name="update" class="btn btn-success">Simpan Perubahan</button>
        </form>
    </div>
</body>
</html>
