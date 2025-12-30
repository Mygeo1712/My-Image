<?php
session_start();
include('../backend/dbconnect.php');

$username = $_POST['username'] ?? '';
$raw_password = $_POST['password'] ?? '';

$sql = "SELECT * FROM users WHERE username = ?";
$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result && $result->num_rows === 1) {
        $user = $result->fetch_assoc();

        if (password_verify($raw_password, $user['password'])) {
            $_SESSION['user_id'] = $user['id'];
            $_SESSION['username'] = $user['username'];
            $_SESSION['role'] = $user['role'];

            switch ($user['role']) {
                case 'admin':
                    header("Location: ../admin/dashboard.php");
                    exit;
                
                case 'courier':
                    if (strlen($raw_password) === 5) {
                        header("Location: ../courier/dashboard.php");
                        exit;
                    } else {
                         $_SESSION['login_error'] = "Password kurir tidak valid.";
                    }
                    break;
                
                case 'user':
                    if (strlen($raw_password) >= 6) {
                        header("Location: ../index.php");
                        exit;
                    } else {
                        $_SESSION['login_error'] = "Password pengguna tidak valid.";
                    }
                    break;
            }
        } else {
            $_SESSION['login_error'] = "Password salah.";
        }
    } else {
        $_SESSION['login_error'] = "Username atau password salah.";
    }
    $stmt->close();
} else {
    $_SESSION['login_error'] = "Kesalahan koneksi database.";
}

header("Location: ../login.php");
exit;
