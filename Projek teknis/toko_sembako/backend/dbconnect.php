<?php
// --- Konfigurasi Koneksi Database ---
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "instant_shop";

// Membuat koneksi
$conn = new mysqli($servername, $username, $password, $dbname);

// Cek koneksi
if ($conn->connect_error) {
    die("Koneksi gagal: " . $conn->connect_error);
}

// -----------------------------------------------------------------

/**
 * --- FUNGSI UNTUK MEMBUAT SLUG URL ---
 * Mengubah string teks (seperti nama produk) menjadi format yang ramah URL.
 * Contoh: "Minyak Goreng Sania 2L" menjadi "minyak-goreng-sania-2l".
 *
 * @param string $text Teks yang akan diubah.
 * @return string Slug yang sudah diformat.
 */
function createSlug($text) {
    // 1. Ubah semua karakter menjadi huruf kecil
    $text = strtolower($text);

    // 2. Ganti semua karakter yang bukan huruf atau angka dengan tanda hubung (-)
    $text = preg_replace('~[^\pL\d]+~u', '-', $text);

    // 3. Hapus tanda hubung yang mungkin ada di awal atau akhir string
    $text = trim($text, '-');

    // 4. Ubah karakter beraksen (seperti á, ñ, ç) menjadi versi ASCII-nya (a, n, c)
    $text = iconv('utf-8', 'us-ascii//TRANSLIT', $text);

    // 5. Hapus karakter yang tidak valid setelah transliterasi
    $text = preg_replace('~[^-\w]+~', '', $text);

    // 6. Jika hasilnya kosong (misalnya karena input hanya simbol), buat fallback slug
    if (empty($text)) {
        return 'n-a-' . time();
    }

    return $text;
}

/**
 * --- FUNGSI UNTUK MEMERIKSA KELENGKAPAN PROFIL PENGGUNA ---
 * Memeriksa apakah data profil pengguna (full_name, phone, address, dll)
 * sudah terisi lengkap.
 *
 * @param mysqli $conn Objek koneksi database.
 * @param int $user_id ID pengguna yang akan diperiksa.
 * @return bool True jika profil lengkap, false jika tidak.
 */
function isProfileComplete($conn, $user_id) {
    $stmt = $conn->prepare("SELECT full_name, phone, province, city, subdistrict, kelurahan, detail_address FROM user_profile WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $profile = $result->fetch_assoc();
    $stmt->close();

    if (!$profile) {
        // Profil belum ada di tabel user_profile, berarti belum lengkap
        return false;
    }

    // Cek setiap kolom wajib. Anda bisa menyesuaikan kolom mana yang dianggap 'wajib'.
    // Saya menggunakan empty() karena ini akan mengecek string kosong, 0, null, dll.
    if (empty($profile['full_name']) ||
        empty($profile['phone']) ||
        empty($profile['province']) ||
        empty($profile['city']) ||
        empty($profile['subdistrict']) ||
        empty($profile['kelurahan']) ||
        empty($profile['detail_address'])) {
        return false;
    }

    return true;
}

?>