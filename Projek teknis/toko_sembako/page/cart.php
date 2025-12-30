<?php
// PHP di file 'page/cart.php'
// Semua pengecekan session_start(), user_id, dan isProfileComplete() sudah ditangani di index.php
// Jadi, file ini hanya perlu asumsi bahwa user_id dan koneksi $conn sudah tersedia.

// Fallback jika file ini diakses langsung (bukan melalui index.php)
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit;
}
if (!isset($conn)) {
    include_once("backend/dbconnect.php");
}

$user_id = $_SESSION['user_id']; // user_id sudah pasti ada jika sampai sini

// Query untuk mengambil item keranjang
$sql = "SELECT c.id AS cart_id, p.id AS product_id, p.name, p.image, p.price, c.quantity
        FROM cart c
        JOIN products p ON c.product_id = p.id
        WHERE c.user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
?>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<div class="container mt-5 bg-light p-4 rounded shadow-sm">
    <h2 class="mb-4 text-center">Keranjang Belanja Anda</h2>

    <?php if ($result->num_rows > 0): ?>
        <form id="cart-form">
            <div class="table-responsive">
                <table class="table table-bordered align-middle text-center">
                    <thead class="table-light">
                        <tr>
                            <th><input type="checkbox" id="check-all"></th>
                            <th>Gambar</th>
                            <th>Nama Produk</th>
                            <th>Harga Satuan</th>
                            <th>Jumlah</th>
                            <th>Subtotal</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php while ($row = $result->fetch_assoc()):
                            $subtotal = $row['price'] * $row['quantity'];
                        ?>
                        <tr data-cart-id="<?= $row['cart_id'] ?>" data-price="<?= $row['price'] ?>">
                            <td><input type="checkbox" name="cart_ids[]" value="<?= $row['cart_id'] ?>" class="check-item"></td>
                            <td><img src="/toko_sembako/uploads/<?= htmlspecialchars($row['image']) ?>" width="60" class="rounded"></td>
                            <td><?= htmlspecialchars($row['name']) ?></td>
                            <td>Rp <?= number_format($row['price'], 0, ',', '.') ?></td>
                            <td>
                                <input type="number" class="quantity-input form-control text-center" value="<?= $row['quantity'] ?>" min="1" style="width: 70px;">
                            </td>
                            <td class="subtotal">Rp <?= number_format($subtotal, 0, ',', '.') ?></td>
                            <td>
                                <button type="button" class="btn btn-sm btn-danger btn-delete">Hapus</button>
                            </td>
                        </tr>
                        <?php endwhile; ?>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colspan="5" class="text-end"><strong>Total Terpilih</strong></td>
                            <td colspan="2"><strong id="total-selected">Rp 0</strong></td>
                        </tr>
                    </tfoot>
                </table>
            </div>

            <div class="d-flex justify-content-between mt-3">
                <a href="/toko_sembako/home" class="btn btn-secondary">← Lanjut Belanja</a>
                <button type="button" id="btn-checkout" class="btn btn-success" disabled>Checkout</button>
            </div>
        </form>
    <?php else: ?>
        <div class="alert alert-info text-center">Keranjang belanja Anda kosong.</div>
        <div class="text-center">
            <a href="/toko_sembako/home" class="btn btn-primary">Belanja Sekarang</a>
        </div>
    <?php endif; ?>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
$(document).ready(function () {
    function formatRp(num) {
        return 'Rp ' + num.toLocaleString('id-ID');
    }

    function updateSubtotal(row) {
        const qty = parseInt(row.find('.quantity-input').val());
        const price = parseFloat(row.data('price'));
        const subtotal = qty * price;
        row.find('.subtotal').text(formatRp(subtotal));
    }

    function updateTotalSelected() {
        let total = 0;
        $('.check-item:checked').each(function () {
            const row = $(this).closest('tr');
            const qty = parseInt(row.find('.quantity-input').val());
            const price = parseFloat(row.data('price'));
            total += qty * price;
        });
        $('#total-selected').text(formatRp(total));
        $('#btn-checkout').prop('disabled', total === 0);
    }

    $('#check-all').on('change', function () {
        $('.check-item').prop('checked', this.checked).trigger('change');
    });

    $(document).on('change', '.check-item', updateTotalSelected);

    $(document).on('input change', '.quantity-input', function () {
        const row = $(this).closest('tr');
        updateSubtotal(row);
        updateTotalSelected(); // Update total jika itemnya sedang dicentang

        const cart_id = row.data('cart-id');
        const quantity = $(this).val();
        $.post('/toko_sembako/ajax/update_cart.php', {cart_id: cart_id, quantity: quantity});
    });

    $(document).on('click', '.btn-delete', function () {
        if (!confirm('Hapus produk ini dari keranjang?')) return;
        const row = $(this).closest('tr');
        const cart_id = row.data('cart-id');

        $.post('/toko_sembako/ajax/delete_cart.php', {cart_id: cart_id}, function (res) {
            if (res.status === 'success') {
                row.remove();
                updateTotalSelected();
                if ($('tbody tr').length === 0) location.reload();
            } else {
                alert(res.message);
            }
        }, 'json');
    });

    // --- LOGIKA CHECKOUT YANG DIPERBARUI ---
    $('#btn-checkout').click(function () {
        const selectedIds = $('.check-item:checked').map(function () {
            return $(this).val();
        }).get();

        if (selectedIds.length === 0) {
            alert('Pilih produk yang ingin di-checkout terlebih dahulu.');
            return;
        }

        // Panggil file AJAX baru untuk menyiapkan checkout
        $.post('/toko_sembako/ajax/prepare_cart_checkout.php', { cart_ids: selectedIds }, function (response) {
            if (response.status === 'success') {
                // Jika sukses, arahkan ke halaman pembayaran
                window.location.href = '/toko_sembako/payment';
            } else {
                alert('Error: ' + response.message);
            }
        }, 'json');
    });

    updateTotalSelected();
});
</script>