<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Instant Shop</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="uploads/Logo3.png" type="image/png">


    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="assets/css/style.css" rel="stylesheet" />
</head>

<body>

    <?php
    include('navbar.php');
    ?>


    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

    <script>
        $(document).ready(function() {
            function fetchNotifications() {
                $.getJSON('/toko_sembako/ajax/fetch_notifications.php', function(data) {
                    console.log("Data diterima:", data); // Untuk debugging

                    if (data && data.status === 'success') {
                        const notifList = $('#notification-list');
                        notifList.empty();

                        if (data.notifications && data.notifications.length > 0) {
                            $.each(data.notifications, function(i, notif) {
                                const isUnreadClass = notif.is_read == 0 ? 'bg-light fw-bold' : 'text-muted';
                                const notifHtml = `
                                <li>
                                    <a class="dropdown-item p-3 ${isUnreadClass}" href="/toko_sembako/transaksi">
                                        <p class="mb-1 small">${notif.message}</p>
                                        <small class="opacity-75">${new Date(notif.created_at).toLocaleString('id-ID', {day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'})}</small>
                                    </a>
                                </li>`;
                                notifList.append(notifHtml);
                            });
                        } else {
                            notifList.append('<li><p class="text-center p-3 text-muted small">Tidak ada notifikasi.</p></li>');
                        }

                        const notifCount = $('#notification-count');
                        if (data.unread_count > 0) {
                            notifCount.text(data.unread_count).show();
                        } else {
                            notifCount.hide();
                        }
                    } else {
                        $('#notification-list').html('<li><p class="text-center p-3 text-danger small">Gagal memuat notifikasi.</p></li>');
                    }
                }).fail(function() {
                    $('#notification-list').html('<li><p class="text-center p-3 text-danger small">Error: Tidak dapat terhubung ke server.</p></li>');
                });
            }

            fetchNotifications();

            $('#notificationDropdown').on('show.bs.dropdown', function() {
                if ($('#notification-count').is(':visible')) {
                    $.getJSON('/toko_sembako/ajax/fetch_notifications.php?action=mark_read', function(data) {
                        if (data.status === 'success') {
                            $('#notification-count').fadeOut('slow');
                            $('#notification-list .bg-light').removeClass('bg-light fw-bold').addClass('text-muted');
                        }
                    });
                }
            });

            setInterval(fetchNotifications, 60000); 
        });
    </script>

</body>

</html>