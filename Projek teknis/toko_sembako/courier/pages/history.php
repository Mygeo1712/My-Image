<?php
// Pastikan user adalah kurir yang login
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'courier') {
    exit('Akses Ditolak');
}
$courier_id = $_SESSION['user_id'];

// --- Query untuk pesanan yang SEDANG DIANTAR oleh kurir ini ---
$query_ongoing = "
    SELECT 
        o.order_group_id, u.username AS customer_name, up.full_name, up.address, up.detail_address, up.phone,
        SUM(o.total_price) AS grand_total, o.delivery_schedule,
        GROUP_CONCAT(p.name SEPARATOR '||') as product_names,
        GROUP_CONCAT(o.quantity SEPARATOR '||') as quantities
    FROM orders o
    JOIN users u ON o.user_id = u.id
    JOIN user_profile up ON u.id = up.user_id
    JOIN products p ON o.product_id = p.id
    WHERE o.courier_id = ? AND o.status = 'dikirim'
    GROUP BY o.order_group_id
    ORDER BY o.delivery_schedule ASC
";
$stmt_ongoing = $conn->prepare($query_ongoing);
$stmt_ongoing->bind_param("i", $courier_id);
$stmt_ongoing->execute();
$result_ongoing = $stmt_ongoing->get_result();

// --- Query untuk pesanan yang SUDAH SELESAI diantar oleh kurir ini ---
$query_completed = "
    SELECT 
        o.order_group_id, u.username AS customer_name, o.delivery_schedule, SUM(o.total_price) AS grand_total, o.proof_of_delivery
    FROM orders o
    JOIN users u ON o.user_id = u.id
    WHERE o.courier_id = ? AND o.status = 'selesai'
    GROUP BY o.order_group_id
    ORDER BY o.delivery_schedule DESC
";
$stmt_completed = $conn->prepare($query_completed);
$stmt_completed->bind_param("i", $courier_id);
$stmt_completed->execute();
$result_completed = $stmt_completed->get_result();
?>

<!-- Bagian 1: Pesanan Sedang Diantar -->
<h3 class="mb-3">Tugas Saat Ini</h3>
<div class="row">
    <?php if ($result_ongoing->num_rows > 0): ?>
        <?php while ($order = $result_ongoing->fetch_assoc()): ?>
            <div class="col-12 mb-4">
                <div class="card h-100 shadow-sm border-primary">
                    <div class="card-header bg-primary text-white">
                        <i class="bi bi-truck"></i> <strong>Pesanan Sedang Diantar: #<?= substr(str_replace('ORDER-', '', $order['order_group_id']), 0, 8) ?></strong>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-7">
                                <h5 class="card-title"><?= htmlspecialchars($order['full_name'] ?: $order['customer_name']) ?></h5>
                                <p class="card-text mb-2">
                                    <i class="bi bi-geo-alt-fill text-danger"></i> 
                                    <?= htmlspecialchars($order['detail_address'] ?? $order['address']) ?>
                                </p>
                                <p class="card-text"><i class="bi bi-telephone-fill text-success"></i> <?= htmlspecialchars($order['phone']) ?></p>
                                
                                <div class="mt-3">
                                    <h6 class="text-muted">Item Pesanan:</h6>
                                    <ul class="list-unstyled" style="font-size: 0.9rem;">
                                        <?php
                                        $product_names = explode('||', $order['product_names']);
                                        $quantities = explode('||', $order['quantities']);
                                        for ($i = 0; $i < count($product_names); $i++):
                                        ?>
                                            <li>- <?= htmlspecialchars($quantities[$i]) ?>x <?= htmlspecialchars($product_names[$i]) ?></li>
                                        <?php endfor; ?>
                                    </ul>
                                </div>
                            </div>
                            <div class="col-md-5 border-md-start mt-3 mt-md-0 pt-3 pt-md-0">
                                <form class="form-complete-order" enctype="multipart/form-data">
                                    <input type="hidden" name="group_id" value="<?= htmlspecialchars($order['order_group_id']) ?>">
                                    <div class="mb-3">
                                        <label for="proof_<?= htmlspecialchars($order['order_group_id']) ?>" class="form-label fw-bold">
                                            <i class="bi bi-camera-fill"></i> Unggah Bukti Pengiriman
                                        </label>
                                        <input class="form-control form-control-sm" type="file" name="proof_image" id="proof_<?= htmlspecialchars($order['order_group_id']) ?>" accept="image/*" required>
                                        <small class="form-text text-muted">Foto pelanggan dengan produk yang diterima.</small>
                                    </div>
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-success"><i class="bi bi-check-circle-fill"></i> Selesaikan Pesanan</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        <?php endwhile; ?>
    <?php else: ?>
        <div class="col-12">
            <div class="alert alert-secondary text-center">Anda sedang tidak mengantar pesanan apapun.</div>
        </div>
    <?php endif; ?>
