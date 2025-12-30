<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id']) || !isset($_GET['group_id'])) {
    exit('Akses tidak sah.');
}

$user_id = $_SESSION['user_id'];
$group_id = $_GET['group_id'];

$query = "
    SELECT 
        o.id, o.product_id, o.quantity, o.delivery_schedule, o.order_date, o.status,
        p.name, p.price, p.stock + o.quantity AS available_stock
    FROM orders o
    JOIN products p ON o.product_id = p.id
    WHERE o.user_id = ? AND o.order_group_id = ?
";
$stmt = $conn->prepare($query);
$stmt->bind_param("is", $user_id, $group_id);
$stmt->execute();
$result = $stmt->get_result();
$order_items = [];
while ($row = $result->fetch_assoc()) {
    $order_items[] = $row;
}
$stmt->close();

if (empty($order_items) || strtolower($order_items[0]['status']) !== 'pending' || (new DateTime())->getTimestamp() - (new DateTime($order_items[0]['order_date']))->getTimestamp() > 3600) {
    $_SESSION['notification'] = ['type' => 'danger', 'message' => 'Pesanan ini sudah tidak dapat diedit lagi.'];
    echo "<script>window.location.href = '/toko_sembako/transaksi';</script>";
    exit();
}

date_default_timezone_set('Asia/Jakarta');
$now = new DateTime();
$deliverySlots = ['today' => [], 'future' => []];
$expressDeliveryTime = (new DateTime())->modify('+1 hour');
if ($expressDeliveryTime->format('H') < 21) {
    $deliverySlots['today'][] = ['value' => $expressDeliveryTime->format('H:i:s'), 'text'  => 'Express (Tiba sekitar pukul ' . $expressDeliveryTime->format('H:i') . ')'];
}
$standardRanges = ["09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00", "17:00-19:00", "19:00-21:00"];
foreach ($standardRanges as $range) {
    $rangeStartHour = (int)explode(':', explode('-', $range)[0])[0];
    if ($rangeStartHour > $expressDeliveryTime->format('H')) {
        $deliverySlots['today'][] = ['value' => explode('-', $range)[0] . ':00', 'text'  => $range];
    }
    $deliverySlots['future'][] = ['value' => explode('-', $range)[0] . ':00', 'text'  => $range];
}
?>

