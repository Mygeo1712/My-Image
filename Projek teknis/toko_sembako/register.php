<?php
session_start();
include('backend/dbconnect.php');

$error = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = trim($_POST['username']);
    $email    = trim($_POST['email']);
    $password = $_POST['password'];

    if (empty($username) || empty($email) || empty($password)) {
        $error = "Semua field harus diisi.";
    } elseif (strlen($password) < 6) {
        $error = "Password minimal harus terdiri dari 6 karakter.";
    } else {
        $checkSql = "SELECT id FROM users WHERE username = ? OR email = ?";
        $stmtCheck = $conn->prepare($checkSql);
        $stmtCheck->bind_param("ss", $username, $email);
        $stmtCheck->execute();
        $resultCheck = $stmtCheck->get_result();

        if ($resultCheck->num_rows > 0) {
            $error = "Username atau email sudah digunakan.";
        } else {
            $passwordHash = password_hash($password, PASSWORD_DEFAULT);
            $role = 'user';

            $sql  = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("ssss", $username, $passwordHash, $email, $role);

            if ($stmt->execute()) {
                $success = "Registrasi berhasil. Silakan <a href='login.php'>login</a>.";
            } else {
                $error = "Registrasi gagal. Silakan coba lagi.";
            }
            $stmt->close();
        }
        $stmtCheck->close();
    }
}
?>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Register page</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body {
            background: url('') no-repeat center center fixed;
            background-size: cover;
            background-repeat: no-repeat;
            background-position: center;
            font-family: 'Segoe UI', sans-serif;
            transition: background-image 0.5s ease-in-out;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }

        .login-container {
            background-color: rgba(0, 0, 0, 0.6);
            border-radius: 25px;
            padding: 40px 30px;
            color: #fff;
            max-width: 400px;
            width: 100%;
            box-shadow: 0 0 10px rgba(255, 255, 255, 0.2);
        }

        .checkbox-label {
            font-size: 0.9em;
        }

        .footer-note {
            color: #ccc;
            font-size: 0.75em;
            text-align: center;
        }

        .hover-back:hover {
            color: #ccc !important;
        }

        @media (max-width: 576px) {
            .login-container {
                padding: 30px 20px;
                max-width: 90%;
            }

            h3 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>

<body>

    <div class="login-container">

        <h3 class="mb-4 text-center"><strong>Register</strong></h3>

        <?php if ($error): ?>
            <div class="alert alert-danger"><?= htmlspecialchars($error) ?></div>
        <?php elseif ($success): ?>
            <div class="alert alert-success"><?= $success ?></div>
        <?php endif; ?>

        <form method="POST" action="">
            <input type="text" name="username" class="form-control mb-3" placeholder="Username" required value="<?= isset($_POST['username']) ? htmlspecialchars($_POST['username']) : '' ?>">
            <input type="email" name="email" class="form-control mb-3" placeholder="Email" required value="<?= isset($_POST['email']) ? htmlspecialchars($_POST['email']) : '' ?>">
            <input type="password" name="password" class="form-control mb-2" placeholder="Password" id="password" required>

            <div class="form-check text-start ms-2 mb-3">
                <input type="checkbox" class="form-check-input" id="showPassword" onclick="togglePassword()">
                <label for="showPassword" class="form-check-label checkbox-label">Show Password</label>
            </div>

            <button type="submit" class="btn btn-primary w-100 text-center">Register</button>

            <div class="text-center">
                <a href="login.php" class="mt-3 d-block text-white text-decoration-none hover-back"><- back</a>
            </div>
        </form>

        <div class="mt-3 footer-note">©2025 Toko Sembako</div>
    </div>

    <script>
        function togglePassword() {
            const pwField = document.getElementById("password");
            pwField.type = pwField.type === "password" ? "text" : "password";
        }

        const backgrounds = [
            'assets/background_img/bg1.jpg',
            'assets/background_img/bg2.jpg'
        ];

        function setDynamicBackground() {
            const hour = new Date().getHours();
            const index = Math.floor(hour % backgrounds.length);
            document.body.style.backgroundImage = `url('${backgrounds[index]}')`;
        }

        setDynamicBackground();
    </script>

</body>

</html>