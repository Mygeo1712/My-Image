<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit;
}

$user_id = $_SESSION['user_id'];

if (!isset($conn)) {
    require_once 'backend/dbconnect.php';
}

$stmt = $conn->prepare("SELECT u.username, u.email, up.full_name, up.phone, up.address, up.profile_photo,
                             up.province, up.city, up.subdistrict, up.kelurahan, up.detail_address
                       FROM users u
                       LEFT JOIN user_profile up ON u.id = up.user_id
                       WHERE u.id = ?");

$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();
$stmt->close();

$profile_alert_message = '';
if (isset($_SESSION['profile_alert'])) {
    $profile_alert_message = $_SESSION['profile_alert'];
    unset($_SESSION['profile_alert']);
}

$user['full_name'] = $user['full_name'] ?: 'Belum diatur';
$user['phone'] = $user['phone'] ?: 'Belum diatur';

$display_phone_formatted = $user['phone'];

if ($display_phone_formatted !== 'Belum diatur' && !empty($display_phone_formatted)) {
    if (str_starts_with($display_phone_formatted, '62')) {
        $display_phone_formatted = substr($display_phone_formatted, 2);
    }
    $display_phone_formatted = '+62 ' . $display_phone_formatted;
}
$user['email'] = $user['email'] ?: 'Belum diatur';
$user['detail_address'] = $user['detail_address'] ?: 'Belum diatur';
$user['kelurahan'] = $user['kelurahan'] ?: 'Belum diatur';
$user['subdistrict'] = $user['subdistrict'] ?: 'Belum diatur';
$user['city'] = $user['city'] ?: 'Belum diatur';
$user['province'] = $user['province'] ?: 'Belum diatur';

$formatted_address = '';
if ($user['detail_address'] != 'Belum diatur') {
    $formatted_address .= htmlspecialchars($user['detail_address']);
}
if ($user['kelurahan'] != 'Belum diatur') {
    $formatted_address .= ($formatted_address ? ', ' : '') . 'Kel. ' . htmlspecialchars($user['kelurahan']);
}
if ($user['subdistrict'] != 'Belum diatur') {
    $formatted_address .= ($formatted_address ? ', ' : '') . 'Kec. ' . htmlspecialchars($user['subdistrict']);
}
if ($user['city'] != 'Belum diatur') {
    $formatted_address .= ($formatted_address ? ', ' : '') . 'Kota ' . htmlspecialchars($user['city']);
}
if ($user['province'] != 'Belum diatur') {
    $formatted_address .= ($formatted_address ? ', ' : '') . 'Prov. ' . htmlspecialchars($user['province']);
}
if (empty($formatted_address)) {
    $formatted_address = 'Alamat belum diatur';
}

$jakarta_data = [
    'province' => 'DKI Jakarta',
    'cities' => [
        'Jakarta Pusat' => [
            'Menteng' => ['Gondangdia', 'Kebon Sirih', 'Menteng', 'Cikini', 'Kebon Melati'],
            'Tanah Abang' => ['Bendungan Hilir', 'Karet Tengsin', 'Kebon Kacang', 'Kampung Bali', 'Petamburan', 'Kebon Melati'],
            'Sawah Besar' => ['Pasar Baru', 'Gunung Sahari Utara', 'Mangga Dua Selatan', 'Karang Anyar', 'Kartini'],
            'Gambir' => ['Gambir', 'Kebon Kelapa', 'Petojo Selatan', 'Duri Pulo', 'Cideng', 'Petojo Utara'],
            'Cempaka Putih' => ['Cempaka Putih Timur', 'Cempaka Putih Barat', 'Rawasari'],
            'Johar Baru' => ['Tanah Tinggi', 'Kampung Rawa', 'Galur', 'Johar Baru'],
            'Kemayoran' => ['Gunung Sahari Selatan', 'Kemayoran', 'Kebon Kosong', 'Harapan Mulya', 'Cempaka Baru', 'Utan Panjang', 'Sumur Batu', 'Serdang'],
            'Senen' => ['Senen', 'Kwitang', 'Kenari', 'Paseban', 'Kramat', 'Bungur']
        ],
        'Jakarta Utara' => [
            'Penjaringan' => ['Penjaringan', 'Pluit', 'Pekojan', 'Jembatan Lima', 'Jembatan Besi', 'Angke', 'Kapuk Muara'],
            'Tanjung Priok' => ['Tanjung Priok', 'Kebon Bawang', 'Sungai Bambu', 'Papanggo', 'Warakas', 'Sunaterto Utara', 'Sunaterto Selatan', 'Lagoa'],
            'Koja' => ['Koja', 'Lagu', 'Tugu Utara', 'Tugu Selatan', 'Rawa Badak Selatan', 'Rawa Badak Utara'],
            'Cilincing' => ['Cilincing', 'Kalibaru', 'Marunda', 'Semper Barat', 'Semper Timur', 'Sukapura', 'Rorotan'],
            'Pademangan' => ['Pademangan Timur', 'Pademangan Barat', 'Ancol'],
            'Kelapa Gading' => ['Kelapa Gading Barat', 'Kelapa Gading Timur', 'Pegangsaan Dua']
        ],
        'Jakarta Barat' => [
            'Cengkareng' => ['Cengkareng Barat', 'Cengkareng Timur', 'Duri Kosambi', 'Kapuk', 'Kedaung Kali Angke', 'Rawa Buaya'],
            'Grogol Petamburan' => ['Grogol', 'Jelambar', 'Jelambar Baru', 'Wijaya Kusuma', 'Tanjung Duren Utara', 'Tanjung Duren Selatan', 'Tomang'],
            'Kalideres' => ['Kalideres', 'Kamal', 'Pegadungan', 'Semanan', 'Tegal Alur'],
            'Kebon Jeruk' => ['Kebon Jeruk', 'Duri Kepa', 'Kedoya Selatan', 'Kedoya Utara', 'Sukabumi Utara', 'Kelapa Dua'],
            'Palmerah' => ['Palmerah', 'Jati Pulo', 'Kota Bambu Utara', 'Kota Bambu Selatan', 'Slipi'],
            'Taman Sari' => ['Pinangsia', 'Glodok', 'Keagungan', 'Krukut', 'Mangga Besar', 'Maphar', 'Tangki', 'Tamansari'],
            'Tambora' => ['Tanah Sereal', 'Tambora', 'Roa Malaka', 'Pekapuran', 'Jembatan Lima', 'Krendang', 'Duri Utara', 'Kalisari', 'Jembatan Besi', 'Angke'],
            'Kembangan' => ['Kembangan Selatan', 'Kembangan Utara', 'Meruya Utara', 'Meruya Selatan', 'Srengseng', 'Joglo']
        ],
        'Jakarta Selatan' => [
            'Cilandak' => ['Cilandak Barat', 'Lebak Bulus', 'Pondok Labu', 'Gandaria Selatan', 'Fatmawati'],
            'Jagakarsa' => ['Jagakarsa', 'Tanjung Barat', 'Lenteng Agung', 'Srengseng Sawah', 'Ciganjur', 'Cipedak'],
            'Kebayoran Baru' => ['Selong', 'Gunung', 'Kramat Pela', 'Gandaria Utara', 'Cipete Utara', 'Pulo', 'Petogogan', 'Rawa Barat', 'Senayan', 'Melawai'],
            'Kebayoran Lama' => ['Grogol Utara', 'Grogol Selatan', 'Cipulir', 'Kebayoran Lama Utara', 'Kebayoran Lama Selatan', 'Pondok Pinang'],
            'Mampang Prapatan' => ['Mampang Prapatan', 'Kuningan Barat', 'Pela Mampang', 'Tegal Parang', 'Bangka'],
            'Pancoran' => ['Pancoran', 'Kalibata', 'Rawa Jati', 'Duren Tiga', 'Cikoko', 'Pengadegan'],
            'Pasar Minggu' => ['Pejaten Barat', 'Pejaten Timur', 'Cilandak Timur', 'Jati Padang', 'Kebagusan', 'Ragunan', 'Cipedak', 'Pasar Minggu'],
            'Pesanggrahan' => ['Pesanggrahan', 'Bintaro', 'Ulujami', 'Petukangan Utara', 'Petukangan Selatan'],
            'Setiabudi' => ['Setiabudi', 'Karet', 'Karet Semanggi', 'Karet Kuningan', 'Kuningan', 'Guntur', 'Manggarai', 'Menteng Atas', 'Pasar Manggis'],
            'Tebet' => ['Tebet Barat', 'Tebet Timur', 'Kebon Baru', 'Manggarai', 'Manggarai Selatan', 'Bukit Duri', 'Menteng Dalam']
        ],
        'Jakarta Timur' => [
            'Cakung' => ['Cakung Timur', 'Cakung Barat', 'Jatinegara', 'Penggilingan', 'Pulogebang', 'Ujung Menteng', 'Rawa Terate'],
            'Cipayung' => ['Cipayung', 'Lubang Buaya', 'Ceger', 'Munjul', 'Pondok Ranggon', 'Cilangkap', 'Setu', 'Bambu Apus'],
            'Ciracas' => ['Cibubur', 'Ciracas', 'Kelapa Dua Wetan', 'Susukan', 'Rambutan'],
            'Duren Sawit' => ['Duren Sawit', 'Pondok Bambu', 'Pondok Kelapa', 'Malaka Jaya', 'Malaka Sari', 'Pondok Kopi', 'Klender', 'Cipete'],
            'Jatinegara' => ['Bali Mester', 'Kampung Melayu', 'Bidara Cina', 'Cipinang Cempedak', 'Rawa Bunga', 'Cipinang Besar Utara', 'Cipinang Besar Selatan', 'Cipinang Muara'],
            'Kramat Jati' => ['Kramat Jati', 'Batu Ampar', 'Balekambang', 'Dukuh', 'Cawang', 'Cililitan', 'Bungur', 'Makasar'],
            'Makasar' => ['Makasar', 'Pinang Ranti', 'Halim Perdanakusuma', 'Cipinang Melayu', 'Kebon Pala'],
            'Matraman' => ['Pisangan Baru', 'Utan Kayu Selatan', 'Utan Kayu Utara', 'Kayu Manis', 'Pal Meriam', 'Kebon Manggis'],
            'Pasar Rebo' => ['Pekayon', 'Kampung Gedong', 'Cijantung', 'Baru', 'Kalismurni', 'Cililitan'],
            'Pulo Gadung' => ['Pisangan Timur', 'Cipinang', 'Jati', 'Rawamangun', 'Pulo Gadung', 'Kayu Putih', 'Jatinegara Kaum']
        ],
        'Kepulauan Seribu' => [
            'Pulau Panggang' => ['Pulau Panggang', 'Pulau Harapan'],
            'Pulau Kelapa' => ['Pulau Kelapa', 'Pulau Kelapa Dua']
        ]
    ]
];
?>

<div class="container py-4 profile-page-container">
    <?php if ($profile_alert_message): ?>
        <div class="alert alert-warning alert-dismissible fade show text-center" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <?= htmlspecialchars($profile_alert_message) ?>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <?php endif; ?>

    <div class="card shadow-lg border-0 mb-4 profile-header-card">
        <div class="card-body p-4 d-flex align-items-center flex-column flex-md-row text-center text-md-start">
            <div class="profile-photo-wrapper me-md-4 mb-3 mb-md-0">
                <?php if (!empty($user['profile_photo'])): ?>
                    <img src="<?= htmlspecialchars($user['profile_photo']) ?>" alt="Profile Photo" class="profile-photo">
                <?php else: ?>
                    <div class="profile-photo bg-light d-flex align-items-center justify-content-center">
                        <i class="bi bi-person-fill" style="font-size: 5rem; color: #6c757d;"></i>
                    </div>
                <?php endif; ?>
            </div>
            <div class="flex-grow-1">
                <h1 class="mb-0 text-primary fw-bold display-5"><?= htmlspecialchars($user['username']) ?></h1>
                <?php if ($user['full_name'] != 'Belum diatur'): ?>
                    <h4 class="text-muted fw-normal mt-1 mb-0"><?= htmlspecialchars($user['full_name']) ?></h4>
                <?php endif; ?>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-lg-6">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-body p-4">
                    <h5 class="card-title mb-4 text-dark"><i class="bi bi-info-circle me-2 text-primary"></i>Informasi Kontak</h5>
                    <ul class="list-unstyled profile-detail-list">
                        <li>
                            <i class="bi bi-envelope-fill me-3 text-secondary"></i>
                            <div>
                                <strong class="d-block">Email</strong>
                                <span><?= htmlspecialchars($user['email']) ?></span>
                            </div>
                        </li>
                        <li>
                            <i class="bi bi-phone-fill me-3 text-secondary"></i>
                            <div>
                                <strong class="d-block">Telepon</strong>
                                <span><?= htmlspecialchars($display_phone_formatted) ?></span>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="col-lg-6">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-body p-4">
                    <h5 class="card-title mb-4 text-dark"><i class="bi bi-house-door-fill me-2 text-primary"></i>Informasi Alamat</h5>
                    <ul class="list-unstyled profile-detail-list">
                        <li>
                            <i class="bi bi-geo-alt-fill me-3 text-secondary"></i>
                            <div>
                                <strong class="d-block">Alamat Lengkap</strong>
                                <span><?= $formatted_address ?></span>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <div class="text-end mt-4">
        <button id="edit-profile-btn" class="btn btn-primary btn-lg shadow-sm">
            <i class="bi bi-pencil-square me-2"></i>Edit Profile
        </button>
    </div>
</div>

<div class="modal fade" id="editProfileModal" tabindex="-1" aria-labelledby="editProfileModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editProfileModalLabel">Edit Profile</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="editProfileModalBody">
                Loading...
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    const jakartaData = <?= json_encode($jakarta_data) ?>;

    function populateDropdown(selectElement, options, selectedValue = '') {
        const defaultText = 'Pilih ' + selectElement.attr('id').replace('province', 'Provinsi').replace('city', 'Kota').replace('subdistrict', 'Kecamatan').replace('kelurahan', 'Kelurahan');
        selectElement.html('<option value="">' + defaultText + '</option>');

        if (Array.isArray(options)) {
            $.each(options, function(index, value) {
                selectElement.append('<option value="' + value + '"' + (value === selectedValue ? ' selected' : '') + '>' + value + '</option>');
            });
        } else {
            $.each(options, function(key, value) {
                selectElement.append('<option value="' + key + '"' + (key === selectedValue ? ' selected' : '') + '>' + key + '</option>');
            });
        }
    }

    window.initializeAddressDropdowns = function() {
        const currentUserProvince = '<?= htmlspecialchars($user["province"]) ?>';
        const currentUserCity = '<?= htmlspecialchars($user["city"]) ?>';
        const currentUserSubdistrict = '<?= htmlspecialchars($user["subdistrict"]) ?>';
        const currentUserKelurahan = '<?= htmlspecialchars($user["kelurahan"]) ?>';
        
        console.log("initializeAddressDropdowns called (inside modal context)");
        console.log("Initial User Data for dropdowns:", {
            province: currentUserProvince,
            city: currentUserCity,
            subdistrict: currentUserSubdistrict,
            kelurahan: currentUserKelurahan
        });
        console.log("Jakarta Data used:", jakartaData);

        $('#editProfileModalBody').off('change', '#city').on('change', '#city', function() {
            const selectedCity = $(this).val();
            const $subdistrictSelect = $('#subdistrict');
            const $kelurahanSelect = $('#kelurahan');

            $subdistrictSelect.html('<option value="">Pilih Kecamatan</option>');
            $kelurahanSelect.html('<option value="">Pilih Kelurahan</option>');

            if (selectedCity && jakartaData.cities[selectedCity]) {
                console.log("City changed to:", selectedCity);
                populateDropdown($subdistrictSelect, jakartaData.cities[selectedCity]);
            } else {
                console.warn("City not found in jakartaData.cities:", selectedCity);
            }
        });

        $('#editProfileModalBody').off('change', '#subdistrict').on('change', '#subdistrict', function() {
            const selectedCity = $('#city').val();
            const selectedSubdistrict = $(this).val();
            const $kelurahanSelect = $('#kelurahan');

            $kelurahanSelect.html('<option value="">Pilih Kelurahan</option>');

            if (selectedCity && selectedSubdistrict && jakartaData.cities[selectedCity] && jakartaData.cities[selectedCity][selectedSubdistrict]) {
                console.log("Subdistrict changed to:", selectedSubdistrict);
                populateDropdown($kelurahanSelect, jakartaData.cities[selectedCity][selectedSubdistrict]);
            } else {
                console.warn("Subdistrict or City not found for Kelurahan population:", selectedCity, selectedSubdistrict);
            }
        });

        if (currentUserCity && jakartaData.cities) {
            console.log("Populating City dropdown with:", currentUserCity);
            populateDropdown($('#city'), jakartaData.cities, currentUserCity);
        }

        if (currentUserCity && currentUserSubdistrict && jakartaData.cities && jakartaData.cities[currentUserCity]) {
            console.log("Populating Subdistrict dropdown with:", currentUserSubdistrict);
            populateDropdown($('#subdistrict'), jakartaData.cities[currentUserCity], currentUserSubdistrict);
        }

        if (currentUserCity && currentUserSubdistrict && currentUserKelurahan && jakartaData.cities && jakartaData.cities[currentUserCity] && jakartaData.cities[currentUserCity][currentUserSubdistrict]) {
            console.log("Populating Kelurahan dropdown with:", currentUserKelurahan);
            populateDropdown($('#kelurahan'), jakartaData.cities[currentUserCity][currentUserSubdistrict], currentUserKelurahan);
        }
    };

    $(document).ready(function() {
        var editProfileModal = new bootstrap.Modal(document.getElementById('editProfileModal'));

        $('#edit-profile-btn').click(function() {
            $('#editProfileModalBody').html('Loading...');
            editProfileModal.show();

            $.ajax({
                url: '/toko_sembako/page/edit_account.php',
                method: 'GET',
                success: function(data) {
                    $('#editProfileModalBody').html(data);
                    initializeAddressDropdowns();
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.error("AJAX Error loading form:", textStatus, errorThrown, jqXHR.responseText);
                    $('#editProfileModalBody').html('<p class="text-danger">Error loading form. Check console for details.</p>');
                }
            });
        });

        $(document).on('submit', '#edit-profile-form', function(e) {
            e.preventDefault();

            var formData = new FormData(this);

            $.ajax({
                url: '/toko_sembako/page/edit_account.php',
                method: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    if (response.trim() === 'updated') {
                        alert('Profile updated successfully!');
                        editProfileModal.hide();
                        location.reload();
                    } else {
                        $('#editProfileModalBody').html(response);
                        initializeAddressDropdowns();
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.error("AJAX Error submitting form:", textStatus, errorThrown, jqXHR.responseText);
                    alert('Error submitting form. Check console for details.');
                }
            });
        });

        $(document).on('change', '#profile_photo', function(e) {
            const file = e.target.files[0];
            if (file && file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    let preview = document.querySelector('#profile-photo-preview');

                    if (!preview) {
                        preview = document.createElement('img');
                        preview.id = 'profile-photo-preview';
                        preview.className = 'img-thumbnail mt-2';
                        preview.style.maxWidth = '120px';
                        preview.style.borderRadius = '8px';
                        e.target.parentNode.appendChild(preview);
                    }
                    preview.src = event.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    });
</script>

<style>
    .profile-page-container {
        max-width: 900px;
        margin-top: 2rem !important;
        margin-bottom: 2rem !important;
    }

    .profile-header-card {
        border-radius: 1rem;
        background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
        position: relative;
        overflow: hidden;
    }
    .profile-header-card::before {
        content: '';
        position: absolute;
        top: -50px;
        left: -50px;
        width: 150px;
        height: 150px;
        background-color: rgba(0, 123, 255, 0.05);
        border-radius: 50%;
        filter: blur(30px);
        z-index: 0;
    }

    .profile-photo-wrapper {
        width: 150px;
        height: 150px;
        border-radius: 50%;
        overflow: hidden;
        border: 4px solid #007bff;
        box-shadow: 0 0 0 8px rgba(0, 123, 255, 0.1);
        flex-shrink: 0;
        background-color: #f8f9fa;
    }

    .profile-photo {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
    }

    .card.h-100 {
        border-radius: 0.75rem;
        transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
    }
    .card.h-100:hover {
        transform: translateY(-3px);
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
    }

    .profile-detail-list {
        padding-left: 0;
    }
    .profile-detail-list li {
        display: flex;
        align-items: center;
        margin-bottom: 1rem;
        font-size: 1.05rem;
    }
    .profile-detail-list li:last-child {
        margin-bottom: 0;
    }
    .profile-detail-list i {
        font-size: 1.5rem;
        margin-right: 1rem;
        color: #6c757d;
        min-width: 30px;
        text-align: center;
    }
    .profile-detail-list strong {
        color: #343a40;
        font-weight: 600;
    }
    .profile-detail-list span {
        color: #495057;
        word-wrap: break-word;
    }

    .btn-lg {
        padding: 0.75rem 2rem;
        font-size: 1.1rem;
        border-radius: 0.5rem;
        transition: background-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
    }
    .btn-primary:hover {
        transform: translateY(-2px);
        box-shadow: 0 0.25rem 0.5rem rgba(0, 123, 255, 0.3) !important;
    }

    @media (max-width: 767.98px) {
        .profile-header-card .card-body {
            flex-direction: column;
            text-align: center;
        }
        .profile-photo-wrapper {
            margin-right: 0 !important;
            margin-bottom: 1.5rem !important;
        }
    }
</style>    