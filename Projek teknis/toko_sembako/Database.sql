-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 09 Jun 2025 pada 18.46
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `instant_shop`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `admin_logs`
--

CREATE TABLE `admin_logs` (
  `id` int(11) NOT NULL,
  `admin_id` int(11) NOT NULL,
  `activity` text DEFAULT NULL,
  `activity_time` datetime NOT NULL DEFAULT current_timestamp(),
  `activity_type` varchar(50) NOT NULL DEFAULT 'activity',
  `logout_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `admin_logs`
--

INSERT INTO `admin_logs` (`id`, `admin_id`, `activity`, `activity_time`, `activity_type`, `logout_time`) VALUES
(6, 46, 'Admin login', '2025-06-04 05:33:43', 'login', '2025-06-04 05:34:10'),
(7, 44, 'Admin login', '2025-06-04 05:36:38', 'login', '2025-06-04 05:36:40'),
(8, 46, 'Admin login', '2025-06-04 05:36:53', 'login', NULL),
(9, 46, 'Menambah admin baru: mio', '2025-06-04 05:38:28', 'add_admin', NULL),
(10, 46, 'Menghapus user: lolo (ID: 4)', '2025-06-04 06:10:28', 'delete_user', '2025-06-04 06:24:01'),
(11, 46, 'Admin login', '2025-06-04 06:24:10', 'login', NULL),
(12, 44, 'Admin login', '2025-06-04 19:32:19', 'login', '2025-06-04 19:33:04'),
(13, 44, 'Admin login', '2025-06-04 19:48:37', 'login', '2025-06-04 19:56:13'),
(14, 44, 'Admin login', '2025-06-04 20:11:36', 'login', NULL),
(15, 44, 'Menghapus produk: Beras (5kg)', '2025-06-04 20:32:55', 'delete_product', NULL),
(16, 44, 'Menghapus produk: Bimoli 2L', '2025-06-04 20:33:01', 'delete_product', NULL),
(17, 44, 'Menghapus produk: Susu murah sehat ', '2025-06-04 20:34:31', 'delete_product', NULL),
(18, 44, 'Menghapus produk: Apel', '2025-06-04 20:35:50', 'delete_product', NULL),
(19, 44, 'Menambah produk: Sambal Asli ABC', '2025-06-04 20:42:31', 'add_product', NULL),
(20, 44, 'Menambah produk: Kecap bango ', '2025-06-04 20:46:38', 'add_product', NULL),
(21, 44, 'Menambah produk: Lemonilo-Kaldu Pelezat Ayam ', '2025-06-04 20:50:29', 'add_product', '2025-06-04 21:33:37'),
(22, 44, 'Admin login', '2025-06-05 00:00:17', 'login', NULL),
(23, 44, 'Menghapus user: mimi1 (ID: 49)', '2025-06-05 00:01:17', 'delete_user', NULL),
(24, 44, 'Menghapus user: mimi (ID: 48)', '2025-06-05 00:01:19', 'delete_user', '2025-06-05 00:04:29'),
(25, 44, 'Admin login', '2025-06-05 14:42:10', 'login', '2025-06-05 16:03:49'),
(26, 44, 'Admin login', '2025-06-05 16:03:58', 'login', '2025-06-05 16:44:02'),
(27, 44, 'Admin login', '2025-06-05 16:44:10', 'login', NULL),
(28, 44, 'Menambah produk: Tak tau', '2025-06-05 16:44:31', 'add_product', '2025-06-05 23:57:35'),
(29, 44, 'Admin login', '2025-06-05 23:58:58', 'login', NULL),
(30, 44, 'Admin login', '2025-06-06 02:54:19', 'login', NULL),
(31, 44, 'Admin login', '2025-06-08 22:17:56', 'login', NULL),
(32, 44, 'Menambah produk: kaleng', '2025-06-08 22:26:00', 'add_product', NULL),
(33, 44, 'Admin login', '2025-06-09 13:29:33', 'login', NULL),
(34, 44, 'Menambah admin baru: admin1', '2025-06-09 13:30:49', 'add_admin', '2025-06-09 13:31:08'),
(35, 57, 'Admin login', '2025-06-09 13:31:17', 'login', NULL),
(36, 57, 'Mengubah stok produk \'Beras Maknyuss\' (ID: 5) menjadi 28', '2025-06-09 13:38:28', 'update_stock', NULL),
(37, 57, 'Logout dari sistem', '2025-06-09 13:41:47', 'logout', '2025-06-09 13:41:47'),
(38, 44, 'Admin login', '2025-06-09 13:41:54', 'login', NULL),
(39, 44, 'Logout dari sistem', '2025-06-09 14:24:41', 'logout', '2025-06-09 14:24:41'),
(40, 57, 'Admin login', '2025-06-09 15:22:32', 'login', NULL),
(41, 57, 'Menambah produk: suka suka sekali sangat bagus', '2025-06-09 15:23:34', 'add_product', NULL),
(42, 57, 'Menambah produk: Sambal lado pedas anti angin', '2025-06-09 15:26:55', 'add_product', NULL),
(43, 57, 'Logout dari sistem', '2025-06-09 15:41:07', 'logout', '2025-06-09 15:41:07'),
(44, 57, 'Admin login', '2025-06-09 16:49:56', 'login', NULL),
(45, 57, 'Logout dari sistem', '2025-06-09 17:00:02', 'logout', '2025-06-09 17:00:02'),
(46, 44, 'Admin login', '2025-06-09 17:07:51', 'login', NULL),
(47, 44, 'Logout dari sistem', '2025-06-09 17:33:47', 'logout', '2025-06-09 17:33:47'),
(48, 44, 'Logout dari sistem', '2025-06-09 17:48:56', 'logout', '2025-06-09 17:48:56'),
(49, 44, 'Logout dari sistem', '2025-06-09 17:54:51', 'logout', '2025-06-09 17:54:51'),
(50, 44, 'Logout dari sistem', '2025-06-09 18:12:56', 'logout', '2025-06-09 18:12:56'),
(51, 44, 'Logout dari sistem', '2025-06-09 19:44:31', 'logout', '2025-06-09 19:44:31'),
(52, 44, 'Menambah produk: Fresh Milk', '2025-06-09 22:05:27', 'add_product', NULL),
(53, 44, 'Menghapus produk: Tak tau', '2025-06-09 22:11:53', 'delete_product', NULL),
(54, 44, 'Menambah produk: 1', '2025-06-09 22:12:50', 'add_product', NULL),
(55, 44, 'Menambah produk: 2', '2025-06-09 22:13:11', 'add_product', NULL),
(56, 44, 'Menambah produk: 3', '2025-06-09 22:13:36', 'add_product', NULL),
(57, 44, 'Menambah produk: 4', '2025-06-09 22:14:25', 'add_product', NULL),
(58, 44, 'Menambah produk: 5', '2025-06-09 22:15:07', 'add_product', NULL),
(59, 44, 'Menambah produk: Kecap Bango 2', '2025-06-09 22:16:06', 'add_product', NULL),
(60, 44, 'Menambah produk: Big Mac – Ikonik, Ganda & Penuh Rasa', '2025-06-09 22:18:00', 'add_product', NULL),
(61, 44, 'Menambah produk: Bimoli special', '2025-06-09 22:31:37', 'add_product', NULL),
(62, 44, 'Menambah produk: Ultra Aroma Extra Pedas', '2025-06-09 22:32:54', 'add_product', NULL),
(63, 44, 'Menambah produk: Barilla Saus Pasta Ricotta – Kelembutan Italia dalam Setiap Gigitan', '2025-06-09 22:34:02', 'add_product', NULL),
(64, 44, 'Menambah produk: Nestlé Dancow – Nutrisi Lengkap untuk Generasi Sehat Indonesia', '2025-06-09 22:35:08', 'add_product', NULL),
(65, 44, 'Menambah produk: Bakso Daging Sapi – Kenikmatan Klasik Penuh Cita Rasa', '2025-06-09 22:37:34', 'add_product', NULL),
(66, 44, 'Menambah produk: Desaku Kunyit Bubuk – Bumbu Dapur Andalan untuk Warna dan Aroma Khas', '2025-06-09 22:38:33', 'add_product', NULL),
(67, 44, 'Menambah produk: Minyak wijen', '2025-06-09 22:40:16', 'add_product', NULL),
(68, 44, 'Menambah produk: Royco Bumbu Kuning Serbaguna – Praktisnya Bumbu Khas Indonesia', '2025-06-09 22:43:04', 'add_product', NULL),
(69, 44, 'Menambah produk: Paprika powder', '2025-06-09 22:44:40', 'add_product', NULL),
(70, 44, 'Logout dari sistem', '2025-06-09 23:18:43', 'logout', '2025-06-09 23:18:43');

-- --------------------------------------------------------

--
-- Struktur dari tabel `cart`
--

CREATE TABLE `cart` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `cart`
--

INSERT INTO `cart` (`id`, `user_id`, `product_id`, `quantity`, `created_at`, `updated_at`) VALUES
(9, 42, 7, 4, '2025-06-03 17:39:42', '2025-06-03 17:39:42');

-- --------------------------------------------------------

--
-- Struktur dari tabel `contact_info`
--

CREATE TABLE `contact_info` (
  `id` int(11) NOT NULL,
  `address` text DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `facebook_url` varchar(255) DEFAULT NULL,
  `instagram_url` varchar(255) DEFAULT NULL,
  `twitter_url` varchar(255) DEFAULT NULL,
  `whatsapp_number` varchar(255) DEFAULT NULL,
  `Maps_embed` text DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `contact_info`
--

INSERT INTO `contact_info` (`id`, `address`, `phone_number`, `email`, `facebook_url`, `instagram_url`, `twitter_url`, `whatsapp_number`, `Maps_embed`, `updated_at`, `updated_by`) VALUES
(1, 'Krida Wacana Christian University, Jl. Tanjung Duren Raya No.4, RT.12/RW.2, Tj. Duren Utara, Kota Jakarta Barat, Daerah Khusus Ibukota Jakarta', '+6282147193627', 'info@tokosembako.com', 'https://facebook.com/tokosembako', 'https://instagram.com/tokosembako', '', '6282147193627', '<iframe src=\"https://www.google.com/maps/embed?pb=!1m16!1m12!1m3!1d3625.7824907285544!2d106.78535147455584!3d-6.175179193812194!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!2m1!1sukrida%20kampus%201!5e1!3m2!1sen!2sid!4v1749475327310!5m2!1sen!2sid\" width=\"600\" height=\"450\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>', '2025-06-09 13:23:15', 44);

-- --------------------------------------------------------

--
-- Struktur dari tabel `gallery_images`
--

CREATE TABLE `gallery_images` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `image_name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT 'Umum',
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `uploaded_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `gallery_images`
--

INSERT INTO `gallery_images` (`id`, `title`, `description`, `image_name`, `category`, `uploaded_at`, `uploaded_by`) VALUES
(1, 'Panen Sayuran Segar', 'Foto sayuran segar langsung dari petani mitra kami, siap untuk dikirim ke dapur Anda.', '1749486668_download.jpg', 'Produk Segar', '2025-06-09 14:47:06', 1),
(3, 'Proses Pengemasan Higienis', 'Setiap produk dikemas dengan standar kebersihan tinggi untuk menjaga kualitasnya.', '1749486980_download (1).jpg', 'Proses Pengemasan', '2025-06-09 14:47:06', 1),
(5, 'Senyum Pelanggan Kami', 'Salah satu pelanggan setia kami yang bahagia setelah menerima pesanan.', '1749487128_download (2).jpg', 'Testimoni', '2025-06-09 14:47:06', 1),
(6, 'Baksos Ramadan Bersama Warga', 'Kegiatan bakti sosial kami di bulan Ramadan bersama warga sekitar.', '1749487255_download (3).jpg', 'Kegiatan Sosial', '2025-06-09 14:47:06', 1),
(8, 'Diskon Heboh Akhir Bulan', 'Jangan lewatkan promo spesial akhir bulan kami untuk berbagai produk pilihan!', '1749487301_download (4).jpg', 'Promo', '2025-06-09 14:47:06', 1),
(9, 'Beragam Rempah Pilihan', 'Koleksi rempah-rempah pilihan yang akan menyempurnakan setiap masakan Anda.', '1749487389_download (5).jpg', 'Umum', '2025-06-09 14:47:06', 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `order_group_id` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `notifications`
--

INSERT INTO `notifications` (`id`, `user_id`, `order_group_id`, `message`, `is_read`, `created_at`) VALUES
(1, 45, 'ORDER-6846c9c9119061.38821167', 'Pesanan Anda #6846c9c9 telah dikirim dan sedang dalam perjalanan!', 1, '2025-06-09 11:47:36'),
(2, 45, 'ORDER-6846c9c9119061.38821167', 'Pesanan #6846c9c9 telah Anda terima. Terima kasih telah berbelanja!', 1, '2025-06-09 11:51:46'),
(3, 45, 'ORDER-6846cd1fa53f26.33562219', 'Pesanan Anda #6846cd1f telah dikirim dan sedang dalam perjalanan!', 1, '2025-06-09 12:01:52'),
(4, 45, 'ORDER-6846cd1fa53f26.33562219', 'Pesanan #6846cd1f telah Anda terima. Terima kasih telah berbelanja!', 1, '2025-06-09 12:02:21'),
(5, 45, 'ORDER-6846ce0ee56737.49620948', 'Pesanan Anda #6846ce0e telah dikirim dan sedang dalam perjalanan!', 1, '2025-06-09 12:05:45'),
(6, 45, 'ORDER-6846cf0d3ebbf1.68434061', 'Pesanan Anda #6846cf0d telah dikirim dan sedang dalam perjalanan!', 1, '2025-06-09 12:09:57'),
(7, 45, 'ORDER-6846d0d172e9d7.01072849', 'Pesanan Anda #6846d0d1 telah dikirim dan sedang dalam perjalanan!', 1, '2025-06-09 12:17:30'),
(8, 45, 'ORDER-6846d0d172e9d7.01072849', 'Pesanan #6846d0d1 telah Anda terima. Terima kasih telah berbelanja!', 1, '2025-06-09 12:18:09'),
(9, 45, 'ORDER-6846d30aca6a32.12040898', 'Pesanan Anda #6846d30a telah dikirim dan sedang dalam perjalanan!', 1, '2025-06-09 12:27:00');

-- --------------------------------------------------------

--
-- Struktur dari tabel `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `order_group_id` varchar(255) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `courier_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `delivery_schedule` datetime DEFAULT NULL,
  `status` varchar(50) NOT NULL DEFAULT 'pending',
  `proof_of_delivery` varchar(255) DEFAULT NULL,
  `order_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `orders`
--

INSERT INTO `orders` (`id`, `order_group_id`, `user_id`, `courier_id`, `product_id`, `quantity`, `total_price`, `payment_method`, `delivery_schedule`, `status`, `proof_of_delivery`, `order_date`) VALUES
(77, '', 7, NULL, 5, 3, 0.00, '', NULL, 'pending', NULL, '2025-06-03 22:34:06'),
(78, '', 45, NULL, 11, 3, 0.00, '', NULL, 'pending', NULL, '2025-06-04 16:47:26'),
(80, '', 45, NULL, 12, 6, 0.00, '', NULL, 'pending', NULL, '2025-06-05 17:11:56'),
(81, '', 45, NULL, 13, 5, 0.00, '', NULL, 'pending', NULL, '2025-06-05 18:08:39'),
(82, '', 45, NULL, 5, 12, 0.00, '', NULL, 'pending', NULL, '2025-06-05 19:55:17'),
(83, '', 44, NULL, 7, 100, 0.00, '', NULL, 'pending', NULL, '2025-06-05 19:56:28'),
(84, '', 44, NULL, 9, 4, 0.00, '', NULL, 'pending', NULL, '2025-06-05 21:55:00'),
(85, '', 44, NULL, 15, 30, 0.00, '', NULL, 'pending', NULL, '2025-06-08 15:41:12'),
(86, '', 44, NULL, 15, 50, 0.00, '', NULL, 'pending', NULL, '2025-06-08 15:44:59'),
(87, '', 44, NULL, 15, 3, 0.00, '', NULL, 'pending', NULL, '2025-06-08 15:49:42'),
(89, '', 44, NULL, 15, 1, 0.00, '', NULL, 'pending', NULL, '2025-06-08 15:57:37'),
(90, '', 44, NULL, 15, 5, 0.00, '', NULL, 'pending', NULL, '2025-06-08 16:00:35'),
(91, '', 44, NULL, 15, 4, 0.00, '', NULL, 'pending', NULL, '2025-06-08 16:09:35'),
(92, '', 44, NULL, 15, 5, 0.00, '', NULL, 'pending', NULL, '2025-06-08 16:09:57'),
(93, '', 44, NULL, 15, 1, 0.00, '', NULL, 'pending', NULL, '2025-06-08 16:21:44'),
(94, '', 44, NULL, 15, 99, 0.00, '', NULL, 'pending', NULL, '2025-06-08 16:22:01'),
(95, '', 44, NULL, 15, 3, 0.00, '', NULL, 'pending', NULL, '2025-06-08 16:22:50'),
(96, '', 44, NULL, 15, 96, 0.00, '', NULL, 'pending', NULL, '2025-06-08 16:48:25'),
(97, '', 45, NULL, 17, 1, 13114.00, 'COD', '2025-06-16 15:00:00', 'pending', NULL, '2025-06-09 08:51:31'),
(98, '', 45, NULL, 16, 4, 488888.00, 'COD', '2025-06-09 18:52:00', 'pending', NULL, '2025-06-09 08:52:44'),
(99, '', 45, NULL, 17, 5, 0.00, '', NULL, 'pending', NULL, '2025-06-09 08:56:30'),
(100, '', 45, NULL, 15, 6, 0.00, '', NULL, 'pending', NULL, '2025-06-09 08:56:30'),
(101, '', 45, NULL, 17, 1, 13114.00, 'COD', '2025-06-12 17:00:00', 'pending', NULL, '2025-06-09 09:08:22'),
(102, '', 45, NULL, 12, 6, 131400.00, 'COD', '2025-06-09 17:11:14', 'pending', NULL, '2025-06-09 09:11:22'),
(103, '', 45, NULL, 16, 4, 488888.00, 'COD', '2025-06-09 17:11:14', 'pending', NULL, '2025-06-09 09:11:22'),
(104, '', 45, NULL, 17, 4, 52456.00, 'COD', '2025-06-09 17:11:14', 'pending', NULL, '2025-06-09 09:11:22'),
(105, '', 45, NULL, 12, 6, 131400.00, 'COD', '2025-06-09 19:00:00', 'pending', NULL, '2025-06-09 09:11:37'),
(106, '', 45, NULL, 16, 4, 488888.00, 'COD', '2025-06-09 19:00:00', 'pending', NULL, '2025-06-09 09:11:37'),
(107, '', 45, NULL, 17, 4, 52456.00, 'COD', '2025-06-09 19:00:00', 'pending', NULL, '2025-06-09 09:11:37'),
(108, '', 45, NULL, 12, 6, 131400.00, 'COD', '2025-07-17 19:00:00', 'pending', NULL, '2025-06-09 09:15:02'),
(109, '', 45, NULL, 16, 4, 488888.00, 'COD', '2025-07-17 19:00:00', 'pending', NULL, '2025-06-09 09:15:02'),
(110, '', 45, NULL, 17, 4, 52456.00, 'COD', '2025-07-17 19:00:00', 'pending', NULL, '2025-06-09 09:15:02'),
(116, 'ORDER-6846af49829410.44994271', 57, NULL, 17, 6, 78684.00, 'COD', '2025-06-09 19:00:00', 'dibatalkan', NULL, '2025-06-09 09:54:17'),
(117, 'ORDER-6846af49829410.44994271', 57, NULL, 16, 5, 611110.00, 'COD', '2025-06-09 19:00:00', 'dibatalkan', NULL, '2025-06-09 09:54:17'),
(118, 'ORDER-6846af49829410.44994271', 57, NULL, 6, 30, 1800000.00, 'COD', '2025-06-09 19:00:00', 'dibatalkan', NULL, '2025-06-09 09:54:17'),
(119, 'ORDER-6846a6c51ee3a7.11642694', 45, 58, 12, 6, 131400.00, 'COD', '2025-06-09 18:00:16', 'selesai', 'proof_ORDER-6846a6c51ee3a7.11642694_1749467005.png', '2025-06-09 09:17:57'),
(120, 'ORDER-6846a6c51ee3a7.11642694', 45, 58, 16, 4, 488888.00, 'COD', '2025-06-09 18:00:16', 'selesai', 'proof_ORDER-6846a6c51ee3a7.11642694_1749467005.png', '2025-06-09 09:17:57'),
(121, 'ORDER-6846a6c51ee3a7.11642694', 45, 58, 17, 4, 52456.00, 'COD', '2025-06-09 18:00:16', 'selesai', 'proof_ORDER-6846a6c51ee3a7.11642694_1749467005.png', '2025-06-09 09:17:57'),
(122, 'ORDER-6846a6c51ee3a7.11642694', 45, 58, 5, 27, 1876500.00, 'COD', '2025-06-09 18:00:16', 'selesai', 'proof_ORDER-6846a6c51ee3a7.11642694_1749467005.png', '2025-06-09 09:17:57'),
(123, 'ORDER-6846b387dfcf94.53920582', 44, 58, 13, 1, 22000.00, 'COD', '2025-06-09 18:12:17', 'selesai', 'proof_ORDER-6846b387dfcf94.53920582_1749467598.png', '2025-06-09 10:12:23'),
(124, 'ORDER-6846c18b16f2d0.99719811', 45, 58, 17, 5, 65570.00, 'COD', '2025-06-10 09:00:00', 'selesai', 'proof_ORDER-6846c18b16f2d0.99719811_1749468111.png', '2025-06-09 11:12:11'),
(125, 'ORDER-6846c18b16f2d0.99719811', 45, 58, 16, 2, 244444.00, 'COD', '2025-06-10 09:00:00', 'selesai', 'proof_ORDER-6846c18b16f2d0.99719811_1749468111.png', '2025-06-09 11:12:11'),
(128, 'ORDER-6846c2b27af265.99798498', 45, 58, 10, 3, 223500.00, 'COD', '2025-06-10 09:00:00', 'selesai', 'proof_ORDER-6846c2b27af265.99798498_1749468218.png', '2025-06-09 11:17:06'),
(129, 'ORDER-6846c2b27af265.99798498', 45, 58, 17, 6, 78684.00, 'COD', '2025-06-10 09:00:00', 'selesai', 'proof_ORDER-6846c2b27af265.99798498_1749468218.png', '2025-06-09 11:17:06'),
(130, 'ORDER-6846c5ba5955d4.35418974', 45, NULL, 16, 4, 488888.00, 'COD', '2025-06-10 13:00:00', 'pending', NULL, '2025-06-09 11:30:02'),
(131, 'ORDER-6846c5ba5955d4.35418974', 45, NULL, 12, 3, 65700.00, 'COD', '2025-06-10 13:00:00', 'pending', NULL, '2025-06-09 11:30:02'),
(132, 'ORDER-6846c5fcbca005.11234685', 45, 58, 17, 2, 26228.00, 'COD', '2025-06-09 19:31:05', 'selesai', 'proof_ORDER-6846c5fcbca005.11234685_1749468716.png', '2025-06-09 11:31:08'),
(133, 'ORDER-6846c67bb09169.76041370', 45, 58, 17, 3, 39342.00, 'COD', '2025-06-09 19:32:30', 'selesai', 'proof_ORDER-6846c67bb09169.76041370_1749468850.png', '2025-06-09 11:33:15'),
(134, 'ORDER-6846c9c9119061.38821167', 45, 58, 17, 4, 52456.00, 'COD', '2025-06-09 19:47:14', 'selesai', 'proof_ORDER-6846c9c9119061.38821167_1749469906.png', '2025-06-09 11:47:21'),
(135, 'ORDER-6846cd0ecf5d50.79747645', 45, NULL, 17, 4, 52456.00, 'COD', '2025-06-19 11:00:00', 'pending', NULL, '2025-06-09 12:01:18'),
(136, 'ORDER-6846cd1fa53f26.33562219', 45, 58, 16, 3, 366666.00, 'COD', '2025-06-09 20:01:31', 'selesai', 'proof_ORDER-6846cd1fa53f26.33562219_1749470541.png', '2025-06-09 12:01:35'),
(137, 'ORDER-6846ce0ee56737.49620948', 45, NULL, 17, 1, 13114.00, 'COD', '2025-06-09 20:05:30', 'dikirim', NULL, '2025-06-09 12:05:34'),
(138, 'ORDER-6846cf0d3ebbf1.68434061', 45, NULL, 17, 1, 13114.00, 'COD', '2025-06-09 20:09:45', 'dikirim', NULL, '2025-06-09 12:09:49'),
(139, 'ORDER-6846d0d172e9d7.01072849', 45, 58, 5, 1, 69500.00, 'COD', '2025-06-09 20:17:12', 'selesai', 'proof_ORDER-6846d0d172e9d7.01072849_1749471489.png', '2025-06-09 12:17:21'),
(140, 'ORDER-6846d30aca6a32.12040898', 45, NULL, 17, 2, 26228.00, 'COD', '2025-06-09 20:26:44', 'dikirim', NULL, '2025-06-09 12:26:50'),
(141, 'ORDER-6846e35483f2d3.68286020', 59, NULL, 17, 1, 13114.00, 'COD', '2025-06-17 13:00:00', 'pending', NULL, '2025-06-09 13:36:20'),
(142, 'ORDER-6846eb49405fa8.00860218', 60, NULL, 16, 2, 244444.00, 'COD', '2025-06-18 09:00:00', 'pending', NULL, '2025-06-09 14:10:17'),
(143, 'ORDER-6846f7271901d2.99813781', 61, NULL, 17, 3, 39342.00, 'COD', '2025-06-10 09:00:00', 'pending', NULL, '2025-06-09 15:00:55');

-- --------------------------------------------------------

--
-- Struktur dari tabel `posts`
--

CREATE TABLE `posts` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `slug` varchar(255) NOT NULL,
  `content` longtext NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `author_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `type` varchar(50) DEFAULT 'artikel'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `posts`
--

INSERT INTO `posts` (`id`, `title`, `slug`, `content`, `image`, `author_id`, `created_at`, `updated_at`, `type`) VALUES
(2, 'Manfaat Belanja Sembako Online di Era Digital', 'manfaat-belanja-sembako-online-di-era-digital', 'Dalam era serba digital ini, belanja kebutuhan pokok atau sembako secara online menjadi pilihan yang semakin populer. Bukan tanpa alasan, ada banyak manfaat yang bisa Anda dapatkan, mulai dari efisiensi waktu hingga kemudahan akses berbagai produk. Artikel ini akan membahas secara rinci keuntungan-keuntungan tersebut.', '1749481005_img1.jpg', 44, '2025-06-09 14:54:24', '2025-06-09 14:56:45', 'artikel'),
(3, 'Tips Memilih Buah dan Sayur Segar ala Toko Sembako', 'tips-memilih-buah-dan-sayur-segar-ala-toko-sembako', 'Mendapatkan buah dan sayur yang segar adalah kunci utama untuk hidangan yang lezat dan bergizi. Namun, bagaimana cara memilihnya dengan tepat? Tim ahli kami dari Toko Sembako akan berbagi tips praktis yang bisa Anda terapkan saat berbelanja, baik online maupun offline.', '1749486265_2.jpg', 44, '2025-06-09 14:54:24', '2025-06-09 16:24:25', 'artikel'),
(4, 'Resep Praktis: Nasi Goreng Kampung dengan Bumbu Instan Toko Sembako', 'resep-praktis-nasi-goreng-kampung-dengan-bumbu-instan-toko-sembako', 'Ingin menyajikan nasi goreng kampung yang lezat tanpa ribet? Dengan bumbu instan pilihan dari Toko Sembako, Anda bisa membuatnya dalam hitungan menit! Ikuti resep mudah ini untuk hidangan malam yang sempurna.', '1749486340_3.jpg', 44, '2025-06-09 14:54:24', '2025-06-09 16:25:40', 'artikel'),
(5, 'Berita Terbaru: Toko Sembako Membuka Cabang Baru di Pusat Kota!', 'berita-terbaru-toko-sembako-membuka-cabang-baru-di-pusat-kota', 'Kami sangat gembira mengumumkan bahwa Toko Sembako kini hadir lebih dekat dengan Anda! Cabang terbaru kami resmi dibuka di jantung kota, menawarkan pengalaman belanja sembako yang lebih nyaman dan lengkap. Kunjungi kami segera dan nikmati promo pembukaan!', '1749486390_4.jpg', 44, '2025-06-09 14:54:24', '2025-06-09 16:26:30', 'berita');

-- --------------------------------------------------------

--
-- Struktur dari tabel `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `category` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `image` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `products`
--

INSERT INTO `products` (`id`, `name`, `slug`, `category`, `description`, `price`, `stock`, `image`, `created_at`) VALUES
(5, 'Beras Maknyuss', 'beras-maknyuss', 'beras', '🌾 Beras Maknyuss – Pulen Banget, Bikin Nagih Tiap Suapan\r\nRasakan kelembutan nasi yang menggugah selera dengan Beras Maknyuss, beras pilihan keluarga Indonesia. Diproses dari padi berkualitas tinggi dengan teknologi modern, menghasilkan nasi yang putih, harum, dan pulen maksimal.\r\n\r\n✨ Keunggulan Beras Maknyuss:\r\n\r\nPulen Sempurna: Cocok untuk nasi putih harian atau nasi box katering.\r\n\r\nRasa Alami: Tidak menggunakan pemutih atau pewangi sintetis.\r\n\r\nBersih & Higienis: Diproses secara ketat untuk kualitas terbaik.\r\n\r\nTerpercaya: Digemari jutaan keluarga di seluruh Indonesia.\r\n\r\n📦 Tersedia dalam kemasan 5 kg – praktis dan ekonomis.\r\n\r\n💬 Testimoni Pelanggan:\r\n\"Nasinya enak banget! Pulen dan harum tanpa tambahan apa pun.\" – Pak Arif, Bandung\r\n\r\n🛒 Maknyuss banget di lidah, mantap di hati!\r\nBeras Maknyuss – Rasa Juara, Pilihan Keluarga Sejati.', 69500.00, 0, 'Beras_Maknyuss.jpeg', '2025-06-02 18:09:11'),
(6, 'Beras Palmares', 'beras-palmares', 'beras', '🌾 Beras Palmares – Pulen, Harum, dan Berkualitas Tinggi\r\nNikmati sajian nasi yang sempurna setiap hari dengan Beras Kepala Premium, pilihan tepat untuk keluarga yang mengutamakan kualitas. Beras ini berasal dari padi pilihan yang diproses secara modern tanpa campuran, menghasilkan butiran utuh, bersih, dan putih alami.\r\n\r\n✨ Keunggulan Beras Kepala Premium:\r\n\r\nButiran Bulat Utuh: Tidak mudah patah, tampak cantik saat dimasak.\r\n\r\nAroma Harum Alami: Tanpa pewangi buatan, wanginya berasal dari kesegaran panen.\r\n\r\nTekstur Pulen & Lembut: Cocok untuk semua jenis masakan, dari nasi putih sehari-hari hingga nasi uduk dan nasi kuning.\r\n\r\nBebas Pengawet: Aman dikonsumsi oleh seluruh anggota keluarga, termasuk anak-anak.\r\n\r\n📦 Tersedia dalam kemasan 5 kg & 10 kg – hemat dan praktis untuk kebutuhan rumah tangga atau usaha kuliner.\r\n\r\n💬 Testimoni Pelanggan:\r\n\"Setelah coba Beras Kepala ini, keluarga saya tidak mau ganti yang lain. Nasinya pulen banget dan wanginya bikin lapar!\" – Ibu Rani, Jakarta\r\n\r\n🛒 Dapatkan sekarang dan rasakan perbedaannya!\r\nBeras Kepala Premium – dari sawah terbaik, untuk meja makan terbaik Anda.', 60000.00, 1, 'Beras_Palmares.jpeg', '2025-06-02 18:15:49'),
(7, 'Beras Sania', 'beras-sania', 'beras', '🌾 Beras Sania – Lezat Alami dari Ladang Terbaik\r\nBangun hari dengan semangat dari sepiring nasi istimewa! Beras Sania hadir sebagai sahabat dapur yang setia, diolah dari padi unggulan dan diproses secara higienis tanpa bahan tambahan. Rasakan setiap butirnya yang memanjakan lidah dan menenangkan hati.\r\n\r\n✨ Kenapa Memilih Beras Sania?\r\n\r\nButirannya Cantik dan Bersinar: Nampak bening alami tanpa pemutih, enak dilihat, apalagi disantap!\r\n\r\nAroma Panen Segar: Harumnya bukan dari pewangi, tapi dari kualitas tanah dan udara sawah yang subur.\r\n\r\nTekstur Lembut & Pulen Maksimal: Masak apa pun tetap pas—nasi putih rumahan, nasi liwet, hingga bento kekinian.\r\n\r\nTahan Simpan, Anti Bau Apek: Kadar air rendah membuat beras awet dan bebas kutu lebih lama.\r\n\r\n📦 Kemasan 5 kg & 10 kg – fleksibel untuk kebutuhan dapur rumah hingga bisnis kuliner harian.\r\n\r\n💬 Cerita Pelanggan:\r\n“Dulu sering gonta-ganti merek, sekarang cukup Sania saja. Nasinya pulen dan bikin makan nambah terus!” – Mbak Yuni, Surabaya\r\n\r\n🛒 Waktunya ganti ke yang lebih baik!\r\nBeras Sania – Bukan Sekadar Nasi, Tapi Rasa yang Membekas.', 7450.00, 14, 'Beras_Sania.jpeg', '2025-06-03 05:34:15'),
(8, 'Beras Merah', 'beras-merah', 'beras', '🌾 Beras Merah – Pilihan Sehat, Rasa Nikmat\r\nBeralih ke gaya hidup sehat kini lebih mudah dengan Beras Merah, sumber karbohidrat kompleks yang kaya serat dan nutrisi. Ditanam di lahan subur dan diproses tanpa bahan kimia, beras ini mempertahankan warna merah alaminya serta rasa khas yang gurih.\r\n\r\n✨ Keunggulan Beras Merah:\r\n\r\nKaya Nutrisi: Mengandung vitamin B, zat besi, dan antioksidan yang mendukung kesehatan jantung dan pencernaan.\r\n\r\nIndeks Glikemik Rendah: Cocok untuk penderita diabetes dan mereka yang menjalani program diet.\r\n\r\nTekstur Unik: Sedikit kenyal dengan rasa yang khas, memberikan sensasi berbeda dari nasi putih biasa.\r\n\r\nBebas Pengawet: Diproses alami tanpa tambahan bahan kimia, aman untuk konsumsi sehari-hari.\r\n\r\n📦 Kemasan Tersedia: 5 kg – praktis untuk kebutuhan rumah tangga atau usaha kuliner.\r\n\r\n💬 Testimoni Pelanggan:\r\n\"Sejak beralih ke Beras Merah, saya merasa lebih bertenaga dan kenyang lebih lama. Rasanya juga enak!\" – Bapak Andi, Jakarta\r\n\r\n🛒 Dapatkan sekarang dan rasakan manfaatnya!\r\nBeras Merah – Langkah Sehat untuk Hidup Lebih Baik.', 77900.00, 30, 'Beras_Merah.jpeg', '2025-06-03 18:57:20'),
(9, 'Sumo Merah Beras Premium 5kg', 'sumo-merah-beras-premium-5kg', 'beras', '🌾 Beras Sumo – Besar, Pulen, dan Bikin Kenyang Lebih Lama\r\nKalau urusan nasi, jangan tanggung! Beras Sumo hadir untuk Anda yang menginginkan kualitas dan kepuasan dalam setiap suapan. Dengan ukuran butiran yang lebih besar, tekstur pulen sempurna, dan rasa alami, Beras Sumo siap menjadi andalan di meja makan keluarga Indonesia.\r\n\r\n✨ Keunggulan Beras Sumo:\r\n\r\n🍚 Butiran Besar & Utuh:\r\nSesuai namanya, Beras Sumo punya ukuran butir yang mantap dan tidak mudah patah saat dimasak.\r\n\r\n🌾 Tekstur Pulen yang Konsisten:\r\nTidak keras, tidak lembek – pas di lidah dan cocok untuk segala jenis masakan, dari nasi putih sehari-hari hingga olahan nasi khas nusantara.\r\n\r\n🌸 Aroma Segar Tanpa Tambahan:\r\nDiproses alami tanpa pewangi buatan, menghadirkan wangi khas beras segar yang menggoda selera.\r\n\r\n✅ Bersih & Siap Masak:\r\nDicuci minimal, karena sudah melalui proses penyortiran dan pembersihan modern.\r\n\r\n📦 Tersedia dalam kemasan 5 kg & 10 kg – praktis untuk rumah tangga, restoran, atau usaha katering.\r\n\r\n💬 Testimoni Pelanggan:\r\n“Ukuran butirannya beda dari yang lain, nasinya pulen banget dan gak cepat basi. Cocok buat usaha warteg saya.” – Ibu Ria, Bekasi\r\n\r\n🛒 Beli sekarang, rasakan sensasi nasi yang beda!\r\nBeras Sumo – Porsi Besar, Rasa Juara.', 81300.00, 30, 'Beras Sumo.jpeg', '2025-06-03 20:58:08'),
(10, 'Beras Sawah Hijau 5kg', 'beras-sawah-hijau-5kg', 'beras', '🌾 Beras Sawah Hijau – Alami, Pulen, dan Sehat untuk Keluarga\r\nHadir dari lahan persawahan subur yang dikelola secara alami, Beras Sawah Hijau adalah pilihan tepat untuk Anda yang mengutamakan kualitas dan kesehatan. Rasa pulennya nikmat, aromanya segar alami, dan prosesnya bebas bahan kimia—cocok untuk menu harian hingga sajian spesial keluarga.\r\n\r\n✨ Keunggulan Beras Sawah Hijau:\r\n\r\n🍃 Ditanam Secara Alami:\r\nBeras ini berasal dari sawah yang tidak menggunakan pestisida kimia, aman dan lebih sehat untuk dikonsumsi setiap hari.\r\n\r\n🌾 Tekstur Pulen & Tidak Mudah Basi:\r\nHasil masakan lebih awet dan tetap pulen walau disimpan lama—praktis untuk keluarga dan pelaku usaha makanan.\r\n\r\n🌸 Aroma Alami yang Menenangkan:\r\nBebas pewangi buatan, memberikan keharuman khas beras segar dari sawah alami.\r\n\r\n✅ Bersih & Siap Dimasak:\r\nMelalui proses sortir modern, beras ini bersih dan hanya perlu dicuci ringan sebelum dimasak.\r\n\r\n📦 Tersedia dalam kemasan 5 kg, 10 kg, dan 25 kg – ideal untuk rumah tangga, UMKM, hingga restoran skala besar.\r\n\r\n💬 Testimoni Pelanggan:\r\n“Sejak pakai Beras Sawah Hijau, keluarga jadi doyan makan di rumah. Nasinya pulen banget dan wangi alami.” – Pak Arman, Bandung\r\n\r\n🛒 Pesan sekarang dan rasakan kebaikan alami dalam setiap suapan!\r\nBeras Sawah Hijau – Pulen Alami, Lezat Setiap Hari.', 74500.00, 47, 'Beras_Sawah_Hijau.jpeg', '2025-06-03 21:53:38'),
(11, 'Sambal Asli ABC', 'sambal-asli-abc', 'bumbu', '🌶 Sambal Asli ABC – Pedasnya Pas, Nikmatnya Otentik\r\nSambal Asli ABC dibuat dari cabai segar pilihan dan resep khas Indonesia yang telah melegenda. Rasa pedasnya pas, tidak menyengat di lidah, namun tetap menggugah selera—cocok untuk semua jenis hidangan, dari nasi goreng, ayam goreng, hingga mie instan.\r\n\r\n✨ Keunggulan Sambal Asli ABC:\r\n\r\n🌶 Dibuat dari Cabai Segar Pilihan:\r\nMenggunakan bahan-bahan berkualitas tinggi seperti cabai merah segar, bawang, dan tomat asli, memberikan cita rasa pedas yang alami dan lezat.\r\n\r\n🍴 Rasa Pedas yang Seimbang & Otentik:\r\nTidak terlalu pedas, tidak terlalu manis—diracik dengan sempurna sesuai selera lidah Indonesia.\r\n\r\n🧂 Tanpa Pengawet Berbahaya:\r\nProses produksi modern dengan standar tinggi memastikan sambal ini aman dikonsumsi sehari-hari tanpa mengorbankan cita rasa.\r\n\r\n🥄 Siap Saji & Praktis:\r\nLangsung nikmati sambalnya tanpa perlu repot mengulek—praktis untuk anak kost, ibu rumah tangga, hingga pecinta kuliner sejati.\r\n\r\n📦 Tersedia dalam berbagai ukuran kemasan:\r\nDari sachet kecil hingga botol besar—ideal untuk konsumsi pribadi hingga kebutuhan dapur usaha kuliner.\r\n\r\n💬 Testimoni Pelanggan:\r\n“Setiap makan pasti cari Sambal ABC. Rasanya pas banget, kayak buatan ibu di rumah.” – Ibu Rini, Surabaya\r\n\r\n🛒 Yuk, lengkapi hidanganmu dengan Sambal Asli ABC – karena makan tanpa sambal, rasanya belum lengkap!\r\nSambal Asli ABC – Pedasnya Bikin Kangen.', 8300.00, 62, 'IMG-20250602-WA0009.jpg', '2025-06-04 13:42:31'),
(12, 'Kecap bango', 'kecap-bango', 'bumbu', '🍛 Kecap Bango – Cita Rasa Otentik dari Fermentasi Alami Kedelai Hitam\r\n\r\nKecap Bango dibuat melalui proses fermentasi alami menggunakan kedelai hitam pilihan, menghasilkan rasa manis gurih yang khas dan warna hitam pekat alami. Proses fermentasi ini melibatkan mikroorganisme seperti Aspergillus sp., yang berperan dalam mengembangkan cita rasa dan aroma khas kecap. Kedelai hitam yang digunakan telah dijemur dan disterilkan sebelum proses fermentasi, memastikan kualitas dan keamanan produk. \r\n\r\n\r\n✨ Keunggulan Kecap Bango:\r\n\r\nFermentasi Alami: Menggunakan mikroorganisme Aspergillus sp. dalam proses fermentasi, menghasilkan rasa yang kaya dan kompleks.\r\n\r\nBahan Berkualitas: Terbuat dari kedelai hitam pilihan yang telah dijemur dan disterilkan, memastikan kualitas dan keamanan produk.\r\nharga.web.id\r\n\r\nTanpa Bahan Pengawet: Proses produksi yang higienis tanpa tambahan bahan pengawet, menjaga keaslian rasa dan kesehatan konsumen.\r\n\r\nRasa Manis Gurih Khas: Cocok untuk berbagai jenis masakan, dari tumisan hingga olesan bakaran, memberikan cita rasa khas Indonesia.\r\n💬 Testimoni Pelanggan:\r\n\r\n\"Sejak pakai Kecap Bango, masakan saya jadi lebih lezat dan kaya rasa. Rasa manis gurihnya pas, cocok untuk semua masakan keluarga.\" – Bu Rina, Jakarta\r\n\r\n🛒 Lengkapi Dapur Anda dengan Kecap Bango – Pilihan Tepat untuk Cita Rasa Nusantara!\r\n\r\nKecap Bango tersedia di berbagai toko dan platform e-commerce seperti Tokopedia, Shopee, Indomaret, dan Lulu Hypermarket. Harga dapat bervariasi tergantung lokasi dan promosi yang berlaku.', 21900.00, 40, 'IMG-20250602-WA0010.jpg', '2025-06-04 13:46:38'),
(13, 'Lemonilo-Kaldu Pelezat Ayam', 'lemonilo-kaldu-pelezat-ayam', 'bumbu', '🍗 Lemonilo Kaldu Pelezat Ayam Kampung – Lezat Alami, Aman untuk Keluarga\r\n\r\nLemonilo Kaldu Pelezat Ayam Kampung dirancang untuk memberikan rasa gurih alami pada masakan Anda tanpa tambahan bahan kimia sintetis. Produk ini menggunakan ekstrak ayam kampung asli yang diproses secara higienis, menghasilkan kaldu dengan cita rasa autentik dan kaya nutrisi.\r\n\r\n✨ Keunggulan Lemonilo Kaldu Pelezat Ayam Kampung:\r\n\r\nTanpa MSG dan Bahan Sintetis: Tidak mengandung monosodium glutamat (MSG), pengawet, pewarna, atau perisa buatan, menjadikannya pilihan aman untuk seluruh anggota keluarga, termasuk anak-anak dan ibu hamil.\r\n\r\nBahan Alami Berkualitas: Terbuat dari garam, gula, bawang, ekstrak ayam kampung, lada putih, maizena, dan kunyit, memberikan rasa gurih alami tanpa tambahan bahan kimia.\r\n\r\nSertifikasi Halal: Telah mendapatkan sertifikasi halal dari MUI, memastikan kehalalan dan kebersihan produk.\r\n\r\nCocok untuk Berbagai Masakan: Ideal digunakan dalam berbagai jenis masakan, seperti sup, tumisan, dan hidangan berkuah lainnya, memberikan rasa lezat tanpa perlu tambahan bumbu lainnya.\r\n\r\nPraktis dan Ekonomis: Kemasan botol 80 gram yang praktis digunakan dan disimpan, serta ekonomis untuk penggunaan sehari-hari.', 22000.00, 197, 'IMG-20250602-WA0011.jpg', '2025-06-04 13:50:29'),
(15, 'Bernardi Corned Beef 290 g – Padat Rasa, Praktis untuk Segala Masakan', 'bernardi-corned-beef-290-g-padat-rasa-praktis-untuk-segala-masakan', 'bumbu', '🥫 Bernardi Corned Beef 290 g – Padat Rasa, Praktis untuk Segala Masakan\r\n\r\nBernardi Corned Beef berbahan potongan daging sapi asli tanpa campuran tepung—diblend dengan rempah bumbu pilihan dan diproses steril sehingga siap disantap langsung dari kaleng atau sebagai bahan masakan \r\nrenyswalayanku.membercard.id\r\n+10\r\nmegaswalayan.com\r\n+10\r\ntokokumakassar.com\r\n+10\r\n. Teksturnya padat, mudah setelah dipotong, dan tidak “ampas” seperti beberapa kornet sachet \r\nreddit.com\r\n.\r\n\r\n✨ Keunggulan:\r\n\r\n100 % Daging Sapi tanpa filler—menjamin cita rasa asli dan lebih gurih.\r\n\r\nMulti‑fungsi dalam masakan: bisa diiris untuk sandwich, dicampur telur jadi omelet (tortang carne norte), campuran masak, atau dipanaskan langsung.\r\n\r\nCara penyajian fleksibel: santap langsung, tumis bawang/saus pedas, atau kombinasikan ke pasta, nasi goreng, hingga pangsit goreng ala reddit \r\nblibli.com\r\n+14\r\nreddit.com\r\n+14\r\nrenyswalayanku.membercard.id\r\n+14\r\nmegaswalayan.com\r\n+3\r\nen.wikipedia.org\r\n+3\r\nen.wikipedia.org\r\n+3\r\n.', 51500.00, 30, 'IMG-20250602-WA0019.jpg', '2025-06-08 15:26:00'),
(16, 'Diamond Milk – Kesegaran Unggul', 'diamond-milk-kesegaran-unggul', 'susu', '🥛 Diamond Milk – Kesegaran Unggul, Tepat untuk Segala Hari\r\n\r\nDiamond Milk hadir dari proses pasteurisasi higienis, menjaga kesegaran alami susu sapi lokal. Setiap tetesnya creamy dan ringan, cocok untuk seluruh keluarga—anak-anak hingga dewasa, bahkan untuk camilan sehat atau tambahan energi instan.\r\n\r\n✨ Keunggulan Diamond Milk:\r\n\r\nTanpa Pengawet & MSG: Diproses minimalis, hanya susu segar murni, tanpa bahan sintetis tambahan—rasa “clean” yang nyaman untuk konsumsi harian.\r\n\r\nVarian Selera yang Bervariasi: Dari Full Cream, cokelat, stroberi, low-fat high-calcium, hingga varian “sereal”—memanjakan lidah semua anggota keluarga.\r\n\r\nKemasan Fleksibel: Multi-varian tersedia dalam kemasan 125 ml (praktis), 200 ml (pas untuk bekal), hingga 1 L (hemat untuk keluarga).\r\n\r\nKaya Nutrisi: Terutama varian low-fat high-calcium, memberi tambahan nutrisi penting tanpa meningkatkan kadar lemak jenuh.', 17400.00, 50, 'IMG-20250602-WA0022.jpg', '2025-06-09 08:23:34'),
(17, 'Heinz Yellow Mustard - 255 gram', 'heinz-yellow-mustard-255-gram', 'bumbu', '🌭 Heinz Yellow Mustard – Rasa Klasik, Tekstur Lembut, Cemilan Harian\r\n\r\nHeinz Yellow Mustard hadir dari resep tradisional Amerika, diproses higienis untuk menjaga cita rasa autentik yang ringan dan menonjolkan perpaduan mustard seed, cuka, dan kunyit—memberikan warna kuning cerah dan rasa mild yang bersahaja namun kaya karakter. Cocok sebagai pelengkap hot dog, burger, sandwich, saus celup, bahkan dressing atau marinade.\r\n\r\n✨ Keunggulan Heinz Yellow Mustard:\r\n\r\nTekstur Creamy & Mudah Dijangkau: Botol dengan tutup di bawah (squeeze bottle) memudahkan pengendalian jumlah dan mencegah sisa yang terbuang \r\n\r\nRasa Tangy Seimbang: Kombinasi rasa asam ringan dan sweetness yang “tangy-but-clean” — tidak terlalu tajam, memberikan sentuhan yang pas pada berbagai hidangan .\r\n\r\nDukungan Pelanggan Positif: Rating tinggi (4.6–4.7 ★) dengan lebih dari ribuan review; pengguna menyebut “smooth texture”, “consistent flavor”, dan “great value” .\r\n\r\nSerba Guna untuk Laser Memudahkan Masakan: Cocok untuk hot dogs, burger, pretzel, sandwich, dan dijadikan dressing atau marinade — sangat fleksibel dalam penggunaan', 300000.00, 48, 'IMG-20250602-WA0024.jpg', '2025-06-09 08:26:55'),
(18, 'Fresh Milk', 'fresh-milk', 'susu', '🥛 Fresh Milk – Kesegaran Alami, Baik untuk Keluarga\r\n\r\nFresh Milk kami disajikan langsung dari peternakan lokal, diproses secara higienis dengan metode pasteurisasi untuk menjaga rasa alami dan kandungan nutrisinya. Cocok jadi teman sarapan, smoothies, ataupun secangkir teh hangat.\r\n\r\n✨ Keunggulan Fresh Milk:\r\n\r\nTanpa Pengawet & Bahan Kimia: Diproses simpel, tanpa MSG, pewarna, pengawet, atau bahan sintetis apa pun – hanya susu segar murni.\r\n\r\n100% Susu Segar yang Alami: Menggunakan susu sapi asli yang dijaga kesehatannya oleh ahli gizi dan dokter hewan, sehingga rasa tetap “light” dan segar, tanpa aftertaste yang eneg \r\npertanian.trunojoyo.ac.id\r\n+4\r\ngreenfieldsdairy.com\r\n+4\r\nblibli.com\r\n+4\r\n.\r\n\r\nDijamin Halal & Aman: Diproduksi di fasilitas bersertifikat, aman dikonsumsi seluruh anggota keluarga—khususnya untuk anak di atas 1 tahun dan ibu hamil.\r\n\r\nFleksibel untuk Beragam Hidangan: Nikmati langsung dingin, panaskan untuk kopi atau teh, atau campurkan ke sereal, pancake, oatmeal, dan smoothie favorit.\r\n\r\nPraktis & Ekonomis: Tersedia dalam kemasan 250 ml dan 1 L—praktis untuk konsumsi harian atau stok keluarga.', 13800.00, 40, 'IMG-20250602-WA0012.jpg', '2025-06-09 15:05:27'),
(19, 'Ayam Axedum Gainusa (Chicken Grill Axedum) – Siap Oven, Praktis dan Lezat', 'ayam-axedum-gainusa-chicken-grill-axedum-siap-oven-praktis-dan-lezat', 'daging', '🍗 Ayam Axedum Gainusa (Chicken Grill Axedum) – Siap Oven, Praktis dan Lezat\r\n\r\nDeskripsi Produk:\r\nProduk ini adalah ayam utuh yang telah dipersiapkan untuk dipanggang dalam oven — tersedia dalam versi segar atau beku. Sesuai namanya (Chicken Grill), ayam ini sudah siap untuk digrill dengan bumbu natural sebelum dibekukan \r\naxedum.md\r\n+1\r\ntiobisnis2.blogspot.com\r\n+1\r\n.\r\n\r\nNilai Gizi per 100 g:\r\n\r\nLemak: 7.4 g\r\n\r\nProtein: 20.9 g\r\n\r\nKarbohidrat: 0.2 g\r\n\r\nKalori: 150 kkal \r\naxedum.md\r\n+1\r\naxedum.md\r\n+1\r\n\r\n=> Menunjukkan komposisi seimbang untuk daging panggang yang cukup bergizi.\r\n\r\nPenyimpanan:\r\nPerlu disimpan pada suhu 0–4 °C dan kelembapan relatif antara 85–95%, idealnya di bagian freezer chiller untuk menjaga kesegaran \r\ntiobisnis2.blogspot.com\r\naxedum.md\r\n.\r\n\r\nCara Penyajian:\r\nCukup dipanaskan dalam oven hingga kulitnya renyah—praktis seperti produk ayam rotisserie siap makan yang tersedia di banyak restoran.', 60000.00, 12, 'IMG-20250602-WA0013.jpg', '2025-06-09 15:12:50'),
(20, '🌶️ Bon Cabe Level 30 – Pedasnya Nendang, Bikin Nagih!', 'bon-cabe-level-30-pedasnya-nendang-bikin-nagih', 'bumbu', '🌶️ Bon Cabe 30 – Pedasnya Nendang, Bikin Nagih!\r\n\r\nBon Cabe 30 adalah sambal tabur pedas praktis dengan tingkat kepedasan level 30 yang pas untuk kamu yang doyan sensasi pedas menggigit tanpa bikin perut mules. Terbuat dari cabai asli pilihan dan bumbu alami, Bon Cabe menghadirkan cita rasa sambal khas Indonesia yang bisa ditaburkan di semua makanan favoritmu, mulai dari mie, nasi goreng, hingga gorengan.\r\n\r\n✨ Keunggulan Bon Cabe 30:\r\n\r\nPedas Nendang tapi Tetap Nikmat: Level pedas 30 memberikan sensasi panas yang pas, bikin makanan jadi makin menggugah selera tanpa membuat rasa jadi pahit atau terlalu pedas.\r\n\r\nPraktis dan Serbaguna: Kemasan sachet kecil cocok dibawa kemana saja, siap tabur kapan pun kamu ingin menambah rasa pedas dan gurih.\r\n\r\nBahan Alami: Menggunakan cabai asli, bawang putih, garam, dan bumbu alami lain tanpa bahan pengawet berbahaya.\r\n\r\nCocok untuk Semua Makanan: Taburkan pada mie instan, kerupuk, tahu goreng, bahkan pizza untuk sensasi pedas unik yang berbeda.', 35000.00, 11, 'IMG-20250602-WA0015.jpg', '2025-06-09 15:13:11'),
(21, 'Ayam Butchers – Dari Peternak ke Meja, Kesegaran Terjaga', 'ayam-butchers-dari-peternak-ke-meja-kesegaran-terjaga', 'daging', '🍗 Ayam Butchers – Dari Peternak ke Meja, Kesegaran Terjaga\r\n\r\nAyam potong ala Butchers berarti ayam broiler utuh atau terpotong (fillet, paha, sayap) yang diproses secara higienis dan dikemas jika perlu langsung dikonsumsi atau diolah. Umumnya berasal dari peternakan modern (seperti So Good dari Japfa) dan melalui proses cepat dari peternakan ke toko supaya kesegarannya maksimal \r\nternakpertama.com\r\n+15\r\nberita.99.co\r\n+15\r\nreddit.com\r\n+15\r\nmocipay.com\r\n+3\r\ngoodwinsbutchery.com\r\n+3\r\nberita.99.co\r\n+3\r\n.\r\n\r\n✨ Alasan Memilih Ayam Butchers:\r\n\r\nKesegaran & Kebersihan Terjamin – Banyak datang segar, belum sempat beku, ideal untuk konsumsi atau masak langsung. Suplemen nutrisi ayam juga dikontrol agar bebas bau.\r\n\r\nVariasi Potongan Lengkap – Dari ayam utuh, fillet dada, paha, sayap, sampai daging giling siap masak.\r\n\r\nHarga Kompetitif – Umumnya antara Rp 20.000–25.000/kg untuk ayam broiler segar di Jakarta sekitar 1–2 kg per eko', 88000.00, 9, 'IMG-20250602-WA0016.jpg', '2025-06-09 15:13:36'),
(22, 'Chicken Seasoning – Sentuh Rasa Alami, Ringan di Dapur', 'chicken-seasoning-sentuh-rasa-alami-ringan-di-dapur', 'bumbu', '🍗 Chicken Seasoning – Sentuh Rasa Alami, Ringan di Dapur\r\n\r\nChicken seasoning adalah bumbu serbaguna yang dirancang khusus untuk menonjolkan rasa gurih alami ayam tanpa harus memasak kaldu dari awal. Tersedia dalam bentuk bubuk atau marinasi instan, produk ini menawarkan kemudahan dan rasa maksimal untuk masakan harian kamu.\r\n\r\n✨ Keunggulan Chicken Seasoning:\r\n\r\nFormulasi alami & praktis – Banyak varian menggunakan bahan asli ayam atau kaldu, tanpa MSG, pengawet, atau pewarna sintetis, seperti Pura Chicken Seasoning yang memakai ayam kampung dan rempah alami, tanpa pemanis & pewarna buatan \r\nblibli.com\r\n+4\r\ncekbarang.id\r\n+4\r\ndailymartazzahra.com\r\n+4\r\nreddit.com\r\n.\r\n\r\nSerbaguna untuk berbagai hidangan – Cocok untuk bubur, tumisan, panggang, sup, atau nasi goreng; ada juga varian marinasi seperti Kalasan dan Savoury dari Sasa untuk ayam goreng rasa tradisional \r\nmadeinindonesia.com\r\n+1\r\nb2peru.pe\r\n+1\r\n.\r\n\r\nKemasan fleksibel – Ada varian sachet kecil (20 – 100 g) hingga pouch besar (500 g–1 kg), sesuai kebutuhan rumah tangga, catering, atau restoran.', 17800.00, 19, 'IMG-20250602-WA0017.jpg', '2025-06-09 15:14:25'),
(23, 'Basics Fresh Eggs – Telur Segar Berkualitas untuk Kebutuhan Sehari-hari', 'basics-fresh-eggs-telur-segar-berkualitas-untuk-kebutuhan-sehari-hari', 'telur', '🥚 Basics Fresh Eggs – Telur Segar Berkualitas untuk Kebutuhan Sehari-hari\r\n\r\nBasics Fresh Eggs hadir dalam kemasan praktis isi 10 butir, cocok untuk kebutuhan memasak harian Anda. Telur ayam segar ini dipilih dengan ketat dan dikemas dengan standar kebersihan tinggi untuk memastikan kualitas dan kesegaran tetap terjaga hingga sampai di tangan konsumen.\r\n\r\n✨ Keunggulan Basics Fresh Eggs:\r\n\r\nKesegaran Terjamin: Dipanen dan dikemas dengan cepat untuk menjaga kualitas terbaik dan rasa alami telur.\r\n\r\nUkuran Seragam: Setiap butir memiliki ukuran standar yang sesuai untuk berbagai resep masakan.\r\n\r\nKemasan Praktis: Plastik transparan yang kuat, melindungi telur dari benturan dan memudahkan penyimpanan.\r\n\r\nSerbaguna: Ideal untuk sarapan, kue, makanan olahan, atau hidangan sehari-hari.', 38000.00, 13, 'IMG-20250602-WA0020.jpg', '2025-06-09 15:15:07'),
(24, 'Bango Kecap Manis  – Warisan, Rasa Sejati 620 ml', 'bango-kecap-manis-warisan-rasa-sejati-620-ml', 'bumbu', '🥢 Bango Kecap Manis  – Warisan, Rasa Sejati 620 ml\r\n\r\nDibuat sepenuh hati dari bahan alami unggulan—kedelai hitam Mallika asli, gula kelapa, garam, dan air—Bango menghadirkan rasa manis-gurih kental dengan aroma khas kedelai yang legit. Diformulasi tanpa pengawet, pewarna, dan MSG tambahan – “karena rasa tak pernah bohong” sejak 1928 \r\nhargacampur.com\r\n+14\r\nbango.co.id\r\n+14\r\nbiggo.id\r\n+14\r\nbiggo.id\r\n.\r\n\r\n✨ Keunggulan yang Membuatnya Istimewa:\r\n\r\nBahan Alami & Tradisional: menggunakan empat bahan inti berkualitas tinggi, diproses melalui fermentasi alami – menciptakan rasa kedelai yang kaya dan autentik .\r\n\r\nKemasan Botol Kaca Premium: menjaga kualitas dan aroma lebih awet, cocok dijadikan koleksi di dapur atau hadiah.\r\n\r\nMultiguna untuk Segala Hidangan: sempurna sebagai cocolan gorengan, untuk nunggang sate, semur daging, nasi goreng, bakar ikan, sayur tumis, hingga sebagai bumbu tambahan hidangan rumahan \r\nreddit.com\r\n+15\r\nmonotaro.id\r\n+15\r\nbiggo.id\r\n+15\r\n.\r\n\r\nTerpercaya & Bersertifikat: telah terdaftar di BPOM dan bersertifikasi halal.', 45700.00, 11, 'IMG-20250602-WA0021.jpg', '2025-06-09 15:16:06'),
(25, 'Big Mac – Ikonik, Ganda & Penuh Rasa', 'big-mac-ikonik-ganda-penuh-rasa', 'bumbu', '🍔 Big Mac – Ikonik, Ganda & Penuh Rasa\r\n\r\nTerdiri dari dua lapis daging sapi 100 %, selada renyah, irisan bawang, acar, satu slice keju American, dan saus special khas McD—all packed di antara tiga lapis roti wijen. Big Mac adalah ikon burger McDonald’s yang terkenal dengan keseimbangan rasa manis, gurih, dan creamy di setiap gigitan.\r\n\r\n🌟 Keunggulan Big Mac\r\nDaging Sapi 100 % & Bumbu Terstandar\r\nMenggunakan dua patty daging sapi tanpa bahan pengawet artifisial, sehingga rasanya konsisten dan berkualitas \r\nreddit.com\r\n+10\r\nmcdonalds.com\r\n+10\r\nmcdonalds.co.id\r\n+10\r\nmcdonalds.co.id\r\n.\r\n\r\nSaus Spesial Karakteristik yang Khas\r\nSaus creamy-nya jadi ciri khas utama, menghadirkan rasa tangy-mild yang membedakan dari burger lainnya.\r\n\r\nTekstur & Build yang Seimbang\r\nSensasi segar dari sayur dan lembut dari roti wijen, berpadu apik dalam struktur tiga lapis klasik.', 50000.00, 20, 'IMG-20250602-WA0023.jpg', '2025-06-09 15:18:00'),
(26, 'Bimoli special', 'bimoli-special', 'minyak', '🍳 Bimoli Minyak Goreng – Kualitas Terpercaya untuk Setiap Masakan Anda\r\n\r\nBimoli Minyak Goreng hadir dalam kemasan praktis isi 2 liter, pilihan ideal untuk kebutuhan memasak sehari-hari keluarga Anda. Minyak goreng sawit berkualitas tinggi ini diproduksi dengan \"Pemurnian Multi Proses (PMP)\" yang menjaga zat-zat penting dan manfaat kesehatan tetap terjaga. Bimoli dipercaya sejak tahun 1970-an untuk menghasilkan masakan yang renyah dan nikmat.\r\n\r\n✨ Keunggulan Bimoli Minyak Goreng 2 Liter:\r\n\r\nKualitas Terjamin: Dibuat dari biji kelapa sawit pilihan dan diproses dengan teknologi Golden Refinery untuk menghasilkan minyak goreng jernih dan berkualitas.\r\nStabil Panasnya: Minyak cepat panas dan stabil suhunya saat digunakan untuk menggoreng, memastikan masakan matang sempurna luar dan dalam tanpa cepat gosong.\r\nMenambah Cita Rasa: Memberikan kerenyahan dan aroma sedap pada setiap masakan, menjadikan hidangan lebih istimewa.\r\nBebas Kolesterol: Diformulasikan bebas kolesterol, serta diperkaya dengan Beta Karoten (pro-vitamin A) dan Vitamin E, baik untuk kesehatan.\r\nKemasan Praktis: Tersedia dalam kemasan pouch refill 2 liter yang kuat dan mudah digunakan untuk pengisian ulang.', 36000.00, 19, 'IMG-20250602-WA0025.jpg', '2025-06-09 15:31:37'),
(27, 'Ultra Aroma Extra Pedas', 'ultra-aroma-extra-pedas', 'bumbu', '🔥 Ultra Aroma Bumbu Tabur Rasa Extra Pedas – Sensasi Pedas Menggila untuk Camilan Favorit Anda!\r\nUltra Aroma Bumbu Tabur Rasa Extra Pedas hadir untuk Anda para pencinta pedas yang ingin meningkatkan level kenikmatan camilan dan masakan. Dikemas praktis, bumbu ini siap menyulap keripik, kentang goreng, makaroni, basreng, cilok, atau berbagai hidangan lainnya menjadi luar biasa pedas dan menggugah selera. Dibuat oleh PT. Ultra Aroma, perusahaan seasoning terkemuka asal Semarang, bumbu ini menjamin kualitas dan keamanan dengan izin edar BPOM RI serta sertifikasi Halal MUI.\r\n\r\n✨ Keunggulan Ultra Aroma Bumbu Tabur Rasa Extra Pedas:\r\nTingkat Kepedasan Maksimal: Dirancang khusus untuk memberikan sensasi pedas yang membakar lidah, cocok bagi Anda yang berani menghadapi tantangan rasa.\r\nAplikasi Mudah & Praktis: Cukup taburkan pada makanan yang sudah matang atau olahan, aduk rata, dan nikmati kepedasannya secara instan.\r\nSerbaguna: Ideal untuk berbagai jenis camilan seperti keripik singkong, kentang goreng, popcorn, sosis, tahu crispy, atau bahkan sebagai tambahan pada hidangan berkuah.\r\nKualitas Terjamin: Diproduksi dengan standar kebersihan dan keamanan pangan yang tinggi, serta telah memiliki izin edar BPOM dan sertifikat Halal.\r\nMenambah Daya Tarik Makanan: Tidak hanya pedas, bumbu ini juga memberikan aroma yang khas dan meningkatkan cita rasa keseluruhan pada makanan Anda.', 7000.00, 31, 'IMG-20250602-WA0026.jpg', '2025-06-09 15:32:54'),
(28, 'Barilla Saus Pasta Ricotta – Kelembutan Italia dalam Setiap Gigitan', 'barilla-saus-pasta-ricotta-kelembutan-italia-dalam-setiap-gigitan', 'bumbu', '🧀 Barilla Saus Pasta Ricotta – Kelembutan Italia dalam Setiap Gigitan\r\nBarilla Saus Pasta Ricotta menghadirkan perpaduan sempurna antara lembutnya keju ricotta dengan tomat Italia yang matang. Saus ini diciptakan untuk memberikan cita rasa otentik dan ringan pada hidangan pasta Anda, menjadikannya pilihan ideal untuk santapan keluarga yang lezat dan praktis. Diproduksi tanpa bahan pengawet tambahan dan menggunakan bahan-bahan berkualitas tinggi, saus ini adalah cara mudah untuk menikmati hidangan Italia di rumah.\r\n\r\n✨ Keunggulan Barilla Saus Pasta Ricotta:\r\nPerpaduan Rasa yang Harmonis: Menggabungkan kelembutan krim ricotta dengan kesegaran tomat Italia, menciptakan rasa yang kaya namun tetap ringan dan seimbang.\r\nBahan Berkualitas Tinggi: Dibuat dari tomat Italia pilihan yang matang di bawah sinar matahari dan keju ricotta asli, serta diperkaya dengan keju Grana Padano dan Pecorino Romano untuk cita rasa yang lebih dalam.\r\nTanpa Bahan Pengawet Tambahan: Menjaga keaslian rasa dan kualitas alami dari setiap bahan.\r\nPraktis dan Cepat: Siap pakai, cukup panaskan dan tuang di atas pasta favorit Anda untuk hidangan lezat dalam waktu singkat.\r\nSerbaguna: Cocok untuk berbagai jenis pasta, seperti Spaghetti, Penne, Fusilli, atau bahkan sebagai dasar untuk hidangan panggang seperti Lasagna atau Cannelloni.\r\nGluten-Free: Pilihan yang baik bagi mereka yang memiliki preferensi diet bebas gluten.', 70.00, 13, 'IMG-20250602-WA0028.jpg', '2025-06-09 15:34:02'),
(29, 'Nestlé Dancow – Nutrisi Lengkap untuk Generasi Sehat Indonesia', 'nestle-dancow-nutrisi-lengkap-untuk-generasi-sehat-indonesia', 'susu', '🥛 Nestlé Dancow – Nutrisi Lengkap untuk Generasi Sehat Indonesia\r\nNestlé Dancow menawarkan beragam produk susu yang diformulasikan khusus untuk setiap tahapan usia, mulai dari bayi hingga anak usia sekolah, bahkan ada juga varian yang cocok untuk seluruh anggota keluarga. Dancow berkomitmen untuk menyediakan nutrisi esensial yang mendukung tumbuh kembang optimal anak, daya tahan tubuh, dan kemampuan belajar.\r\n\r\n✨ Keunggulan Produk Dancow:\r\nDiformulasikan Sesuai Tahap Usia: Dancow memiliki seri produk seperti Dancow 1+ (untuk usia 1-3 tahun), Dancow 3+ (untuk usia 3-5 tahun), Dancow 5+ (untuk usia 5-6 tahun), dan Dancow FortiGro (untuk usia sekolah 6-12 tahun ke atas, termasuk dewasa). Setiap formula disesuaikan dengan kebutuhan nutrisi spesifik di setiap tahap perkembangan.\r\nKandungan Nutrisi Lengkap: Produk Dancow umumnya diperkaya dengan berbagai vitamin dan mineral penting seperti Protein, Kalsium, Zat Besi, Zink, Vitamin A, C, D, E, dan B kompleks (B1, B2, B6, B9, B12), serta DHA dan Omega 3 & 6. Nutrisi ini krusial untuk mendukung daya tahan tubuh, perkembangan otak, kepadatan tulang dan gigi, serta energi untuk beraktivitas.\r\nPilihan Rasa dan Kemasan Beragam: Dancow tersedia dalam berbagai rasa favorit anak-anak seperti madu, vanila, dan cokelat. Selain itu, tersedia dalam berbagai ukuran kemasan seperti box, pouch (refill), dan sachet, memudahkan konsumen untuk memilih sesuai kebutuhan dan anggaran.\r\nKualitas Terjamin: Sebagai produk dari Nestlé, Dancow diproduksi dengan standar kualitas internasional dan telah memiliki izin edar dari BPOM RI serta sertifikasi Halal MUI, menjamin keamanan dan kualitas produk.', 91000.00, 20, 'IMG-20250602-WA0029.jpg', '2025-06-09 15:35:08'),
(30, 'Bakso Daging Sapi – Kenikmatan Klasik Penuh Cita Rasa', 'bakso-daging-sapi-kenikmatan-klasik-penuh-cita-rasa', 'daging', '🍲 Bakso Daging Sapi – Kenikmatan Klasik Penuh Cita Rasa\r\nBakso daging sapi adalah salah satu hidangan favorit masyarakat Indonesia yang tak lekang oleh waktu. Bola-bola daging giling yang kenyal ini disajikan dalam kuah kaldu sapi bening yang gurih, dilengkapi dengan mi, bihun, tahu, siomay, pangsit goreng, serta taburan bawang goreng dan seledri. Cita rasa yang kaya, perpaduan tekstur, dan aroma harum membuatnya menjadi hidangan yang selalu dirindukan, baik sebagai santapan hangat di musim hujan maupun sebagai hidangan lezat kapan saja.\r\n\r\n✨ Keunggulan Bakso Daging Sapi:\r\nRasa Autentik dan Gurih: Dibuat dari daging sapi pilihan yang digiling halus, menghasilkan cita rasa daging yang kuat dan kuah kaldu yang kaya rempah, memberikan pengalaman makan yang memuaskan.\r\nTekstur Kenyal yang Pas: Bola bakso memiliki kekenyalan yang optimal, tidak terlalu lembek namun juga tidak terlalu keras, menandakan kualitas daging dan proses pembuatannya yang tepat.\r\nSumber Protein Hewani: Bakso daging sapi merupakan sumber protein yang baik, penting untuk membangun dan memperbaiki sel tubuh, serta memberikan energi.\r\nHidangan Serbaguna: Dapat dinikmati kapan saja, baik sebagai makan siang, makan malam, atau sekadar camilan hangat. Cocok untuk semua kalangan usia.\r\nFleksibilitas Penyajian: Bisa disajikan sesuai selera dengan tambahan saus sambal, kecap manis, cuka, atau perasan jeruk limau untuk memperkaya rasa.', 25000.00, 25, 'IMG-20250602-WA0030.jpg', '2025-06-09 15:37:34'),
(31, 'Desaku Kunyit Bubuk – Bumbu Dapur Andalan untuk Warna dan Aroma Khas', 'desaku-kunyit-bubuk-bumbu-dapur-andalan-untuk-warna-dan-aroma-khas', 'bumbu', '🌶️ Desaku Kunyit Bubuk – Bumbu Dapur Andalan untuk Warna dan Aroma Khas\r\nDesaku Kunyit Bubuk adalah bumbu dapur esensial yang terbuat dari rimpang kunyit pilihan yang dikeringkan dan digiling halus. Dikenal karena warna kuning keemasannya yang cerah dan aroma khasnya yang hangat, kunyit bubuk ini menjadi kunci dalam menciptakan berbagai hidangan tradisional Indonesia. Desaku memastikan kualitas kunyit bubuknya tetap terjaga, memberikan kemudahan bagi Anda untuk menghadirkan cita rasa autentik pada masakan rumahan.\r\n\r\n✨ Keunggulan Desaku Kunyit Bubuk:\r\nWarna dan Aroma Autentik: Memberikan warna kuning alami yang cantik serta aroma khas kunyit yang kuat, esensial untuk masakan Indonesia seperti gulai, kari, nasi kuning, dan opor.\r\nKualitas Terjaga: Diproses dari kunyit pilihan yang dikeringkan dan digiling dengan standar kebersihan tinggi, menjamin kualitas dan kemurnian produk.\r\nPraktis dan Mudah Digunakan: Dalam bentuk bubuk, kunyit ini sangat praktis dan mudah ditambahkan ke dalam masakan tanpa perlu mengupas atau mengulek rimpang kunyit segar.\r\nBumbu Serbaguna: Tidak hanya untuk masakan berkuah, kunyit bubuk juga ideal untuk bumbu marinasi ayam atau ikan, campuran bumbu tumisan, atau bahkan untuk membuat minuman herbal.\r\nHigenis dan Aman: Dikemas dengan baik untuk menjaga kesegaran dan kebersihan produk, serta telah memiliki izin edar dari BPOM RI dan sertifikasi Halal MUI.', 2000.00, 31, 'IMG-20250602-WA0032.jpg', '2025-06-09 15:38:33'),
(32, 'Minyak wijen', 'minyak-wijen', 'bumbu', '✨ Keunggulan Minyak Wijen:\r\nAroma Khas yang Kuat: Minyak wijen sangrai memiliki aroma panggang yang intens dan sedikit nutty, memberikan kedalaman rasa yang unik pada hidangan. Ini adalah ciri khas masakan Asia, terutama hidangan Cina, Jepang, dan Korea.\r\nPeningkat Rasa: Cukup beberapa tetes minyak wijen sangrai dapat secara signifikan meningkatkan cita rasa sup, tumisan, salad, saus celup, hingga marinasi.\r\nKandungan Antioksidan: Biji wijen kaya akan antioksidan seperti sesamol dan sesamin, yang juga terdapat dalam minyaknya.\r\nSumber Lemak Sehat: Mengandung asam lemak tak jenuh ganda dan tunggal yang bermanfaat bagi kesehatan jantung.\r\nSerbaguna dalam Masakan:\r\nMinyak Wijen Sangrai (gelap): Ideal sebagai finishing oil untuk menonjolkan aroma. Tambahkan setelah masakan matang, pada saus salad, dressing, atau sebagai bumbu untuk mi dan sup.\r\nMinyak Wijen Biasa (terang): Dapat digunakan untuk menumis atau menggoreng dengan panas sedang, meskipun kurang umum di pasaran Indonesia dibandingkan yang sangrai.', 31000.00, 18, 'IMG-20250602-WA0038.jpg', '2025-06-09 15:40:16'),
(33, 'Royco Bumbu Kuning Serbaguna – Praktisnya Bumbu Khas Indonesia', 'royco-bumbu-kuning-serbaguna-praktisnya-bumbu-khas-indonesia', 'bumbu', '🍲 Royco Bumbu Kuning Serbaguna – Praktisnya Bumbu Khas Indonesia\r\nRoyco Bumbu Kuning Serbaguna adalah solusi praktis untuk menghadirkan cita rasa khas masakan Indonesia yang kaya rempah dengan warna kuning cerah dari kunyit. Bumbu siap pakai ini diformulasikan dengan perpaduan rempah-rempah pilihan, seperti kunyit, bawang merah, bawang putih, ketumbar, dan lainnya, sehingga Anda nggak perlu lagi repot mengulek bumbu dasar. Cocok untuk berbagai jenis masakan, bumbu ini membantu Anda menciptakan hidangan lezat dengan mudah dan cepat.\r\n\r\n✨ Keunggulan Royco Bumbu Kuning Serbaguna:\r\nPraktis dan Hemat Waktu: Nggak perlu lagi kupas dan ulek aneka bumbu. Cukup tambahkan Royco Bumbu Kuning, masakan Anda langsung beraroma dan bercita rasa khas.\r\nRasa Autentik Khas Indonesia: Perpaduan rempah-rempah pilihan yang pas menghasilkan cita rasa bumbu kuning yang otentik, cocok untuk berbagai hidangan nusantara.\r\nSerbaguna: Sesuai namanya, bumbu ini bisa dipakai untuk beragam masakan. Mulai dari ayam goreng, ikan bumbu kuning, tumisan, hingga opor dan kari instan.\r\nMemberi Warna Cantik: Kandungan kunyitnya nggak hanya memberikan rasa, tapi juga warna kuning alami yang menggugah selera pada masakan Anda.\r\nKualitas Terjamin: Diproduksi oleh Royco, merek terpercaya di Indonesia, bumbu ini sudah terjamin kualitasnya dan punya izin edar BPOM serta sertifikasi Halal MUI.', 3000.00, 19, 'IMG-20250602-WA0035.jpg', '2025-06-09 15:43:04'),
(34, 'Paprika powder', 'paprika-powder', 'bumbu', '🌶️ Paprika Powder – Bumbu Ajaib untuk Warna dan Rasa\r\nPaprika powder adalah bumbu serbaguna yang terbuat dari buah paprika kering yang digiling. Berbeda dengan cabai bubuk yang fokus pada rasa pedas, paprika powder lebih dikenal karena warna merahnya yang cantik dan rasanya yang manis, sedikit pahit, atau kadang sedikit pedas, tergantung jenis paprika yang digunakan. Bumbu ini adalah staples dalam berbagai masakan dunia, mulai dari hidangan Hungaria hingga masakan Spanyol dan Timur Tengah.\r\n\r\n✨ Keunggulan Paprika Powder:\r\nPemberi Warna Alami: Memberikan warna merah keemasan yang indah pada hidangan, sangat cocok untuk menambah daya tarik visual pada sup, tumisan, saus, dan marinasi.\r\nProfil Rasa yang Beragam:\r\nSweet Paprika (Manis): Jenis paling umum, rasanya ringan, manis, dan sedikit fruity. Ideal untuk hidangan yang tidak ingin pedas.\r\nHot Paprika (Pedas): Mengandung sedikit capsaicin, memberikan sentuhan pedas yang lembut.\r\nSmoked Paprika (Asap): Dibuat dari paprika yang diasap sebelum digiling, memberikan aroma dan rasa smokey yang kuat dan unik.\r\nAroma yang Khas: Memiliki aroma hangat dan sedikit manis yang dapat memperkaya kompleksitas rasa masakan.\r\nSumber Antioksidan: Paprika kaya akan antioksidan, termasuk karotenoid, yang baik untuk kesehatan.\r\nSerbaguna dalam Masakan: Cocok untuk bumbu marinasi daging ayam, ikan, atau sapi, campuran bumbu tumisan, taburan telur dadar, sup goulash, paella, atau sebagai pewarna alami untuk nasi dan saus.', 30000.00, 25, 'IMG-20250602-WA0037.jpg', '2025-06-09 15:44:40');

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role` enum('user','admin','courier') DEFAULT 'user',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `email`, `role`, `created_at`) VALUES
(1, 'admin', '0192023a7bbd73250516f069df18b500', 'admin@instantshop.com', 'admin', '2025-04-23 13:47:58'),
(5, '1111', '562b530cff1f5bca3b1a4c1ad4ad9962', 'geovano.412023024@civitas.ukrida.ac.id', 'user', '2025-04-24 06:15:47'),
(6, 'Andreas', 'd36a36c25ffe288126d40000c6c5714e', 'geoyansen@gmail.com', 'user', '2025-04-24 06:16:40'),
(7, '123', '827ccb0eea8a706c4c34a16891f84e7b', 'geoyansen@gmail.com', 'user', '2025-04-24 07:48:13'),
(11, 'ERIK', '827ccb0eea8a706c4c34a16891f84e7b', 'geoyansen@gmail.com', 'user', '2025-04-24 08:08:16'),
(12, 'Roi', 'e10adc3949ba59abbe56e057f20f883e', 'fz@gmail.com', 'user', '2025-05-01 16:50:28'),
(13, 'Caci', '40a729fb2dcc2a4f0d5594ac49be6a98', 'ovanjas616@gmail.com', 'user', '2025-05-02 07:54:12'),
(15, 'Bili', 'e10adc3949ba59abbe56e057f20f883e', 'Bili@gmail.com', 'user', '2025-06-02 10:25:14'),
(16, 'nani', 'e10adc3949ba59abbe56e057f20f883e', 'nani@gmail.com', 'user', '2025-06-02 11:45:10'),
(17, 'nina', 'e10adc3949ba59abbe56e057f20f883e', 'nina@gmail.com', 'user', '2025-06-02 11:48:13'),
(18, 'dani', 'e10adc3949ba59abbe56e057f20f883e', 'dani@gmail.com', 'user', '2025-06-02 11:48:45'),
(19, 'dandi', 'c4ca4238a0b923820dcc509a6f75849b', 'dandi@gmail.com', 'user', '2025-06-02 11:55:54'),
(20, 'dandidu', '$2y$10$FgW8A9FuKqcmThy3DsV5yeNBZIiEuaLKb7VCCdEiIHGZGSpCQeifO', 'dandi1@gmail.com', 'user', '2025-06-02 12:11:13'),
(23, 'malik', '$2y$10$E2w7Meg5U1DBvCvMbYpQyeesghSRgfwC.8YyTZQFflWfStWsZbtb6', 'malik@gmail.com', 'user', '2025-06-02 14:59:01'),
(24, 'malik1', '$2y$10$ShlY5dtT7sOigpJ9zaiK/.ZZq9GVQwLWV8qpdaSq.yiSRYQU8QFXC', 'malik1@gmail.com', 'user', '2025-06-02 15:00:35'),
(25, 'adam', '$2y$10$1Xlzt/KIAXmI9v06x.XQcOsxfq04WAewceMst3ZinfHZvh7GKBjtK', 'adam@gmail.com', 'user', '2025-06-02 15:04:43'),
(26, 'malik21', '$2y$10$R8BvXCsY54LVu5rK8pOA.e7gkGbkgZuqd.eH.Hpy17UCmSNW.XeFG', 'malik21@gmail.com', 'user', '2025-06-02 15:08:41'),
(28, 'malik23', 'e10adc3949ba59abbe56e057f20f883e', 'malik23@gmail.com', 'user', '2025-06-02 15:16:05'),
(30, 'redbull', '$2y$10$6E/2Nk1Ofevz5P7mYto1a.nEeTIpIjQ7AvpHkxHcCARwUv86Lh3eS', 'redbull@gmail.com', 'admin', '2025-06-03 14:22:27'),
(31, 'sumi', '$2y$10$zpp1KHOlHZEIz3pLLAeJquzN4XSGEKKgu7LZSAzVJGPlT6pbGFrWm', 'sumi@gmail.com', 'admin', '2025-06-03 14:30:41'),
(32, 'gala', '$2y$10$Z/8haXMZv38BrnjZDPToFuvn4kYh9qHZttngkyJqPcJvMz7ZRQXvS', 'gala@gmail.com', 'admin', '2025-06-03 14:33:42'),
(33, 'bintang', '$2y$10$M2BIkuDGdi3zatTLqY0YWO7mHfNiIpCArCaD9o1GHJMDaTbeEosX.', 'bintang@gmail.com', 'admin', '2025-06-03 14:34:29'),
(34, 'bintang1', '$2y$10$Vh8Q7.KF/oKKCHhkl5nGuetVhslEeEyCDKydYeiBT0UW.YHh.KOsO', 'bintang1@gmail.com', 'admin', '2025-06-03 14:37:44'),
(35, 'bintang12', '$2y$10$RwnmrANFWm.beM.hQPr7SuGY9Yj9/6eY7T1ghnQqdgi8T8wvXG.2m', 'bintang12@gmail.com', 'admin', '2025-06-03 14:43:09'),
(36, 'biba', '$2y$10$UefYbHyk2qOJjHBiWc3ChuietxzVxb/Xah2TdJe.Z4.CbKVmHc33m', 'biba@gmail.com', 'admin', '2025-06-03 16:54:20'),
(37, 'saya', '$2y$10$LRAW6qFnBr.QHGCOsiT0UeXbfLTeM5.1PxiiEtXqxidsYaMi.YcS.', 'saya@gmail.com', 'admin', '2025-06-03 16:55:10'),
(38, 'niger', 'e10adc3949ba59abbe56e057f20f883e', 'niga@gmail.com', 'user', '2025-06-03 17:10:48'),
(39, 'bihun', '$2y$10$zTy6QubKDxgr3dkY.DKt6./NxhYZGkzMRMFbcmqq6Wi4FExlbbIV.', 'bihun@gmail.com', 'admin', '2025-06-03 17:24:48'),
(40, 'sayur', '81dc9bdb52d04dc20036dbd8313ed055', 'sayur@gmail.com', 'admin', '2025-06-03 17:27:56'),
(41, 'titik', '$2y$10$MBhnquybTrZIhgSW.wwGfutS9brhqEd6A1n0GrKgO5lB06xK6msVa', 'titik@gmail.com', 'admin', '2025-06-03 17:34:53'),
(42, 'biro', '$2y$10$ROuv4rsVWRfdXpRlHs1vcOV56bB4NSqjtM3YoeACQDqIvoO8nFQYG', 'biro@gmail.com', 'user', '2025-06-03 17:38:28'),
(43, 'ovan1', '$2y$10$vy4EG18/.Ycoa5s8ln8uTu/utiRp7.Z61ihFzU4Rz1sMsph2XvUk6', 'ovan12@gmail.com', 'user', '2025-06-03 17:40:30'),
(44, 'ukrida', '$2y$10$UFN1RGGXsLUIXohvjVMNJeFIec0PTR4bD1tSUhIehc8RAF6k3f8gq', 'ukrida@gmail.com', 'admin', '2025-06-03 17:46:31'),
(45, 'ovan', '$2y$10$7eqeUPNm6CLQzNvoGXpBEOzPTYs3xHWIPwxXDIzlP3WiBuQEv4YcK', 'ovan@gmail.com', 'user', '2025-06-03 20:11:40'),
(46, 'miki', '$2y$10$SPVty313Pjqm4swsEjdB1eI5O9UD.LA8lVU5cLW24.mpxGtw2wtzW', 'miki123@gmail.com', 'admin', '2025-06-03 22:16:55'),
(47, 'mio', '$2y$10$Rp517kFyViKMV1UL6AbvcONTm0NLO/ld0ZC2fNB5KFPKyn.klWEL.', 'mio1@gmail.com', 'admin', '2025-06-03 22:38:28'),
(50, 'root', '$2y$10$NwywMPOWGxNn7YjqA5dgueCxskEq0f811hTagQgwBFrvR0ocY7Vq6', NULL, 'user', '2025-06-04 17:01:23'),
(51, 'mimi', '$2y$10$rmMbqdWW1LWS3NmIbWG1jOmCkAtaKRygBWcrb0.FwA3nekfGz53Gq', 'mimi@gmail.com', 'user', '2025-06-04 17:02:58'),
(52, 'mimi1', '$2y$10$Z0crVWNx4iomKKtKPI1WHOGXsReyJtfqZOvRdAEIPSciuu0RndK6C', 'mimi1@gmail.com', 'user', '2025-06-04 17:04:20'),
(53, 'mimi2', '$2y$10$kc4s.uPjgEVxr.TPXVT0duM6tgIffWMlx2C.3roKXgHH3ctLZrfXa', 'mimi2@gmail.com', 'user', '2025-06-04 17:05:02'),
(54, 'suryo', '$2y$10$AJcCM5XQE9M/ZjxdfLIL0efCHwAo9pYpsZ.XqpP7bIu.tO/DkolHW', 'suryo@gmail.com', 'user', '2025-06-04 17:54:23'),
(55, 'mimi3', '$2y$10$Thp9BooVBsnj0KuYU0P9hODoB2VWPzXap8Sob9yYL1Jj3ANexG4La', 'mimi3@gmail.com', 'user', '2025-06-04 17:56:37'),
(56, 'bemo', '$2y$10$NV7E9IV5W/yRYTTsp.41Zebh7ctLsEqXUGAPm.uNXiEyf0LATIFSi', 'bemo@gmail.com', 'user', '2025-06-04 18:08:06'),
(57, 'admin1', '$2y$10$GkBWuH6wg3A.aUma39TSRugNQdcOzgeDQIJhisR5Y7wANwhdtsPGO', 'admin1@gmail.com', 'admin', '2025-06-09 06:30:49'),
(58, 'Bobi', '$2y$10$xqYWEW98pAjMtqSOVyeucu5b8U1Bj/g6ebXCwg6Dd4R8hbBdIHsT6', NULL, 'courier', '2025-06-09 10:33:29'),
(59, 'Niki', '$2y$10$zvfNBCfRhDI8SmaCZvvIL.z7f.aH5Bs45DhNOBB4i6RVLP3mq2Htu', 'niki@gmail.com', 'user', '2025-06-09 13:35:35'),
(60, 'Biru', '$2y$10$En/NRcvXWO/COobHO0v5TeWunnIDHwUlyUb3lRG555KG0LS2q76pK', 'biru@gmail.com', 'user', '2025-06-09 13:56:04'),
(61, 'Baku', '$2y$10$xMrRNUzsMLOmGCGNhr8w9.2gxFeQ4NGkk1iO/eUxeBe1NqECsxtwG', 'baku@gmail.com', 'user', '2025-06-09 14:59:36');

-- --------------------------------------------------------

--
-- Struktur dari tabel `user_profile`
--

CREATE TABLE `user_profile` (
  `user_id` int(11) NOT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `province` varchar(100) DEFAULT NULL,
  `subdistrict` varchar(100) DEFAULT NULL,
  `kelurahan` varchar(100) DEFAULT NULL,
  `detail_address` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `user_profile`
--

INSERT INTO `user_profile` (`user_id`, `profile_photo`, `full_name`, `phone`, `address`, `city`, `created_at`, `province`, `subdistrict`, `kelurahan`, `detail_address`) VALUES
(44, '/toko_sembako/uploads/IMG-20250602-WA0034.jpg', 'Mamamia123', '0824242424', 'jALAN Baru', NULL, '2025-06-08 17:36:34', NULL, NULL, NULL, NULL),
(45, '/toko_sembako/uploads/IMG-20250602-WA0017.jpg', 'Geovano Yansen Jas', '0824242424', 'Jl.Sembako 123', 'Jakarta Barat', '2025-06-08 17:36:34', 'Jakarta', 'Tanjung Duren ', 'Tanjung Duren Utara', 'Kos Eveline No 221'),
(54, 'uploads/Geovano Yansen Jas_412023024_Kepelatihan VOXA.jpg', 'George', '082121211212', 'sbaeb', NULL, '2025-06-08 17:36:34', NULL, NULL, NULL, NULL),
(55, 'uploads/Geovano Yansen Jas_412023024_Kepelatihan VOXA.jpg', 'Geovano Yansen Jas', '3114141', '4111', NULL, '2025-06-08 17:36:34', NULL, NULL, NULL, NULL),
(58, NULL, 'Bobi', NULL, NULL, NULL, '2025-06-09 10:33:29', NULL, NULL, NULL, NULL),
(60, '/toko_sembako/uploads/Geovano Yansen Jas_412023024_Kepelatihan VOXA.jpg', 'Biru saga jaya', '08113324242', 'Garut raya', 'Jakarta Barat', '2025-06-09 14:09:38', 'Jakarta', 'grogol', 'Tj.Utara', 'Kos eveline nomor 2'),
(61, '/toko_sembako/uploads/Screenshot 2024-09-11 175501.png', 'Geovano Yansen Jas', '0824242424', 'wfwf', 'Jakarta Barat', '2025-06-09 15:00:33', 'Jakarta', 'Grogol', 'TJ. Utara', 'Kos eveline No. 221');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `admin_logs`
--
ALTER TABLE `admin_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `admin_id` (`admin_id`);

--
-- Indeks untuk tabel `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_cart_item` (`user_id`,`product_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indeks untuk tabel `contact_info`
--
ALTER TABLE `contact_info`
  ADD PRIMARY KEY (`id`),
  ADD KEY `updated_by` (`updated_by`);

--
-- Indeks untuk tabel `gallery_images`
--
ALTER TABLE `gallery_images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `uploaded_by` (`uploaded_by`);

--
-- Indeks untuk tabel `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeks untuk tabel `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `courier_id` (`courier_id`);

--
-- Indeks untuk tabel `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `slug` (`slug`),
  ADD KEY `author_id` (`author_id`);

--
-- Indeks untuk tabel `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `slug` (`slug`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indeks untuk tabel `user_profile`
--
ALTER TABLE `user_profile`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `admin_logs`
--
ALTER TABLE `admin_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT untuk tabel `cart`
--
ALTER TABLE `cart`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT untuk tabel `contact_info`
--
ALTER TABLE `contact_info`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `gallery_images`
--
ALTER TABLE `gallery_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT untuk tabel `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT untuk tabel `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=144;

--
-- AUTO_INCREMENT untuk tabel `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT untuk tabel `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `admin_logs`
--
ALTER TABLE `admin_logs`
  ADD CONSTRAINT `admin_logs_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `contact_info`
--
ALTER TABLE `contact_info`
  ADD CONSTRAINT `contact_info_ibfk_1` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `gallery_images`
--
ALTER TABLE `gallery_images`
  ADD CONSTRAINT `gallery_images_ibfk_1` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`courier_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `user_profile`
--
ALTER TABLE `user_profile`
  ADD CONSTRAINT `user_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
