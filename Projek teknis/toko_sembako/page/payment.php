<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}
if (!isset($conn)) {
    include_once("backend/dbconnect.php");
}

$user_id = $_SESSION['user_id'];

$checkout_data = [];
$is_single_item = false;

if (isset($_SESSION['checkout_items'])) {
    $checkout_data['items'] = $_SESSION['checkout_items']['items'];
    $checkout_data['total_price'] = $_SESSION['checkout_items']['grand_total'];
} else if (isset($_SESSION['checkout_item'])) {
    $is_single_item = true;
    $item = $_SESSION['checkout_item'];
    $checkout_data['items'] = [$item];
    $checkout_data['total_price'] = $item['total_price'];
} else {
    header("Location: /toko_sembako/home");
    exit();
}

date_default_timezone_set('Asia/Jakarta');
$now = new DateTime();
$deliverySlots = ['today' => [], 'future' => []];

$expressDeliveryTime = (new DateTime())->modify('+1 hour');
$cutoffHour = 21;

$currentTimeHour = (int)$now->format('H');

if ($currentTimeHour < ($cutoffHour - 1)) {
    $expressEndHour = (int)$expressDeliveryTime->format('H');
    if ($expressEndHour < $cutoffHour) {
        $deliverySlots['today'][] = [
            'value' => $expressDeliveryTime->format('H:i:s'),
            'text'  => 'Express (Tiba sekitar pukul ' . $expressDeliveryTime->format('H:i') . ')'
        ];
    }
}

$standardRanges = [
    "09:00-11:00", "11:00-13:00", "13:00-15:00",
    "15:00-17:00", "17:00-19:00", "19:00-21:00"
];

