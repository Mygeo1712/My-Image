<?php
// Pastikan user adalah kurir yang login
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'courier') {
    exit('Akses Ditolak');
}
$courier_id = $_SESSION['user_id'];

// --- LOGIKA BARU: Cek apakah kurir ini sudah punya pesanan aktif ---
$check_ongoing_query = "SELECT COUNT(*) as ongoing_count FROM orders WHERE courier_id = ? AND status = 'dikirim'";
$stmt_check = $conn->prepare($check_ongoing_query);
$stmt_check->bind_param("i", $courier_id);
$stmt_check->execute();
$ongoing_result = $stmt_check->get_result()->fetch_assoc();
$has_ongoing_delivery = ($ongoing_result['ongoing_count'] > 0);
$stmt_check->close();


// --- Query untuk mengambil pesanan yang SIAP DIAMBIL ---
$query_available = "
    SELECT 
        o.order_group_id,
        up.full_name AS customer_name,
        up.address, up.detail_address, up.phone,
        SUM(o.total_price) AS grand_total,
        o.delivery_schedule
    FROM orders o
    JOIN users u ON o.user_id = u.id
    JOIN user_profile up ON u.id = up.user_id
    WHERE o.status = 'dikirim' AND o.courier_id IS NULL
    GROUP BY o.order_group_id
    ORDER BY o.delivery_schedule ASC
";
$result_available = $conn->query($query_available);
?>

<!-- Notifikasi jika kurir sudah memiliki pesanan aktif -->
<?php if ($has_ongoing_delivery): ?>
<div class="alert alert-warning text-center" role="alert">
    <i class="bi bi-info-circle-fill"></i> 
    Anda memiliki <strong>1 pesanan yang sedang diantar</strong>. Silakan selesaikan terlebih dahulu sebelum mengambil pesanan baru.
    <a href="dashboard.php?page=history" class="alert-link">Lihat di Riwayat Pengantaran</a>.
</div>
<?php endif; ?>


<div class="row">
    <?php if ($result_available && $result_available->num_rows > 0): ?>
        <?php while ($order = $result_available->fetch_assoc()): ?>
            <div class="col-md-6 col-lg-4 mb-4 order-card">
                <div class="card h-100 shadow-sm">
                    <div class="card-header">
                        <strong>Order #<?= substr(str_replace('ORDER-', '', $order['order_group_id']), 0, 8) ?></strong>
                    </div>
                    <div class="card-body">
                        <h5 class="card-title"><?= htmlspecialchars($order['customer_name']) ?></h5>
                        <p class="card-text">
                            <i class="bi bi-geo-alt-fill text-danger"></i> 
                            <?= htmlspecialchars($order['detail_address'] ?? $order['address']) ?>
                        </p>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item">
                                <i class="bi bi-telephone-fill text-success"></i> <?= htmlspecialchars($order['phone']) ?>
                            </li>
                            <li class="list-group-item">
                                <i class="bi bi-calendar-check"></i> <?= date('d M Y, H:i', strtotime($order['delivery_schedule'])) ?>
                            </li>
                            <li class="list-group-item">
                                <i class="bi bi-cash-coin text-primary"></i> 
                                <strong>Rp <?= number_format($order['grand_total'], 0, ',', '.') ?> (COD)</strong>
                            </li>
                        </ul>
                    </div>
                    <div class="card-footer bg-white border-0 text-center">
                        <!-- Tombol akan di-disable jika kurir sudah punya pesanan aktif -->
                        <button class="btn btn-primary w-100 btn-take-order" 
                                data-group-id="<?= htmlspecialchars($order['order_group_id']) ?>"
                                <?= $has_ongoing_delivery ? 'disabled' : '' ?>>
                            Ambil Pesanan Ini
                        </button>
                    </div>
                </div>
            </div>
        <?php endwhile; ?>
    <?php else: ?>
        <div class="col-12">
            <div class="alert alert-info text-center">
                <i class="bi bi-box2-heart fs-1"></i>
                <h4 class="alert-heading mt-2">Tidak ada pesanan yang siap diantar.</h4>
                <p>Silakan kembali lagi nanti.</p>
            </div>
        </div>
    <?php endif; ?>
</div>

<script>
$(document).ready(function() {
    $('.btn-take-order').on('click', function() {
        const button = $(this);
        const groupId = button.data('group-id');
        
        if (confirm('Anda yakin ingin mengambil pesanan ini untuk diantar?')) {
            // Langsung disable semua tombol "Ambil" lainnya
            $('.btn-take-order').not(button).prop('disabled', true);
            button.prop('disabled', true).text('Memproses...');
            
            $.post('../ajax/courier_take_order.php', { group_id: groupId }, function(response) {
                if (response.status === 'success') {
                    // Tampilkan notifikasi sukses
                    $('#alert-container-courier').html('<div class="alert alert-success">Pesanan berhasil diambil! Anda akan diarahkan ke halaman riwayat.</div>');
                    // Arahkan ke halaman riwayat untuk melihat pesanan yang baru diambil
                    setTimeout(() => {
                        window.location.href = 'dashboard.php?page=history';
                    }, 2000);
                } else {
                    alert('Gagal: ' + response.message);
                    // Jika gagal, aktifkan kembali semua tombol
                    $('.btn-take-order').prop('disabled', false).text('Ambil Pesanan Ini');
                }
            }, 'json').fail(function() {
                alert('Error: Tidak dapat terhubung ke server.');
                $('.btn-take-order').prop('disabled', false).text('Ambil Pesanan Ini');
            });
        }
    });
});
</script>