<div class="container mt-5 mb-5">
    <form id="edit-order-form">
        <input type="hidden" name="group_id" value="<?= htmlspecialchars($group_id) ?>">
        <div class="row g-4">
            <div class="col-lg-7">
                <div class="card shadow-sm">
                    <div class="card-header bg-warning text-dark">
                        <h4 class="mb-0">Edit Pesanan #<?= substr(str_replace('ORDER-', '', $group_id), 0, 8) ?></h4>
                    </div>
                    <div class="card-body">
                        <h5>Item Saat Ini</h5>
                        <ul class="list-group" id="current-items-list">
                            <?php foreach ($order_items as $item): ?>
                                <li class="list-group-item d-flex justify-content-between align-items-center" data-product-id="<?= $item['product_id'] ?>" data-price="<?= $item['price'] ?>">
                                    <div>
                                        <strong><?= htmlspecialchars($item['name']) ?></strong><br>
                                        <small class="text-muted">Rp <?= number_format($item['price']) ?></small>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <input type="number" name="items[<?= $item['product_id'] ?>]" class="form-control form-control-sm item-quantity" value="<?= $item['quantity'] ?>" min="1" max="<?= $item['available_stock'] ?>" style="width: 70px;">
                                        <button type="button" class="btn btn-sm btn-outline-danger ms-2 btn-remove-item"><i class="bi bi-trash"></i></button>
                                    </div>
                                </li>
                            <?php endforeach; ?>
                        </ul>
                        <hr>
                        <h5>Tambah Produk Lain</h5>
                        <div class="input-group">
                            <input type="text" id="search-product-input" class="form-control" placeholder="Cari produk...">
                        </div>
                        <div id="search-results" class="mt-2" style="max-height: 300px; overflow-y: auto;"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-5">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h5>Jadwal Pengiriman</h5>
                        <div class="mb-3">
                            <label for="delivery_date" class="form-label">Tanggal</label>
                            <input type="date" class="form-control" id="delivery_date" name="delivery_date" value="<?= date('Y-m-d', strtotime($order_items[0]['delivery_schedule'])) ?>" min="<?= date('Y-m-d') ?>" required>
                        </div>
                        <div class="mb-4">
                            <label for="delivery_time" class="form-label">Waktu</label>
                            <select class="form-select" id="delivery_time" name="delivery_time" required>
                                <option value="" disabled>Pilih Waktu</option>
                                <optgroup label="Hari Ini" id="today-time-slots"><?php foreach ($deliverySlots['today'] as $slot) echo "<option value='{$slot['value']}'>{$slot['text']}</option>"; ?></optgroup>
                                <optgroup label="Besok & Seterusnya" id="future-time-slots" style="display:none;"><?php foreach ($deliverySlots['future'] as $slot) echo "<option value='{$slot['value']}'>{$slot['text']}</option>"; ?></optgroup>
                            </select>
                        </div>
                        <hr>
                        <h5>Ringkasan Total</h5>
                        <div class="d-flex justify-content-between">
                            <span>Total Harga Baru</span>
                            <strong id="new-total-price">Rp 0</strong>
                        </div>
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-success btn-lg"><i class="bi bi-save"></i> Simpan Semua Perubahan</button>
                            <a href="/toko_sembako/transaksi" class="btn btn-secondary">Batal</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<div id="edit-alert-container" style="position: fixed; top: 20px; right: 20px; z-index: 9999;"></div>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
    $(document).ready(function() {
        function calculateTotal() {
            let total = 0;
            $('#current-items-list li').each(function() {
                const price = parseFloat($(this).data('price'));
                const quantity = parseInt($(this).find('.item-quantity').val());
                if (!isNaN(price) && !isNaN(quantity)) {
                    total += price * quantity;
                }
            });
            $('#new-total-price').text('Rp ' + total.toLocaleString('id-ID'));
        }

        function updateDeliveryTimes() {
            const selectedDate = $('#delivery_date').val();
            if (selectedDate === '<?= date('Y-m-d') ?>') {
                $('#today-time-slots').show();
                $('#future-time-slots').hide();
            } else {
                $('#today-time-slots').hide();
                $('#future-time-slots').show();
            }

            const originalScheduleDate = '<?= date('Y-m-d', strtotime($order_items[0]['delivery_schedule'])) ?>';
            const originalScheduleTime = '<?= date('H:i:s', strtotime($order_items[0]['delivery_schedule'])) ?>';

            if (selectedDate === originalScheduleDate) {
                $('#delivery_time').val(originalScheduleTime);
            } else {
                $('#delivery_time').val('');
            }
        }

        $('#delivery_date').on('change', updateDeliveryTimes);
        updateDeliveryTimes();

        $(document).on('click', '.btn-remove-item', function() {
            if ($('#current-items-list li').length > 1) {
                $(this).closest('li').remove();
                calculateTotal();
            } else {
                alert('Pesanan harus memiliki minimal satu produk.');
            }
        });

        $('#search-product-input').on('keyup', function() {
            const keyword = $(this).val();
            if (keyword.length < 2) {
                $('#search-results').empty();
                return;
            }
            $.get('/toko_sembako/ajax/search_products_for_edit.php', {
                keyword: keyword
            }, function(data) {
                $('#search-results').html(data);
            });
        });

        $(document).on('click', '.btn-add-to-order', function(e) {
            e.preventDefault();
            const card = $(this).closest('.search-result-card');
            const productId = card.data('product-id');
            const name = card.data('name');
            const price = card.data('price');
            const stock = card.data('stock');

            if ($(`#current-items-list li[data-product-id="${productId}"]`).length > 0) {
                alert('Produk ini sudah ada di dalam pesanan.');
                return;
            }

            const newItemHtml = `
            <li class="list-group-item d-flex justify-content-between align-items-center" data-product-id="${productId}" data-price="${price}">
                <div>
                    <strong>${name}</strong><br>
                    <small class="text-muted">Rp ${parseFloat(price).toLocaleString('id-ID')}</small>
                </div>
                <div class="d-flex align-items-center">
                    <input type="number" name="items[${productId}]" class="form-control form-control-sm item-quantity" 
                            value="1" min="1" max="${stock}" style="width: 70px;">
                    <button type="button" class="btn btn-sm btn-outline-danger ms-2 btn-remove-item"><i class="bi bi-trash"></i></button>
                </div>
            </li>`;
            $('#current-items-list').append(newItemHtml);
            calculateTotal();
            $('#search-product-input').val('');
            $('#search-results').empty();
        });

        $(document).on('change input', '.item-quantity', calculateTotal);

        $('#edit-order-form').on('submit', function(e) {
            e.preventDefault();
            const submitButton = $(this).find('button[type="submit"]');
            submitButton.prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Menyimpan...');

            $.post('/toko_sembako/ajax/update_order.php', $(this).serialize(), function(response) {
                if (response.status === 'success') {
                    window.location.href = '/toko_sembako/transaksi';
                } else {
                    alert(response.message || 'Terjadi kesalahan.');
                    submitButton.prop('disabled', false).html('<i class="bi bi-save"></i> Simpan Perubahan');
                }
            }, 'json').fail(function() {
                alert('Tidak dapat terhubung ke server.');
                submitButton.prop('disabled', false).html('<i class="bi bi-save"></i> Simpan Perubahan');
            });
        });

        calculateTotal();
    });
</script>