foreach ($standardRanges as $range) {
    $rangeStartHour = (int)explode(':', explode('-', $range)[0])[0];
    
    if ($rangeStartHour > $currentTimeHour && $rangeStartHour < $cutoffHour) {
        $deliverySlots['today'][] = [
            'value' => explode('-', $range)[0] . ':00',
            'text'  => $range
        ];
    }

    $deliverySlots['future'][] = [
        'value' => explode('-', $range)[0] . ':00',
        'text'  => $range
    ];
}
usort($deliverySlots['today'], function($a, $b) {
    return strtotime($a['value']) - strtotime($b['value']);
});
?>

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <form id="payment-form">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Konfirmasi Pesanan & Pengiriman</h4>
                    </div>
                    <div class="card-body">
                        <h5>Ringkasan Pesanan</h5>
                        <div class="border p-3 rounded mb-4">
                            <?php foreach ($checkout_data['items'] as $item): ?>
                            <div class="d-flex align-items-center <?php echo count($checkout_data['items']) > 1 ? 'mb-3' : ''; ?>">
                                <img src="/toko_sembako/uploads/<?= htmlspecialchars($item['image']) ?>" width="60" class="me-3 rounded">
                                <div>
                                    <h6 class="mb-1"><?= htmlspecialchars($item['name']) ?></h6>
                                    <p class="mb-0 text-muted"><?= htmlspecialchars($item['quantity']) ?> x Rp <?= number_format($item['price'], 0, ',', '.') ?></p>
                                </div>
                            </div>
                            <?php endforeach; ?>
                            <hr>
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Total Pembayaran</h5>
                                <h5 class="mb-0 text-success fw-bold">Rp <?= number_format($checkout_data['total_price'], 0, ',', '.') ?></h5>
                            </div>
                        </div>

                        <h5>Metode Pembayaran</h5>
                        <div class="border p-3 rounded mb-4 bg-light">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="payment_method" id="cod" value="COD" checked>
                                <label class="form-check-label" for="cod">
                                    <strong><i class="bi bi-truck me-2"></i>Bayar di Tempat (COD)</strong>
                                </label>
                            </div>
                            <small class="text-muted mt-2 d-block">Metode pembayaran lain akan segera tersedia.</small>
                        </div>
                        
                        <h5>Jadwal Pengiriman</h5>
                        <div class="border p-3 rounded">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="delivery_date" class="form-label fw-bold">Tanggal Pengiriman</label>
                                    <input type="date" class="form-control" id="delivery_date" name="delivery_date" value="<?= date('Y-m-d') ?>" min="<?= date('Y-m-d') ?>" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="delivery_time" class="form-label fw-bold">Waktu Pengiriman</label>
                                    <select class="form-select" id="delivery_time" name="delivery_time" required>
                                        <option value="" disabled selected>Pilih Waktu</option>
                                        </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-footer text-end">
                        <button type="submit" class="btn btn-success w-100 fs-5">
                            <i class="bi bi-shield-check-fill"></i> Konfirmasi Pesanan
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<div id="payment-alert-container" style="position: fixed; top: 20px; right: 20px; z-index: 9999;"></div>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
$(document).ready(function() {
    const today = '<?= date('Y-m-d') ?>';
    const deliverySlotsToday = <?= json_encode($deliverySlots['today']) ?>;
    const deliverySlotsFuture = <?= json_encode($deliverySlots['future']) ?>;

    function updateDeliveryTimes() {
        const selectedDate = $('#delivery_date').val();
        const $deliveryTimeSelect = $('#delivery_time');
        
        $deliveryTimeSelect.empty();
        $deliveryTimeSelect.append('<option value="" disabled selected>Pilih Waktu</option>');

        let slotsToUse = [];
        let optgroupLabel = '';

        if (selectedDate === today) {
            slotsToUse = deliverySlotsToday;
            optgroupLabel = 'Hari Ini';
        } else {
            slotsToUse = deliverySlotsFuture;
            optgroupLabel = 'Besok & Seterusnya';
        }
        
        if (slotsToUse.length > 0) {
            let optgroup = $(`<optgroup label="${optgroupLabel}"></optgroup>`);
            $.each(slotsToUse, function(index, slot) {
                optgroup.append($('<option>', {
                    value: slot.value,
                    text: slot.text
                }));
            });
            $deliveryTimeSelect.append(optgroup);
        } else {
            $deliveryTimeSelect.append('<option value="" disabled>Tidak ada slot tersedia</option>');
        }
        
        if ($deliveryTimeSelect.find('option:not([value=""]):first').length > 0) {
            $deliveryTimeSelect.val($deliveryTimeSelect.find('option:not([value=""]):first').val());
        } else {
            $deliveryTimeSelect.val('');
        }
    }

    $('#delivery_date').on('change', updateDeliveryTimes);
    updateDeliveryTimes();


    function showAlert(type, message) {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show shadow-lg" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>`;
        $('#payment-alert-container').html(alertHtml);
    }

    $('#payment-form').on('submit', function(e) {
        e.preventDefault();
        
        if (!$('#delivery_time').val()) {
            showAlert('danger', 'Mohon pilih waktu pengiriman.');
            return;
        }

        const submitButton = $(this).find('button[type="submit"]');
        submitButton.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Memproses Pesanan...');

        $.post('/toko_sembako/ajax/process_cod_order.php', $(this).serialize(), function(response) {
            if (response.status === 'success') {
                showAlert('success', 'Pesanan berhasil dibuat! Anda akan diarahkan ke halaman riwayat transaksi.');
                setTimeout(() => {
                    window.location.href = '/toko_sembako/transaksi';
                }, 2500);
            } else {
                showAlert('danger', response.message || 'Terjadi kesalahan.');
                submitButton.prop('disabled', false).html('<i class="bi bi-shield-check-fill"></i> Konfirmasi Pesanan');
            }
        }, 'json').fail(function() {
            showAlert('danger', 'Tidak dapat terhubung ke server. Silakan coba lagi.');
            submitButton.prop('disabled', false).html('<i class="bi bi-shield-check-fill"></i> Konfirmasi Pesanan');
        });
    });
});
</script>