</div>

<hr class="my-5">

<!-- Bagian 2: Riwayat Selesai -->
<h3 class="mb-3">Riwayat Pengantaran Selesai</h3>
<div class="table-responsive">
    <table class="table table-striped table-bordered">
        <thead class="table-light">
            <tr>
                <th>ID Pesanan</th>
                <th>Pelanggan</th>
                <th>Waktu Selesai</th>
                <th>Total (COD)</th>
                <th>Bukti Kirim</th>
            </tr>
        </thead>
        <tbody>
            <?php if ($result_completed->num_rows > 0): ?>
                <?php while ($order = $result_completed->fetch_assoc()): ?>
                    <tr>
                        <td>#<?= substr(str_replace('ORDER-', '', $order['order_group_id']), 0, 8) ?></td>
                        <td><?= htmlspecialchars($order['customer_name']) ?></td>
                        <td><?= date('d M Y, H:i', strtotime($order['delivery_schedule'])) ?></td>
                        <td>Rp <?= number_format($order['grand_total'], 0, ',', '.') ?></td>
                        <td>
                            <?php if (!empty($order['proof_of_delivery'])): ?>
                                <a href="/toko_sembako/Bukti_Konfirmasi_Pesanan/<?= htmlspecialchars($order['proof_of_delivery']) ?>" target="_blank" class="btn btn-sm btn-outline-info">
                                    <i class="bi bi-eye"></i> Lihat
                                </a>
                            <?php else: ?>
                                -
                            <?php endif; ?>
                        </td>
                    </tr>
                <?php endwhile; ?>
            <?php else: ?>
                <tr>
                    <td colspan="5" class="text-center">Anda belum memiliki riwayat pengantaran.</td>
                </tr>
            <?php endif; ?>
        </tbody>
    </table>
</div>

<script>
$(document).ready(function() {
    $('.form-complete-order').on('submit', function(e) {
        e.preventDefault();
        const form = $(this);
        const submitButton = form.find('button[type="submit"]');
        const formData = new FormData(this);

        submitButton.prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Mengunggah...');

        $.ajax({
            url: '../ajax/courier_complete_order.php',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            dataType: 'json',
            success: function(response) {
                if (response.status === 'success') {
                    $('#alert-container-courier').html('<div class="alert alert-success alert-dismissible fade show" role="alert">Pesanan berhasil diselesaikan!<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>');
                    setTimeout(() => location.reload(), 1500);
                } else {
                    alert('Gagal: ' + response.message);
                    submitButton.prop('disabled', false).html('<i class="bi bi-check-circle-fill"></i> Selesaikan Pesanan');
                }
            },
            error: function() {
                alert('Error: Tidak dapat terhubung ke server.');
                submitButton.prop('disabled', false).html('<i class="bi bi-check-circle-fill"></i> Selesaikan Pesanan');
            }
        });
    });
});
</script>
