<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($conn)) {
    include_once("backend/dbconnect.php");
}

if (isset($_GET['slug'])) {
    $slug = $_GET['slug'];

    $sql = "SELECT * FROM products WHERE slug = ?";
    $stmt = $conn->prepare($sql);

    $stmt->bind_param("s", $slug);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    if (!$row) {
        echo "<div class='container mt-5'><div class='alert alert-danger text-center'>Produk tidak ditemukan.</div></div>";
        return;
    }
} else {
    echo "<div class='container mt-5'><div class='alert alert-danger text-center'>URL produk tidak valid.</div></div>";
    return;
}

$is_stock_available = ($row['stock'] > 0);
$stock_message = $is_stock_available ? "Stok: " . htmlspecialchars($row['stock']) : "Stok Habis";
$buy_button_disabled = $is_stock_available ? "" : "disabled";
?>

<div class="container mt-5">
    <div class="row g-4 bg-light p-4 rounded shadow-sm">
        <div class="text-start">
            <a href="/toko_sembako/home" class="btn btn-outline-secondary mb-4">
                ← Kembali ke Daftar Produk
            </a>
        </div>
        <div class="col-md-6 text-center">
            <img src="/toko_sembako/uploads/<?= htmlspecialchars($row['image']) ?>"
                class="img-fluid rounded shadow-sm"
                style="max-height: 400px; object-fit: contain;"
                alt="<?= htmlspecialchars($row['name']) ?>">
        </div>

        <div class="col-md-6 d-flex flex-column justify-content-between">
            <div>
                <h3><?= htmlspecialchars($row['name']) ?></h3>
                <p class="text-success fw-bold fs-4">Rp <?= number_format($row['price'], 0, ',', '.') ?></p>
                <p class="mb-3 <?= $is_stock_available ? 'text-muted' : 'text-danger fw-bold' ?>">
                    <?= $stock_message ?>
                </p>

                <form id="buy-now-form" class="mt-3">
                    <input type="hidden" name="product_id" value="<?= $row['id'] ?>">

                    <div class="d-flex align-items-center gap-2 mb-3">
                        <button type="button" id="qty-minus" class="btn btn-outline-secondary" <?= $buy_button_disabled ?>>
                            <i class="fa fa-minus"></i>
                        </button>
                        <input type="number" id="quantity" name="quantity" value="1" min="1"
                            class="form-control text-center" style="max-width: 70px;" <?= $buy_button_disabled ?>>
                        <button type="button" id="qty-plus" class="btn btn-outline-secondary" <?= $buy_button_disabled ?>>
                            <i class="fa fa-plus"></i>
                        </button>
                        <div id="total-price" class="fw-bold text-success ms-3">
                            Rp <?= number_format($row['price'], 0, ',', '.') ?>
                        </div>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-success w-50" <?= $buy_button_disabled ?>>
                            <i class="fa fa-check"></i> Beli Sekarang
                        </button>
                        <button type="button" id="add-to-cart" class="btn btn-warning w-50" <?= $buy_button_disabled ?>>
                            <i class="fa fa-shopping-cart"></i> Keranjang
                        </button>
                    </div>
                </form>
            </div>

            <div class="mt-4">
                <h6>Deskripsi Produk</h6>
                <div class="bg-white p-3 rounded border overflow-auto" style="max-height: 200px; font-size: 0.95rem;">
                    <?= nl2br(htmlspecialchars($row['description'])) ?>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="order-alert" class="alert text-center" style="display: none; position: fixed; top: 20px; left: 50%; transform: translateX(-50%); z-index: 9999;"></div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
    $(document).ready(function() {
        const price = <?= (int)$row['price'] ?>;
        const maxStock = <?= (int)$row['stock'] ?>;

        function updateTotal() {
            let qty = parseInt($('#quantity').val());
            if (isNaN(qty) || qty < 1) qty = 1;

            if (qty > maxStock) qty = maxStock;
            if (maxStock === 0) qty = 0;

            $('#quantity').val(qty);
            $('#total-price').text('Rp ' + (price * qty).toLocaleString('id-ID'));

            if (maxStock === 0) {
                $('#qty-minus, #qty-plus, #quantity, #buy-now-form button[type="submit"], #add-to-cart').prop('disabled', true);
            } else {
                $('#buy-now-form button[type="submit"], #add-to-cart').prop('disabled', false);
            }
        }

        $('#qty-minus').click(() => {
            let qty = parseInt($('#quantity').val());
            if (qty > 1) {
                $('#quantity').val(qty - 1);
            }
            updateTotal();
        });

        $('#qty-plus').click(() => {
            let qty = parseInt($('#quantity').val());
            if (qty < maxStock) {
                $('#quantity').val(qty + 1);
            }
            updateTotal();
        });

        $('#quantity').on('input change', updateTotal);
        updateTotal();

        $('#buy-now-form').on('submit', function(e) {
            e.preventDefault();

            $.post('/toko_sembako/ajax/prepare_checkout.php', $(this).serialize(), function(response) {
                if (response.status === 'success') {
                    window.location.href = '/toko_sembako/payment';
                } else {
                    showAlert('danger', response.message || 'Terjadi kesalahan.');
                }
            }, 'json');
        });

        $('#add-to-cart').click(function() {
            $.post('/toko_sembako/ajax/add_to_cart.php', {
                product_id: $('input[name="product_id"]').val(),
                quantity: $('#quantity').val()
            }, function(response) {
                showAlert(response.status === 'success' ? 'success' : 'danger', response.message);

                if (response.status === 'success') {
                    setTimeout(() => {
                        window.location.href = '/toko_sembako/home';
                    }, 1500);
                }
            }, 'json');

        });

        function showAlert(type, message) {
            $('#order-alert')
                .removeClass('alert-success alert-danger alert-warning')
                .addClass('alert-' + type)
                .html(message)
                .slideDown();

            setTimeout(() => $('#order-alert').slideUp(), 3000);
        }
    });
</script>