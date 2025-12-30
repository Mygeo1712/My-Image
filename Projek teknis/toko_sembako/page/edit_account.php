<?php
session_start();
require '../backend/dbconnect.php';

if (!isset($_SESSION['user_id'])) {
    http_response_code(403);
    echo "Unauthorized";
    exit;
}

$user_id = $_SESSION['user_id'];

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

$stmt = $conn->prepare("SELECT u.email, up.full_name, up.phone, up.address, up.profile_photo,
                            up.province, up.city, up.subdistrict, up.kelurahan, up.detail_address
                       FROM users u
                       LEFT JOIN user_profile up ON u.id = up.user_id
                       WHERE u.id = ?");
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();
$stmt->close();

$user['full_name'] = $user['full_name'] ?: '';
$user['email'] = $user['email'] ?: '';
$user['phone'] = $user['phone'] ?: '';
$user['address'] = $user['address'] ?: '';
$user['province'] = $user['province'] ?: '';
$user['city'] = $user['city'] ?: '';
$user['subdistrict'] = $user['subdistrict'] ?: '';
$user['kelurahan'] = $user['kelurahan'] ?: '';
$user['detail_address'] = $user['detail_address'] ?: '';
$user['profile_photo'] = $user['profile_photo'] ?: '';

$display_phone = $user['phone'];
if (str_starts_with($display_phone, '0')) {
    $display_phone = substr($display_phone, 1); 
} elseif (str_starts_with($display_phone, '62')) {
    $display_phone = substr($display_phone, 2); 
}


if ($_SERVER["REQUEST_METHOD"] == "POST") {
    
    $full_name = filter_input(INPUT_POST, 'full_name', FILTER_SANITIZE_FULL_SPECIAL_CHARS);
    $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
    $phone_input = filter_input(INPUT_POST, 'phone', FILTER_SANITIZE_NUMBER_INT);
    $detail_address = filter_input(INPUT_POST, 'detail_address', FILTER_SANITIZE_FULL_SPECIAL_CHARS);
    $province = filter_input(INPUT_POST, 'province', FILTER_SANITIZE_FULL_SPECIAL_CHARS);
    $city = filter_input(INPUT_POST, 'city', FILTER_SANITIZE_FULL_SPECIAL_CHARS);
    $subdistrict = filter_input(INPUT_POST, 'subdistrict', FILTER_SANITIZE_FULL_SPECIAL_CHARS);
    $kelurahan = filter_input(INPUT_POST, 'kelurahan', FILTER_SANITIZE_FULL_SPECIAL_CHARS);
    $phone = '62' . $phone_input;

    $errors = [];

    if (empty($full_name)) {
        $errors[] = "Nama lengkap wajib diisi.";
    }
    if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $errors[] = "Email tidak valid.";
    }
    if (!preg_match('/^62[0-9]{8,11}$/', $phone)) {
        $errors[] = "Nomor telepon tidak valid. Harus diawali '62' dan 8-11 digit setelahnya.";
    }
    if (empty($detail_address)) {
        $errors[] = "Alamat detail wajib diisi.";
    }
    if (empty($province) || $province !== $jakarta_data['province']) {
        $errors[] = "Provinsi tidak valid.";
    }
    if (empty($city) || !isset($jakarta_data['cities'][$city])) {
        $errors[] = "Kota tidak valid.";
    }
    if (empty($subdistrict) || !isset($jakarta_data['cities'][$city][$subdistrict])) {
        $errors[] = "Kecamatan tidak valid.";
    }
    if (empty($kelurahan) || !in_array($kelurahan, $jakarta_data['cities'][$city][$subdistrict])) {
        $errors[] = "Kelurahan tidak valid.";
    }

    $profile_photo_path = $user['profile_photo']; 
    if (isset($_FILES['profile_photo']) && $_FILES['profile_photo']['error'] === UPLOAD_ERR_OK) {
        $target_dir = $_SERVER['DOCUMENT_ROOT'] . "/toko_sembako/uploads/profile_photos/";
        if (!is_dir($target_dir)) {
            mkdir($target_dir, 0777, true); 
        }

        $imageFileType = strtolower(pathinfo($_FILES["profile_photo"]["name"], PATHINFO_EXTENSION));
        $unique_filename = uniqid('profile_') . '.' . $imageFileType;
        $target_file = $target_dir . $unique_filename;

        $allowed_extensions = array("jpg", "jpeg", "png", "gif");
        if (!in_array($imageFileType, $allowed_extensions)) {
            $errors[] = "Maaf, hanya file JPG, JPEG, PNG & GIF yang diizinkan.";
        }
        if ($_FILES["profile_photo"]["size"] > 5 * 1024 * 1024) { 
            $errors[] = "Maaf, ukuran file terlalu besar (maks 5MB).";
        }

        if (empty($errors)) {
            if (move_uploaded_file($_FILES["profile_photo"]["tmp_name"], $target_file)) {
                $profile_photo_path = "/toko_sembako/uploads/profile_photos/" . $unique_filename; 
            } else {
                $errors[] = "Maaf, terjadi kesalahan saat mengunggah foto Anda.";
            }
        }
    }

    if (empty($errors)) {
        $conn->begin_transaction();
        try {
            // Update email in users table
            $stmt_users = $conn->prepare("UPDATE users SET email = ? WHERE id = ?");
            $stmt_users->bind_param("si", $email, $user_id);
            $stmt_users->execute();
            $stmt_users->close();

            // Format the full address for the 'address' column if needed
            $formatted_full_address = htmlspecialchars($detail_address);
            if (!empty($kelurahan)) $formatted_full_address .= ($formatted_full_address ? ', ' : '') . 'Kel. ' . htmlspecialchars($kelurahan);
            if (!empty($subdistrict)) $formatted_full_address .= ($formatted_full_address ? ', ' : '') . 'Kec. ' . htmlspecialchars($subdistrict);
            if (!empty($city)) $formatted_full_address .= ($formatted_full_address ? ', ' : '') . 'Kota ' . htmlspecialchars($city);
            if (!empty($province)) $formatted_full_address .= ($formatted_full_address ? ', ' : '') . 'Prov. ' . htmlspecialchars($province);


            // Check if user_profile exists or insert/update
            $stmt_check = $conn->prepare("SELECT user_id FROM user_profile WHERE user_id = ?");
            $stmt_check->bind_param("i", $user_id);
            $stmt_check->execute();
            $exists = $stmt_check->get_result()->num_rows > 0;
            $stmt_check->close();

            if ($exists) {
                $stmt_profile = $conn->prepare("UPDATE user_profile SET full_name=?, phone=?, address=?, profile_photo=?, province=?, city=?, subdistrict=?, kelurahan=?, detail_address=? WHERE user_id=?");
                $stmt_profile->bind_param("sssssssssi", $full_name, $phone, $formatted_full_address, $profile_photo_path, $province, $city, $subdistrict, $kelurahan, $detail_address, $user_id);
            } else {
                $stmt_profile = $conn->prepare("INSERT INTO user_profile (user_id, full_name, phone, address, profile_photo, province, city, subdistrict, kelurahan, detail_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                $stmt_profile->bind_param("isssssssss", $user_id, $full_name, $phone, $formatted_full_address, $profile_photo_path, $province, $city, $subdistrict, $kelurahan, $detail_address);
            }
            $stmt_profile->execute();
            $stmt_profile->close();

            $conn->commit();
            echo "updated"; // Signal success to AJAX
            exit;
        } catch (mysqli_sql_exception $e) {
            $conn->rollback();
            error_log("Profile update failed: " . $e->getMessage());
            echo "Error: Terjadi kesalahan database saat memperbarui profil.";
            exit;
        }
    } else {
        echo '<div class="alert alert-danger" role="alert">';
        foreach ($errors as $error) {
            echo '<p class="mb-0">' . htmlspecialchars($error) . '</p>';
        }
        echo '</div>';
    }
}
?>

<form id="edit-profile-form" method="POST" enctype="multipart/form-data">
    <div class="mb-3">
        <label for="full_name" class="form-label">Nama Lengkap</label>
        <input id="full_name" type="text" name="full_name" class="form-control" value="<?= htmlspecialchars($user['full_name']) ?>" required>
    </div>

    <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input id="email" type="email" name="email" class="form-control" value="<?= htmlspecialchars($user['email']) ?>" required>
    </div>

    <div class="mb-3">
        <label for="phone" class="form-label">Telepon</label>
        <div class="input-group">
            <span class="input-group-text">+62</span>
            <input id="phone" type="tel" name="phone" class="form-control" placeholder="8XXXXXXXXX"
                   value="<?= htmlspecialchars($display_phone) ?>" pattern="[0-9]{8,11}"
                   title="Masukkan 8-11 digit angka setelah +62 (contoh: 81234567890)" required>
        </div>
    </div>

    <hr class="my-4">

    <div class="mb-3">
        <label for="province" class="form-label">Provinsi</label>
        <select class="form-select" id="province" name="province" required>
            <option value="">Pilih Provinsi</option>
            <option value="<?= htmlspecialchars($jakarta_data['province']) ?>"
                <?= $user['province'] === $jakarta_data['province'] ? 'selected' : '' ?>>
                <?= htmlspecialchars($jakarta_data['province']) ?>
            </option>
        </select>
    </div>

    <div class="mb-3">
        <label for="city" class="form-label">Kota/Kabupaten</label>
        <select class="form-select" id="city" name="city" required>
            <option value="">Pilih Kota</option>
            <?php
            if ($user['province'] === $jakarta_data['province']) {
                foreach ($jakarta_data['cities'] as $city_name => $subdistricts) {
                    echo '<option value="' . htmlspecialchars($city_name) . '" ' . ($user['city'] === $city_name ? 'selected' : '') . '>' . htmlspecialchars($city_name) . '</option>';
                }
            }
            ?>
        </select>
    </div>

    <div class="mb-3">
        <label for="subdistrict" class="form-label">Kecamatan</label>
        <select class="form-select" id="subdistrict" name="subdistrict" required>
            <option value="">Pilih Kecamatan</option>
            <?php
            if (!empty($user['city']) && isset($jakarta_data['cities'][$user['city']])) {
                foreach ($jakarta_data['cities'][$user['city']] as $subd_name => $kelurahan_list) {
                    echo '<option value="' . htmlspecialchars($subd_name) . '" ' . ($user['subdistrict'] === $subd_name ? 'selected' : '') . '>' . htmlspecialchars($subd_name) . '</option>';
                }
            }
            ?>
        </select>
    </div>

    <div class="mb-3">
        <label for="kelurahan" class="form-label">Kelurahan</label>
        <select class="form-select" id="kelurahan" name="kelurahan" required>
            <option value="">Pilih Kelurahan</option>
            <?php
            if (!empty($user['city']) && !empty($user['subdistrict']) && isset($jakarta_data['cities'][$user['city']][$user['subdistrict']])) {
                foreach ($jakarta_data['cities'][$user['city']][$user['subdistrict']] as $kel) {
                    echo '<option value="' . htmlspecialchars($kel) . '" ' . ($user['kelurahan'] === $kel ? 'selected' : '') . '>' . htmlspecialchars($kel) . '</option>';
                }
            }
            ?>
        </select>
    </div>
    
    <div class="mb-3">
        <label for="detail_address" class="form-label">Detail Alamat (Nama Jalan, Nomor Rumah, RT/RW, Patokan)</label>
        <textarea class="form-control" id="detail_address" name="detail_address" rows="3" required><?= htmlspecialchars($user['detail_address']) ?></textarea>
    </div>

    <div class="mb-3">
        <label for="profile_photo" class="form-label">Foto Profil</label>
        <input type="file" name="profile_photo" class="form-control" accept="image/*">
        <?php if (!empty($user['profile_photo'])): ?>
            <img id="profile-photo-preview" src="<?= htmlspecialchars($user['profile_photo']) ?>" alt="Preview" class="img-thumbnail mt-2" style="max-width:120px; border-radius: 8px;">
        <?php endif; ?>
    </div>
    
    <div class="text-end">
        <button type="submit" class="btn btn-primary">Simpan</button>
    </div>
</form>