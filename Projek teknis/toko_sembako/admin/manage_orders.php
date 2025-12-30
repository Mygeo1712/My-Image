<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
    header("Location: ../login.php");
    exit();
}
require_once '../backend/dbconnect.php';

// Query untuk mengambil semua pesanan yang dikelompokkan
$query = "
    SELECT 
        o.order_group_id,
        u.username AS customer_name,
        SUM(o.total_price) AS grand_total,
        o.delivery_schedule,
        o.status,
        o.order_date
    FROM orders o
    JOIN users u ON o.user_id = u.id
    GROUP BY o.order_group_id
    ORDER BY o.order_date DESC
";
$result = $conn->query($query);

function getStatusBadgeClass($status) {
    switch (strtolower($status)) {
        case 'pending': return 'bg-warning text-dark';
        case 'dikirim': return 'bg-info text-dark';
        case 'selesai': return 'bg-success text-white';
        case 'dibatalkan': return 'bg-danger text-white';
        default: return 'bg-secondary text-white';
    }
}
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Manajemen Pesanan</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="bg-dark text-light">
    <div class="container mt-5">
        <h1>Manajemen Pesanan</h1>
        <a href="dashboard.php" class="btn btn-secondary mb-4">← Kembali ke Dashboard</a>
        
        <div id="alert-container"></div>

        <div class="table-responsive">
            <table class="table table-bordered table-dark table-striped align-middle">
                <thead>
                    <tr>
                        <th>ID Pesanan</th>
                        <th>Pelanggan</th>
                        <th>Total Bayar</th>
                        <th>Jadwal Kirim</th>
                        <th>Status</th>
                        <th style="width: 200px;">Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if ($result->num_rows > 0): ?>
                        <?php while ($order = $result->fetch_assoc()): ?>
                            <tr id="order-row-<?= htmlspecialchars($order['order_group_id']) ?>">
                                <td>#<?= substr(str_replace('ORDER-', '', $order['order_group_id']), 0, 8) ?></td>
                                <td><?= htmlspecialchars($order['customer_name']) ?></td>
                                <td>Rp <?= number_format($order['grand_total'], 0, ',', '.') ?></td>
                                <td><?= date('d M Y, H:i', strtotime($order['delivery_schedule'])) ?></td>
                                <td class="status-cell">
                                    <span class="badge <?= getStatusBadgeClass($order['status']) ?>">
                                        <?= ucfirst(htmlspecialchars($order['status'])) ?>
                                    </span>
                                </td>
                                <td class="action-cell">
                                    <?php if ($order['status'] == 'pending'): ?>
                                        <button class="btn btn-sm btn-primary btn-update-status" data-group-id="<?= htmlspecialchars($order['order_group_id']) ?>" data-new-status="dikirim">
                                            <i class="bi bi-send"></i> Proses & Kirim
                                        </button>
                                        <button class="btn btn-sm btn-outline-danger btn-update-status" data-group-id="<?= htmlspecialchars($order['order_group_id']) ?>" data-new-status="dibatalkan">
                                            <i class="bi bi-x-circle"></i>
                                        </button>
                                    <?php elseif ($order['status'] == 'dikirim'): ?>
                                        <button class="btn btn-sm btn-danger btn-update-status" data-group-id="<?= htmlspecialchars($order['order_group_id']) ?>" data-new-status="dibatalkan">
                                            Batalkan
                                        </button>
                                    <?php else: ?>
                                        <span class="text-muted fst-italic">Tidak ada aksi</span>
                                    <?php endif; ?>
                                </td>
                            </tr>
                        <?php endwhile; ?>
                    <?php else: ?>
                        <tr>
                            <td colspan="6" class="text-center">Tidak ada pesanan.</td>
                        </tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
    $(document).ready(function() {
        $('.btn-update-status').on('click', function() {
            const button = $(this);
            const groupId = button.data('group-id');
            const newStatus = button.data('new-status');
            const confirmationMessage = newStatus === 'dibatalkan' ? 'Anda yakin ingin membatalkan pesanan ini?' : 'Proses dan kirim pesanan ini?';

            if (confirm(confirmationMessage)) {
                // Nonaktifkan semua tombol pada baris yang sama
                button.closest('td').find('.btn').prop('disabled', true);
                button.html('<span class="spinner-border spinner-border-sm"></span>');

                $.post('action/update_order_status.php', {
                    group_id: groupId,
                    status: newStatus
                }, function(response) {
                    if (response.status === 'success') {
                        location.reload(); // Reload halaman untuk melihat perubahan
                    } else {
                        alert('Gagal mengubah status: ' + response.message);
                        button.closest('td').find('.btn').prop('disabled', false);
                         button.html(newStatus === 'dibatalkan' ? '<i class="bi bi-x-circle"></i>' : '<i class="bi bi-send"></i> Proses & Kirim');
                    }
                }, 'json').fail(function() {
                     alert('Error: Tidak dapat terhubung ke server.');
                     button.closest('td').find('.btn').prop('disabled', false);
                     button.html(newStatus === 'dibatalkan' ? '<i class="bi bi-x-circle"></i>' : '<i class="bi bi-send"></i> Proses & Kirim');
                });
            }
        });
    });
    </script>
</body>
</html>
