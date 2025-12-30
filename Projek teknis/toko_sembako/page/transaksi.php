<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

$user_id = $_SESSION['user_id'];

$query = "
    SELECT 
        o.order_group_id,
        SUM(o.total_price) AS grand_total,
        o.payment_method,
        o.delivery_schedule,
        o.status,
        o.order_date,
        GROUP_CONCAT(p.name SEPARATOR '||') AS product_names,
        GROUP_CONCAT(p.image SEPARATOR '||') AS product_images,
        GROUP_CONCAT(o.quantity SEPARATOR '||') AS quantities
    FROM orders o
    JOIN products p ON o.product_id = p.id
    WHERE o.user_id = ?
    GROUP BY o.order_group_id, o.delivery_schedule, o.status, o.order_date, o.payment_method
    ORDER BY o.order_date DESC
";

$stmt = $conn->prepare($query);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

function getStatusBadge($status) {
    switch (strtolower($status)) {
        case 'pending': return 'bg-warning text-dark';
        case 'dikirim': return 'bg-info text-dark';
        case 'selesai': return 'bg-success';
        case 'dibatalkan': return 'bg-danger';
        default: return 'bg-secondary';
    }
}
?>

<div class="container mt-5 mb-5">
    <h2 class="mb-4">Riwayat Transaksi Anda</h2>

    <?php if ($result->num_rows > 0): ?>
        <?php while ($order_group = $result->fetch_assoc()): ?>
            <?php
                $product_names = explode('||', $order_group['product_names']);
                $product_images = explode('||', $order_group['product_images']);
                $quantities = explode('||', $order_group['quantities']);

                $can_edit = false;
                $is_pending = strtolower($order_group['status']) === 'pending';
                $order_time = new DateTime($order_group['order_date']);
                $current_time = new DateTime();
                $time_diff_seconds = $current_time->getTimestamp() - $order_time->getTimestamp();

                if ($is_pending && $time_diff_seconds < 3600) {
                    $can_edit = true;
                }
            ?>
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-light d-flex flex-wrap justify-content-between align-items-center">
                    <div>
                        <strong>Order ID: #<?= substr(str_replace('ORDER-', '', $order_group['order_group_id']), 0, 8) ?></strong>
                        <span class="text-muted ms-2">| Tanggal Pesan: <?= date('d M Y, H:i', strtotime($order_group['order_date'])) ?></span>
                    </div>
                    <span class="badge <?= getStatusBadge($order_group['status']) ?> fs-6 mt-2 mt-md-0">
                        <?= ucfirst(htmlspecialchars($order_group['status'])) ?>
                    </span>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-lg-8">
                            <?php for ($i = 0; $i < count($product_names); $i++): ?>
                            <div class="d-flex align-items-center mb-3">
                                <img src="/toko_sembako/uploads/<?= htmlspecialchars($product_images[$i]) ?>" 
                                    width="60" class="me-3 rounded border">
                                <div>
                                    <h6 class="mb-1"><?= htmlspecialchars($product_names[$i]) ?></h6>
                                    <p class="mb-0 text-muted">Jumlah: <?= htmlspecialchars($quantities[$i]) ?></p>
                                </div>
                            </div>
                            <?php endfor; ?>
                        </div>

                        <div class="col-lg-4 border-lg-start mt-3 mt-lg-0 pt-3 pt-lg-0">
                            <div class="mb-3">
                                <h6 class="text-muted">Jadwal Pengiriman</h6>
                                <p class="mb-0"><i class="bi bi-calendar-event"></i> <?= date('l, d M Y', strtotime($order_group['delivery_schedule'])) ?></p>
                                <p class="mb-0"><i class="bi bi-clock"></i> Pukul <?= date('H:i', strtotime($order_group['delivery_schedule'])) ?> WIB</p>
                            </div>
                            <hr>
                            <div>
                                <h6 class="text-muted">Total Pembayaran (<?= htmlspecialchars($order_group['payment_method']) ?>)</h6>
                                <h4 class="text-success fw-bold">Rp <?= number_format($order_group['grand_total'], 0, ',', '.') ?></h4>
                            </div>
                        </div>
                    </div>
                </div>
                <?php if ($can_edit): ?>
                <div class="card-footer bg-light text-end">
                    <a href="/toko_sembako/edit-order/<?= htmlspecialchars($order_group['order_group_id']) ?>" class="btn btn-warning btn-sm">
                        <i class="bi bi-pencil-square"></i> Edit Jadwal Pengiriman
                    </a>
                </div>
                <?php endif; ?>
            </div>
        <?php endwhile; ?>
    <?php else: ?>
        <div class="alert alert-info text-center">
            <i class="bi bi-cart-x fs-1"></i>
            <h4 class="alert-heading mt-2">Anda Belum Memiliki Transaksi</h4>
            <p>Sepertinya Anda belum pernah melakukan pembelian. Ayo mulai berbelanja!</p>
            <a href="/toko_sembako/home" class="btn btn-primary">Mulai Belanja</a>
        </div>
    <?php endif; ?>
</div>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">